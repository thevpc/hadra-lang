package net.hl.compiler.core;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import net.vpc.common.textsource.JTextSourceFactory;
import net.vpc.common.textsource.JTextSourceRoot;

public abstract class HOptions<T extends HOptions> {

    private final List<JTextSourceRoot> roots = new ArrayList<>();
    private final Set<String> classpath = new LinkedHashSet<>();
    private final EnumSet<HTarget> targets = EnumSet.noneOf(HTarget.class);
    private File javaFolder;
    private File classFolder;
    private File jarFolder;
    private String projectRoot;
    private boolean clean;

    /**
     * in incremental mode preprocessor wont be executed and all project
     * information is supposed to be in the indexer. If not an error will be
     * reported.
     */
    private boolean incremental = false;

    public Set<String> getClassPath() {
        return Collections.unmodifiableSet(classpath);
    }

    public T addClassPathItem(String path) {
        classpath.add(path);
        return (T) this;
    }

    public <T, R extends HOptions> T setAll(HOptions<R> t) {
        this.roots.addAll(t.roots);
        this.classpath.addAll(t.classpath);
        this.javaFolder = t.javaFolder;
        this.clean = t.clean;
        this.incremental = t.incremental;
        this.projectRoot = t.projectRoot;
        return (T) this;
    }

    public String getProjectRoot() {
        return projectRoot;
    }

    public T setProjectRoot(String projectRoot) {
        this.projectRoot = projectRoot;
        return (T) this;
    }

    public boolean isIncremental() {
        return incremental;
    }

    public T setIncremental(boolean incremental) {
        this.incremental = incremental;
        return (T) this;
    }

    public boolean isClean() {
        return clean;
    }

    public T clean() {
        return clean(true);
    }

    public T clean(boolean clean) {
        this.clean = clean;
        return (T) this;
    }

    public JTextSourceRoot[] roots() {
        return roots.toArray(new JTextSourceRoot[0]);
    }

    public File getJavaFolder() {
        return javaFolder;
    }

    public T setJavaFolder(File generateJavaFolder) {
        this.javaFolder = generateJavaFolder;
        return (T) this;
    }

    public T setJavaFolder(String folder) {
        this.javaFolder = folder == null ? null : new File(folder);
        return (T) this;
    }

    public File getClassFolder() {
        return classFolder;
    }

    public T setClassFolder(File folder) {
        this.classFolder = folder;
        return (T) this;
    }

    public T setClassFolder(String folder) {
        this.classFolder = folder == null ? null : new File(folder);
        return (T) this;
    }

    public File getJarFolder() {
        return jarFolder;
    }

    public T setJarFolder(File folder) {
        this.jarFolder = folder;
        return (T) this;
    }

    public T setJarFolder(String folder) {
        this.jarFolder = folder == null ? null : new File(folder);
        return (T) this;
    }

    public T addSourceResourcesFolder(String path) {
        roots.add(JTextSourceFactory.rootResourceFolder(path, "*.hl"));
        return (T) this;
    }

    public T addSourceResourcesFile(String path) {
        roots.add(JTextSourceFactory.rootResourceFile(path));
        return (T) this;
    }

    public T addSourceMavenProject(String path) {
        addSourceFile(new File(path, "src/main/hl"));
        return (T) this;
    }

    public T addSourceFile(String path) {
        roots.add(JTextSourceFactory.rootFile(new File(path)));
        return (T) this;
    }

    public T addSourceFile(File file) {
        roots.add(JTextSourceFactory.rootFile(file));
        return (T) this;
    }

    public T addSourceLibraryURL(String url) {
        try {
            roots.add(JTextSourceFactory.rootURLFolder(new URL(url), "*.hl"));
        } catch (MalformedURLException e) {
            throw new UncheckedIOException(e);
        }
        return (T) this;
    }

    public T addSourceFileURL(String url) {
        try {
            roots.add(JTextSourceFactory.rootURL(new URL(url)));
        } catch (MalformedURLException e) {
            throw new UncheckedIOException(e);
        }
        return (T) this;
    }

    public T addSourceText(String text, String sourceName) {
        roots.add(JTextSourceFactory.rootString(text, sourceName));
        return (T) this;
    }

    public T addTarget(HTarget t) {
        if (t != null) {
            targets.add(t);
        }
        return (T) this;
    }

    public T removeTarget(HTarget t) {
        if (t != null) {
            targets.remove(t);
        }
        return (T) this;
    }

    public boolean isTargetAny(HTarget... ts) {
        for (HTarget t : ts) {
            if (isTarget(t)) {
                return true;
            }
        }
        return false;
    }

    public boolean isTarget(HTarget t) {
        if (t == null) {
            return false;
        }
        switch (t) {
            case JAR:
                return isTarget0(HTarget.JAR);
            case CLASS:
                return isTarget0(HTarget.CLASS) || isTargetAny(HTarget.JAR);
            case JAVA:
                return isTarget0(HTarget.JAVA) || isTargetAny(HTarget.JAR, HTarget.CLASS);
            case RESOLVED_AST:
                return isTarget0(HTarget.RESOLVED_AST) || isTargetAny(HTarget.JAVA);
            case AST:
                return isTarget0(HTarget.AST) || isTarget(HTarget.RESOLVED_AST);
        }
        return isTarget0(t);
    }

    private boolean isTarget0Any(HTarget... ts) {
        for (HTarget t : ts) {
            if (isTarget0(t)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTarget0(HTarget t) {
        if (targets.contains(t)) {
            return true;
        }
        if (t != null) {
            switch (t) {
                case JAVA: {
                    return javaFolder != null;
                }
                case CLASS: {
                    return classFolder != null;
                }
                case JAR: {
                    return jarFolder != null;
                }
            }
        }
        return false;
    }

    public Set<HTarget> getTargets() {
        return targets;
    }

}
