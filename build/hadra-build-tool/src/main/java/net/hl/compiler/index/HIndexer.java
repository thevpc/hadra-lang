package net.hl.compiler.index;

import net.thevpc.jeep.JIndexer;
import net.thevpc.jeep.core.JIndexQuery;
import net.hl.compiler.ast.HNDeclareType;

import java.util.Set;

public interface HIndexer extends JIndexer {

    void indexProject(HIndexedProject project);

    Set<HIndexedClass> searchTypes();

    HIndexedClass searchType(String fullName);

    Set<HIndexedClass> searchTypes(JIndexQuery query);

    Set<HIndexedPackage> searchPackages();

    Set<HIndexedPackage> searchPackages(JIndexQuery query);

    /**
     * first package with fullName
     * @param fullName fullName
     * @return
     */
    HIndexedPackage searchPackage(String fullName);

    Set<HIndexedField> searchFields(String declaringType, String fieldNameOrNull,boolean inherited);

    Set<HIndexedField> searchFields(JIndexQuery query,boolean inherited);

    Set<HIndexedMethod> searchMethods(String declaringType, String methodNameOrNull,boolean inherited);

    Set<HIndexedMethod> searchMethods(JIndexQuery query,boolean inherited);

    Set<HIndexedConstructor> searchConstructors(String declaringType);

    Set<HIndexedConstructor> searchConstructors(JIndexQuery query);

    HIndexedProject searchProject(String projectRoot);
    Set<HIndexedProject> searchProjects();

    void indexType(HIndexedClass p);

    void indexField(HIndexedField p);

    void indexMethod(HIndexedMethod p);

    void indexConstructor(HIndexedConstructor p);

    void indexDeclareType(String uuid, HNDeclareType item);
}
