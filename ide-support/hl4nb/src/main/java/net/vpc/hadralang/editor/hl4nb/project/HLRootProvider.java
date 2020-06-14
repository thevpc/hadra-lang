package net.vpc.hadralang.editor.hl4nb.project;

import org.netbeans.modules.maven.spi.queries.JavaLikeRootProvider;
import org.netbeans.spi.project.ProjectServiceProvider;

@ProjectServiceProvider(service=JavaLikeRootProvider.class, projectType="org-netbeans-modules-maven")
public class HLRootProvider implements JavaLikeRootProvider {

    @Override public String kind() {
        return "hl";
    }

}
