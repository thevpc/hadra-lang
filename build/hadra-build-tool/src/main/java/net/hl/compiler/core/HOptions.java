package net.hl.compiler.core;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import net.thevpc.common.textsource.JTextSourceFactory;
import net.thevpc.common.textsource.JTextSourceRoot;

public abstract class HOptions<T extends HOptions> {

    private final List<JTextSourceRoot> sources = new ArrayList<>();
    private final Set<String> classpath = new LinkedHashSet<>();
    private final EnumSet<HTask> tasks = EnumSet.noneOf(HTask.class);
    private String javaFolder;
    private String classFolder;
    private String jarFolder;
    private String projectRoot;
    private String targetFolder;

    /**
     * in incremental mode pre-processor wont be executed and all project
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
        this.sources.addAll(t.sources);
        this.classpath.addAll(t.classpath);
        this.javaFolder = t.javaFolder;
        this.targetFolder = t.targetFolder;
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

    public JTextSourceRoot[] sources() {
        return sources.toArray(new JTextSourceRoot[0]);
    }

    public String getJavaFolder() {
        return javaFolder;
    }

    public T setJavaFolder(String generateJavaFolder) {
        this.javaFolder = generateJavaFolder;
        return (T) this;
    }

    public String getTargetFolder() {
        return targetFolder;
    }

    public T setTargetFolder(String target) {
        this.targetFolder = target;
        return (T) this;
    }

    public String getClassFolder() {
        return classFolder;
    }

    public T setClassFolder(String folder) {
        this.classFolder = folder;
        return (T) this;
    }

    public String getJarFolder() {
        return jarFolder;
    }

    public T setJarFolder(String folder) {
        this.jarFolder = folder;
        return (T) this;
    }

    public T addSourceResourcesFolder(String path) {
        sources.add(JTextSourceFactory.rootResourceFolder(path, "*.hl"));
        return (T) this;
    }

    public T addSourceResourcesFile(String path) {
        sources.add(JTextSourceFactory.rootResourceFile(path));
        return (T) this;
    }

    public T addSourceMavenProject(String path) {
        addSourceFile(new File(path, "src/main/hl"));
        return (T) this;
    }

    public T addSourceFile(String path) {
        sources.add(JTextSourceFactory.rootFile(new File(path)));
        return (T) this;
    }

    public T addSourceFile(File file) {
        sources.add(JTextSourceFactory.rootFile(file));
        return (T) this;
    }

    public T addSourceLibraryURL(String url) {
        try {
            sources.add(JTextSourceFactory.rootURLFolder(new URL(url), "*.hl"));
        } catch (MalformedURLException e) {
            throw new UncheckedIOException(e);
        }
        return (T) this;
    }

    public T addSourceFileURL(String url) {
        try {
            sources.add(JTextSourceFactory.rootURL(new URL(url)));
        } catch (MalformedURLException e) {
            throw new UncheckedIOException(e);
        }
        return (T) this;
    }

    public T addSourceText(String text, String sourceName) {
        sources.add(JTextSourceFactory.rootString(text, sourceName));
        return (T) this;
    }

    public T addTask(HTask t) {
        if (t != null) {
            tasks.add(t);
        }
        return (T) this;
    }

    public T removeTask(HTask t) {
        if (t != null) {
            tasks.remove(t);
        }
        return (T) this;
    }

    public boolean containsAnyTask(HTask... others) {
        for (HTask other : others) {
            if (getTasks().contains(other)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAllTasks(HTask... others) {
        for (HTask other : others) {
            if (!getTasks().contains(other)) {
                return false;
            }
        }
        return true;
    }

    public Set<HTask> getTasks() {
        return tasks;
    }

}
