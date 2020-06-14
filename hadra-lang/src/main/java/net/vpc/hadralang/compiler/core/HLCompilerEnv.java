package net.vpc.hadralang.compiler.core;

import net.vpc.common.jeep.JContext;

import java.util.*;

public class HLCompilerEnv implements JCompilerEnv{
    private HLProject project;
    private HLCOptions options;
    private JContext context;
    private JModuleId effectiveModuleId;
    private Set<String> effectiveClassPathIds;
    private Set<String> effectiveClassPathFiles;

    private List<HLDependency> dependencies=new ArrayList<>();
    private Map<String,String> properties=new LinkedHashMap<>();

    public HLCompilerEnv(HLProject project, HLCOptions options, JContext context) {
        this.project = project;
        this.options = options;
        this.context = context;
    }

    public HLProject project() {
        return project;
    }

    public HLCOptions options() {
        return options;
    }

    public JContext context() {
        return context;
    }

    public Map<String, String> properties() {
        return properties;
    }

    public HLDependency[] dependencies(){
        return dependencies.toArray(new HLDependency[0]);
    }

    public void addDependency(HLDependency d){
        if(d!=null){
            dependencies.add(d);
        }
    }

    public JModuleId getEffectiveModuleId() {
        return effectiveModuleId;
    }

    public HLCompilerEnv setEffectiveModuleId(JModuleId effectiveModuleId) {
        this.effectiveModuleId = effectiveModuleId;
        return this;
    }

    public Set<String> getEffectiveClassPathIds() {
        return effectiveClassPathIds;
    }

    public HLCompilerEnv setEffectiveClassPathIds(Set<String> effectiveClassPathIds) {
        this.effectiveClassPathIds = effectiveClassPathIds;
        return this;
    }

    public Set<String> getEffectiveClassPathFiles() {
        return effectiveClassPathFiles;
    }

    public HLCompilerEnv setEffectiveClassPathFiles(Set<String> effectiveClassPathFiles) {
        this.effectiveClassPathFiles = effectiveClassPathFiles;
        return this;
    }
}
