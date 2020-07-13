package net.hl.compiler.core;

import net.vpc.common.jeep.JContext;

import java.util.*;

public class HCompilerEnv implements JCompilerEnv{
    private HProject project;
    private HOptions options;
    private JContext context;
    private JModuleId effectiveModuleId;
    private Set<String> effectiveClassPathIds;
    private Set<String> effectiveClassPathFiles;

    private List<HDependency> dependencies=new ArrayList<>();
    private Map<String,String> properties=new LinkedHashMap<>();

    public HCompilerEnv(HProject project, HOptions options, JContext context) {
        this.project = project;
        this.options = options;
        this.context = context;
    }

    public HProject project() {
        return project;
    }

    public HOptions options() {
        return options;
    }

    public JContext context() {
        return context;
    }

    public Map<String, String> properties() {
        return properties;
    }

    public HDependency[] dependencies(){
        return dependencies.toArray(new HDependency[0]);
    }

    public void addDependency(HDependency d){
        if(d!=null){
            dependencies.add(d);
        }
    }

    public JModuleId getEffectiveModuleId() {
        return effectiveModuleId;
    }

    public HCompilerEnv setEffectiveModuleId(JModuleId effectiveModuleId) {
        this.effectiveModuleId = effectiveModuleId;
        return this;
    }

    public Set<String> getEffectiveClassPathIds() {
        return effectiveClassPathIds;
    }

    public HCompilerEnv setEffectiveClassPathIds(Set<String> effectiveClassPathIds) {
        this.effectiveClassPathIds = effectiveClassPathIds;
        return this;
    }

    public Set<String> getEffectiveClassPathFiles() {
        return effectiveClassPathFiles;
    }

    public HCompilerEnv setEffectiveClassPathFiles(Set<String> effectiveClassPathFiles) {
        this.effectiveClassPathFiles = effectiveClassPathFiles;
        return this;
    }
}
