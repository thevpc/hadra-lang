package net.thevpc.hl.test;

import net.hl.compiler.index.*;
import net.thevpc.hl.test.util.TestHelper;
import net.thevpc.jeep.impl.index.mem.JIndexStoreMemory;
import net.thevpc.jeep.core.JIndexQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TestIndexer {
    @BeforeAll
    static void beforeAll(){
        TestHelper.openWorkspace();
    }

    @Test
    public void testJarIndexer() {
        HIndexerImpl mem=new HIndexerImpl(null);
        mem.indexLibrary(new File("/data/public/git/vpc-common/vpc-common-jeep/target/vpc-common-jeep-1.2.7.jar"),true, null);
        System.out.println("");
    }

    @Test
    public void testJIndexStoreMemory() {
        JIndexStoreMemory store = new JIndexStoreMemory();
        HIndexerImpl ii = new HIndexerImpl(store);
        HIndexedMethod m1 = new HIndexedMethod("m1", new String[0],
                new String[]{"a"},
                new String[0],
                "r",
                "cls", new AnnInfo [0], "test"
        );
        HIndexedMethod m2 = new HIndexedMethod("m2", new String[0],
                new String[]{"a"},
                new String[0],
                "r",
                "cls", new AnnInfo[0], "test"
        );
        store.index("test", HElementTypes.METHOD, m1, true);
        store.index("test", HElementTypes.METHOD, m2, true);
        store.index("test", HElementTypes.METHOD, m2, true);
        for (HIndexedMethod cls : ii.searchMethods("cls", null, true)) {
            System.out.println(cls);
        }
        Assertions.assertEquals(2, ii.searchMethods("cls", null, true).size());
    }

    @Test
    public void testJIndexStoreMemory2() {
        JIndexStoreMemory store = new JIndexStoreMemory();
        HIndexerImpl ii = new HIndexerImpl(store);
        ii.indexSDK(null, true, null);
        System.out.println(ii.searchPackage("java"));
        Assertions.assertNotNull(ii.searchPackage("java"));
        Assertions.assertNotNull( ii.searchPackage("java.lang"));

        int countOk = 0;
        int countAll = 0;
        for (HIndexedClass cls : ii.searchTypes(new JIndexQuery().whereEq("packages", "java"))) {
            if (cls.getFullName().equals("java.lang.Number")) {
                countOk++;
            }
            if (cls.getFullName().equals("java.lang.Integer")) {
                countOk++;
            }
            countAll++;
        }
        System.out.println("All="+countAll);
        Assertions.assertEquals(2, countOk);
        Assertions.assertTrue(countAll>3000);

    }
}
