package net.hl.compiler.index;

import net.thevpc.jeep.*;
import net.thevpc.jeep.core.JIndexQuery;
import net.thevpc.jeep.core.index.DefaultJIndexDocument;
import net.thevpc.jeep.util.Chronometer;
import net.thevpc.jeep.util.JeepUtils;
import net.hl.compiler.ast.HNBlock;
import net.hl.compiler.ast.HNDeclareType;
import net.hl.lang.IntRef;
import net.hl.lang.Ref;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class HIndexerImpl implements HIndexer {

    private static final Logger LOG = Logger.getLogger(HIndexerImpl.class.getName());
    JIndexStore store;

    public HIndexerImpl() {
        this(null);
    }

    public HIndexerImpl(JIndexStore store) {
        if (store == null) {
            store = new JIndexStoreMemory();
        }
        this.store = store;
    }

    @Override
    public int indexSDK(String sdkHome, boolean force, JCompilerLog clog) {
        if (sdkHome == null) {
            sdkHome = System.getProperty("java.home");
        }
        if (sdkHome != null) {
            File file = new File(sdkHome, "lib" + File.separator + "rt.jar");
            if (file.exists()) {
                //this is pre JDK 9 SDK
                return indexLibrary(file, force, clog);
            }
            file = new File(sdkHome, "jmods");
            if (file.isDirectory()) {
                //this is JDK 9 SDK or later
                File[] files = file.listFiles(x -> x.getName().endsWith(".jmod") && x.isFile());
                if (files != null) {
                    int x = 0;
                    for (File file1 : files) {
                        int v = indexLibrary(file1, force, clog);
                        if (v > 0) {
                            x += v;
                        }
                    }
                    return x;
                }
            }
        }
        throw new JParseException("unable to resolve SDK location (rt.jar). using home :" + sdkHome);
    }

    @Override
    public int indexSource(JCompilationUnit compilationUnit, JCompilerLog clog) {
        Chronometer chrono = Chronometer.start();
        String uuid = compilationUnit.getSource().name();
        store.removeIndex(uuid);
        HNBlock body = (HNBlock) compilationUnit.getAst();
        IntRef counter = Ref.of(0);
        if (body != null) {
            for (HNDeclareType item : body.findDeclaredTypes()) {
                indexDeclareType(uuid, item, counter);
            }
//            for (HNDeclareIdentifier item : body.findDeclaredIdentifiers()) {
//                indexDeclareIdentifier(uuid, item);
//            }
//            for (HNDeclareInvokable item : body.findDeclaredInvokables()) {
//                indexDeclareInvokable(uuid, item);
//            }
//            for (HNDeclareMetaPackage item : body.findDeclaredModules()) {
//                indexDeclareMetaPackage(uuid, item);
//            }
        }
        LOG.log(Level.FINE, "index source {0} ({1} classes ) in {2}", new Object[]{uuid, counter.get(), chrono.stop().getDuration()});
        if (clog != null) {
            clog.jinfo("II01", "index", null, "index source {0} ({1} classes ) in {2}", new Object[]{uuid, counter.get(), chrono.stop().getDuration()});
        }
        return counter.get();
    }

    @Override
    public int indexLibrary(URL url, boolean force, JCompilerLog clog) {
        if ("file".equals(url.getProtocol())) {
            File f;
            try {
                f = new File(url.toURI());
            } catch (URISyntaxException e) {
                f = new File(url.getPath());
            }
            return indexLibrary(f, force, clog);
        } else {
            throw new JFixMeLaterException();
        }
    }

    private boolean isIndexedLibrary(String uuid, long lastModified) {
        String elementType = "LibFileInfo";
        JIndexDocument[] allOld = store.searchDocuments(uuid, elementType).toArray(new JIndexDocument[0]);
        if (allOld.length > 0) {
            for (int i = 1; i < allOld.length; i++) {
                store.removeIndex(uuid, elementType, allOld[i].getId());
            }
            long oldLastModified = Long.parseLong(allOld[0].getValue("lastModified"));
            if (oldLastModified == lastModified) {
                //already indexed
                return true;
            }
        }
        return false;
    }

    @Override
    public int indexLibrary(File file, boolean force, JCompilerLog clog) {
        String uuid = file.getAbsolutePath();
        IntRef counter = Ref.of(0);
        try {
            uuid = file.getCanonicalPath();
        } catch (Exception ex) {
            //
        }
        String elementType = "LibFileInfo";
        long lastModified = file.lastModified();
        if (!force) {
            if (isIndexedLibrary(uuid, lastModified)) {
                return 0;
            }
        }
        LOG.log(Level.FINE, "index library {0} started...", uuid);
        Chronometer chrono = Chronometer.start();
        if (file.isDirectory()) {
            try {
                Files.walk(file.toPath()).forEach(x -> readFile(x.toFile(), file, counter, clog));
            } catch (IOException e) {
                LOG.log(Level.FINE, "index library {0} failed with : {1}", new Object[]{uuid, e.toString()});
                if (clog != null) {
                    clog.jerror("IE01", "index", null, "index library {0} failed with : {1}", new Object[]{uuid, e.toString()});
                }
                return counter.get();
            }
        } else {
            String fname = file.getName().toLowerCase();
            if (fname.endsWith(".jar")) {
                try (JarFile jar = new JarFile(uuid)) {
                    Stream<JarEntry> str = jar.stream();
                    str.forEach(z -> readJar(jar, z, counter,clog));
                } catch (IOException e) {
                    LOG.log(Level.FINE, "index library {0} failed with : {1}", new Object[]{uuid, e.toString()});
                    if (clog != null) {
                        clog.jerror("IE01", "index", null, "index library {0} failed with : {1}", new Object[]{uuid, e.toString()});
                    }
                    return counter.get();
                }
            } else if (fname.endsWith(".jmod")) {
                try (ZipFile jar = new ZipFile(uuid)) {
                    Stream<ZipEntry> str = (Stream<ZipEntry>) jar.stream();
                    str.forEach(z -> readJmod(jar, z, counter,clog));
                } catch (IOException e) {
                    LOG.log(Level.FINE, "index library {0} failed with : {1}", new Object[]{uuid, e.toString()});
                    if (clog != null) {
                        clog.jerror("IE01", "index", null, "index library {0} failed with : {1}", new Object[]{uuid, e.toString()});
                    }
                    return counter.get();
                }
            } else if (fname.endsWith(".zip")) {
                try (ZipFile jar = new ZipFile(uuid)) {
                    Stream<ZipEntry> str = (Stream<ZipEntry>) jar.stream();
                    str.forEach(z -> readJmod(jar, z, counter,clog));
                } catch (IOException e) {
                    LOG.log(Level.FINE, "index library {0} failed with : {1}", new Object[]{uuid, e.toString()});
                    if (clog != null) {
                        clog.jerror("IE01", "index", null, "index library {0} failed with : {1}", new Object[]{uuid, e.toString()});
                    }
                    return counter.get();
                }
            } else {
                throw new IllegalArgumentException("unable to index library. unsupported library file type : " + fname);
            }
        }
        store.index(uuid, elementType,
                new DefaultJIndexDocument(uuid)
                        .add("lastModified", String.valueOf(lastModified), true), true
        );
        LOG.log(Level.FINE, "index Library {0} ({1} classes) finished in {2}", new Object[]{uuid, counter.get(), chrono.stop().getDuration()});
        if (clog != null) {
            clog.jinfo("II02", "index", null, "index library {0} ({1} classes) finished in {2}", new Object[]{uuid, counter.get(), chrono.stop().getDuration()});
        }
        return counter.get();
    }

    private void readFile(File entry, File root, IntRef typesCounter, JCompilerLog clog) {
        String name = entry.getAbsolutePath();
        try {
            name = entry.getCanonicalPath();
        } catch (Exception ex) {
            //
        }
        String rname = root.getAbsolutePath();
        try {
            rname = root.getCanonicalPath();
        } catch (Exception ex) {
            //
        }
        if (name.endsWith(".class") && isValidClassFile(name)) {
            try (InputStream jis = new FileInputStream(entry)) {
                ClassReader cr = new ClassReader(jis);
                cr.accept(new JavaClassVisitor(this, rname), ClassReader.SKIP_FRAMES);
                typesCounter.inc();
            } catch (IOException e) {
                LOG.log(Level.FINE, "read Class file failed with : {0}", e.toString());
                if (clog != null) {
                    clog.warn("IE03", "index", null, String.format("read class file failed with : {0}", e.toString()));
                }
            }
        }
    }

    private void readJar(JarFile jarFile, JarEntry entry, IntRef typesCounter, JCompilerLog clog) {
        String name = entry.getName();
        if (name.endsWith(".class") && isValidClassFile(name)) {
            try (InputStream jis = jarFile.getInputStream(entry)) {
                ClassReader cr = new ClassReader(jis);
                cr.accept(new JavaClassVisitor(this, jarFile.getName()), ClassReader.SKIP_FRAMES);
                typesCounter.inc();
            } catch (IOException e) {
                LOG.log(Level.FINE, "read jar jar file failed with : {0}", e.toString());
                if (clog != null) {
                    clog.warn("IE04", "index", null, String.format("read jar file failed with : {0}", e.toString()));
                }
            }
        }
    }

    private void readJmod(ZipFile jmodFile, ZipEntry entry, IntRef typesCounter, JCompilerLog clog) {
        String name = entry.getName();
        if (name.startsWith("classes/") && name.endsWith(".class") && isValidClassFile(name)) {
            try (InputStream jis = jmodFile.getInputStream(entry)) {
                ClassReader cr = new ClassReader(jis);
                cr.accept(new JavaClassVisitor(this, jmodFile.getName()), ClassReader.SKIP_FRAMES);
                typesCounter.inc();
            } catch (IOException e) {
                LOG.log(Level.FINE, "read jmod file failed with : {0}", e.toString());
                if (clog != null) {
                    clog.warn("IE05", "index", null, String.format("read jmod file failed with : {0}", e.toString()));
                }
            }
        }
    }

    private void readJavaZip(ZipFile jmodFile, ZipEntry entry, IntRef typesCounter, JCompilerLog clog) {
        String name = entry.getName();
        if (name.endsWith(".class") && isValidClassFile(name)) {
            try (InputStream jis = jmodFile.getInputStream(entry)) {
                ClassReader cr = new ClassReader(jis);
                cr.accept(new JavaClassVisitor(this, jmodFile.getName()), ClassReader.SKIP_FRAMES);
                typesCounter.inc();
            } catch (IOException e) {
                LOG.log(Level.FINE, "read zip file failed with : {0}", e.toString());
                if (clog != null) {
                    clog.warn("IE06", "index", null, String.format("read zip file failed with : {0}", e.toString()));
                }
            }
        }
    }

    public void indexDeclareType(String uuid, HNDeclareType item) {
        indexDeclareType(uuid, item, new IntRef(0));
    }

    public void indexDeclareType(String uuid, HNDeclareType item, IntRef typesCounter) {
        typesCounter.inc();
        //remove old indexes
        for (String elementType : new String[]{
            "Method",
            "Field",
            "Type",
            "Constructor"
        }) {
            for (JIndexDocument d : store.searchDocuments(uuid, elementType, new JIndexQuery().whereEq("declaringType", item.getFullName()))) {
                store.removeIndex(uuid, elementType, d.getId());
            }
        }
        indexType(new HIndexedClass(item, uuid));
        JNode body = item.getBody();
        if (body instanceof HNBlock) {
            HNBlock bloc = (HNBlock) body;
            for (HNDeclareType item2 : bloc.findDeclaredTypes()) {
                indexDeclareType(uuid, item2, typesCounter);
            }
//            for (HNDeclareIdentifier item2 : bloc.getVarDeclarations()) {
//                indexDeclareIdentifier(uuid, item2);
//            }
//            for (HNDeclareInvokable item2 : bloc.getFunctionDeclarations()) {
//                indexDeclareInvokable(uuid, item2);
//            }
        }
    }

//    private void indexDeclareIdentifier(String uuid, HNDeclareIdentifier item) {
//        int count = item.getIdentifierTokens().length;
//        for (int i = 0; i < count; i++) {
//            store.index(uuid, "Field", new HLIndexedField(item, i),true);
//        }
//    }
//    private void indexDeclareInvokable(String uuid, HNDeclareInvokable item) {
//        if (item.isConstructor()) {
//            store.index(uuid, "Constructor", new HLIndexedConstructor(item),true);
//        } else {
//            store.index(uuid, "Method", new HLIndexedMethod(item),true);
//        }
//    }
    @Override
    public void indexProject(HIndexedProject project) {
        LOG.log(Level.FINE, "index project {0} @ {1}", new Object[]{project.getId(), project.getSource()});
        store.index(project.getSource(), "Project", project, true);
    }

    @Override
    public Set<HIndexedClass> searchTypes() {
        return store.searchElements(null, "Type");
    }

    @Override
    public HIndexedClass searchType(String fullName) {
        return JeepUtils.first(store.searchElements(null, "Type", new JIndexQuery().whereEq("fullName", fullName)));
    }

    @Override
    public Set<HIndexedClass> searchTypes(JIndexQuery query) {
        return store.searchElements(null, "Type", query);
    }

    private Set<String> searchTypeHierarchy(String className) {
        Set<String> result = new LinkedHashSet<>();
        Stack<String> todo = new Stack<>();
        todo.add(className);
        while (!todo.isEmpty()) {
            String c = todo.pop();
            if (!result.contains(c)) {
                result.add(c);
                HIndexedClass cc = searchType(c);
                if (cc != null) {
                    todo.addAll(Arrays.asList(cc.getSuperTypes()));
                }
            }
        }
        return result;
    }

    /**
     * for(int x, b<t,c>z) //should handle this error
     *
     * @param query
     * @param inherited
     * @return
     */
    @Override
    public Set<HIndexedField> searchFields(JIndexQuery query, boolean inherited) {
        LinkedHashSet<HIndexedField> all = new LinkedHashSet<>();
        for (HIndexedField field : store.<HIndexedField>searchElements(null, "Field", query)) {
            all.add(field);
            if (inherited) {
                Set<String> dt = searchTypeHierarchy(field.getDeclaringType());
                dt.remove(field.getDeclaringType());
                Stack<String> typesToCheck = new Stack<>();
                typesToCheck.addAll(dt);
                while (!typesToCheck.isEmpty()) {
                    String t = typesToCheck.pop();
                    for (HIndexedField f2 : store.<HIndexedField>searchElements(t, "Field", query)) {
                        //TODO fix me
                        if (!f2.getAnnotations().contains("private")) {
                            all.add(f2);
                        }
                    }
                }
            }
        }
        return all;
    }

    @Override
    public Set<HIndexedField> searchFields(String declaringType, String fieldNameOrNull, boolean inherited) {
        JIndexQuery q = new JIndexQuery();
        if (declaringType != null) {
            q.whereEq("declaringType", declaringType);
        }
        if (fieldNameOrNull != null) {
            q.whereEq("name", fieldNameOrNull);
        }
        return searchFields(q, inherited);
    }

    @Override
    public Set<HIndexedMethod> searchMethods(JIndexQuery query, boolean inherited) {
        LinkedHashSet<HIndexedMethod> all = new LinkedHashSet<>();
        for (HIndexedMethod field : store.<HIndexedMethod>searchElements(null, "Method", query)) {
            all.add(field);
            if (inherited) {
                Set<String> dt = searchTypeHierarchy(field.getDeclaringType());
                dt.remove(field.getDeclaringType());
                Stack<String> typesToCheck = new Stack<>();
                typesToCheck.addAll(dt);
                while (!typesToCheck.isEmpty()) {
                    String t = typesToCheck.pop();
                    for (HIndexedMethod f2 : store.<HIndexedMethod>searchElements(t, "Method", query)) {
                        //TODO fix me
                        if (!f2.getAnnotations().contains("private")) {
                            all.add(f2);
                        }
                    }
                }
            }
        }
        return all;
    }

    @Override
    public Set<HIndexedMethod> searchMethods(String declaringType, String methodNameOrNull, boolean inherited) {
        JIndexQuery q = new JIndexQuery();
        if (declaringType != null) {
            q.whereEq("declaringType", declaringType);
        }
        if (methodNameOrNull != null) {
            q.whereEq("name", methodNameOrNull);
        }
        return searchMethods(q, inherited);
    }

    @Override
    public Set<HIndexedConstructor> searchConstructors(JIndexQuery query) {
        return store.searchElements(null, "Constructor", query);
    }

    @Override
    public Set<HIndexedConstructor> searchConstructors(String declaringType) {
        JIndexQuery q = new JIndexQuery();
        if (declaringType != null) {
            q.whereEq("declaringType", declaringType);
        }
        return store.searchElements(null, "Constructor", q);
    }

    @Override
    public Set<HIndexedPackage> searchPackages() {
        return store.searchElements(null, "Package");
    }

    @Override
    public Set<HIndexedPackage> searchPackages(JIndexQuery query) {
        return store.searchElements(null, "Package", query);
    }

    @Override
    public HIndexedPackage searchPackage(String fullName) {
        JIndexQuery q = new JIndexQuery();
        q.whereEq("fullName", fullName);
        return JeepUtils.first(store.searchElements(null, "Package", q));
    }

    @Override
    public Set<HIndexedProject> searchProjects() {
        return store.searchElements(null, "Project");
    }

    public HIndexedProject searchProject(String projectRoot) {
        return JeepUtils.first(
                store.searchElements(null, "Project",
                        new JIndexQuery().whereEq("projectRoot", projectRoot)
                )
        );
    }

    public void indexPackage(HIndexedPackage p) {
        String source = p.getSource();
        store.index(source, "Package", p, false);
        for (String o : p.getParents()) {
            store.index(source, "Package", new HIndexedPackage(source, o), false);
        }
    }

    @Override
    public void indexType(HIndexedClass p) {
        LOG.log(Level.FINE, "index type {0}", p.getFullName());
        indexType0(p);
    }

    public void indexType0(HIndexedClass p) {
        store.index(p.getSource(), "Type", p, true);
        indexPackage(new HIndexedPackage(p.getSource(), p.getPackageName()));
    }

    @Override
    public void indexField(HIndexedField p) {
        LOG.log(Level.FINE, "index field {0} {1}.{2}", new Object[]{p.getType(), p.getDeclaringType(), p.getName()});
        indexField0(p);
    }

    public final void indexField0(HIndexedField p) {
        store.index(p.getSource(), "Field", p, true);
    }

    @Override
    public void indexMethod(HIndexedMethod p) {
        LOG.log(Level.FINE, "index method {0} {1}{2}", new Object[]{
            p.getReturnType().isEmpty() ? "?" : p.getReturnType(), p.getDeclaringType().isEmpty() ? "" : (p.getDeclaringType() + "."), p.getSignature()
        });
        indexMethod0(p);
    }

    public final void indexMethod0(HIndexedMethod p) {
        store.index(p.getSource(), "Method", p, true);
    }

    @Override
    public void indexConstructor(HIndexedConstructor p) {
        LOG.log(Level.FINE, "index constructor {0}.{1}", new Object[]{p.getDeclaringType(), p.getSignature()});
        indexConstructor0(p);
    }

    public final void indexConstructor0(HIndexedConstructor p) {
        store.index(p.getSource(), "Constructor", p, true);
    }

    public boolean isValidClassFile(String name) {
        int i = name.lastIndexOf('/');
        if (i < 0) {
            i = 0;
        } else {
            i = i + 1;
        }
        for (int j = i; j < name.length(); j++) {
            char c = name.charAt(j);
            switch (c) {
                case '-': {
                    return false;
                }
                case '$': {
                    if (j + 1 < name.length()) {
                        char c2 = name.charAt(j + 1);
                        if (c2 >= '0' && c2 <= '9') {
                            return false;
                        }
                    }
                    break;
                }
                default: {
                    if (!Character.isJavaIdentifierPart(c) && c != '.') {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
