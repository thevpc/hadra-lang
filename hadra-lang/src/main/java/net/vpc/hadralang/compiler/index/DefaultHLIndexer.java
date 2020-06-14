package net.vpc.hadralang.compiler.index;

import net.vpc.common.jeep.*;
import net.vpc.common.jeep.core.JIndexQuery;
import net.vpc.common.jeep.core.index.DefaultJIndexDocument;
import net.vpc.common.jeep.util.Chronometer;
import net.vpc.common.jeep.util.JeepUtils;
import net.vpc.hadralang.compiler.parser.ast.HNBlock;
import net.vpc.hadralang.compiler.parser.ast.HNDeclareType;
import net.vpc.hadralang.stdlib.IntRef;
import net.vpc.hadralang.stdlib.Ref;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
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
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DefaultHLIndexer implements HLIndexer {
    private static final Logger LOG = Logger.getLogger(DefaultHLIndexer.class.getName());
    JIndexStore store;
    private static Pattern BAD_CLASS_NAME = Pattern.compile(".*[$][0-9].*");

    public DefaultHLIndexer() {
        this(null);
    }

    public DefaultHLIndexer(JIndexStore store) {
        if (store == null) {
            store = new JIndexStoreMemory();
        }
        this.store = store;
    }

    @Override
    public int indexSDK(String sdkHome, boolean force) {
        if (sdkHome == null) {
            sdkHome = System.getProperty("java.home");
        }
        if (sdkHome != null) {
            File file = new File(sdkHome, "lib" + File.separator + "rt.jar");
            if (file.exists()) {
                return indexLibrary(file, false);
            }
        }
        return 0;
    }

    @Override
    public int indexSource(JCompilationUnit compilationUnit) {
        Chronometer chrono = Chronometer.start();
        String uuid = compilationUnit.getSource().name();
        store.removeIndex(uuid);
        HNBlock body = (HNBlock) compilationUnit.getAst();
        IntRef counter = Ref.of(0);
        if (body != null) {
            for (HNDeclareType item : body.findDeclaredTypes()) {
                indexDeclareType(uuid, item,counter);
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
        LOG.log(Level.INFO, "Index Source " + uuid + " ("+counter.get()+" classes )"+" in " + chrono.stop().getDuration());
        return counter.get();
    }

    @Override
    public int indexLibrary(URL url, boolean force) {
        if ("file".equals(url.getProtocol())) {
            File f;
            try {
                f = new File(url.toURI());
            } catch (URISyntaxException e) {
                f = new File(url.getPath());
            }
            return indexLibrary(f, force);
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
    public int indexLibrary(File file, boolean force) {
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
        LOG.log(Level.INFO, "Index Library " + uuid + " started...");
        Chronometer chrono = Chronometer.start();
        if (file.isDirectory()) {
            try {
                Files.walk(file.toPath()).forEach(x -> readFile(x.toFile(),file,counter));
            } catch (IOException e) {
                LOG.log(Level.INFO, "Index Library " + uuid + " failed with : " + e.toString());
                return counter.get();
            }
        } else {
            try (JarFile jar = new JarFile(uuid)) {
                Stream<JarEntry> str = jar.stream();
                str.forEach(z -> readJar(jar, z,counter));
            } catch (IOException e) {
                LOG.log(Level.INFO, "Index Library " + uuid + " failed with : " + e.toString());
                return counter.get();
            }
        }
        store.index(uuid, elementType,
                new DefaultJIndexDocument(uuid)
                        .add("lastModified", String.valueOf(lastModified), true), true
        );
        LOG.log(Level.INFO, "Index Library " + uuid + " ("+counter.get()+" classes )"+ " finished in " + chrono.stop().getDuration());
        return counter.get();
    }

    private void readFile(File entry,File root,IntRef typesCounter) {
        String name = entry.getAbsolutePath();
        try{
            name = entry.getCanonicalPath();
        }catch (Exception ex){
            //
        }
        String rname = root.getAbsolutePath();
        try{
            rname = root.getCanonicalPath();
        }catch (Exception ex){
            //
        }
        if (name.endsWith(".class") && !BAD_CLASS_NAME.matcher(name).matches()) {
            try (InputStream jis = new FileInputStream(entry)) {
                ClassReader cr = new ClassReader(jis);
                cr.accept(new JavaClassVisitor(this, rname), ClassReader.SKIP_FRAMES);
                typesCounter.inc();
            } catch (IOException e) {
                LOG.log(Level.INFO, "Read Class file failed with : " + e.toString());
            }
        }
    }

    private void readJar(JarFile jar, JarEntry entry,IntRef typesCounter) {
        String name = entry.getName();
        if (name.endsWith(".class") && !BAD_CLASS_NAME.matcher(name).matches()) {
            try (InputStream jis = jar.getInputStream(entry)) {
                ClassReader cr = new ClassReader(jis);
                cr.accept(new JavaClassVisitor(this, jar.getName()), ClassReader.SKIP_FRAMES);
                typesCounter.inc();
            } catch (IOException e) {
                LOG.log(Level.INFO, "Read Class file failed with : " + e.toString());
            }
        }
    }

    public void indexDeclareType(String uuid, HNDeclareType item) {
        indexDeclareType(uuid,item,new IntRef(0));
    }

    public void indexDeclareType(String uuid, HNDeclareType item,IntRef typesCounter) {
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
        indexType(new HLIndexedClass(item,uuid));
        JNode body = item.getBody();
        if (body instanceof HNBlock) {
            HNBlock bloc = (HNBlock) body;
            for (HNDeclareType item2 : bloc.findDeclaredTypes()) {
                indexDeclareType(uuid, item2,typesCounter);
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
    public void indexProject(HLIndexedProject project){
        LOG.log(Level.INFO, "index project " + project.getId()+" @ "+project.getSource());
        store.index(project.getSource(), "Project", project, true);
    }

    @Override
    public Set<HLIndexedClass> searchTypes() {
        return store.searchElements(null, "Type");
    }

    @Override
    public HLIndexedClass searchType(String fullName) {
        return JeepUtils.first(store.searchElements(null, "Type", new JIndexQuery().whereEq("fullName", fullName)));
    }

    @Override
    public Set<HLIndexedClass> searchTypes(JIndexQuery query) {
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
                HLIndexedClass cc = searchType(c);
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
    public Set<HLIndexedField> searchFields(JIndexQuery query, boolean inherited) {
        LinkedHashSet<HLIndexedField> all = new LinkedHashSet<>();
        for (HLIndexedField field : store.<HLIndexedField>searchElements(null, "Field", query)) {
            all.add(field);
            if (inherited) {
                Set<String> dt = searchTypeHierarchy(field.getDeclaringType());
                dt.remove(field.getDeclaringType());
                Stack<String> typesToCheck = new Stack<>();
                typesToCheck.addAll(dt);
                while (!typesToCheck.isEmpty()) {
                    String t = typesToCheck.pop();
                    for (HLIndexedField f2 : store.<HLIndexedField>searchElements(t, "Field", query)) {
                        if (!Modifier.isPrivate(f2.getModifiers())) {
                            all.add(f2);
                        }
                    }
                }
            }
        }
        return all;
    }

    @Override
    public Set<HLIndexedField> searchFields(String declaringType, String fieldNameOrNull, boolean inherited) {
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
    public Set<HLIndexedMethod> searchMethods(JIndexQuery query, boolean inherited) {
        LinkedHashSet<HLIndexedMethod> all = new LinkedHashSet<>();
        for (HLIndexedMethod field : store.<HLIndexedMethod>searchElements(null, "Method", query)) {
            all.add(field);
            if (inherited) {
                Set<String> dt = searchTypeHierarchy(field.getDeclaringType());
                dt.remove(field.getDeclaringType());
                Stack<String> typesToCheck = new Stack<>();
                typesToCheck.addAll(dt);
                while (!typesToCheck.isEmpty()) {
                    String t = typesToCheck.pop();
                    for (HLIndexedMethod f2 : store.<HLIndexedMethod>searchElements(t, "Method", query)) {
                        if (!Modifier.isPrivate(f2.getModifiers())) {
                            all.add(f2);
                        }
                    }
                }
            }
        }
        return all;
    }


    @Override
    public Set<HLIndexedMethod> searchMethods(String declaringType, String methodNameOrNull, boolean inherited) {
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
    public Set<HLIndexedConstructor> searchConstructors(JIndexQuery query) {
        return store.searchElements(null, "Constructor", query);
    }

    @Override
    public Set<HLIndexedConstructor> searchConstructors(String declaringType) {
        JIndexQuery q = new JIndexQuery();
        if (declaringType != null) {
            q.whereEq("declaringType", declaringType);
        }
        return store.searchElements(null, "Constructor", q);
    }

    @Override
    public Set<HLIndexedPackage> searchPackages() {
        return store.searchElements(null, "Package");
    }

    @Override
    public Set<HLIndexedPackage> searchPackages(JIndexQuery query) {
        return store.searchElements(null, "Package", query);
    }

    @Override
    public HLIndexedPackage searchPackage(String fullName) {
        JIndexQuery q = new JIndexQuery();
        q.whereEq("fullName", fullName);
        return JeepUtils.first(store.searchElements(null, "Package", q));
    }


    @Override
    public Set<HLIndexedProject> searchProjects() {
        return store.searchElements(null, "Project");
    }

    public HLIndexedProject searchProject(String projectRoot) {
        return JeepUtils.first(
                store.searchElements(null, "Project",
                        new JIndexQuery().whereEq("projectRoot",projectRoot)
                        )
        );
    }

    public void indexPackage(HLIndexedPackage p) {
        String source = p.getSource();
        store.index(source, "Package", p, false);
        for (String o : p.getParents()) {
            store.index(source, "Package", new HLIndexedPackage(source, o), false);
        }
    }

    @Override
    public void indexType(HLIndexedClass p) {
        LOG.log(Level.INFO, "index type " + p.getFullName());
        indexType0(p);
    }

    public void indexType0(HLIndexedClass p) {
        store.index(p.getSource(), "Type", p, true);
        indexPackage(new HLIndexedPackage(p.getSource(), p.getPackageName()));
    }

    @Override
    public void indexField(HLIndexedField p) {
        LOG.log(Level.INFO, "index field " + p.getType() + " " + p.getDeclaringType() + "." + p.getName());
        indexField0(p);
    }

    public final void indexField0(HLIndexedField p) {
        store.index(p.getSource(), "Field", p, true);
    }

    @Override
    public void indexMethod(HLIndexedMethod p) {
        LOG.log(Level.INFO, "index method " + (p.getReturnType().isEmpty() ? "?" : p.getReturnType()) + " "
                + (p.getDeclaringType().isEmpty() ? "" : (p.getDeclaringType() + ".")) + p.getSignature()
        );
        indexMethod0(p);
    }

    public final void indexMethod0(HLIndexedMethod p) {
        store.index(p.getSource(), "Method", p, true);
    }

    @Override
    public void indexConstructor(HLIndexedConstructor p) {
        LOG.log(Level.INFO, "index constructor " + p.getDeclaringType() + "." + p.getSignature());
        indexConstructor0(p);
    }
    public final void indexConstructor0(HLIndexedConstructor p) {
        store.index(p.getSource(), "Constructor", p, true);
    }

}
