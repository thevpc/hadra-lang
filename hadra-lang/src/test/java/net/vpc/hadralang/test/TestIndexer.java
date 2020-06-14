package net.vpc.hadralang.test;

import net.vpc.common.jeep.JIndexStoreMemory;
import net.vpc.common.jeep.core.JIndexQuery;
import net.vpc.hadralang.compiler.index.DefaultHLIndexer;
import net.vpc.hadralang.compiler.index.HLIndexedClass;
import net.vpc.hadralang.compiler.index.HLIndexedMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TestIndexer {

    @Test
    public void testJarIndexer() {
        DefaultHLIndexer mem=new DefaultHLIndexer(null);
        mem.indexLibrary(new File("/data/public/git/vpc-common/vpc-common-jeep/target/vpc-common-jeep-1.2.7.jar"),true);
        System.out.println("");
    }

    @Test
    public void testJIndexStoreMemory() {
        JIndexStoreMemory store = new JIndexStoreMemory();
        DefaultHLIndexer ii = new DefaultHLIndexer(store);
        HLIndexedMethod m1 = new HLIndexedMethod("m1", new String[0],
                new String[]{"a"},
                new String[0],
                "r",
                "cls", 0, "test"
        );
        HLIndexedMethod m2 = new HLIndexedMethod("m2", new String[0],
                new String[]{"a"},
                new String[0],
                "r",
                "cls", 0, "test"
        );
        store.index("test", "Method", m1, true);
        store.index("test", "Method", m2, true);
        store.index("test", "Method", m2, true);
        for (HLIndexedMethod cls : ii.searchMethods("cls", null, true)) {
            System.out.println(cls);
        }
        Assertions.assertEquals(2, ii.searchMethods("cls", null, true).size());
    }

    @Test
    public void testJIndexStoreMemory2() {
        JIndexStoreMemory store = new JIndexStoreMemory();
        DefaultHLIndexer ii = new DefaultHLIndexer(store);
        ii.indexSDK(null, true);
        System.out.println(ii.searchPackage("java"));
        Assertions.assertNotNull(ii.searchPackage("java"));
        Assertions.assertNotNull( ii.searchPackage("java.lang"));

        int countOk = 0;
        int countAll = 0;
        for (HLIndexedClass cls : ii.searchTypes(new JIndexQuery().whereEq("packages", "java"))) {
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
