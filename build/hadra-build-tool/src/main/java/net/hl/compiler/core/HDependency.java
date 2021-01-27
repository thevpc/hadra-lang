package net.hl.compiler.core;

import net.thevpc.jeep.util.JStringUtils;

public class HDependency {
    private String name;
    private String scope;
    private boolean optional;
    private String[] exclusions;

    public HDependency(String name, String scope, boolean optional, String[] exclusions) {
        this.name = name;
        this.scope = scope;
        this.optional = optional;
        this.exclusions = exclusions == null ? new String[0] : exclusions;
    }

    public String getName() {
        return name;
    }

    public String getScope() {
        return scope;
    }

    public boolean isOptional() {
        return optional;
    }

    public String[] getExclusions() {
        return exclusions;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append(name);
        boolean first=true;

        if(!JStringUtils.isBlank(scope) && !"compile".equals(scope)){
            if(first){
                sb.append("?");
                first=false;
            }
            sb.append("scope=").append(scope);
        }

        if(optional){
            if(first){
                sb.append("?");
                first=false;
            }
            sb.append("optional=").append(optional);
        }
        if(exclusions.length>0){
            if(first){
                sb.append("?");
                first=false;
            }
            sb.append("exclusions=").append(String.join(",",
                    exclusions
            ));
        }
        return sb.toString();
    }
}
