package net.hl.compiler.stages.generators.java;

import java.io.BufferedInputStream;
import net.hl.compiler.core.HOptions;
import net.hl.compiler.core.HProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import net.hl.compiler.HL;
import net.hl.compiler.core.HTarget;
import net.hl.compiler.core.JModuleId;
import net.hl.compiler.stages.AbstractHStage;
import net.hl.compiler.utils.DepIdAndFile;
import net.thevpc.common.msg.Messages;
import net.thevpc.jeep.util.JStringUtils;
import net.thevpc.common.textsource.JTextSource;
import net.thevpc.common.textsource.JTextSourceFactory;
import net.thevpc.common.textsource.JTextSourceToken;
import net.thevpc.common.textsource.log.JSourceMessage;
import net.thevpc.nuts.NutsConstants;
import net.thevpc.nuts.NutsDependency;
import net.thevpc.nuts.NutsDescriptor;

public class HStage10JavaCompiler extends AbstractHStage {

    private static final Logger LOG = Logger.getLogger(HStage10JavaCompiler.class.getName());

    @Override
    public HTarget[] getTargets() {
        return new HTarget[]{HTarget.RUN, HTarget.COMPILE, HTarget.JAVA};
    }

    @Override
    public boolean isEnabled(HProject project, HL options) {
        if ((options.containsAnyTargets(HTarget.COMPILE, HTarget.RUN))) {
            if (options.containsAllTargets(HTarget.JAVA)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void processProject(HProject project, HOptions options) {
        HJavaContextHelper jn = HJavaContextHelper.of(project);

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        List<JavaFileObject> compilationUnits = new ArrayList<>();
        for (String javaFile : jn.getJavaFiles()) {
            compilationUnits.add(new SimpleJavaFileObjectImpl(new File(javaFile).toURI(), JavaFileObject.Kind.SOURCE));
        }
        File classesFolder = options.getClassFolder();
        if (classesFolder == null) {
            classesFolder = new File("target/hl/classes");
        }
        if (options.isClean()) {
            if (classesFolder.isDirectory()) {
//                if(new File(".java-generated").exists()){
//                   folder.get
//                }
            }
        }
        classesFolder.mkdirs();
        DepIdAndFile[] dependencyFiles = project.getIndexedProject().getDependencies();
        List<String> joptions = new ArrayList<>();
        joptions.add("-classpath");
        joptions.add(Arrays.stream(dependencyFiles).map(x -> x.getFile()).collect(Collectors.joining(File.pathSeparator)));
        joptions.add("-d");
        joptions.add(classesFolder.getPath());

        JModuleId m = JModuleId.valueOf(project.getIndexedProject().getModuleId());
        String jarName = (JStringUtils.isBlank(m.getGroupId()) ? "" : (m.getGroupId() + "-"))
                + m.getArtifactId() + "-" + m.getVersion() + ".jar";
        CompilationTask task = compiler.getTask(null, null, diagnostics, joptions, null, compilationUnits);
        boolean success = task.call();
        for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
            Level level = Level.SEVERE;
            switch (diagnostic.getKind()) {
                case ERROR: {
                    level = Level.SEVERE;
                    break;
                }
                case WARNING: {
                    level = Level.WARNING;
                    break;
                }
                case NOTE: {
                    level = Level.INFO;
                    break;
                }
                case MANDATORY_WARNING: {
                    level = Level.WARNING;
                    break;
                }
                case OTHER: {
                    level = Level.FINE;
                    break;
                }
            }
            project.log().add(
                    new JSourceMessage(
                            diagnostic.getCode(),
                            "javac",
                            new DiagnosticJTextSourceToken(diagnostic),
                            Messages.text(level, diagnostic.getMessage(null))
                    )
            );
        }

        if (success) {
            //need to generate descriptor that contains dependencies...
            //project.getIndexedProject().getDependencies()
            NutsDescriptor desc = project.getWorkspace().descriptor().descriptorBuilder()
                    .setId(project.getIndexedProject().getModuleId())
                    .setPackaging("jar")
                    .addPlatform("java")
                    .addDependencies(
                            Arrays.stream(project.getIndexedProject().getDependencies())
                                    .map(
                                            i
                                            -> project.getWorkspace().dependency().builder()
                                                    .setId(
                                                            project.getWorkspace().id().parser().parse(i.getId())
                                                    ).build()
                                    ).toArray(NutsDependency[]::new)
                    ).build();
            project.getWorkspace().descriptor().formatter(desc).setSession(project.getSession())
                    .print(new File("target/hl/classes/META-INF/" + NutsConstants.Files.DESCRIPTOR_FILE_NAME));
            File jarFolder = options.getJarFolder();
            if (jarFolder == null) {
                jarFolder = new File("target/hl");
            }
            jarFolder.mkdirs();
            File jarPath = new File(jarFolder, jarName);
            generateJar(project, jarPath, classesFolder);
            jn.setOutputJarFile(jarPath);
        }
    }

    private void generateJar(HProject project, File jarFile, File... classesAndResourcesFolders) {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        try (JarOutputStream target = new JarOutputStream(new FileOutputStream(jarFile), manifest)) {
            for (File classesAndResourcesFolder : classesAndResourcesFolders) {
                if (classesAndResourcesFolder.isDirectory()) {
                    addToJar(classesAndResourcesFolder, classesAndResourcesFolder, target);
                } else {
                    addToJar(classesAndResourcesFolder.getParentFile(), classesAndResourcesFolder, target);
                }
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private void addToJar(File sourceRoot, File source, JarOutputStream target) throws IOException {
        BufferedInputStream in = null;
        try {
            String rootPath = sourceRoot.getPath().replace("\\", "/");
            String sourcePath = source.getPath().replace("\\", "/");
            sourcePath = sourcePath.substring(rootPath.length());
            if (source.isDirectory()) {
                String name = sourcePath;
                if (!name.isEmpty()) {
                    if (!name.endsWith("/")) {
                        name += "/";
                    }
                    if (!name.equals("/") && name.startsWith("/")) {
                        name = name.substring(1);
                    }
                    JarEntry entry = new JarEntry(name);
                    entry.setTime(source.lastModified());
                    target.putNextEntry(entry);
                    target.closeEntry();
                }
                for (File nestedFile : source.listFiles()) {
                    addToJar(sourceRoot, nestedFile, target);
                }
                return;
            }

            String name = sourcePath;
            if (!name.equals("/") && name.startsWith("/")) {
                name = name.substring(1);
            }
            JarEntry entry = new JarEntry(name);
            entry.setTime(source.lastModified());
            target.putNextEntry(entry);
            in = new BufferedInputStream(new FileInputStream(source));

            byte[] buffer = new byte[1024];
            while (true) {
                int count = in.read(buffer);
                if (count == -1) {
                    break;
                }
                target.write(buffer, 0, count);
            }
            target.closeEntry();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    static class DiagnosticJTextSourceToken implements JTextSourceToken {

        private final Diagnostic diagnostic;

        public DiagnosticJTextSourceToken(Diagnostic diagnostic) {
            this.diagnostic = diagnostic;
        }

        @Override
        public JTextSource getSource() {
            final Object s = diagnostic.getSource();
            if (s instanceof JavaFileObject) {
                final URI u = ((JavaFileObject) s).toUri();
                return JTextSourceFactory.fromURI(u);
            }
            return null;//String.valueOf();
        }

        @Override
        public int getStartLineNumber() {
            return (int) diagnostic.getLineNumber();
        }

        @Override
        public int getStartColumnNumber() {
            return (int) diagnostic.getColumnNumber();
        }

        @Override
        public int getStartCharacterNumber() {
            return (int) diagnostic.getStartPosition();
        }

        @Override
        public int getEndLineNumber() {
            return (int) diagnostic.getLineNumber();
        }

        @Override
        public int getEndColumnNumber() {
            return (int) diagnostic.getColumnNumber();
        }

        @Override
        public int getEndCharacterNumber() {
            return (int) diagnostic.getEndPosition();
        }

        @Override
        public JTextSourceToken copy() {
            return new DiagnosticJTextSourceToken(diagnostic);
        }

        @Override
        public long getTokenNumber() {
            return -1;
        }
    }

    private class SimpleJavaFileObjectImpl extends SimpleJavaFileObject {

        public SimpleJavaFileObjectImpl(URI uri, Kind kind) {
            super(uri, kind);
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return new String(Files.readAllBytes(Paths.get(toUri())));
        }
    }

}
