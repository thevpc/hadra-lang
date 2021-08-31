package net.hl.build.mvnplugin;

import net.hl.compiler.HL;
import net.hl.compiler.core.HProject;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import net.thevpc.nuts.Nuts;

/**
 *
 */
@Mojo(
        name = "hl",
        threadSafe = true,
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE
)
//@Execute(goal = "hl")
public class HLMvnPlugin extends AbstractMojo {

    @Component
    private BuildPluginManager pluginManager;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${project.build.directory}", required = true, readonly = true)
    private String buildDirectory;

    //<editor-fold desc="Parameters">
    /**
     * The directory where the Hadra Lang files ({@code *.hl}) are located.
     */
    @Parameter(defaultValue = "${basedir}/src/main/hl")
    private File sourceDirectory;

    /**
     * output directory where the Java files are generated.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/hl")
    private File outputDirectory;

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public HLMvnPlugin setSourceDirectory(File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
        return this;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public HLMvnPlugin setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
        return this;
    }
    //</editor-fold>

    public void execute()
            throws MojoExecutionException {
        List<String> compileClasspathElements = new ArrayList<>();
        try {
            compileClasspathElements = project.getCompileClasspathElements();
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Unable to resolve dependencies", e);
        }
        List<URL> classLoaderURLs = new ArrayList<>();
        for (String item : compileClasspathElements) {
            if (item.equals(buildDirectory) || item.startsWith(buildDirectory + File.separator)) {
                //ignore
            } else {
                try {
                    if (item.startsWith("file://") || item.startsWith("http://") || item.startsWith("https://")) {
                        classLoaderURLs.add(new URL(item));
                    } else {
                        classLoaderURLs.add(new File(item).toURI().toURL());
                    }
                } catch (MalformedURLException e) {
                    throw new MojoExecutionException("invalid classpath URL : " + item);
                }
            }
        }
        URLClassLoader classLoader = new URLClassLoader(classLoaderURLs.toArray(new URL[0]),
                Thread.currentThread().getContextClassLoader()
        );
        HL hl = new HL(classLoader, null, Nuts.openWorkspace());
        for (String item : compileClasspathElements) {
            if (item.equals(buildDirectory) || item.startsWith(buildDirectory + File.separator)) {
                //ignore
            } else {
                hl.addClassPathItem(item);
            }
        }
        hl.addSourceFile(getSourceDirectory());
        hl.setJavaFolder(getOutputDirectory() == null ? null : getOutputDirectory().getPath());

        HProject project = hl.compile();
        if (!project.isSuccessful()) {
            throw new MojoExecutionException(
                    "compilation failed with " + project.getErrorCount() + " errors.");
        }
    }
}
