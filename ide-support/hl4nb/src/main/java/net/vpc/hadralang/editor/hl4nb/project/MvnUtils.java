/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.hadralang.editor.hl4nb.project;

import java.util.List;
import org.netbeans.modules.maven.api.ModelUtils;
import org.netbeans.modules.maven.model.pom.Build;
import org.netbeans.modules.maven.model.pom.Dependency;
import org.netbeans.modules.maven.model.pom.POMComponentFactory;
import org.netbeans.modules.maven.model.pom.POMModel;
import org.netbeans.modules.maven.model.pom.Plugin;
import org.netbeans.modules.maven.model.pom.PluginExecution;

/**
 *
 * @author vpc
 */
public class MvnUtils {

    public static final MvnArtifact HADRA = new MvnArtifact("net.vpc.hadralang", "hadra-lang", "1.0.0"); // NOI18N
    public static final MvnArtifact HADRA_MAVEN_PLUGIN = new MvnArtifact("net.vpc.hadralang", "hadra-maven-plugin", "1.0.0"); // NOI18N
    public static final MvnArtifact MAVEN_COMPILER = new MvnArtifact("org.apache.maven.plugins", "maven-compiler-plugin", "3.3"); // NOI18N

//    <plugin>
//                <groupId>net.vpc.hadra-lang</groupId>
//                <artifactId>hadra-maven-plugin</artifactId>
//                <version>1.0.0</version>
//                <configuration>
//                </configuration>
//                <executions>
//                    <execution>
//                        <goals>
//                            <goal>hlc</goal>
//                        </goals>
//                    </execution>
//                </executions>
//            </plugin>
    public static Plugin toPlugin(POMModel model, MvnArtifact a) {
        POMComponentFactory factory = model.getFactory();
        Plugin plugin = factory.createPlugin();
        plugin.setGroupId(a.getGroupId());
        plugin.setArtifactId(a.getArtifactId());
        plugin.setVersion(a.getVersion());
//        plugin.setConfiguration(createConfiguration());
        if (a.equals(HADRA_MAVEN_PLUGIN)) {
            PluginExecution e = factory.createExecution();
            e.addGoal("hlc");
            plugin.addExecution(e);
        }
        return plugin;
    }

    public static Dependency toDependency(POMModel model, MvnArtifact a) {
        POMComponentFactory factory = model.getFactory();
        Dependency dep = factory.createDependency();
        dep.setGroupId(a.getGroupId());
        dep.setArtifactId(a.getArtifactId());
        dep.setVersion(a.getVersion());
        return dep;
    }

    public static Plugin searchPlugin(final POMModel model, MvnArtifact artifact) {
        Build build = model.getProject().getBuild();
        if (build == null) {
            return null;
        }
        List<Plugin> plugins = build.getPlugins();
        if (plugins != null) {
            for (Plugin plugin : plugins) {
                if (artifact.getGroupId().equals(plugin.getGroupId())
                        && artifact.getArtifactId().equals(plugin.getArtifactId())) {
                    return plugin;
                }
            }
        }
        return null;
    }

    public static boolean hasModelDependency(POMModel model, MvnArtifact a) {
        return ModelUtils.hasModelDependency(model, a.getGroupId(), a.getArtifactId());
    }

    public static Dependency searchDependency(POMModel model, MvnArtifact a) {
        for (Dependency dependency : model.getProject().getDependencies()) {
            if (a.getGroupId().equals(dependency.getGroupId())
                    && a.getArtifactId().equals(dependency.getArtifactId())) {
                return dependency;
            }
        }
        return null;
    }
}
