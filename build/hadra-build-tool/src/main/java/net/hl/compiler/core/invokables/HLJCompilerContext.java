package net.hl.compiler.core.invokables;

import net.hl.compiler.index.*;
import net.thevpc.jeep.*;
import net.thevpc.jeep.core.AbstractJConverter;
import net.thevpc.jeep.core.DefaultJTypedValue;
import net.thevpc.jeep.core.JIndexQuery;
import net.thevpc.jeep.core.types.DefaultTypeName;
import net.thevpc.jeep.core.types.JTypeNameBounded;
import net.thevpc.jeep.impl.JTypesSPI;
import net.thevpc.jeep.impl.compiler.DefaultJImportInfo;
import net.thevpc.jeep.impl.compiler.JCompilerContextImpl;
import net.thevpc.jeep.impl.functions.*;
import net.thevpc.jeep.impl.types.*;
import net.thevpc.jeep.source.JTextSourceFactory;
import net.thevpc.jeep.util.*;
import net.hl.compiler.core.HFunctionType;
import net.hl.compiler.core.HProject;
import net.hl.compiler.core.HMissingLinkageException;
import net.hl.compiler.core.elements.*;
import net.hl.compiler.ast.*;
import net.hl.compiler.utils.*;
import net.hl.lang.Tuple;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HLJCompilerContext extends JCompilerContextImpl {

    private static final Logger LOG = Logger.getLogger(HLJCompilerContext.class.getName());
    private HProject project;
    //    private Function<JType, JConverter1[]> contextConverterSupplier = new Function<JType, JConverter1[]>() {
//        @Override
//        public JConverter1[] apply(JType from) {
//            List<JConverter1> all = new ArrayList<>();
//            Set<String> classNamesToImport = resolveImportStatics(imports());
//            for (String cls : classNamesToImport) {
//                JType jType = null;
//                try {
//                    jType = context().types().forName(cls);
//                } catch (Exception ex) {
//                    //some how this not accessible... ignore it
//                }
//                if (jType != null) {
//                    all.addAll(
//                            Arrays.stream(jType.declaredMethods(true))
//                                    .map(x -> {
//                                        if (x.isStatic() && x.isPublic()
//                                                && x.signature().argsCount() == 1 && !x.signature().isVarArgs()
//                                                && !x.returnType().name().equals("void")
//                                                && x.signature().argType(0).isAssignableFrom(from)
//                                                && x.name().equals(getStaticConstructorName(x.returnType()))) {
//                                            return new StaticConstructorConverter(x);
//                                        }
//                                        return null;
//                                    }).filter(Objects::nonNull)
//                                    .collect(Collectors.toList()));
//                }
//            }
//            return all.toArray(new JConverter1[0]);
//        }
//    };
    private Set<String> _resolveImportStatics = null;

    public HLJCompilerContext(HProject project) {
        this(0, 0, new JNodePath(), new JImportInfo[0], project.languageContext(), null, project.log(), project, null, null);
    }

    public HLJCompilerContext(HProject project, JCompilationUnit compilationUnit) {
        this(0, 0, new JNodePath(compilationUnit.getAst()), new JImportInfo[0], project.languageContext(), null, project.log(), project, compilationUnit, null);
    }

    public HLJCompilerContext(int iteration, int pass, JNodePath path, JImportInfo[] imports, JContext context, String packageName, JCompilerLog log, HProject project, JCompilationUnit compilationUnit, HLJCompilerContext parent) {
        super(iteration, pass, path, imports, context, packageName, log == null ? context.log() : log, compilationUnit, parent);
        this.project = project;
    }

    public static boolean isValidTypeName(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        char[] charArray = s.toCharArray();
        for (int i = 1; i < charArray.length; i++) {
            char c = charArray[i];
            if (!Character.isJavaIdentifierPart(c)) {
                return false;
            }
        }
        return true;
    }

    public HIndexer indexer() {
        return project.indexer();
    }

    public HProject project() {
        return project;
    }

    public JTypes types() {
        return getContext().types();
    }

    private Set<String> resolveImportStatics() {
        if (_resolveImportStatics == null) {
            _resolveImportStatics = resolveImportStatics(buildValidImports().toArray(new JImportInfo[0]));
        }
        return _resolveImportStatics;
    }

    private Set<String> resolveImportStatics(JImportInfo[] imports) {
        LinkedHashSet<String> ii = new LinkedHashSet<>();
        if (imports != null) {
            for (JImportInfo a : expandImports(imports)) {
                String _anImport = a.importValue();
                if (_anImport.endsWith(".*")) {
                    String cls = _anImport.substring(0, _anImport.length() - 2);
                    for (String fullName : project.indexer().searchTypes(new JIndexQuery().whereEq("fullName", cls))
                            .stream().map(x -> x.getFullName()).collect(Collectors.toList())) {
                        ii.add(fullName);
                        break;
                    }
                }else if (_anImport.endsWith(".**")) {
                    String cls = _anImport.substring(0, _anImport.length() - 3);
                    for (String fullName : project.indexer().searchTypes(new JIndexQuery().whereEq("fullName", cls))
                            .stream().map(x -> x.getFullName()).collect(Collectors.toList())) {
                        ii.add(fullName);
                        break;
                    }
                }
            }
        }
        return ii;
    }

    //    private Set<String> expandImports(String[] _anImport, JToken location) {
//        Map<String, Set<String>> cache = new HashMap<>();
//        LinkedHashSet<String> all = new LinkedHashSet<>();
//        for (String s : _anImport) {
//            all.addAll(expandImports(s, cache, location));
//        }
//        return all;
//    }
    private Set<JImportInfo> expandImports(JImportInfo[] importInfos) {
        Map<String, Set<JImportInfo>> cache = new HashMap<>();
        LinkedHashSet<JImportInfo> all = new LinkedHashSet<>();
        for (JImportInfo anImport : importInfos) {
            if (anImport != null) {
                all.addAll(expandImports(anImport, cache));
            }
        }
        return all;
    }

    private Set<JImportInfo> expandImports(JImportInfo _anImport, Map<String, Set<JImportInfo>> cache) {
        String _anImportValue = _anImport.importValue();
        final JToken location = _anImport.token();
        if (JStringUtils.isBlank(_anImportValue)) {
            return Collections.emptySet();
        }
        Set<JImportInfo> s = cache.get(_anImportValue);
        if (s != null) {
            if (s instanceof _InvalidSet) {
                getLog().jerror("X045", null, location, "recursive imports/exports :" + _anImport);
            }
            return s;
        }
        cache.put(_anImportValue, _InvalidSet.INSTANCE);
        if (_anImportValue.endsWith(".**")) {
            String ns = _anImportValue.substring(0, _anImportValue.length() - 3);
            LinkedHashSet<JImportInfo> all = new LinkedHashSet<>();
            for (HIndexedClass cls : project.indexer().searchTypes(new JIndexQuery().whereEq("fullName", ns))) {
                all.add(new DefaultJImportInfo(cls.getFullName(), _anImport.token()));
                all.add(new DefaultJImportInfo(cls.getFullName() + ".*", _anImport.token()));
                for (String export : cls.getExports()) {
                    all.addAll(expandImports(new DefaultJImportInfo(export, location), cache));
                }
                cache.put(_anImportValue, all);
                return all;
            }
            //if not this is perhaps a packageName.**
        }
        Set<JImportInfo> ret = Collections.singleton(_anImport);
        cache.put(_anImportValue, ret);
        return ret;
    }

    private Map<String, String> resolveTypeImports(JImportInfo[] anImports, Predicate<String> predicate) {
        Map<String, String> m = new HashMap<>();
        for (JImportInfo a : expandImports(anImports)) {
            String _anImport = a.importValue();
            if (_anImport.endsWith(".*")) {
                String ns = _anImport.substring(0, _anImport.length() - 2);
                Stream<HIndexedClass> s1 = project.indexer().searchTypes(new JIndexQuery().whereEq("fullName", ns))
                        .stream();
                if (predicate != null) {
                    s1 = s1.filter(x -> predicate.test(x.getFullName()));
                }
                Map<String, String> found = s1.collect(Collectors.toMap(
                        e -> e.getSimpleName2(),
                        e -> e.getFullName(),
                        (o1, o2) -> {
                            if (!o1.equals(o2)) {
                                if (predicate == null) {
                                    getLog().jerror("S032", null,
                                            getNode().getStartToken(), "ambiguous class to import :"
                                                    + "\n\t" + o1
                                                    + "\n\t" + o2
                                    );
                                }
                            }
                            return o1;
                        }
                ));
                if (!found.isEmpty()) {
                    //this was a class
                    m.putAll(found);
                } else {
                    //check packageName
                    s1 = project.indexer().searchTypes(new JIndexQuery().whereEq("package", ns))
                            .stream();
                    if (predicate != null) {
                        s1 = s1.filter(x -> predicate.test(x.getFullName()));
                    }
                    found = s1.collect(Collectors.toMap(
                            e -> e.getSimpleName2(),
                            e -> e.getFullName(),
                            (o1, o2) -> {
                                if (!o1.equals(o2)) {
                                    if (predicate == null) {
                                        getLog().jerror("S032", null,
                                                getNode().getStartToken(), "ambiguous class to import :"
                                                        + "\n\t" + o1
                                                        + "\n\t" + o2
                                        );
                                    }
                                }
                                return o1;
                            }
                    ));
                    if (!found.isEmpty()) {
                        //this was a package
                        m.putAll(found);
                    } else {
                        if (predicate == null) {
                            getLog().jerror("X045", null, a.token(), "invalid import: " + _anImport);
                        }
                    }
                }
            } else if (_anImport.endsWith(".**")) {
                String ns = _anImport.substring(0, _anImport.length() - 3);
                //this must be a package because type @export expansion is already processed
                //check packages (not package)
                Set<HIndexedClass> ls1 = project.indexer().searchTypes(new JIndexQuery().whereEq("packages", ns));
                Stream<HIndexedClass> s1 = project.indexer().searchTypes(new JIndexQuery().whereDotStart("packages", ns))
                        .stream();
                if (predicate != null) {
                    s1 = s1.filter(x -> predicate.test(x.getFullName()));
                }
                Map<String, String> found = s1
                        .collect(Collectors.toMap(
                                e -> e.getSimpleName2(),
                                e -> e.getFullName(),
                                (o1, o2) -> {
                                    if (!o1.equals(o2)) {
                                        if (predicate == null) {
                                            getLog().jerror("S032", null,
                                                    getNode().getStartToken(), "ambiguous class to import :"
                                                            + "\n\t" + o1
                                                            + "\n\t" + o2
                                            );
                                        }
                                    }
                                    return o1;
                                }
                        ));
                if (!found.isEmpty()) {
                    //this was a package
                    m.putAll(found);
                } else {
                    if (predicate == null) {
                        getLog().jerror("X045", null, anImports[0].token(), "invalid import: " + _anImport);
                    }
                }
            } else {
                //this should be a valid class; import static is not supported
                Stream<HIndexedClass> s1 = project.indexer().searchTypes(new JIndexQuery().whereEq("fullName", _anImport))
                        .stream();
                if (predicate != null) {
                    s1 = s1.filter(x -> predicate.test(x.getFullName()));
                }
                Map<String, String> found = s1
                        .collect(Collectors.toMap(
                                e -> e.getSimpleName2(),
                                e -> e.getFullName(),
                                (o1, o2) -> {
                                    if (!o1.equals(o2)) {
                                        if (predicate == null) {
                                            getLog().jerror("S032", null,
                                                    getNode().getStartToken(), "ambiguous class to import :"
                                                            + "\n\t" + o1
                                                            + "\n\t" + o2
                                            );
                                        }
                                    }
                                    return o1;
                                }
                        ));
                if (!found.isEmpty()) {
                    //this was a class
                    m.putAll(found);
                } else {
                    if (predicate == null) {
                        getLog().jerror("X045", null, anImports[0].token(), "invalid import: " + _anImport);
                    }
                }
            }
        }
        return m;
    }

    //    private Map<String, String> resolveTypeImports(JImportInfo anImport) {
//        String _anImport = anImport == null ? null : ((DefaultJImportInfo) anImport).importValue();
//        if (_anImport == null || _anImport.isEmpty()) {
//            return Collections.emptyMap();
//        }
//        Set<String> allNamespaces = module().getNamespaces(true, true);
////        Set<String> allTypes = module().getTypeNames();
//        if (_anImport.endsWith(".*")) {
//            String ns = _anImport.substring(0, _anImport.length() - 2);
//            if (allNamespaces.contains(ns)) {
//                return project.indexer().searchClasses(new JIndexQuery().whereEq("package", ns))
//                        .stream()
//                        .collect(Collectors.toMap(
//                                e -> e.getSimpleName2(),
//                                e -> e.getFullName()
//                        ));
//            }
//        } else if (_anImport.endsWith(".**")) {
//            String ns = _anImport.substring(0, _anImport.length() - 3);
//            for (HLIndexedClass fullName : project.indexer().searchClasses(new JIndexQuery().whereEq("fullName", ns))) {
//
//            }
//
//            return project.indexer().searchClasses(new JIndexQuery().whereEq("packages", ns))
//                    .stream()
//                    .collect(Collectors.toMap(
//                            e -> e.getSimpleName2(),
//                            e -> e.getFullName()
//                    ));
//
//            if (allNamespaces.contains(ns)) {
//                //ignore it
//                return allTypes
//                        .stream().filter(x -> {
//                            if (x.startsWith(ns + ".")) {
//                                x = x.substring(ns.length() + 1);
//                                return x.indexOf('.') < 0;
//                            }
//                            return false;
//                        })
//                        .collect(Collectors.toMap(
//                                e -> resolveTypeAccessibleName(e, node()),
//                                e -> e, (o1, o2) -> {
//                                    if (!o1.equals(o2)) {
//                                        log().jerror("S032", "ambiguous class to import :"
//                                                        + "\n\t" + o1
//                                                        + "\n\t" + o2,
//                                                node().startToken()
//                                        );
//                                    }
//                                    return o1;
//                                }
//                        ));
//            } else if (allTypes.contains(ns)) {
//                //
//                Map<String, String> set = new LinkedHashMap<>();
//                JType jType = null;
//                try {
//                    jType = context().types().forName(ns);
//                } catch (Exception ex) {
//                    //some how this not accessible... ignore it
//                }
//                if (jType != null) {
//                    JImportInfo[] a = getExports(jType);
//                    if (a != null) {
//                        for (JImportInfo s : a) {
////                            System.out.println("s="+s);
//                            for (Map.Entry<String, String> e : resolveTypeImports(s).entrySet()) {
//                                if (set.containsKey(e.getKey()) && !set.get(e.getKey()).equals(e.getValue())) {
//                                    log().jerror("S032", "ambiguous class to import :"
//                                                    + "\n\t" + e.getValue()
//                                                    + "\n\t" + set.get(e.getKey()),
//                                            node().startToken()
//                                    );
//                                } else {
//                                    set.put(e.getKey(), e.getValue());
//                                }
//                            }
//                        }
//                    }
//                    return set;
//                }
//            }
//        } else if (allTypes.contains(_anImport)) {
//            Map<String, String> singleton = new HashMap<>();
//            singleton.put(resolveTypeAccessibleName(_anImport, node()), _anImport);
//            return singleton;
//        }
//        return Collections.emptyMap();
//    }
    private String resolveTypeAccessibleName(String type, JNode location) {
        JType jType = null;
        try {
            jType = getContext().types().forName(type);
            StringBuilder sb = new StringBuilder();
            while (jType != null) {
                if (sb.length() > 0) {
                    sb.insert(0, ".");
                }
                sb.insert(0, jType.simpleName());
                jType = jType.getDeclaringType();
            }
            return sb.toString();
        } catch (Exception ex) {
            //some how this not accessible... ignore it
            throw new JShouldNeverHappenException(ex);
        }
    }

    private JImportInfo createJImportInfo(String imp, String src) {
        JToken token = new JToken();
        token.source = JTextSourceFactory.fromString(imp, src);
        return new DefaultJImportInfo(imp, token);
    }

    private JImportInfo[] getExports(JType jType) {
        String[] a = jType.getExports();
        if (a != null) {
            JImportInfo[] ti = new JImportInfo[a.length];
            for (int i = 0; i < ti.length; i++) {
                ti[i] = createJImportInfo(a[i], jType.getName());
            }
            return ti;
        }
        return null;
    }

    //    private Set<String> resolveImportStatics(JImportInfo anImport) {
//        String _anImport = ((DefaultJImportInfo) anImport).importValue();
//        if (_anImport.endsWith(".*")) {
//            String cls = _anImport.substring(0, _anImport.length() - 2);
//            if (module().getTypeNames().contains(cls)) {
//                //imports all methods of this class
//                return Collections.singleton(cls);
//            }
//        } else if (_anImport.endsWith(".**")) {
//            String cls = _anImport.substring(0, _anImport.length() - 3);
//            //this is a class!
//            if (module().getTypeNames().contains(cls)) {
//                LinkedHashSet<String> set = new LinkedHashSet<>();
//                set.add(cls);
//                JType jType = null;
//                try {
//                    jType = context().types().forName(cls);
//                } catch (Exception ex) {
//                    //some how this not accessible... ignore it
//                }
//                if (jType != null) {
//                    JImportInfo[] a = getExports(jType);
//                    if (a != null) {
//                        set.addAll(resolveImportStatics(a));
//                    }
//                    return set;
//                }
//            } else if (module().getNamespaces(true, true).contains(cls)) {
//                //this should be a package... ignore it.
//            } else {
//                JToken s = null;//node().startToken().copy();
////                s.startCharacterNumber=0;
////                s.endCharacterNumber=1;
////                s.startLineNumber=0;
////                s.endLineNumber=0;
////                s.startColumnNumber=0;
////                s.endColumnNumber=0;
////                s.image=anImport;
////                s.sval=anImport;
////                s.ttype=JToken.TT_WORD;
//                log().jerror("S033", "neither a class nor a package to import could be resolved for : " + anImport, anImport.token());
//            }
//        }
//        return Collections.emptySet();
//    }
    private JField[] resolveStaticFieldsByNameFromImports(String name) {
        List<JField> all = new ArrayList<>();
        Set<String> classNamesToImport = resolveImportStatics();
        for (String cls : classNamesToImport) {
            JType jType = null;
            try {
                jType = getContext().types().forName(cls);
            } catch (Exception ex) {
                //somehow this not accessible... ignore it
            }
            if (jType != null) {
                all.addAll(
                        Arrays.stream(jType.getDeclaredFields())
                                .filter(x -> x.isStatic() && isVisible(x))
                                .filter(x -> x.name().equals(name))
                                .collect(Collectors.toList())
                );
            }
        }
//        for (JResolver resolver : context().resolvers().getResolvers()) {
//            JFunction[] jFunctions = resolver.resolveFunctionsByName(name, argsCount, context());
//            if (jFunctions != null) {
//                for (JFunction jFunction : jFunctions) {
//                    if (jFunction != null) {
//                        all.add(HSharedUtils.resolveToMethod(jFunction));
//                    }
//                }
//            }
//        }
        return all.toArray(new JField[0]);
    }

    private JMethod[] resolveStaticMethodsByNameFromImports(String name, int argsCount) {
        List<JMethod> all = new ArrayList<>();
        Set<String> classNamesToImport = resolveImportStatics();
        for (String cls : classNamesToImport) {
            JType jType = null;
            try {
                jType = getContext().types().forName(cls);
            } catch (Exception ex) {
                //some how this not accessible... ignore it
            }
            if (jType != null) {
                all.addAll(
                        Arrays.stream(jType.getDeclaredMethods(
                                        namesWithAliases(name, null), argsCount, true))
                                .filter(x -> x.isStatic() && isVisible(x))
                                .collect(Collectors.toList())
                );
            }
        }
        for (JResolver resolver : getContext().resolvers().getResolvers()) {
            JFunction[] jFunctions = resolver.resolveFunctionsByName(name, argsCount, getContext());
            if (jFunctions != null) {
                for (JFunction jFunction : jFunctions) {
                    if (jFunction != null) {
                        all.add(HSharedUtils.resolveToMethod(jFunction));
                    }
                }
            }
        }
        return all.toArray(new JMethod[0]);
    }

    public void visit(JNodeVisitor visitor) {
        for (JCompilationUnit compilationUnit : project.getCompilationUnits()) {
            JNode root = compilationUnit.getAst();
            root.visit(visitor);
        }
        getMetaPackageType().visit(visitor);
    }

    private String[] namesWithAliases(String name, HFunctionType opType) {
        if (opType == HFunctionType.GET) {
            return new String[]{
                    JeepUtils.propertyToGetter(name, false),
                    JeepUtils.propertyToGetter(name, true)
            };
        }
        if (opType == HFunctionType.SET) {
            return new String[]{
                    JeepUtils.propertyToSetter(name)
            };
        }
        Set<String> all = new HashSet<>();
        all.add(name);
        switch (name) {
            case "+":
            case "plus": {
                all.add(HExtensionNames.PLUS_SHORT);
                all.add(HExtensionNames.PLUS_LONG);
                break;
            }
            case "-":
            case "minus": {
                all.add("-");
                all.add("sub");
                all.add("minus");
                break;
            }
            case "/":
            case "div": {
                all.add("/");
                all.add("div");
                break;
            }
            case "*":
            case "mul": {
                all.add("*");
                all.add("mul");
                break;
            }
            case "^":
            case "pow": {
                all.add("^");
                all.add("pow");
                break;
            }
            case "^^":
            case "xor": {
                all.add("^^");
                all.add("xor");
                break;
            }
            case "&&":
            case "and": {
                all.add("&&");
                all.add("and");
                break;
            }
            case "||":
            case "or": {
                all.add("||");
                all.add("or");
                break;
            }
            case "&":
            case "amp": {
                all.add("&");
                all.add("amp");
                break;
            }
            case "|":
            case "pipe": {
                all.add("|");
                all.add("pipe");
                break;
            }
            case "%":
            case "rem": {
                all.add("%");
                all.add("rem");
                break;
            }
            case "+=":
            case "plusAssign": {
                all.add("+=");
                all.add("plusAssign");
                break;
            }
            case "-=":
            case "minusAssign": {
                all.add("-=");
                all.add("minusAssign");
                break;
            }
            case "*=":
            case "mulAssign": {
                all.add("*=");
                all.add("mulAssign");
                break;
            }
            case "/=":
            case "divAssign": {
                all.add("/=");
                all.add("divAssign");
                break;
            }
            case "^=":
            case "powAssign": {
                all.add("^=");
                all.add("powAssign");
                break;
            }
            case "^^=":
            case "xorAssign": {
                all.add("^^=");
                all.add("xorAssign");
                break;
            }
            case "&=":
            case "ampAssign": {
                all.add("&=");
                all.add("ampAssign");
                break;
            }
            case "&&=":
            case "andAssign": {
                all.add("&&=");
                all.add("andAssign");
                break;
            }
            case "|=":
            case "pipeAssign": {
                all.add("|=");
                all.add("pipeAssign");
                break;
            }
            case "||=":
            case "orAssign": {
                all.add("||=");
                all.add("orAssign");
                break;
            }
            case "~=":
            case "tildeAssign": {
                all.add("~=");
                all.add("tildeAssign");
                break;
            }
            case ":=":
            case "columnAssign": {
                all.add(":=");
                all.add("columnAssign");
                break;
            }
            case "++":
            case "inc": {
                all.add("++");
                all.add("inc");
                break;
            }
            case "~":
            case "tilde": {
                all.add("~");
                all.add("tilde");
                break;
            }
            case "--":
            case "dec": {
                all.add("--");
                all.add("dec");
                break;
            }
            case "postfix_++":
            case "postfix_inc": {
                all.add("postfix_++");
                all.add("postfix_inc");
                break;
            }
            case "postfix_--":
            case "postfix_dec": {
                all.add("--");
                all.add("postfix_dec");
                break;
            }
            case HExtensionNames.NEW_RANGE_II_SHORT:
            case HExtensionNames.NEW_RANGE_II_LONG: {
                all.add(HExtensionNames.NEW_RANGE_II_SHORT);
                all.add(HExtensionNames.NEW_RANGE_II_LONG);
                break;
            }
            case HExtensionNames.NEW_RANGE_EI_SHORT:
            case HExtensionNames.NEW_RANGE_EI_LONG: {
                all.add(HExtensionNames.NEW_RANGE_EI_SHORT);
                all.add(HExtensionNames.NEW_RANGE_EI_LONG);
                break;
            }
            case HExtensionNames.NEW_RANGE_IE_SHORT:
            case HExtensionNames.NEW_RANGE_IE_LONG: {
                all.add(HExtensionNames.NEW_RANGE_IE_SHORT);
                all.add(HExtensionNames.NEW_RANGE_IE_LONG);
                break;
            }
            case HExtensionNames.NEW_RANGE_EE_SHORT:
            case HExtensionNames.NEW_RANGE_EE_LONG: {
                all.add(HExtensionNames.NEW_RANGE_EE_SHORT);
                all.add(HExtensionNames.NEW_RANGE_EE_LONG);
                break;
            }
            default: {
                StringBuilder sb = new StringBuilder();
                boolean withOps = false;
                for (char c : name.toCharArray()) {
                    switch (c) {
                        case '&': {
                            withOps = true;
                            sb.append("amp");
                            break;
                        }
                        case '|': {
                            withOps = true;
                            sb.append("pipe");
                            break;
                        }
                        case '+': {
                            withOps = true;
                            sb.append("plus");
                            break;
                        }
                        case '-': {
                            withOps = true;
                            sb.append("minus");
                            break;
                        }
                        case '*': {
                            withOps = true;
                            sb.append("mul");
                            break;
                        }
                        case '/': {
                            withOps = true;
                            sb.append("div");
                            break;
                        }
                        case '^': {
                            withOps = true;
                            sb.append("pow");
                            break;
                        }
                        case '~': {
                            withOps = true;
                            sb.append("tilde");
                            break;
                        }
                        case '!': {
                            withOps = true;
                            sb.append("not");
                            break;
                        }
                        case '<': {
                            withOps = true;
                            sb.append("lt");
                            break;
                        }
                        case '=': {
                            withOps = true;
                            sb.append("eq");
                            break;
                        }
                        case '>': {
                            withOps = true;
                            sb.append("gt");
                            break;
                        }
                        case '$': {
                            withOps = true;
                            sb.append("$");
                            break;
                        }
                        case '#': {
                            withOps = true;
                            sb.append("sharp");
                            break;
                        }
                        case '%': {
                            withOps = true;
                            sb.append("mod");
                            break;
                        }
                        case ':': {
                            withOps = true;
                            sb.append("column");
                            break;
                        }
                        case '?': {
                            withOps = true;
                            sb.append("excl");
                            break;
                        }
                        default: {
                            if (isOpChar(c)) {
                                throw new JFixMeLaterException("op name not handled " + c);
                            } else {
                                sb.append(c);
                            }
                        }
                    }
                }
                if (withOps) {
                    return new String[]{
                            name,
                            sb.toString()
                    };
                } else {
                    return new String[]{name};
                }
            }
        }
        if (opType == HFunctionType.POSTFIX_UNARY) {
            return all.stream().map(x -> ("postfix_" + x)).toArray(String[]::new);
        }
        return all.toArray(new String[0]);
    }

    public boolean isOpChar(char c) {
        switch (c) {
            case '&':
            case '|':
            case '+':
            case '-':
            case '*':
            case '/':
            case '^':
            case '~':
            case '!':
            case '<':
            case '=':
            case '>':
            case '$':
            case '#':
            case '%':
            case ':':
            case '?':
                return true;
            default: {
                return false;
            }
        }
    }

    public JInvokable findStaticMatch(JOnError jOnError, JType staticType, String name, HFunctionType functionType, JTypePattern[] args, JToken location, FindMatchFailInfo failInfo) {
        int argsCount = args == null ? 0 : args.length;
        String[] names = namesWithAliases(name, functionType);
        if (failInfo == null) {
            failInfo = new FindMatchFailInfo(null);
        }
        if (failInfo.getSignatureString() == null) {
            failInfo.setSignatureString(staticType.getName() + "." + names[0]
                    + (args == null ? "" : JTypePattern.signatureString(args))
            );
        }
        if (failInfo.getConversions() == null) {
            failInfo.setConversions(new ConversionTrace(this));
        }
        switch (functionType) {
            case GET: {
                if (argsCount != 0) {
                    getLog().jerror("S000", null, location, "wrong getter arguments count " + argsCount + "!=" + 0);
                }
                break;
            }
            case SET: {
                if (argsCount != 1) {
                    getLog().jerror("S000", null, location, "wrong setter arguments count " + argsCount + "!=" + 1);
                }
                break;
            }
        }
        JMethod[] possibleMethods = Arrays.stream(staticType.getDeclaredMethods(names, argsCount, true))
                .filter(x -> isVisible(x) && x.isStatic()).toArray(JMethod[]::new);
        JInvokableCost[] jInvokableCosts = filterAcceptable(new LinkedHashSet<>(Arrays.asList(possibleMethods)), args, location, true, new ArrayList<>(), failInfo);
        if (jInvokableCosts.length == 0) {
            failInfo.fail(jOnError, getLog(), location);
            return null;
        }
        return jInvokableCosts[0].getInvokable();
    }

    public JInvokable lookupFunctionMatch(JOnError jOnError, String name, HFunctionType opType, JTypePattern[] args, JToken location) {
        return lookupFunctionMatch(jOnError, name, opType, args, location, null);
    }

    //    public JField findFieldMatch(JOnError jOnError, String name, boolean staticRequired, JType declaringType, JToken location, FindMatchFailInfo failInfo) {
//        Stack<JType> stack = new Stack<>();
//        stack.push(declaringType);
//        boolean foundButInaccessible = false;
//        while (!stack.isEmpty()) {
//            JType tt = stack.pop();
//            JField f = tt.declaredFieldOrNull(name);
//            if (f != null) {
//                if (!staticRequired || f.isStatic()) {
//                    return f;
//                }
//            }
//            for (JType parent : tt.parents()) {
//                stack.push(parent);
//            }
//        }
//        switch (jOnError) {
//            case NULL:
//                return null;
//            case TRACE: {
//                if (foundButInaccessible) {
//                    log().jerror("S044", "field not accessible " + declaringType.name() + "." + name,
////                            "To use "
////                                    + baseTypeNameSafe + "[" + JTypePattern.signatureStringNoPars(oldTypes) + "] operator, you should implement either \n"
////                                    + "\tinstance method: " + baseType.toString() + "." + HLExtensionNames.BRACKET_GET_LONG + "" + JTypePattern.signatureString(oldTypes) + " \n"
////                                    + "\tor\n"
////                                    + "\tstatic method  : " + HLExtensionNames.BRACKET_GET_LONG + "" + JTypePattern.signatureString(args) + " \n",
//                            location);
//                } else {
//                    log().jerror("S044", "field not found " + declaringType.name() + "." + name,
////                            "To use "
////                                    + baseTypeNameSafe + "[" + JTypePattern.signatureStringNoPars(oldTypes) + "] operator, you should implement either \n"
////                                    + "\tinstance method: " + baseType.toString() + "." + HLExtensionNames.BRACKET_GET_LONG + "" + JTypePattern.signatureString(oldTypes) + " \n"
////                                    + "\tor\n"
////                                    + "\tstatic method  : " + HLExtensionNames.BRACKET_GET_LONG + "" + JTypePattern.signatureString(args) + " \n",
//                            location);
//                }
//                break;
//            }
//            case EXCEPTION: {
//                if (foundButInaccessible) {
//                    throw new JParseException("Field not accessible " + declaringType.name() + "." + name);
//                } else {
//                    throw new JParseException("Field not found " + declaringType.name() + "." + name);
//                }
//            }
//        }
//        return null;
//
//    }
    public JInvokable findInstanceMatch(JOnError jOnError, String name, HFunctionType opType, JTypePattern base, JTypePattern[] args, JToken location, FindMatchFailInfo failInfo) {
        JTypePattern[] args2 = JeepUtils.arrayAppend(JTypePattern.class, base, args == null ? new JTypePattern[0] : args);
        return lookupFunctionMatch(
                jOnError, name, opType, args2, location, failInfo
        );
    }

    public JInvokable lookupFunctionMatch(JOnError jOnError, String name, HFunctionType opType, JTypePattern[] args, JToken location, FindMatchFailInfo failInfo) {
        int argsCount = args == null ? 0 : args.length;
        if (failInfo == null) {
            failInfo = new FindMatchFailInfo(null);
        }
        if (failInfo.getSignatureString() == null) {
            failInfo.setSignatureString(name
                    + (args == null ? "" : JTypePattern.signatureString(args))
            );
        }
        if (failInfo.getConversions() == null) {
            failInfo.setConversions(new ConversionTrace(this));
        }
//        if (args == null) {
//            args = new JTypePattern[0];
//        }
        boolean binaryOp = argsCount == 2;
        switch (name) {
            case "==": {
                if (binaryOp) {
                    if ("null".equals(args[0].getType().getName()) || "null".equals(args[1].getType().getName())) {
                        return new PrimitiveEqualsInvokable(getContext().types());
                    }
                    if (args[0].getType().isPrimitive() && args[1].getType().isPrimitive()) {
                        return new PrimitiveEqualsInvokable(getContext().types());
                    }
                    String[] nameAlternatives = new String[]{"==", "eq"};
                    JInvokable m = findFunctionMatchOrNull0(nameAlternatives, args, location, failInfo);
                    if (failInfo.isError() || m != null) {
                        return m;
                    }
                    return new SafeEqualsInvokable(getContext().types());
//                    m = findMatchOrNull0(name, args, new String[]{"compareTo"}, location, error);
//                    if (error[0] || m != null) {
//                        return new CompareToEqualsInvokable(m, context());
//                    }
//                    m = findMatchOrNull0(name, args, nameAlternatives, location, error);
//                    if (m != null) {
//                        return m;
//                    }
                }
                break;
            }
            case "===": {
                if (binaryOp) {
                    if ("null".equals(args[0].getType().getName()) || "null".equals(args[1].getType().getName())) {
                        return new PrimitiveEqualsInvokable(getContext().types());
                    }
                    if (args[0].getType().isPrimitive() && args[1].getType().isPrimitive()) {
                        return new PrimitiveEqualsInvokable(getContext().types());
                    }
                    return new StrictEqualsInvokable(getContext().types());
                }
                break;
            }
            case "!=": {
                if (binaryOp) {
                    if ("null".equals(args[0].getType().getName()) || "null".equals(args[1].getType().getName())) {
                        return new PrimitiveNotEqualsInvokable(getContext().types());
                    }
                    if (args[0].getType().isPrimitive() && args[1].getType().isPrimitive()) {
                        return new PrimitiveNotEqualsInvokable(getContext().types());
                    }
                    String[] nameAlternatives = new String[]{"!=", "neq"};
                    JInvokable m = findFunctionMatchOrNull0(nameAlternatives, args, location, failInfo);
                    if (failInfo.isError() || m != null) {
                        return m;
                    }
                    return new SafeNotEqualsInvokable(getContext().types());

//                    nameAlternatives = new String[]{"==", "eq"};
//
//                    m = findMatchOrNull0(name, args, nameAlternatives, location, error);
//                    if (error[0] || m != null) {
//                        return new EqualsToNotEqualsInvokable(m, context());
//                    }
//                    m = findMatchOrNull0(name, args, new String[]{"compareTo"}, location, error);
//                    if (error[0] || m != null) {
//                        return new CompareToNotEqualsInvokable(m, context());
//                    }
//                    m = findMatchOrNull0(name, args, nameAlternatives, location, error);
//                    if (m != null) {
//                        return m;
//                    }
                }
                break;
            }
            case "!==": {
                if (binaryOp) {
                    if ("null".equals(args[0].getType().getName()) || "null".equals(args[1].getType().getName())) {
                        return new PrimitiveNotEqualsInvokable(getContext().types());
                    }
                    if (args[0].getType().isPrimitive() && args[1].getType().isPrimitive()) {
                        return new PrimitiveNotEqualsInvokable(getContext().types());
                    }
                    String[] nameAlternatives = new String[]{"!=", "neq"};
                    JInvokable m = findFunctionMatchOrNull0(nameAlternatives, args, location, failInfo);
                    if (failInfo.isError() || m != null) {
                        return m;
                    }
                    return new StrictNotEqualsInvokable(getContext().types());
                }
                break;
            }
            case "<=": {
                if (binaryOp) {
                    if (args[0].getType().isPrimitive() && args[1].getType().isPrimitive()) {
                        return new PrimitiveCompareInvokable(getContext().types(), name);
                    }
                    String[] nameAlternatives = new String[]{"<=", "lte"};
                    JInvokable m = findFunctionMatchOrNull0(nameAlternatives, args, location, failInfo);
                    if (failInfo.isError() || m != null) {
                        return m;
                    }
                    m = findFunctionMatchOrNull0(new String[]{"compareTo"}, args, location, failInfo);
                    if (failInfo.isError() || m != null) {
                        return new CompareToLessEqualsThanInvokable(m, getContext());
                    }
                    m = findFunctionMatchOrNull0(new String[]{"compare"}, args, location, failInfo);
                    if (failInfo.isError() || m != null) {
                        return new CompareToLessEqualsThanInvokable(m, getContext());
                    }
                    m = findFunctionMatchOrNull0(nameAlternatives, args, location, failInfo);
                    if (m != null) {
                        return m;
                    }
                }
                break;
            }
            case ">=": {
                if (binaryOp) {
                    if (args[0].getType().isPrimitive() && args[1].getType().isPrimitive()) {
                        return new PrimitiveCompareInvokable(getContext().types(), name);
                    }
                    String[] nameAlternatives = new String[]{">=", "gte"};
                    JInvokable m = findFunctionMatchOrNull0(nameAlternatives, args, location, failInfo);
                    if (failInfo.isError() || m != null) {
                        return m;
                    }
                    m = findFunctionMatchOrNull0(new String[]{"compareTo"}, args, location, failInfo);
                    if (failInfo.isError() || m != null) {
                        return new CompareToGreaterEqualsThanInvokable(m, getContext());
                    }
                    m = findFunctionMatchOrNull0(new String[]{"compare"}, args, location, failInfo);
                    if (failInfo.isError() || m != null) {
                        return new CompareToGreaterEqualsThanInvokable(m, getContext());
                    }
                    m = findFunctionMatchOrNull0(nameAlternatives, args, location, failInfo);
                    if (m != null) {
                        return m;
                    }
                }
                break;
            }
            case "<": {
                if (binaryOp) {
                    if (args[0].getType().isPrimitive() && args[1].getType().isPrimitive()) {
                        return new PrimitiveCompareInvokable(getContext().types(), name);
                    }
                    String[] nameAlternatives = new String[]{"<", "lt"};
                    JInvokable m = findFunctionMatchOrNull0(nameAlternatives, args, location, failInfo);
                    if (failInfo.isError() || m != null) {
                        return m;
                    }
                    m = findFunctionMatchOrNull0(new String[]{"compareTo"}, args, location, failInfo);
                    if (failInfo.isError() || m != null) {
                        return new CompareToLessThanInvokable(m, getContext());
                    }
                    m = findFunctionMatchOrNull0(new String[]{"compare"}, args, location, failInfo);
                    if (failInfo.isError() || m != null) {
                        return new CompareToLessThanInvokable(m, getContext());
                    }
                    m = findFunctionMatchOrNull0(nameAlternatives, args, location, failInfo);
                    if (m != null) {
                        return m;
                    }
                }
                break;
            }
            case ">": {
                if (binaryOp) {
                    if (args[0].getType().isPrimitive() && args[1].getType().isPrimitive()) {
                        return new PrimitiveCompareInvokable(getContext().types(), name);
                    }
                    String[] nameAlternatives = new String[]{">", "gt"};
                    JInvokable m = findFunctionMatchOrNull0(nameAlternatives, args, location, failInfo);
                    if (failInfo.isError() || m != null) {
                        return m;
                    }
                    m = findFunctionMatchOrNull0(new String[]{"compareTo"}, args, location, failInfo);
                    if (failInfo.isError() || m != null) {
                        return new CompareToGreaterThanInvokable(m, getContext());
                    }
                    m = findFunctionMatchOrNull0(new String[]{"compare"}, args, location, failInfo);
                    if (failInfo.isError() || m != null) {
                        return new CompareToGreaterThanInvokable(m, getContext());
                    }
                    m = findFunctionMatchOrNull0(nameAlternatives, args, location, failInfo);
                    if (m != null) {
                        return m;
                    }
                }
                break;
            }
            case HExtensionNames.BRACKET_GET_SHORT:
            case HExtensionNames.BRACKET_GET_LONG: {
                if (binaryOp) {
                    String[] nameAlternatives = namesWithAliases(HExtensionNames.BRACKET_GET_LONG, HFunctionType.SPECIAL);
                    JInvokable y = findFunctionMatchOrNull0(nameAlternatives, args, location, failInfo);
                    if (y != null) {
                        return y;
                    }
                    JTypePattern baseType = args[0];
                    JTypePattern[] oldTypes = new JTypePattern[]{args[1]};
                    String baseTypeNameSafe = baseType.toString();
                    if (baseTypeNameSafe.endsWith("[]")) {
                        baseTypeNameSafe = "(" + baseTypeNameSafe + ")";
                    }
                    failInfo.fail(jOnError, getLog(), "S044", null, "To use "
                            + baseTypeNameSafe + "[" + JTypePattern.signatureStringNoPars(oldTypes) + "] operator, you should implement either \n"
                            + "\tinstance method: " + baseType.toString() + "." + HExtensionNames.BRACKET_GET_LONG + "" + JTypePattern.signatureString(oldTypes) + " \n"
                            + "\tor\n"
                            + "\tstatic method  : " + HExtensionNames.BRACKET_GET_LONG + "" + JTypePattern.signatureString(args) + " \n", location);
                    return null;
                }
                break;
            }
            case HExtensionNames.BRACKET_SET_SHORT:
            case HExtensionNames.BRACKET_SET_LONG: {
                if (argsCount >= 3) {
                    ///arg0[arg1,...argn]=lastArg
                    String[] nameAlternatives = namesWithAliases(HExtensionNames.BRACKET_SET_LONG, HFunctionType.SPECIAL);
                    JInvokable y = findFunctionMatchOrNull0(nameAlternatives, args, location, failInfo);
                    if (y != null) {
                        return y;
                    }
                    JTypePattern baseType = args[0];
                    JTypePattern[] oldTypes = new JTypePattern[]{args[1], args[2]};
                    String baseTypeNameSafe = baseType.toString();
                    if (baseTypeNameSafe.endsWith("[]")) {
                        baseTypeNameSafe = "(" + baseTypeNameSafe + ")";
                    }
                    failInfo.fail(jOnError, getLog(),
                            "S044", null,
                            "To use "
                                    + baseTypeNameSafe + "[" + JTypePattern.signatureStringNoPars(oldTypes) + "] set operator, you should implement either \n"
                                    + "\tinstance method: " + baseType.toString() + "." + HExtensionNames.BRACKET_SET_LONG + "" + JTypePattern.signatureString(oldTypes) + " \n"
                                    + "\tor\n"
                                    + "\tstatic method  : " + HExtensionNames.BRACKET_SET_LONG + "" + JTypePattern.signatureString(args) + " \n", location
                    );
                    return null;
                }
                break;
            }
        }
        String[] nameAlternatives = namesWithAliases(name, opType);
        JInvokable y = findFunctionMatchOrNull0(nameAlternatives, args, location, failInfo);
        if (y != null) {
            return y;
        }
        failInfo.fail(jOnError, getLog(), location);
        return null;
    }

    private JInvokable findFunctionMatchOrNull0(String[] nameAlternatives, JTypePattern[] args, JToken location, FindMatchFailInfo failInfo) {
        JInvokableCost[] r = findAllMatchesBase(nameAlternatives, args, location, true, failInfo);
        if (r.length == 0) {
            return null;
        }
        return r[0].getInvokable();
    }

    private void fillAcceptableStaticMethodMatches(String[] nameAlternatives, JTypePattern[] args, Set<JInvokable> acceptable, FindMatchFailInfo failInfo) {
        int callArgumentsCount = args.length;
        Set<String> staticImportedTypes = resolveImportStatics();
        for (String nameAlternative : nameAlternatives) {
            if (staticImportedTypes.isEmpty()) {
                failInfo.addAlternative("function", nameAlternative + JTypePattern.signatureString(args));
            } else {
                for (String importedType : staticImportedTypes) {
                    failInfo.addAlternative("function", importedType + "." + nameAlternative + JTypePattern.signatureString(args));
                    failInfo.addImport(importedType);
                }
            }
            for (JMethod jMethod : resolveStaticMethodsByNameFromImports(nameAlternative, callArgumentsCount)) {
                acceptable.add(jMethod);
            }
            for (JFunction function : getContext().functions().findFunctions(nameAlternative, callArgumentsCount)) {
                acceptable.add(HSharedUtils.resolveToMethod(function));
            }
        }
    }

    private JInvokableCost[] fillAcceptableLocalSourceMethodMatches(String[] nameAlternatives, JTypePattern[] args, Set<JInvokable> acceptable, boolean bestOnly, List<JInvokableCost> finalResultNonBestOnly, FindMatchFailInfo failInfo) {
        int callArgumentsCount = args.length;
        boolean noLambda = this.isTypes(args);
        for (JNode jNode : getPath().parent()) {
            if (jNode instanceof HNDeclareType) {
                JMethod[] jMethods = getOrCreateType(((HNDeclareType) jNode))
                        .getDeclaredMethods(
                                nameAlternatives,
                                callArgumentsCount,
                                true
                        );
                for (String nameAlternative : nameAlternatives) {
                    failInfo.addAlternative("context method", getOrCreateType(((HNDeclareType) jNode)).getRawType().getName() + "." + nameAlternative + JTypePattern.signatureString(args));
                }
                failInfo.addImport(getOrCreateType(((HNDeclareType) jNode)).getRawType().getName());
                for (JMethod jMethod : jMethods) {
                    acceptable.add(jMethod);
                    failInfo.addAvailable(jMethod);
                }
            }
        }
        if (acceptable.size() > 0) {
            JInvokable[] ts = acceptable.toArray(new JInvokable[0]);
            if (noLambda) {
                //should remove this
                JInvokable jInvokable = getContext().functions().resolveBestMatch(getCallerInfo(), ts, failInfo.getConversions(), args, null);
                if (jInvokable != null) {
                    if (bestOnly) {
                        return new JInvokableCost[]{new JInvokableCostImpl(
                                jInvokable,
                                0.0
                        )};
                    } else {
                        finalResultNonBestOnly.add(new JInvokableCostImpl(
                                jInvokable,
                                0.0
                        ));
                    }
                }
            } else {
                throw new JFixMeLaterException();
            }
        }
        return null;
    }

    private JInvokableCost[] fillAcceptableArgMethodMatches(String[] nameAlternatives, JTypePattern[] args, Set<JInvokable> acceptable, boolean bestOnly, List<JInvokableCost> finalResultNonBestOnly, FindMatchFailInfo failInfo) {
        int callArgumentsCount = args.length;
        boolean noLambda = this.isTypes(args);
        if (callArgumentsCount > 0) {
            for (String nameAlternative : nameAlternatives) {
                if (args[0].isType()
                        && !args[0].getType().isPrimitive()
                        && !args[0].getType().isArray()) {
                    JType arg0 = args[0].getType();
                    JTypePattern[] type2 = Arrays.copyOfRange(args, 1, callArgumentsCount);
                    String sig2 = JTypeUtils.sig(nameAlternative, type2, false, true);

                    failInfo.addAlternative("1st arg method", arg0.getRawType().getName() + "." + sig2);
//                        failInfo.addImport(arg0.rawType().name());

                    JMethod[] possibleMethods = Arrays.stream(arg0.getDeclaredMethods(new String[]{nameAlternative}, type2.length, true))
                            .filter(x -> isVisible(x) && !x.isStatic()).toArray(JMethod[]::new);
                    JMethod m2 = null;
                    try {
                        m2 = (JMethod) getContext().functions().resolveBestMatch(getCallerInfo(), possibleMethods, failInfo.getConversions(), type2, null);
                    } catch (JMultipleInvokableMatchFound ex) {
                        getLog().jerror("X057", null, null, ex.getMessage());
                        failInfo.setError(true);
                        return new JInvokableCost[0];
                    }
                    for (JMethod jMethod : possibleMethods) {
                        failInfo.addAvailable(jMethod);
                    }
                    if (m2 != null) {
                        JArgumentConverter[] ac = new JArgumentConverter[m2.getSignature().argsCount()];
                        JType[] cTypes = new JType[ac.length + 1];
                        cTypes[0] = arg0;
                        for (int i = 0; i < ac.length; i++) {
                            int newIndex = i + 1;
                            JType newType = m2.getSignature().argType(i);
                            ac[i] = new JArgumentConverterByIndex(newIndex, newType);
                            cTypes[i + 1] = newType;
                        }
                        int instanceArgumentIndex = 0;
                        JType instanceArgumentType = arg0;
                        ConvertedJMethod2 cc = new ConvertedJMethod2(
                                m2, ac,
                                cTypes,
                                new JInstanceArgumentResolverFromArgumentByIndex(instanceArgumentIndex, instanceArgumentType),
                                null
                        );
                        if (bestOnly) {
                            return new JInvokableCost[]{new JInvokableCostImpl(
                                    cc,
                                    0.1
                            )};
                        } else {
                            finalResultNonBestOnly.add(new JInvokableCostImpl(
                                    cc,
                                    0.1
                            ));
                        }
                    }
                }
                //check reverse methods if this is a binary operator method
                boolean binaryReversibleOperator = callArgumentsCount == 2
                        && !nameAlternative.startsWith("_reverse_")
                        && noLambda
                        && args[0].isType()
                        && args[1].isType()
                        && !args[0].getType().getName().equals(args[1].getType().getName())
                        && !args[1].getType().isPrimitive()
                        && !args[1].getType().isArray()
                        && isReversableName(nameAlternative);
                if (binaryReversibleOperator) {
                    String newName = nameAlternative.equals("in") ? "contains" : "_reverse_" + nameAlternative;
                    JTypePattern[] newTypes = new JTypePattern[]{args[0]};
                    String newSig = JTypeUtils.sig(newName, newTypes, false);

                    failInfo.addAlternative("2nd arg method", JTypeUtils.str(args[1]) + "." + newSig);
//                        failInfo.addImport(newTypes[0].rawType().name());

                    JMethod[] possibleMethods = Arrays.stream(args[1].getType().getDeclaredMethods(new String[]{newName}, 1, true))
                            .filter(x -> isVisible(x) && !x.isStatic()).toArray(JMethod[]::new);
                    JMethod m2 = (JMethod) getContext().functions().resolveBestMatch(getCallerInfo(), possibleMethods, failInfo.getConversions(), newTypes, null);
                    if (m2 != null) {
                        ConvertedJMethod2 cc = new ConvertedJMethod2(
                                m2, new JArgumentConverter[]{
                                new JArgumentConverterByIndex(0, args[0].getType())
                        }, new JType[]{args[0].getType(), args[1].getType()},
                                new JInstanceArgumentResolverFromArgumentByIndex(1, args[1].getType()),
                                null
                        );
                        if (bestOnly) {
                            return new JInvokableCost[]{new JInvokableCostImpl(
                                    cc,
                                    0.2
                            )};
                        } else {
                            finalResultNonBestOnly.add(new JInvokableCostImpl(
                                    cc,
                                    0.2
                            ));
                        }
                    }
                }

            }
        }
        return null;
    }

    public boolean isVisible(JField jField) {
        return true;
    }

    public boolean isVisible(JMethod x) {
        return true;
    }

    public boolean isVisible(JConstructor x) {
        return true;
    }

    private boolean isReversableName(String nameAlternative) {
        switch (nameAlternative) {
            case "if":
            case "while": {
                return false;
            }
        }
        return true;
    }

    public JInvokableCost[] findAllMatchesBase(String[] nameAlternatives, JTypePattern[] args,
                                               JToken location, boolean bestOnly, FindMatchFailInfo failInfo) {
        if (failInfo.getConversions() == null) {
            failInfo.setConversions(new ConversionTrace(this));
        }
        Set<JInvokable> acceptable = new LinkedHashSet<>();
        List<JInvokableCost> finalResultNonBestOnly = new ArrayList<>();
        int callArgumentsCount = args == null ? 0 : args.length;
        JTypePattern[] nonNullArgs = args == null ? new JTypePattern[0] : args;
//        boolean noLambda = this.isTypes(args);
        JInvokableCost[] anyResult;

        //check if this can be handled as method of the first or the second argument
        anyResult = fillAcceptableArgMethodMatches(nameAlternatives, nonNullArgs, acceptable, bestOnly, finalResultNonBestOnly, failInfo);
        if (anyResult != null) {
            return anyResult;
        }

        //check if this can be handled in any method in the current context (compilation unit context)
        anyResult = fillAcceptableLocalSourceMethodMatches(nameAlternatives, nonNullArgs, acceptable, bestOnly, finalResultNonBestOnly, failInfo);
        if (anyResult != null) {
            return anyResult;
        }

        acceptable = new LinkedHashSet<>();

        //now that we cannot find any instance method handling the function.
        //we check for static methods....
        fillAcceptableStaticMethodMatches(nameAlternatives, nonNullArgs, acceptable, failInfo);

        Set<String> staticImportedTypes = resolveImportStatics();
        for (String nameAlternative : nameAlternatives) {
            if (isValidTypeName(nameAlternative) && args != null/*constructors should alwaèys have pars*/) {
                JType t = lookupTypeOrNull(DefaultTypeName.of(nameAlternative));
                if (t != null) {
                    //we looking for a constructor?
                    failInfo.addAlternative("constructor", t.getRawType().getName() + JTypePattern.signatureString(nonNullArgs));
                    for (JConstructor cons : t.getDeclaredConstructors()) {
                        JSignature sig = cons.getSignature();
                        if (sig.acceptArgsCount(callArgumentsCount) && isVisible(cons)) {
                            acceptable.add(cons);
                            failInfo.addAvailable(cons);
                        }
                    }
                    //should add static constructors as well
                    String nn = HSharedUtils.getStaticConstructorName(t);
                    for (String importedType : staticImportedTypes) {
                        failInfo.addAlternative("static constructor", importedType + "." + nn + JTypePattern.signatureString(nonNullArgs));
                        failInfo.addImport(importedType);
                    }
                    for (JMethod jMethod : resolveStaticMethodsByNameFromImports(nn, callArgumentsCount)) {
                        if (isVisible(jMethod)) {
                            acceptable.add(jMethod);
                            failInfo.addAvailable(jMethod);
                        }
                    }
                    for (JFunction function : getContext().functions().findFunctions(nn, callArgumentsCount)) {
                        JMethod e = HSharedUtils.resolveToMethod(function);
                        if (isVisible(e)) {
                            acceptable.add(e);
                        }
                    }
                }
            }
        }
        return filterAcceptable(acceptable, args, location, bestOnly, finalResultNonBestOnly, failInfo);
    }

    protected JInvokableCost[] filterAcceptable(Set<JInvokable> acceptable, JTypePattern[] args,
                                                JToken location, boolean bestOnly, List<JInvokableCost> finalResultNonBestOnly, FindMatchFailInfo failInfo) {
        boolean noLambda = this.isTypes(args);
        for (JInvokable jInvokable : acceptable) {
            failInfo.addAvailable(jInvokable);
        }
        if (noLambda) {
            JInvokable[] ts = acceptable.toArray(new JInvokable[0]);
            if (bestOnly) {
                try {
                    JInvokable cc = getContext().functions().resolveBestMatch(getCallerInfo(), ts, failInfo.getConversions(), args, null);
                    if (cc != null) {
                        finalResultNonBestOnly.add(new JInvokableCostImpl(cc, 0.3));
                    }
                } catch (JMultipleInvokableMatchFound ex) {
                    getLog().jerror("X057", null, location, ex.getMessage());
                    failInfo.setError(true);
                    return new JInvokableCost[0];
                }
            } else {
                finalResultNonBestOnly.addAll(
                        Arrays.asList(getContext().functions().resolveMatches(false, ts, failInfo.getConversions(), args, null))
                );
            }
        } else {
            JInvokable[] ts = acceptable
                    .stream().filter(
                            m -> methodMatchesArgs(m, args)
                    )
                    .toArray(JInvokable[]::new);
            if (ts.length == 0) {
                if (bestOnly) {
                    return new JInvokableCost[0];
                }
                return finalResultNonBestOnly.toArray(new JInvokableCost[0]);
            }
            if (ts.length > 1) {
                if (bestOnly) {
                    throw new JMultipleInvokableMatchFound(failInfo.getSignatureString(),
                            ts
                    );
                }
            }
            for (JInvokable t : ts) {
                finalResultNonBestOnly.add(new JInvokableCostImpl(t, 0.3));
            }
        }
        failInfo.getSearchedConverters().addAll(failInfo.getConversions().searchedConverters);
        return finalResultNonBestOnly.toArray(new JInvokableCost[0]);
    }

    public boolean isTypes(JTypePattern[] all) {
        if (all == null) {
            return false;
        }
        for (JTypePattern j : all) {
            if (!j.isType()) {
                return false;
            }
        }
        return true;
    }

    private boolean methodMatchesArgs(JInvokable m, JTypePattern[] args) {
        JSignature s = m.getSignature();
        int callArgumentsCount = args == null ? 0 : args.length;
        if (s.argsCount() == callArgumentsCount) {
            boolean ok = true;
            for (int i = 0; i < callArgumentsCount; i++) {
                JTypePattern ai = args[i];
                JType ji = s.argType(i);
                if (!ai.matchesType(ji)) {
                    return false;
                }
            }
            return true;
        } else {
            for (int i = 0; i < s.argsCount() - 1; i++) {
                JTypePattern ai = args[i];
                JType ji = s.argType(i);
                if (!ai.matchesType(ji)) {
                    return false;
                }
            }
            if (callArgumentsCount == s.argsCount() - 1) {
                return true;
            }
            for (int i = s.argsCount() - 1; i < callArgumentsCount; i++) {
                JTypePattern ai = args[i];
                if (ai.isLambda()) {
                    return false;
                }
                JType ji = s.argType(i);
                if (!ai.matchesType(ji)) {
                    return false;
                }
            }
            return false;
        }
    }

    public boolean isPackage(String packageName) {
        return indexer().searchPackage(packageName) != null;
    }

    public JType lookupType(String nameUsingImports) {
        return lookupType(DefaultTypeName.of(nameUsingImports));
    }

    public JType lookupType(JTypeNameOrVariable nameUsingImports) {
        return lookupTypeOrNull(nameUsingImports);
    }

    //    @Override
//    public HNElement lookupElementByParent(String name, HNElement parent, JToken location) {
//        return null;
//    }
//
//    public HNElement lookupInstanceElement(String name, JType type, JToken location) {
//        for (HLIndexedField p : indexer().searchFields(type.name(),name,true)) {
//            HNElementField field = new HNElementField(name);
//            field.declaringType=type;
//            field.indexedField=p;
//            return field;
//        }
//
////        for (HLIndexedMethod p : indexer().searchMethods(type.name(),name)) {
////            HNIdentifier.HNElementField field = new HNIdentifier.HNElementField(name);
////            field.declaringType=type;
////            field.indexedField=p;
////            return field;
////        }
//        return null;
//    }
    public HNElementLocalVar[] lookupLocalVarDeclarations(String name, JToken location, JNode fromNode, FindMatchFailInfo failInfo) {
        List<HNElement> hnElements = lookupElementNoBaseMulti(
                name,
                null,
                false, false,
                location, fromNode, EnumSet.of(LookupType.LOCAL_VAR), failInfo);
        return hnElements
                .toArray(new HNElementLocalVar[0]);
    }

    private HNElement lookupElementNoBaseOne(
            JOnError onError,
            String name,
            JTypePattern[] args,
            boolean requireStatic,
            boolean lhs,
            JToken location, JNode fromNode, EnumSet<LookupType> lookupTypes, FindMatchFailInfo failInfo) {
        if (failInfo == null) {
            failInfo = new FindMatchFailInfo("symbol");
        }
        List<HNElement> result = lookupElementNoBaseMulti(
                name,
                args,
                requireStatic, lhs,
                location, fromNode, lookupTypes, failInfo);
        if (result.isEmpty()) {
            failInfo.fail(onError, getLog(), location);
            return null;
        } else {
            List<HNElementType> countTypes = new ArrayList<>();
            List<HNElementLocalVar> countVars = new ArrayList<>();
            List<HNElementField> countFields = new ArrayList<>();
            List<HNElementMethod> countMethods = new ArrayList<>();
            List<HNElementConstructor> countConstructors = new ArrayList<>();
            for (HNElement e : result) {
                switch (e.getKind()) {
                    case LOCAL_VAR: {
                        countVars.add((HNElementLocalVar) e);
                        break;
                    }
                    case TYPE: {
                        countTypes.add((HNElementType) e);
                        break;
                    }
                    case FIELD: {
                        countFields.add((HNElementField) e);
                        break;
                    }
                    case METHOD: {
                        countMethods.add((HNElementMethod) e);
                        break;
                    }
                    case CONSTRUCTOR: {
                        countConstructors.add((HNElementConstructor) e);
                        break;
                    }
                }
            }
            if (countVars.size() > 0) {
                if (countVars.size() > 1) {
                    String errorMessage = "ambiguous var access :\n\t"
                            + countVars.stream().map(HNElement::toDescString).collect(Collectors.joining("\n\t"));
                    failInfo.fail(onError, getLog(), "S026", null, errorMessage, location);//always log!
                }
                return result.get(0);
            }
            if (countFields.size() > 0) {
                //get the first visible! no ambiguity here...
//                if (countFields.size() > 1) {
//                    String errorMessage = "ambiguous field access :\n\t"
//                            + countFields.stream().map(HNElement::toDescString).collect(Collectors.joining("\n\t"));
//                    failInfo.fail(onError, log(), "S026", null, errorMessage, location);//always log!
//                }
                return result.get(0);
            }
            if (countTypes.size() > 0) {
                if (countTypes.size() > 1) {
                    String errorMessage = "ambiguous type access :\n\t"
                            + countTypes.stream().map(HNElement::toDescString).collect(Collectors.joining("\n\t"));
                    failInfo.fail(onError, getLog(), "S026", null, errorMessage, location);//always log!
                }
                return result.get(0);
            }
            if (countMethods.size() > 0) {
                List<JInvokable> possibleMethods = new ArrayList<>();
                for (HNElement v : countMethods) {
                    HNElementMethod m = (HNElementMethod) v;
                    JInvokable invokable = m.getInvokable();
                    if (invokable == null) {
                        throw new HMissingLinkageException();
                    }
                    possibleMethods.add(invokable);
                }
                JInvokable[] ts = possibleMethods.toArray(new JInvokable[0]);
                try {
                    JInvokable jInvokable = getContext().functions().resolveBestMatch(getCallerInfo(), ts, failInfo.getConversions(), args == null ? new JTypePattern[0] : args, null);
                    if (jInvokable instanceof JMethod) {
                        JMethod m = (JMethod) jInvokable;
                        return new HNElementMethod(m);
                    }
                    if (jInvokable instanceof JConstructor) {
                        JConstructor m = (JConstructor) jInvokable;
                        return new HNElementConstructor(m.getDeclaringType(), m, null);
                    }
                    JMethod m = (JMethod) jInvokable;
                    return new HNElementMethod(m);
                } catch (JMultipleInvokableMatchFound m) {
                    String errorMessage = "ambiguous method access :\n\t"
                            + Arrays.stream(m.getAllPossibilities()).map(x -> toString()).collect(Collectors.joining("\n\t"));
                    failInfo.fail(onError, getLog(), "S026", null, errorMessage, location);//always log!
                    return null;
                }
            }
            if (result.size() > 0) {
                if (result.size() > 1) {
                    String errorMessage = "ambiguous symbol :\n\t"
                            + result.stream().map(x -> toString()).collect(Collectors.joining("\n\t"));
                    failInfo.fail(onError, getLog(), "S026", null, errorMessage, location);//always log!
                }
                return result.get(0);
            }
            return null;
        }
    }

    private List<HNElement> lookupElementNoBaseMulti(
            String name,
            JTypePattern[] args,
            boolean requireStatic,
            boolean lhs,
            JToken location, JNode fromNode, EnumSet<LookupType> lookupTypes, FindMatchFailInfo failInfo) {
        if (failInfo == null) {
            failInfo = new FindMatchFailInfo("symbol");
        }
        List<HNElement> result = new ArrayList<>();
        if (fromNode == null) {
            fromNode = getNode();
        }
        JNode child = fromNode;
        lookupElementNoBaseFill(name, args, child/*.parentNode()*/, null/*child*/, requireStatic, result, lookupTypes);
        if (args == null /*|| args.length == 0*/) {
            if (lookupTypes == null || lookupTypes.contains(LookupType.TYPE)) {
                JType t = lookupTypeOrNull(name);
                if (t != null) {
                    result.add(new HNElementType(t, types()));
                }
            }
            if (lookupTypes == null
                    || lookupTypes.contains(LookupType.PACKAGE)) {
                if (isPackage(name)) {
                    result.add(new HNElementPackage(name));
                }
                JField[] jFields = resolveStaticFieldsByNameFromImports(name);
                for (JField jField : jFields) {
                    result.add(new HNElementField(jField));
                }
            }
            if (lookupTypes == null
                    || lookupTypes.contains(LookupType.STATIC_FIELD)) {
                JField[] jFields = resolveStaticFieldsByNameFromImports(name);
                for (JField jField : jFields) {
                    result.add(new HNElementField(jField));
                }
            }

        }
        JInvokable m = null;
        if (lookupTypes == null || lookupTypes.contains(LookupType.METHOD)) {
            m = lookupFunctionMatch(JOnError.NULL, name, HFunctionType.NORMAL, args, location, failInfo);
            boolean noArguments = args == null || args.length == 0;
            if (m == null) {
                if (lhs) {

                } else {
                    if (noArguments) {
                        m = lookupFunctionMatch(JOnError.NULL, name, HFunctionType.GET, args, location, failInfo);
                    }
                }
            }
            if (m != null) {
                result.add(new HNElementMethod(m));
            }
        }
        return result;
    }

    //    public HNDeclareTokenBase lookupVarDeclarationOrNull(String name, JToken location) {
//        HNElement hnElement = lookupElementNoBaseOne(JOnError.NULL, name, new JTypePattern[0], isStaticContext(), false, location, node(), lookupTypes, null);
//        if (hnElement != null) {
//            switch (hnElement.getKind()) {
//                case LOCAL_VAR: {
//                    HNElementLocalVar lv = (HNElementLocalVar) hnElement;
//                    HNDeclareTokenBase d = lv.getDeclaration();
//                    if (d != null) {
//                        return d;
//                    } else {
//                        throw new JFixMeLaterException();
//                    }
//                }
//                case FIELD: {
//                    HNElementField lv = (HNElementField) hnElement;
//                    HNDeclareTokenBase d = lv.getDeclaration();
//                    if (d != null) {
//                        return d;
//                    } else if (lv.getField() != null) {
//                        return new JLibField(lv.getField());
//                    } else {
//                        throw new JFixMeLaterException();
//                    }
//                }
//                default: {
//                    break;
//                }
//            }
//        }
//        return null;
//    }
    public void debug(String type, Object msg) {
//        System.out.println("[DEBUG] STAGE[" +stage()+"] "+ type+JeepUtils.threadIndent()+ JToken.escapeString(String.valueOf(msg)));
    }

    public JType lookupTypeOrNull(JTypeNameOrVariable typeName) {
        return lookupTypeOrVariableOrNull(typeName);
    }

    private JType[] lookupTypeOrVariableOrNull(JTypeNameOrVariable[] typeNameOrVar) {
        JType[] aa = new JType[typeNameOrVar.length];
        for (int i = 0; i < aa.length; i++) {
            aa[i] = lookupTypeOrVariableOrNull(typeNameOrVar[i]);
        }
        return aa;
    }

    private JType lookupTypeOrVariableOrNull(JTypeNameOrVariable typeNameOrVar) {
        if (typeNameOrVar instanceof JTypeNameBounded) {
            JTypeNameBounded v = (JTypeNameBounded) typeNameOrVar;
            JDeclaration dec = (JDeclaration) JNodeUtils.findFirstParent(getNode(), x -> x instanceof JDeclaration);
            JTypes types = types();
            JTypesSPI typesSPI = (JTypesSPI) types;
            return typesSPI.createVarType0(
                    v.name(),
                    lookupTypeOrVariableOrNull(v.getLowerBound()),
                    lookupTypeOrVariableOrNull(v.getUpperBound()),
                    dec
            );
        }
        JTypeName typeName = (JTypeName) typeNameOrVar;

        String n = typeName.rawType().name();
        JType jType = lookupTypeOrNull(n);
        if (jType == null) {
            return null;
        }
        JTypeNameOrVariable[] vars = typeName.vars();
        if (vars.length > 0) {
            JType[] aa = lookupTypeOrVariableOrNull(vars);
            if (jType.getName().equals(Tuple.class.getName())) {
                //special case
                jType = HTypeUtils.tupleType(types(), aa);
            } else {
                jType = ((JRawType) jType).parametrize(aa);
            }
        }
        if (typeName.isArray()) {
            jType = jType.toArray(typeName.arrayDimension());
        }
        if (typeName.isVarArg()) {
            jType = jType.toArray();
        }
        return jType;
    }

    private JType lookupTypeOrNull(String name) {
        if (name == null) {
            return null;
        }
        JTypes types = getContext().types();
        JType c = types.forNameOrNull(name);
        if (c != null) {
            return c;
        }
        for (JNode o : getPath().parent()) {
            if (o instanceof HNBlock) {
                HNBlock h = HNBlock.get(o);
                for (HNDeclareType statement : h.getClassDeclarations()) {
                    String name2 = getOrCreateType(statement).getName();
                    c = types.forNameOrNull(name2 + "." + name);
                    if (c != null) {
                        return c;
                    }
                    if (name.equals(name2)) {
                        return getOrCreateType(statement);
                    }
                }
            }
        }
//        for (HNDeclareType visible : metaPackageType().getTopLevelTypeNodesByCompilationUnit(node().startToken().compilationUnit)) {
//            JType jt = getOrCreateType(visible);
//            if (jt.simpleName().equals(name)) {
//                return jt;
//            }
//            if (name.startsWith(jt.simpleName() + ".")) {
//                throw new JFixMeLaterException();
//            }
//        }
        Set<JImportInfo> validImports = buildValidImports();
        String fullPackage = project().getMetaPackageType().getFullPackage();
        if (!JStringUtils.isBlank(fullPackage)) {
            validImports.add(createJImportInfo(fullPackage + ".*", "<default>"));
        }
        //                ,"net.hl.lang.HDefaults"
        Map<String, String> m = resolveTypeImports(validImports.toArray(new JImportInfo[0]), null);
        //how to handle $ names ????
        String y = m.get(name);
        if (y != null) {
            return types.forName(y);
        }
        return null;
    }

    private Set<JImportInfo> buildValidImports() {
        Set<JImportInfo> validImports = new LinkedHashSet<>();

        validImports.addAll(Arrays.asList(getImports()));

        validImports.add(createJImportInfo("java.lang.*", "<default>"));
        validImports.add(createJImportInfo("java.util.*", "<default>"));
        validImports.add(createJImportInfo("java.io.*", "<default>"));
        validImports.add(createJImportInfo("net.hl.lang.*", "<default>"));
        return buildValidImports(validImports);
    }

    private Set<JImportInfo> buildValidImports(Set<JImportInfo> validImports) {
        Set<JImportInfo> result = new HashSet<>();
        Queue<JImportInfo> toProcess = new ArrayDeque<>(validImports);
        while (!toProcess.isEmpty()) {
            JImportInfo next = toProcess.remove();
            if (!result.contains(next)) {
                result.add(next);
                if (next.importValue().endsWith(".**")) {
                    String ns = next.importValue().substring(0, next.importValue().length() - 3);
                    Set<HIndexedClass> s1 = project.indexer().searchTypes(new JIndexQuery().whereDotStart("fullName", ns));
                    for (HIndexedClass hIndexedClass : s1) {

                        for (AnnInfo annotation : hIndexedClass.getAnnotations()) {
                            if (annotation.getName().equals("net.hl.lang.JExports")) {
                                for (Map.Entry<String, AnnValue> e : annotation.getValues().entrySet()) {
                                    if (e.getKey().equals("value")) {
                                        switch (e.getValue().getType()) {
                                            case ARRAY: {
                                                for (AnnValue o : (List<AnnValue>) e.getValue().getValue()) {
                                                    toProcess.add(
                                                            new DefaultJImportInfo(
                                                                    (String) o.getValue(), next.token()
                                                            )
                                                    );
                                                }
                                                break;
                                            }
                                            case STRING: {
                                                toProcess.add(
                                                        new DefaultJImportInfo(
                                                                (String) e.getValue().getValue(), next.token()
                                                        )
                                                );
                                                break;
                                            }
                                            default: {
                                                throw new IllegalArgumentException("unexpected");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public List<JType> lookupTypes(boolean importedOnly, Predicate<String> predicate) {
        Chronometer chrono = Chronometer.start("lookupTypes:" + (importedOnly ? 0 : 1));
        Map<String, JType> result = new LinkedHashMap<>();
        JTypes types = getContext().types();
        for (String stype : new String[]{"boolean", "char", "byte", "short",
                "int",
                "long",
                "float",
                "double"
        }) {
            if (predicate.test(stype)) {
                result.put(stype, types.forName(stype));
            }
        }

        for (JNode o : getPath().parent()) {
            if (o instanceof HNBlock) {
                HNBlock h = HNBlock.get(o);
                for (HNDeclareType statement : h.getClassDeclarations()) {
                    JType type = getOrCreateType(statement);
                    if (predicate.test(type.getName())) {
                        result.put(type.getName(), type);
                    }
                }
            }
        }

        if (importedOnly) {
//            for (HNDeclareType o : ((HNBlock) compilationUnit().getAst()).getStatements().stream().filter(x -> x instanceof HNDeclareType)
//                    .map(x -> (HNDeclareType) x)
//                    .collect(Collectors.toList())) {
//                if (predicate.test(o.getFullName())) {
//                    JType type = o.getOrCreateType(this);
//                    result.put(type.name(), type);
//                }
//            }
            Set<JImportInfo> validImports = new LinkedHashSet<>();

            validImports.addAll(Arrays.asList(getImports()));

            validImports.add(createJImportInfo("java.lang.*", "<default>"));
            validImports.add(createJImportInfo("java.util.*", "<default>"));
            validImports.add(createJImportInfo("java.io.*", "<default>"));
            //                ,"net.hl.lang.HDefaults"
            for (String fqn : resolveTypeImports(validImports.toArray(new JImportInfo[0]), predicate).values()) {
                try {
                    result.put(fqn, types.forName(fqn));
                } catch (Throwable ex) {
                    //ignore
                }
            }
        } else {
//            for (HNDeclareType visible : module().getTopLevelTypeNodesByCompilationUnit(node().startToken().compilationUnit)) {
//                JType jt = visible.getOrCreateType(this);
//                if (predicate.test(jt.name())) {
//                    result.add(jt);
//                }
//            }
//            for (JCompilationUnit compilationUnit : project.getCompilationUnits()) {
//                for (HNDeclareType o : ((HNBlock) compilationUnit.getAst()).getStatements().stream().filter(x -> x instanceof HNDeclareType)
//                        .map(x -> (HNDeclareType) x)
//                        .collect(Collectors.toList())) {
//                    if (predicate.test(o.getFullName())) {
//                        JType type = o.getOrCreateType(this);
//                        result.put(type.name(), type);
//                    }
//                }
//            }
            for (String fqn : project.indexer().searchTypes().stream().map(x -> x.getFullName()).filter(predicate).collect(Collectors.toList())) {
                try {
                    result.put(fqn, types.forName(fqn));
                } catch (Throwable ex) {
                    //ignore
                }
            }
        }
        System.out.println(chrono.stop());
        return new ArrayList<>(result.values());
    }

    public String nextVarName() {
        return nextVarName(null);
    }

    public String nextVarName(String n) {
        return nextVarName(n, getNode());
    }

    public String nextVarName(String n, JNode node) {
        if (n == null) {
            n = "_$V";
        }
        JNode t = lookupEnclosingDeclarationOrMetaPackage(node);
        return HNodeUtils.nextNameFromUserProperty(t, n);
    }

    public String nextVarName2(String n, JNode node) {
        if (n == null) {
            n = "_$V";
        }
        return HNodeUtils.nextNameFromUserProperty(node, n);
    }

    public HNDeclareType lookupEnclosingDeclareTypeImmediate(JNode node) {
        HNode d = lookupEnclosingDeclaration(node);
        if (d == null) {
            return getMetaPackageType();
        }
        if (d instanceof HNDeclareType) {
            return (HNDeclareType) d;
        }
        return null;
    }

    public JType lookupEnclosingType(JNode node) {
        HNDeclareType d = lookupEnclosingDeclareTypeImmediate(node);
        if (d == null) {
            return null;
        } else {
            return getOrCreateType(d);
        }
    }

    //    private JType matchesImports(String name, HNDeclareType statement) {
//        if (name.endsWith(statement.getName()) || name.contains("." + statement.getName() + ".")) {
//            JType jt = statement.getOrCreateType(this);
//            if (name.equals(jt.name())) {
//                return jt;
//            }
//
//            for (String s : imports()) {
//                if (matchesImports(jt, s)) {
//                    return jt;
//                }
//            }
//            for (String s : new String[]{
//                    "java.lang"
//
//            }) {
//                if (matchesImports(jt, s)) {
//                    return jt;
//                }
//            }
//            JNode b = statement.getBody();
//            if (b instanceof HNDeclareType) {
//                JType t = matchesImports(name, (HNDeclareType) b);
//                if (t != null) {
//                    return t;
//                }
//            } else if (b instanceof HNBlock) {
//                for (JNode jNode : ((HNBlock) b).getStatements()) {
//                    if (jNode instanceof HNDeclareType) {
//                        JType t = matchesImports(name, (HNDeclareType) jNode);
//                        if (t != null) {
//                            return t;
//                        }
//                    }
//                }
//            }
//        }
//        return null;
//    }
//    private boolean matchesImports(JType type, String parent) {
//        if (type.name().startsWith(parent + ".")) {
//            JType dt = type.declaringType();
//            if (dt == null) {
//                return type.name().substring(0, (parent + ".").length()).indexOf('.') < 0;
//            }
//        }
//        return false;
//    }
    public HNode lookupEnclosingDeclaration(JNode node) {
        return lookupEnclosingDeclaration(node, false);
    }

    public HNode lookupEnclosingDeclarationOrMetaPackage(JNode node) {
        return lookupEnclosingDeclaration(node, true);
    }

    public HNode lookupEnclosingDeclaration(JNode node, boolean returnMetaPackage) {
        HNode n = (HNode) node;
        while (n != null) {
            HNode p = n.getParentNode();
            if (p == null) {
                break;
            }
            if (p instanceof HNDeclareType) {
                return p;
            }
            if (p instanceof HNLambdaExpression) {
                return p;
            }
            if (p instanceof HNDeclareInvokable) {
                return p;
            }
            if (p instanceof HNTryCatch.CatchBranch) {
                return p;
            }
            if (p instanceof HNTryCatch) {
                return p;
            }
            if (p instanceof HNFor) {
                return p;
            }
            if (p instanceof HNBlock) {
                if (((HNBlock) p).getBlocType() == HNBlock.BlocType.LOCAL_BLOC) {
                    return p;
                }
            }
            n = p;
        }
        if (returnMetaPackage) {
            return getMetaPackageType();
        }
        return null;
    }

    public HNDeclareType lookupEnclosingDeclareType(JNode node) {
        HNode n = (HNode) node;
        while (n != null) {
            HNode p = n.getParentNode();
            if (p == null) {
                break;
            }
            if (p instanceof HNDeclareType) {
                return (HNDeclareType) p;
            }
            n = p;
        }
        return getMetaPackageType();
    }

    public HNDeclareIdentifier lookupEnclosingDeclareIdentifier(HNDeclareToken node) {
        HNode n = (HNode) node;
        while (n != null) {
            HNode p = (HNode) n.getParentNode();
            if (p == null) {
                break;
            }
            if (p instanceof HNDeclareIdentifier) {
                return (HNDeclareIdentifier) p;
            } else {
                if (p instanceof HNDeclareType) {
                    return null;
                }
                if (p instanceof HNLambdaExpression) {
                    return null;
                }
                if (p instanceof HNDeclareInvokable) {
                    return null;
                }
                if (p instanceof HNFor) {
                    return null;
                }
                if (p instanceof HNBlock) {
                    return null;
                }
            }
            n = p;
        }
        return null;
    }

    public HNDeclareInvokable lookupEnclosingInvokable() {
        for (JNode jNode : getPath().parent()) {
            if (jNode instanceof HNDeclareInvokable) {
                return (HNDeclareInvokable) jNode;
            }
        }
        return null;
    }

    private void lookupElementNoBaseFill(String name, JTypePattern[] args, JNode whereToLookInto, JNode child, boolean requireStatic, List<HNElement> result, EnumSet<LookupType> lookupTypes) {
        if (whereToLookInto == null) {
            return;
        }
        boolean staticContext = isStaticContext(whereToLookInto);
        if (requireStatic && !staticContext) {
            return;
        }
        boolean noArguments = args == null || args.length == 0;
        switch (((HNode) whereToLookInto).id()) {
            case H_DECLARE_INVOKABLE: {
                HNDeclareInvokable d = (HNDeclareInvokable) whereToLookInto;

                if (lookupTypes == null || lookupTypes.contains(LookupType.LOCAL_VAR)) {
                    List<HNDeclareIdentifier> arguments = d.getArguments();
                    for (HNDeclareIdentifier argument : arguments) {
                        fillVars(name, result, argument, lookupTypes);
                    }
                }
                lookupElementNoBaseFill(name, args, whereToLookInto.getParentNode(), child, requireStatic, result, lookupTypes);
                break;
            }
            case H_LAMBDA_EXPR: {
                HNLambdaExpression d = (HNLambdaExpression) whereToLookInto;
                if (lookupTypes == null || lookupTypes.contains(LookupType.LOCAL_VAR)) {
                    List<HNDeclareIdentifier> arguments = d.getArguments();
                    for (HNDeclareIdentifier argument : arguments) {
                        fillVars(name, result, argument, lookupTypes);
                    }
                }
                lookupElementNoBaseFill(name, args, whereToLookInto.getParentNode(), child, requireStatic, result, lookupTypes);
                break;
            }
            case H_DECLARE_TYPE: {
                JType dt = getOrCreateType((HNDeclareType) whereToLookInto);//initialize
                HNDeclareType d = (HNDeclareType) whereToLookInto;
                if (noArguments) {
                    if (lookupTypes == null || lookupTypes.contains(LookupType.FIELD)) {
                        List<HNDeclareIdentifier> arguments = d.getMainConstructorArgs();
                        if (arguments != null) {
                            for (HNDeclareIdentifier argument : arguments) {
                                fillFields(name, dt, result, argument);
                            }
                        }
                    }
                }
//                lookupElementNoBaseFill(name, args, d.getBody(), null, requireStatic, result);

                lookupElementNoBaseFill(name, args, whereToLookInto.getParentNode(), child, requireStatic, result, lookupTypes);
                break;
            }
            case H_BLOCK: {
                HNBlock d = (HNBlock) whereToLookInto;
                if (noArguments) {
                    if (lookupTypes == null || lookupTypes.contains(LookupType.LOCAL_VAR)
                            || lookupTypes.contains(LookupType.FIELD)) {
                        for (HNDeclareIdentifier argument : d.findDeclaredIdentifiers()) {
                            HNBlock skipImports = (HNBlock) HNodeUtils.skipImportBlock(d);
                            switch (skipImports.getBlocType()) {
                                case GLOBAL_BODY: {
                                    JType dt = getOrCreateType(getMetaPackageType());
                                    if (lookupTypes == null || lookupTypes.contains(LookupType.FIELD)) {
                                        fillFields(name, dt, result, argument);
                                    }
                                    break;
                                }
                                case CLASS_BODY: {
                                    JType dt = getOrCreateType(((HNDeclareType) skipImports.getParentNode()));
                                    if (lookupTypes == null || lookupTypes.contains(LookupType.FIELD)) {
                                        fillFields(name, dt, result, argument);
                                    }
                                    break;
                                }
                                default: {
                                    if (lookupTypes == null || lookupTypes.contains(LookupType.LOCAL_VAR)) {
                                        fillVars(name, result, argument, lookupTypes);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                if (lookupTypes == null || lookupTypes.contains(LookupType.METHOD)) {
                    for (HNDeclareInvokable argument : d.findDeclaredInvokables()) {
                        if (argument.getName().equals(name)) {
                            if (argument.getInvokable() == null) {
                                throw new HMissingLinkageException();
                            }
                            HNBlock skipImports = (HNBlock) HNodeUtils.skipImportBlock(d);
                            switch (skipImports.getBlocType()) {
                                case GLOBAL_BODY: {
                                    HNElementMethod f = new HNElementMethod(argument.getInvokable());
                                    f.setDeclaringType(getOrCreateType(getMetaPackageType()));
                                    f.setDeclaration(argument);
                                    result.add(f);
                                    break;
                                }
                                case CLASS_BODY: {
                                    HNElementMethod f = new HNElementMethod(argument.getInvokable());
                                    f.setDeclaringType(
                                            getOrCreateType(((HNDeclareType) skipImports.getParentNode()))
                                    );
                                    f.setDeclaration(argument);
                                    result.add(f);
                                    break;
                                }
                            }
                            //local methods not supported yet...
                            //return new HNElementLocalVar(name,argument);
                        }
                    }
                }
                lookupElementNoBaseFill(name, args, whereToLookInto.getParentNode(), child, requireStatic, result, lookupTypes);
                break;
            }
            case H_FOR: {
                if (noArguments) {
                    if (lookupTypes == null || lookupTypes.contains(LookupType.LOCAL_VAR)) {
                        HNFor d = (HNFor) whereToLookInto;
                        for (JNode statement : d.getInitExprs()) {
                            if (statement instanceof HNDeclareIdentifier) {
                                fillVars(name, result, (HNDeclareIdentifier) statement, lookupTypes);
                            }
                        }
                    }
                }
                lookupElementNoBaseFill(name, args, whereToLookInto.getParentNode(), child, requireStatic, result, lookupTypes);
                break;
            }

            case H_SWITCH: {
                if (noArguments) {
                    if (lookupTypes == null || lookupTypes.contains(LookupType.LOCAL_VAR)) {
                        HNSwitch d = (HNSwitch) whereToLookInto;
                        JNode e = HSharedUtils.skipFirstPar(d.getExpr());
                        if (e instanceof HNDeclareIdentifier) {
                            HNDeclareIdentifier argument = (HNDeclareIdentifier) e;
                            fillVars(name, result, argument, lookupTypes);
                        }
                    }
                }
                lookupElementNoBaseFill(name, args, whereToLookInto.getParentNode(), child, requireStatic, result, lookupTypes);
                break;
            }
            case H_TRY_CATCH: {
                if (noArguments) {
                    if (lookupTypes == null || lookupTypes.contains(LookupType.LOCAL_VAR)) {
                        HNTryCatch d = (HNTryCatch) whereToLookInto;
                        JNode e = HSharedUtils.skipFirstPar(d.getResource());
                        if (e instanceof HNDeclareIdentifier) {
                            HNDeclareIdentifier argument = (HNDeclareIdentifier) e;
                            fillVars(name, result, argument, lookupTypes);
                        }
                    }
                }
                lookupElementNoBaseFill(name, args, whereToLookInto.getParentNode(), child, requireStatic, result, lookupTypes);
                break;
            }
            case H_CATCH: {
                if (noArguments) {
                    if (lookupTypes == null || lookupTypes.contains(LookupType.LOCAL_VAR)) {
                        HNTryCatch.CatchBranch d = (HNTryCatch.CatchBranch) whereToLookInto;
                        HNTypeToken[] exceptionTypes = d.getExceptionTypes();
                        JType excType = null;
                        if (exceptionTypes.length == 0) {
                            excType = types().forName(Exception.class.getName());
                        } else {
                            for (HNTypeToken exceptionType : exceptionTypes) {
                                excType = JTypeUtils.firstCommonSuperType(excType,
                                        getTypePattern(true, exceptionType).getType(),
                                        types()
                                );
                            }
                        }
                        HNDeclareTokenIdentifier identifier = d.getIdentifier();
                        if (identifier == null) {
                            identifier = new HNDeclareTokenIdentifier(
                                    HTokenUtils.createToken("exception")
                            );
                        }
                        if (identifier.getElement() == null) {
                            identifier.setElement(new HNElementLocalVar(identifier.getToken().sval, identifier, whereToLookInto.getStartToken())
                                    .setEffectiveType(excType)
                            );
                        }
                        if (identifier.getName().equals(name)) {
                            result.add(identifier.getElement());
                        }
                    }
                }
                lookupElementNoBaseFill(name, args, whereToLookInto.getParentNode(), child, requireStatic, result, lookupTypes);
                break;
            }
            case H_SWITCH_CASE: {
                if (noArguments) {
                    HNSwitch.SwitchCase d = (HNSwitch.SwitchCase) whereToLookInto;
                    if (lookupTypes == null || lookupTypes.contains(LookupType.STATIC_FIELD)) {
                        HNSwitch parentNode = (HNSwitch) d.getParentNode();
                        HNode switchExpr = HSharedUtils.skipFirstPar(parentNode.getExpr());
                        JTypePattern u = null;
                        if (switchExpr instanceof HNDeclareIdentifier) {
                            u = JTypePattern.ofTypeOrNull(((HNDeclareIdentifier) switchExpr).getEffectiveIdentifierType());
                        } else {
                            u = getTypePattern(false, switchExpr);
                        }
                        if (u == null) {
                            throw new HMissingLinkageException();
                        }
                        if (u.isType()) {
                            JField jField = u.getType().boxed().findDeclaredFieldOrNull(name);
                            if (jField != null && jField.isStatic() && isVisible(jField)) {
                                result.add(new HNElementField(jField));
                            }
                        }
                    }
                }
                lookupElementNoBaseFill(name, args, whereToLookInto.getParentNode(), child, requireStatic, result, lookupTypes);
                break;
            }
            case H_IF_WHEN_DO: {
                if (noArguments) {
                    JNode wn = ((HNIf.WhenDoBranchNode) whereToLookInto).getWhenNode();
                    HNElement r = lookupElement_And_JNodeHOpBinaryCall_Elem(wn, name);
                    if (r != null) {
                        result.add(r);
                    }
                }
                lookupElementNoBaseFill(name, args, whereToLookInto.getParentNode(), child, requireStatic, result, lookupTypes);
                break;
            }
            case H_OP_BINARY: {
                if (noArguments) {
                    if (HSharedUtils.isBinaryAndNode(whereToLookInto)) {
                        if (whereToLookInto instanceof HNOpBinaryCall) {
                            HNOpBinaryCall a = (HNOpBinaryCall) whereToLookInto;
                            if (a.getRight() == child) {
                                HNElement r = lookupElement_And_JNodeHOpBinaryCall_Elem(a.getLeft(), name);
                                if (r != null) {
                                    result.add(r);
                                }
                            }
                        } else {
                            throw new JFixMeLaterException();
                        }
                    }
                }
                lookupElementNoBaseFill(name, args, whereToLookInto.getParentNode(), child, requireStatic, result, lookupTypes);
                break;
            }
            case H_SWITCH_IS:
            case H_DECLARE_IDENTIFIER:
            case H_IS: {
                if (noArguments) {
                    HNDeclareTokenHolder holder = (HNDeclareTokenHolder) whereToLookInto;
                    if (lookupTypes == null || lookupTypes.contains(LookupType.LOCAL_VAR)) {
                        HNDeclareToken argument = holder.getDeclareIdentifierTokenBase();
                        fillVars(name, result, argument, lookupTypes);
                    }
                }
                lookupElementNoBaseFill(name, args, whereToLookInto.getParentNode(), child, requireStatic, result, lookupTypes);
                break;
            }
            case H_BRACKETS_POSTFIX: {
                //should i handle other names?
                //perhaps 'last', 'size', 'length' ...
                if (name.equals("$")) {
                    if (lookupTypes == null || lookupTypes.contains(LookupType.DOLLAR_VAR)) {
                        HNBracketsPostfix lop = (HNBracketsPostfix) whereToLookInto;
                        HNElementBracketsVar u = new HNElementBracketsVar(name, lop, whereToLookInto.getStartToken());
                        HNode a = (HNode) lop.getLeft();
                        if (a.getElement().getTypePattern() != null && a.getElement().getTypePattern().isType()) {
                            JType leftType = a.getElement().getTypePattern().getType();
                            if (leftType.isArray()) {
                                u.setEffectiveType(JTypeUtils.forInt(types()));
                            } else if (JTypeUtils.forCharSequence(types()).isAssignableFrom(leftType)) {
                                u.setEffectiveType(JTypeUtils.forInt(types()));
                            } else if (JTypeUtils.forList(types()).isAssignableFrom(leftType)) {
                                u.setEffectiveType(JTypeUtils.forInt(types()));
                            } else {
                                //should i check other thing ?
                                JInvokable m = lookupFunctionMatch(JOnError.TRACE, "getLength", HFunctionType.SPECIAL,
                                        new JTypePattern[]{
                                                JTypePattern.of(leftType)
                                        }, whereToLookInto.getStartToken()
                                );
                                if (m != null) {
                                    u.setInvokable(m);
                                    u.setEffectiveType(m.getReturnType());
                                }
                            }
                        }
                        result.add(u);
                    }
                }
                lookupElementNoBaseFill(name, args, whereToLookInto.getParentNode(), child, requireStatic, result, lookupTypes);
                break;
            }
            default: {
                lookupElementNoBaseFill(name, args, whereToLookInto.getParentNode(), child, requireStatic, result, lookupTypes);
            }
        }
    }

    private void fillVars(String name, List<HNElement> result, HNDeclareIdentifier argument, EnumSet<LookupType> lookupTypes) {
        fillVars(name, result, argument.getIdentifierToken(), lookupTypes);
    }

    private void fillVars(String name, List<HNElement> result, HNDeclareToken argument, EnumSet<LookupType> lookupTypes) {
        if (argument != null) {
            if (argument instanceof HNDeclareTokenIdentifier) {
                fillVars(name, result, (HNDeclareTokenBase) argument, lookupTypes);
            } else if (argument instanceof HNDeclareTokenTuple) {
                for (HNDeclareTokenTupleItem item : ((HNDeclareTokenTuple) argument).getItems()) {
                    fillVars(name, result, item, lookupTypes);
                }
            } else if (argument instanceof HNDeclareTokenList) {
                for (HNDeclareTokenTupleItem item : ((HNDeclareTokenList) argument).getItems()) {
                    fillVars(name, result, item, lookupTypes);
                }
            }
        }
    }

    private void fillVars(String name, List<HNElement> result, HNDeclareTokenBase argument, EnumSet<LookupType> lookupTypes) {
        JToken r = argument.getToken();
        if (r != null) {
            if (r.sval.equals(name)) {
                if (argument instanceof HNDeclareTokenIdentifier) {
                    HNElement element = ((HNDeclareTokenIdentifier) argument).getElement();
                    if (element != null) {
                        result.add(element);
                        return;
                    }
                }
                if (lookupTypes == null || lookupTypes.contains(LookupType.LOCAL_VAR)) {
                    result.add(new HNElementLocalVar(name, argument, r));
                }
            }
        }
    }

    private void fillFields(String name, JType declaringType, List<HNElement> result, HNDeclareIdentifier argument) {
        fillFields(name, declaringType, result, argument.getIdentifierToken());
    }

    private void fillFields(String name, JType declaringType, List<HNElement> result, HNDeclareToken argument) {
        if (argument != null) {
            if (argument instanceof HNDeclareTokenIdentifier) {
                fillFields(name, declaringType, result, (HNDeclareTokenBase) argument);
            } else if (argument instanceof HNDeclareTokenTuple) {
                for (HNDeclareTokenTupleItem item : ((HNDeclareTokenTuple) argument).getItems()) {
                    fillFields(name, declaringType, result, item);
                }
            } else if (argument instanceof HNDeclareTokenList) {
                for (HNDeclareTokenTupleItem item : ((HNDeclareTokenList) argument).getItems()) {
                    fillFields(name, declaringType, result, item);
                }
            }
        }
    }

    private void fillFields(String name, JType declaringType, List<HNElement> result, HNDeclareTokenBase argument) {
        JToken r = argument.getToken();
        if (r != null) {
            if (r.sval.equals(name)) {
                HNElementField e = new HNElementField(name, declaringType, argument, r);
                e.setField(declaringType.getDeclaredField(name));
                result.add(e);
            }
        }
    }

    private HNDeclareTokenBase lookupVarDeclarationType_And_JNodeHOpBinaryCall_Elem(JNode n, String name) {
        if (n instanceof HNIs && ((HNIs) n).getIdentifierToken() != null && name.equals(((HNIs) n).getIdentifierToken().getName())) {
            return (HNDeclareTokenBase) n;
        }
        if (n instanceof HNOpBinaryCall) {
            HNOpBinaryCall b = (HNOpBinaryCall) n;
            HNDeclareTokenBase r = lookupVarDeclarationType_And_JNodeHOpBinaryCall_Elem(b.getLeft(), name);
            if (r != null) {
                return r;
            }
            r = lookupVarDeclarationType_And_JNodeHOpBinaryCall_Elem(b.getRight(), name);
            if (r != null) {
                return r;
            }
        }
        return null;
    }

    private HNElement lookupElement_And_JNodeHOpBinaryCall_Elem(JNode n, String name) {
        if (n instanceof HNIs) {
            HNIs hnIs = (HNIs) n;
            HNDeclareTokenIdentifier identifierTokens = hnIs.getIdentifierToken();
            if (identifierTokens != null) {
                if (identifierTokens.getName().equals(name)) {
                    HNElement e = identifierTokens.getElement();
                    if (e instanceof HNElementLocalVar) {
                        return e;
                    }
                    return new HNElementLocalVar(
                            name,
                            identifierTokens,
                            identifierTokens.getToken()
                    );
                }
            }
        }
        if (n instanceof HNOpBinaryCall) {
            HNOpBinaryCall b = (HNOpBinaryCall) n;
            HNElement r = lookupElement_And_JNodeHOpBinaryCall_Elem(b.getLeft(), name);
            if (r != null) {
                return r;
            }
            r = lookupElement_And_JNodeHOpBinaryCall_Elem(b.getRight(), name);
            if (r != null) {
                return r;
            }
        }
        if (n instanceof HNPars) {
            HNPars p = (HNPars) n;
            for (JNode item : p.getItems()) {
                HNElement r = lookupElement_And_JNodeHOpBinaryCall_Elem(item, name);
                if (r != null) {
                    return r;
                }
            }
        }
        return null;
    }

    public HNDeclareType getMetaPackageType() {
        return project.getMetaPackageType();
    }
//
//    /**
//     * check in an assignment or function argument if the node matches the
//     * expected type and if not (and {@code convert} is armed) converts whenever
//     * possible the node to the expected type. Conversion uses implicit
//     * constructors and static implicit converters that should take the node's
//     * type as unique argument to create and instance of {@code expectedType}.
//     * Null is returned if the node has not a resolvable type yet (in pending
//     * state) or if the conversion is not applicable.
//     *
//     * @param node         node to match
//     * @param expectedType expected type to match the node
//     * @param convert      convert if not assignable from {@code expectedType}
//     * @return the node it self or a converted node or null
//     */
//    protected JNode matchNodeToType(JNode node, JType expectedType, boolean convert) {
//
//        JType tl = expectedType;
//        JType tr = ((HNode) node).getElement().getType();
//        if (tl != null && tr != null) {
//            if (tl.isAssignableFrom(tr)) {
//                return node;
//            } else {
//                JInvokable c = createConverter(JOnError.TRACE, tr, tl, node, null);
//                if (c != null) {
//                    return HSharedUtils.createFunctionCall(node.startToken(), c, node);
//                }
//                return null;
//            }
//        }
//        return null;
//    }

    public JInvokable createConverter(JOnError jOnError, JType from, JType to, JNode location, FindMatchFailInfo failInfo) {
        if (failInfo == null) {
            failInfo = new FindMatchFailInfo(null);
        }
        if (failInfo.getDesc() == null) {
            failInfo.setDesc("converter from " + from + " to " + to);
        }
        if (to.boxed().isAssignableFrom(from.boxed())) {
            //TODO, check if assign nullable to non nullable
            return null;
        }

        for (JConverter converter : getContext().resolvers().getConverters(JTypePattern.of(from))) {
            if (to.isAssignableFrom(converter.targetType().getType())) {
                if (to.boxed().isAssignableFrom(converter.targetType().getType().boxed())) {
                    //TODO, check if assign nullable to non nullable
                    return new JInvokableFromConverter(converter, types());
                }
            }
        }

        //may be i should include only implicit constructors
        // (by the way what is implicit constructor?)
        return this.findConstructorMatch(jOnError, to, new JTypePattern[]{JTypePattern.of(from)}, location.getStartToken(), failInfo);
    }

    public JInvokable findConstructorMatch(JOnError jOnError, JType baseType, JTypePattern[] args, JToken location, FindMatchFailInfo failInfo) {
        ArrayList<JInvokableCost> finalResultNonBestOnly = new ArrayList<>();
        if (failInfo == null) {
            failInfo = new FindMatchFailInfo(baseType.getName() + " constructor");
        }
        if (failInfo.getSignatureString() == null) {
            failInfo.setSignatureString(baseType.getName() + JTypePattern.signatureString(args));
        }
        if (failInfo.getConversions() == null) {
            failInfo.setConversions(new ConversionTrace(this));
        }
        ConversionTrace conversions = new ConversionTrace(this);
        JConstructor[] availableConstructors = baseType.getDeclaredConstructors();
        for (JConstructor possibleInvokable : availableConstructors) {
            if (isVisible(possibleInvokable)) {
                finalResultNonBestOnly.add(new JInvokableCostImpl(possibleInvokable, 0.0));
                failInfo.addAvailable(possibleInvokable);
            }
        }
        failInfo.addAlternative("constructor", baseType.getName() + JTypePattern.signatureString(args));
        JInvokable cc = getContext().functions().resolveBestMatch(getCallerInfo(), availableConstructors, conversions, args, null);
        failInfo.getSearchedConverters().addAll(conversions.searchedConverters);
        if (cc != null) {
            return cc;
        }
        String staticConstructorName = HSharedUtils.getStaticConstructorName(baseType);

        Set<JInvokable> acceptable = new LinkedHashSet<>();
        fillAcceptableStaticMethodMatches(new String[]{staticConstructorName}, args, acceptable, failInfo);
        JInvokableCost[] t = filterAcceptable(acceptable, args, location, true, finalResultNonBestOnly, failInfo);
        if (t.length > 0) {
            return t[0].getInvokable();
        }
        failInfo.fail(jOnError, getLog(), location);
        return null;
    }

    public JType[] jTypes(JTypePattern... all) {
        JType[] a = new JType[all.length];
        for (int i = 0; i < all.length; i++) {
            a[i] = all[i].getType();
        }
        return a;
    }

    public JTypePattern getTypePattern(boolean showError, JNode n) {
        HNode h = (HNode) n;
        JTypePattern typePattern = h.getElement().getTypePattern();
        if (typePattern == null) {
            if (showError) {
                getLog().jerror("S000", null, n.getStartToken(), "unable to resolve symbol type of " + h.getClass().getSimpleName());
            }
        }
        return typePattern;
    }

    public JTypePattern[] getTypePattern(boolean showError, List<HNode> all) {
        JTypePattern[] aa = new JTypePattern[all.size()];
        for (int i = 0; i < all.size(); i++) {
            aa[i] = getTypePattern(showError, all.get(i));
            if (aa[i] == null) {
                return null;
            }
        }
        return aa;
    }

    public JTypePattern[] getTypePattern(boolean showError, JNode... all) {
        JTypePattern[] aa = new JTypePattern[all.length];
        for (int i = 0; i < all.length; i++) {
            aa[i] = getTypePattern(showError, all[i]);
            if (aa[i] == null) {
                return null;
            }
        }
        return aa;
    }

    public JTypePattern getTypePattern(JNode node) {
        HNode hnode = (HNode) node;
        HNElement element = hnode.getElement();
        return element.getTypePattern();
//        if (node instanceof HNLambdaExpression) {
//            HNLambdaExpression lx = (HNLambdaExpression) node;
//            List<HNDeclareIdentifier> arguments = lx.getArguments();
//            JType[] lax = new JType[arguments.size()];
//            for (int j = 0; j < lax.length; j++) {
//                HNTypeToken tn = arguments.get(j).getIdentifierTypeName();
//                JType t = arguments.get(j).getIdentifierType();
//                if (t == null && tn != null) {
//                    return null;
//                }
//                lax[j] = t;
//            }
//            return new JTypePattern(lax);
//        } else if (node instanceof HNTypeToken) {
//            HNTypeToken lx = (HNTypeToken) node;
//            JType t = lx.getTypeVal();
//            if (t == null) {
//                return null;
//            }
//            return new JTypePattern(t);
//        } else {
//            JType t = node == null ? null : node.getType();
//            if (t == null) {
//                return null;
//            }
//            return new JTypePattern(t);
//        }
    }

    public JTypePattern[] getTypePattern(JNode... all) {
        JTypePattern[] aa = new JTypePattern[all.length];
        for (int i = 0; i < all.length; i++) {
            aa[i] = getTypePattern(all[i]);
            if (aa[i] == null) {
                return null;
            }
        }
        return aa;
    }

    public JConverter[] resolveApplicableConverters(JTypePattern from) {
        List<JConverter> all = new ArrayList<>();
        Set<String> classNamesToImport = resolveImportStatics();
        for (String cls : classNamesToImport) {
            JType jType = null;
            try {
                jType = getContext().types().forName(cls);
            } catch (Exception ex) {
                //some how this not accessible... ignore it
            }
            if (jType != null) {
                all.addAll(
                        Arrays.stream(jType.getDeclaredMethods(true))
                                .map(x -> {
                                    if (x.isStatic() && isVisible(x)
                                            && x.getSignature().argsCount() == 1 && !x.getSignature().isVarArgs()
                                            && !x.getReturnType().getName().equals("void")
                                            && x.getSignature().argType(0).isAssignableFrom(from)
                                            && x.getName().equals(HSharedUtils.getStaticConstructorName(x.getReturnType()))) {
                                        return new StaticConstructorConverter(x);
                                    }
                                    return null;
                                }).filter(Objects::nonNull)
                                .collect(Collectors.toList()));
            }
        }
        return all.toArray(new JConverter[0]);
    }

    public HNTypeToken createSpecialTypeToken(String name) {
        return new HNTypeToken(getContext().types().forName(name), null);
    }

    public JSignature signature(JNameSignature sig) {
        //should check imports...
        //TODO
        return JSignature.of(types(), sig);
    }

    public boolean isStaticContext() {
        return isStaticContext(getNode());
    }

    public JType getThisType() {
        return getThisType(getNode());
    }

    public JType getThisType(HNode n) {
        if (n == null) {
            return null;
        }
        String fullChildInfo = n.fullChildInfo();
        if (fullChildInfo.contains("staticInitializers")) {
            return null;
        }
        if (fullChildInfo.contains("mainConstructorArgs")) {
            while (n != null) {
                if (n instanceof HNDeclareType) {
                    return getOrCreateType(((HNDeclareType) n));
                }
                n = n.getParentNode();
            }
            throw new JShouldNeverHappenException();
        }
        if (n instanceof HNDeclareInvokable) {
            HNDeclareInvokable invokable = (HNDeclareInvokable) n;
            if (invokable.isConstructor()) {
                while (n != null) {
                    if (n instanceof HNDeclareType) {
                        return getOrCreateType(((HNDeclareType) n));
                    }
                    n = n.getParentNode();
                }
                throw new JShouldNeverHappenException();
            }
            if (HNAnnotationList.isStatic(invokable.getAnnotations())) {
                return null;
            }
            while (n != null) {
                if (n instanceof HNDeclareType) {
                    return getOrCreateType(((HNDeclareType) n));
                }
                n = n.getParentNode();
            }
            return null;
        }
        if (n instanceof HNDeclareIdentifier) {
            HNDeclareIdentifier invokable = (HNDeclareIdentifier) n;
            if (HNAnnotationList.isStatic(invokable.getAnnotations())) {
                return null;
            }
            while (n != null) {
                if (n instanceof HNDeclareType) {
                    return getOrCreateType(((HNDeclareType) n));
                }
                n = n.getParentNode();
            }
            return null;
        }
        if (n instanceof HNDeclareType) {
            HNDeclareType dtype = (HNDeclareType) n;
            return getOrCreateType(dtype);
        }
        return getThisType(n.getParentNode());
    }

    public boolean isStaticContext(JNode n) {
        if (n == null) {
            return true;
        }
        String fullChildInfo = ((HNode) n).fullChildInfo();
        if (fullChildInfo.contains("staticInitializers")) {
            return true;
        }
        if (fullChildInfo.contains("mainConstructorArgs")) {
            return false;
        }
        if (n instanceof HNDeclareInvokable) {
            HNDeclareInvokable invokable = (HNDeclareInvokable) n;
            if (invokable.isConstructor()) {
                return false;
            }
            return HNAnnotationList.isStatic(invokable.getAnnotations());
        }
        if (n instanceof HNDeclareIdentifier) {
            if (fullChildInfo.contains("mainConstructorArgs")) {
                return false;
            }
            HNDeclareIdentifier invokable = (HNDeclareIdentifier) n;
            if (HNAnnotationList.isStatic(invokable.getAnnotations())) {
                return true;
            }
        }
        if (n instanceof HNDeclareType) {
            HNDeclareType invokable = (HNDeclareType) n;
            if (HNAnnotationList.isStatic(invokable.getAnnotations())) {
                return true;
            }
        }
        return isStaticContext(n.getParentNode());
    }

    public HNElement lookupElement(JOnError onError, String name, HNode dotBase, HNode[] arguments, boolean lhs, JToken location, JNode fromNode, FindMatchFailInfo failInfo) {
        List<JTypePattern> argTypes = new ArrayList<>();
        if (arguments != null) {
            for (JNode item : arguments) {
                HNode hitem = (HNode) item;
                JTypePattern typePattern = getTypePattern(true, hitem);
                if (typePattern == null) {
                    return null;
                }
                argTypes.add(typePattern);
            }
        }
        JTypePattern dotBaseType = null;
        boolean requireStatic = false;
        if (dotBase != null) {
            if (null == dotBase.getElement().getKind()) {
                dotBaseType = getTypePattern(true, dotBase);
                if (dotBaseType == null) {
                    return null;
                }
            } else {
                switch (dotBase.getElement().getKind()) {
                    case PACKAGE:
                        HNElementPackage r2 = (HNElementPackage) dotBase.getElement();
                        String n2 = r2.getName() + "." + name;
                        if (isPackage(n2)) {
                            return new HNElementPackage(n2);
                        } else {
                            JType jtype = lookupType(n2);
                            if (jtype != null) {
                                if (arguments == null) {
                                    return new HNElementType(jtype, types());
                                } else {
                                    JInvokable t = findConstructorMatch(onError, jtype, argTypes.toArray(new JTypePattern[0]), location, failInfo);
                                    if (t != null) {
                                        return new HNElementConstructor(jtype, t, arguments == null ? new HNode[0] : arguments);
                                    }
                                }
                            }
                        }
                        if (failInfo == null) {
                            failInfo = new FindMatchFailInfo("symbol");
                        }
                        failInfo.fail(onError, getLog(), location);
                        return null;
                    case TYPE:
                        HNElementType et = (HNElementType) dotBase.getElement();
                        JType v = et.getValue();
                        if (v == null) {
                            return null;
                        }
                        dotBaseType = JTypePattern.of(v);
                        requireStatic = true;
                        break;
                    default:
                        dotBaseType = getTypePattern(true, dotBase);
                        if (dotBaseType == null) {
                            return null;
                        }
                        break;
                }
            }
        } else {
            requireStatic = isStaticContext();
        }

        HNElement hnElement = lookupElement(onError, name,
                dotBaseType,
                arguments == null ? null : argTypes.toArray(new JTypePattern[0]),
                requireStatic,
                lhs,
                fromNode, location,
                failInfo);
        if (hnElement != null) {
            if (hnElement.getKind() == HNElementKind.METHOD) {
                HNElementMethod m = (HNElementMethod) hnElement;
                if (!m.isArg0TypeProcessed()) {
                    m.setArgNodes(arguments == null ? new HNode[0] : arguments);
                    m.processArg0(dotBase);
                }
            } else if (hnElement.getKind() == HNElementKind.CONSTRUCTOR) {
                HNElementConstructor m = (HNElementConstructor) hnElement;
                m.setArgNodes(arguments == null ? new HNode[0] : arguments);
            }
        }
        return hnElement;
    }

    private HNElement lookupElementWithBase(JOnError onError, String name,
                                            JTypePattern dotBaseType,
                                            JTypePattern[] argTypes,
                                            boolean requireStatic, boolean lhs,
                                            JToken location,
                                            JNode fromNode, FindMatchFailInfo failInfo) {
        if (failInfo == null) {
            failInfo = new FindMatchFailInfo("symbol");
        }
        if (fromNode == null) {
            fromNode = this.getNode();
        }
        if (requireStatic) {
            if (dotBaseType.isType()) {
                JInvokable ctrInvokable = this.findStaticMatch(JOnError.NULL,
                        dotBaseType.getType(),
                        name, HFunctionType.NORMAL,
                        argTypes, location, failInfo);
                if (ctrInvokable == null) {
                    if ((!lhs && (argTypes == null || argTypes.length == 0))) {
                        ctrInvokable = this.findStaticMatch(JOnError.NULL,
                                dotBaseType.getType(),
                                name, HFunctionType.GET,
                                argTypes, location, failInfo);
                    } else if ((lhs && (argTypes != null && argTypes.length == 1))) {
                        ctrInvokable = this.findStaticMatch(JOnError.NULL,
                                dotBaseType.getType(),
                                name, HFunctionType.SET,
                                argTypes, location, failInfo);
                    }
                }
                if (ctrInvokable != null) {
                    HNElementMethod method = new HNElementMethod(ctrInvokable);
//                    method.setArg0Kind(HNElementMethod.Arg0Kind.NONE);
//                    method.setArg0TypeProcessed(true);
                    return method;
                }
                if ((!lhs && (argTypes == null || argTypes.length == 0))
                        || (lhs && (argTypes != null && argTypes.length == 1))) {
                    JField jField = dotBaseType.getType().findDeclaredFieldOrNull(name);
                    if (jField == null) {
                        failInfo.addAlternative("field", dotBaseType.getType().getName() + "." + name);
                    } else {
                        return new HNElementField(jField);
                    }
                }
            }
            failInfo.fail(onError, getLog(), location);
            return null;
        } else {
            JInvokable ctrInvokable = this.findInstanceMatch(JOnError.NULL,
                    name, HFunctionType.NORMAL, dotBaseType,
                    argTypes, location, failInfo);
            if (ctrInvokable == null) {
                if ((!lhs && (argTypes == null || argTypes.length == 0))) {
                    ctrInvokable = this.findInstanceMatch(JOnError.NULL,
                            name, HFunctionType.GET,
                            dotBaseType, argTypes, location, failInfo);
                } else if ((lhs && (argTypes != null && argTypes.length == 1))) {
                    ctrInvokable = this.findInstanceMatch(JOnError.NULL,
                            name, HFunctionType.SET,
                            dotBaseType, argTypes, location, failInfo);
                }
            }
            if (ctrInvokable != null) {
                HNElementMethod method = new HNElementMethod(ctrInvokable);
                method.setArg0Kind(HNElementMethod.Arg0Kind.BASE);
                method.setArg0Type(dotBaseType);
                return method;
            }
            if ((!lhs && (argTypes == null || argTypes.length == 0))
                    || (lhs && (argTypes != null && argTypes.length == 1))) {
                JField jField = dotBaseType.getType().findDeclaredFieldOrNull(name);
                if (jField == null) {
                    failInfo.addAlternative("field", dotBaseType.getType().getName() + "." + name);
                } else {
                    return new HNElementField(jField);
                }
            }
            failInfo.fail(onError, getLog(), location);
            return null;
        }
    }

    private HNElement lookupElement(JOnError onError, String methodName,
                                    JTypePattern dotBaseType,
                                    JTypePattern[] argTypes,
                                    boolean requireStatic, boolean lhs, JNode fromNode, JToken location, FindMatchFailInfo failInfo) {
        if (dotBaseType != null) {
            return lookupElementWithBase(onError, methodName, dotBaseType, argTypes, requireStatic, lhs, location, fromNode, failInfo);
        } else {
            return lookupElementNoBaseOne(onError, methodName, argTypes, requireStatic, lhs, location, fromNode, null, failInfo);
        }
    }

    public JMutableRawType getOrCreateType(HNDeclareType type) {
        JMutableRawType jt = (JMutableRawType) type.getjType();
        if (jt != null) {
            if(jt instanceof JMutableRawType) {
                return (JMutableRawType) jt;
            }else{

            }
        }
        String n = type.getName();
        JNode pn = type.getParentNode();
        HNBlock immediateParent = null;
        JTypes types = types();
        HNDeclareType declaringType = null;
        while (pn != null) {
            if (pn instanceof HNDeclareType) {
                declaringType = (HNDeclareType) pn;
                break;
            } else if (pn instanceof HNBlock) {
                HNBlock pn1 = (HNBlock) pn;
                if (pn1.getBlocType() == HNBlock.BlocType.LOCAL_BLOC || pn1.getBlocType() == HNBlock.BlocType.METHOD_BODY) {
                    if (immediateParent == null) {
                        immediateParent = pn1;
                    }
                }
            }
            pn = pn.getParentNode();
        }
        if (immediateParent != null) {
            declaringType = getMetaPackageType();
        }
        if (declaringType == null) {
            if (!JStringUtils.isBlank(type.getPackageName())) {
                n = type.getPackageName() + "." + n;
            }
            if (!JStringUtils.isBlank(type.getMetaPackage())) {
                n = type.getMetaPackage() + "." + n;
            }
            type.setInternalType(false);
        } else {
            if (!JStringUtils.isBlank(type.getPackageName())) {
                getLog().jerror("X000", null, type.getStartToken(), "internal classes cannot define package");
            }

            JType parentName = getOrCreateType(declaringType);
            String parentFullName;
            if (declaringType.isInternalType()) {
                type.setInternalType(true);
                type.setNonInternalDeclaringType(declaringType.getNonInternalDeclaringType());
                parentFullName = getOrCreateType(declaringType.getNonInternalDeclaringType()).getName();
            } else {
                if (immediateParent != null) {
                    int anonymousNameIndexer = HNodeUtils.incUserProperty(declaringType, "anonymousNameIndexer");
                    n = "$" + anonymousNameIndexer + n;
                }
                parentFullName = parentName.getName();
            }
            n = parentFullName + "." + n;
        }
        JType old = types.forNameOrNull(n);
        if (old != null) {
            //this.getLog().jerror("S012", null, type.getNameToken(), "type declaration : type multiple declarations : " + n);
            return (JMutableRawType) old;
        }
        LOG.log(Level.FINE, "declare type {0}", type.getFullName());
        JTypeKind jTypeKind
                = HNodeUtils.isModifierAnnotation(type.getAnnotations(), "annotation") ? JTypeKind.ANNOTATION
                : HNodeUtils.isModifierAnnotation(type.getAnnotations(), "exception") ? JTypeKind.EXCEPTION
                : HNodeUtils.isModifierAnnotation(type.getAnnotations(), "enum") ? JTypeKind.ENUM
                : HNodeUtils.isModifierAnnotation(type.getAnnotations(), "annotation") ? JTypeKind.ANNOTATION
                : JTypeKind.CLASS;

        jt = (JMutableRawType) types.declareType(n, jTypeKind, false);
        type.setjType(jt);
        ((DefaultJAnnotationInstanceList) jt.getAnnotations())
                .addAll(HNodeUtils.toAnnotations(type.getAnnotations()));
        for (HNExtends extend : type.getExtends()) {
            JType tt = lookupType(extend.getFullName());
            if (tt.isInterface()) {
                jt.addInterface(tt);
            } else {
                jt.setSuperType(tt);
            }
        }
        return jt;
    }

    public void markLocalDeclared(HNElement element, JNode declaringNode, JToken location) {
        Map<String, List<Object>> declarations = (Map<String, List<Object>>) declaringNode.getUserObjects().computeIfAbsent("localDeclarations", (Function<String, Object>) s -> new HashMap<String, Object>());
        String k = null;
        String n = null;
        switch (element.getKind()) {
            case LOCAL_VAR: {
                n = ((HNElementLocalVar) element).getName();
                k = element.getKind().name() + ":" + n;
                List<Object> oldDeclarations = declarations.computeIfAbsent(k, s -> new ArrayList<>());
                if (oldDeclarations.size() > 0) {
                    getLog().jerror("X000", null, location, "multiple local variable declaration : " + n);
                }
                oldDeclarations.add(declaringNode);
                break;
            }
            default: {
                throw new JShouldNeverHappenException();
            }
        }
    }

    @Override
    public HNode getNode() {
        return (HNode) super.getNode();
    }

    @Override
    public JImportInfo[] getImports() {
        LinkedHashSet<JImportInfo> ss = new LinkedHashSet<>();
        ss.addAll(Arrays.asList(super.getImports()));
        JNode n = getNode();
        while (n != null) {
            ss.addAll(((HNode) n).getImports());
            n = n.getParentNode();
        }
        return ss.toArray(new JImportInfo[0]);
    }

    @Override
    public HLJCompilerContext nextNode(JNode node) {
        return (HLJCompilerContext) super.nextNode(node);
    }

    @Override
    public JCompilerContext newInstance(int iteration, int pass, JNodePath path, JImportInfo[] imports, JContext context, String packageName, JCompilerLog log, JCompilationUnit compilationUnit, JCompilerContext parent) {
        return new HLJCompilerContext(iteration,
                pass, path, imports, context, packageName, log, project, compilationUnit, (HLJCompilerContext) parent
        );
    }

    public JCallerInfo getCallerInfo() {
        String n = HSharedUtils.getSourceName(getNode());
        JType y = lookupEnclosingType(getNode());
        return new HCallerInfo(n, y);
    }

    public enum LookupType {
        TYPE, PACKAGE, LOCAL_VAR, STATIC_FIELD, METHOD, DOLLAR_VAR, FIELD
    }

    private static class _InvalidSet extends HashSet {

        private static _InvalidSet INSTANCE = new _InvalidSet();

        private _InvalidSet() {
        }
    }

    private static class StaticConstructorConverter extends AbstractJConverter {

        private final JMethod method;

        public StaticConstructorConverter(JMethod method) {
            super(method.getSignature().argType(0), method.getReturnType(), 1);
            this.method = method;
        }

        public JMethod getMethod() {
            return method;
        }

        @Override
        public Object convert(Object value, JInvokeContext context) {
            JInvokeContext jContext = context.builder().setInstance(new DefaultJTypedValue(
                    null, method.getDeclaringType()
            )).build();
            return method.invoke(jContext);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }
            StaticConstructorConverter that = (StaticConstructorConverter) o;
            return Objects.equals(method, that.method);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), method);
        }

        @Override
        public String toString() {
            return "StaticConstructorConverter("
                    + method
                    + ')';
        }
    }

    public static class ConversionTrace implements Function<JTypePattern, JConverter[]> {

        private HLJCompilerContext context;
        private LinkedHashSet<JConverter> searchedConverters = new LinkedHashSet<>();

        public ConversionTrace(HLJCompilerContext context) {
            this.context = context;
        }

        @Override
        public JConverter[] apply(JTypePattern jType) {
            JConverter[] t = context.resolveApplicableConverters(jType);
            searchedConverters.addAll(Arrays.asList(t));
            return t;
        }
    }

}
