package net.hl.compiler.index;

import net.vpc.common.jeep.JIndexer;
import net.vpc.common.jeep.core.JIndexQuery;
import net.hl.compiler.parser.ast.HNDeclareType;

import java.util.Set;

public interface HLIndexer extends JIndexer {

    void indexProject(HLIndexedProject project);

    Set<HLIndexedClass> searchTypes();

    HLIndexedClass searchType(String fullName);

    Set<HLIndexedClass> searchTypes(JIndexQuery query);

    Set<HLIndexedPackage> searchPackages();

    Set<HLIndexedPackage> searchPackages(JIndexQuery query);

    /**
     * first package with fullName
     * @param fullName fullName
     * @return
     */
    HLIndexedPackage searchPackage(String fullName);

    Set<HLIndexedField> searchFields(String declaringType, String fieldNameOrNull,boolean inherited);

    Set<HLIndexedField> searchFields(JIndexQuery query,boolean inherited);

    Set<HLIndexedMethod> searchMethods(String declaringType, String methodNameOrNull,boolean inherited);

    Set<HLIndexedMethod> searchMethods(JIndexQuery query,boolean inherited);

    Set<HLIndexedConstructor> searchConstructors(String declaringType);

    Set<HLIndexedConstructor> searchConstructors(JIndexQuery query);

    HLIndexedProject searchProject(String projectRoot);
    Set<HLIndexedProject> searchProjects();

    void indexType(HLIndexedClass p);

    void indexField(HLIndexedField p);

    void indexMethod(HLIndexedMethod p);

    void indexConstructor(HLIndexedConstructor p);

    void indexDeclareType(String uuid, HNDeclareType item);
}
