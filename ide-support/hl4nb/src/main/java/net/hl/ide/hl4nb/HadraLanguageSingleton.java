/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.ide.hl4nb;

import net.thevpc.jeep.JIndexStore;
import net.hl.compiler.core.HadraLanguage;
import net.hl.ide.hl4nb.project.HLnbJIndexStore;
import org.netbeans.api.project.Project;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vpc
 */
public class HadraLanguageSingleton {
    public static final HadraLanguage HADRA_LANGUAGE=new HadraLanguage();
    public static Map<String,JIndexStore> stores=new HashMap<>();

    public static JIndexStore getIndexStoreByProject(Project project){
        String id = project.getProjectDirectory().getPath();
        JIndexStore index = stores.get(id);
        if(index==null){
            index=new HLnbJIndexStore(id);
            stores.put(id,index);
        }
        return index;
    }
}
