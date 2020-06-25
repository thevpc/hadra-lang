/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.hl.editor.hl4nb.project;

import org.netbeans.modules.maven.model.ModelOperation;
import org.netbeans.modules.maven.model.pom.Build;
import org.netbeans.modules.maven.model.pom.Dependency;
import org.netbeans.modules.maven.model.pom.POMComponentFactory;
import org.netbeans.modules.maven.model.pom.POMModel;
import org.netbeans.modules.maven.model.pom.Plugin;
import org.netbeans.modules.maven.model.pom.Project;

/**
 * Add hadra-maven-plugin and hadralang into the pom model.
 *
 * This is necessary for compiling both Java and HadraLang files together and
 * also for running mixed Java/HadraLang JUnit tests.
 *
 * @author Martin Janicek
 */
public class HLAddMavenCompilerPlugin implements ModelOperation<POMModel> {

    private POMComponentFactory factory;
    private Project project;

    @Override
    public void performOperation(final POMModel model) {
        factory = model.getFactory();
        project = model.getProject();
        Plugin plugin = MvnUtils.searchPlugin(model, MvnUtils.HADRA_MAVEN_PLUGIN);
        if (plugin == null) {
            Build build = project.getBuild();
            if (build == null) {
                build = factory.createBuild();
                project.setBuild(build);
            }
            build.addPlugin(MvnUtils.toPlugin(model, MvnUtils.HADRA_MAVEN_PLUGIN));
        }
        Dependency dep = MvnUtils.searchDependency(model, MvnUtils.HADRA);
        if (dep == null) {
            project.addDependency(MvnUtils.toDependency(model, MvnUtils.HADRA_MAVEN_PLUGIN));
        }
    }
}
