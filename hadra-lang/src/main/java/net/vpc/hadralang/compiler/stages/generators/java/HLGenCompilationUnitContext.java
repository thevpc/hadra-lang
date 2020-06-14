package net.vpc.hadralang.compiler.stages.generators.java;

import net.vpc.common.jeep.JType;
import net.vpc.common.jeep.JTypes;
import net.vpc.common.jeep.JParameterizedType;
import net.vpc.common.jeep.JTypeArray;

import java.util.*;

public class HLGenCompilationUnitContext {
    private Map<String, String> simpleToFullImport = new LinkedHashMap<>();
    private Map<String, String> simpleToFullStaticImport = new LinkedHashMap<>();
    private JTypes types;
    private JType currentType;
    private GenGlobalContext genGlobalContext;
    private boolean moduleClass;

    public HLGenCompilationUnitContext(GenGlobalContext genGlobalContext, JTypes types) {
        this.genGlobalContext = genGlobalContext;
        this.types = types;
    }

    public boolean isModuleClass() {
        return moduleClass;
    }

    public HLGenCompilationUnitContext setModuleClass(boolean moduleClass) {
        this.moduleClass = moduleClass;
        return this;
    }

    public JType getCurrentType() {
        return currentType;
    }

    public HLGenCompilationUnitContext setCurrentType(JType currentType) {
        this.currentType = currentType;
        return this;
    }

    public GenGlobalContext global() {
        return genGlobalContext;
    }

    public JTypes types() {
        return types;
    }

    public List<String> imports() {
        return new ArrayList<>(simpleToFullImport.values());
    }
    public List<String> staticImports() {
        return new ArrayList<>(simpleToFullStaticImport.values());
    }

    public String nameWithImports(JType type) {
        return nameWithImports(type,false,false);
    }
    public String nameWithImports(JType type, boolean staticImport) {
        return nameWithImports(type,staticImport,false);
    }

    public String nameWithImports(JType type, boolean staticImport, boolean noPrimitive) {
            if(type.isArray()){
                JTypeArray ta=(JTypeArray) type;
                String s = nameWithImports(ta.rootComponentType());
                for (int i = 0; i < ta.arrayDimension(); i++) {
                    s+="[]";
                }
                return s;
            }
            if(type.isRawType()){
                if (type.isPrimitive()) {
                    if (noPrimitive) {
                        return nameWithImports(type.boxed(),noPrimitive);
                    }
                    return type.name();
                }
                StringBuilder sb = new StringBuilder();
                while (true) {
                    if (sb.length() > 0) {
                        sb.insert(0, ".");
                    }
                    sb.insert(0, type.simpleName());
                    JType dt = type.declaringType();
                    if (dt == null) {
                        String ns = type.packageName();
                        if(ns==null){
                            ns="";
                        }
                        String full=ns;
                        String simpleName = sb.toString();
                        if (full.isEmpty()) {
                            full = simpleName;
                        } else {
                            if (full.equals("java.lang")) {
                                return sb.toString();
                            }
                            full += "." + simpleName;
                        }
                        String s = currentType.packageName();
                        if(s==null){
                            s="";
                        }
                        if(full.isEmpty() || s.equals(ns)){
                            return simpleName;
                        }
                        if(staticImport) {
                            String fullWithWildcard = full + ".*";
                            if (simpleToFullStaticImport.containsKey(simpleName)) {
                                if (fullWithWildcard.equals(simpleToFullStaticImport.get(simpleName))) {
                                    return simpleName;
                                } else {
                                    return full;
                                }
                            } else {
                                simpleToFullStaticImport.put(simpleName, fullWithWildcard);
                                return simpleName;
                            }
                        }else{
                            if (simpleToFullImport.containsKey(simpleName)) {
                                if (full.equals(simpleToFullImport.get(simpleName))) {
                                    return simpleName;
                                } else {
                                    return full;
                                }
                            } else {
                                simpleToFullImport.put(simpleName, full);
                                return simpleName;
                            }
                        }
                    }
                    type = dt;
                }
            }
            String rawFullString = nameWithImports(type.rawType(), false);
//        String name = type.name();
//            if (rawFullString.equals(currentType.rawType().name())
//                    || Objects.equals(currentType.packageName(), type.packageName())) {
//                String n = type.simpleName();
//                while (true) {
//                    type = type.declaringType();
//                    if (type == null) {
//                        return n;
//                    }
//                    n = type.simpleName() + "." + n;
//                }
//            }
            JType[] jTypeOrVariables = (type instanceof JParameterizedType)? ((JParameterizedType)type).actualTypeArguments():new JType[0];
            if (jTypeOrVariables.length > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(rawFullString);
                sb.append("<");
                for (int i = 0; i < jTypeOrVariables.length; i++) {
                    if(i>0){
                        sb.append(",");
                    }
                    sb.append(nameWithImports(jTypeOrVariables[i],staticImport,true));
                }
                sb.append(">");
                return sb.toString();
            }
            throw new IllegalArgumentException("Unexpected");

    }
}
