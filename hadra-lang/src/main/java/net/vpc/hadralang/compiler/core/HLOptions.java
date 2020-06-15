package net.vpc.hadralang.compiler.core;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import net.vpc.common.jeep.core.compiler.JSourceFactory;
import net.vpc.common.jeep.core.compiler.JSourceRoot;

public abstract class HLOptions<T extends HLOptions> {

    private final List<JSourceRoot> roots = new ArrayList<>();
    private final Set<String> classpath = new LinkedHashSet<>();
    private File generateJavaFolder;
    private String projectRoot;
    private boolean clean;

    /**
     * in incremental mode preprocessor wont be executed and all project information is
     * supposed to be in the indexer. If not an error will be reported.
     */
    private boolean incremental =false;

    public Set<String> getClassPath() {
        return Collections.unmodifiableSet(classpath);
    }

    public T addClassPathItem(String path) {
        classpath.add(path);
        return (T) this;
    }

    public <T, R extends HLOptions> T setAll(HLOptions<R> t) {
        this.roots.addAll(t.roots);
        this.classpath.addAll(t.classpath);
        this.generateJavaFolder = t.generateJavaFolder;
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
        return (T)this;
    }

    public boolean isClean() {
        return clean;
    }

    public T clean() {
        return clean(true);
    }

    public T clean(boolean clean) {
        this.clean = clean;
        return (T)this;
    }

    public JSourceRoot[] roots() {
        return roots.toArray(new JSourceRoot[0]);
    }

    public File generateJavaFolder() {
        return generateJavaFolder;
    }

    public T generateJavaFolder(File generateJavaFolder) {
        this.generateJavaFolder = generateJavaFolder;
        return (T) this;
    }
    public T generateJavaFolder(String generateJavaFolder) {
        this.generateJavaFolder = generateJavaFolder==null?null:new File(generateJavaFolder);
        return (T) this;
    }

    public T addSourceResourcesFolder(String path) {
        roots.add(JSourceFactory.rootResourceFolder(path, "*.hl"));
        return (T) this;
    }

    public T addSourceResourcesFile(String path) {
        roots.add(JSourceFactory.rootResourceFile(path));
        return (T) this;
    }

    public T addSourceMavenProject(String path) {
        addSourceFile(new File(path,"src/main/hl"));
        return (T) this;
    }

    public T addSourceFile(String path) {
        roots.add(JSourceFactory.rootFile(new File(path)));
        return (T) this;
    }

    public T addSourceFile(File file) {
        roots.add(JSourceFactory.rootFile(file));
        return (T) this;
    }

    public T addSourceLibraryURL(String url) {
        try {
            roots.add(JSourceFactory.rootURLFolder(new URL(url), "*.hl"));
        } catch (MalformedURLException e) {
            throw new UncheckedIOException(e);
        }
        return (T) this;
    }

    public T addSourceFileURL(String url) {
        try {
            roots.add(JSourceFactory.rootURL(new URL(url)));
        } catch (MalformedURLException e) {
            throw new UncheckedIOException(e);
        }
        return (T) this;
    }

    public T addSourceText(String text, String sourceName) {
        roots.add(JSourceFactory.rootString(text,sourceName));
        return (T) this;
    }

}
