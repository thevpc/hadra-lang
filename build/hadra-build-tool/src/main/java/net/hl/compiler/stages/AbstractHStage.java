/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.stages;

import java.util.Set;
import net.hl.compiler.HL;
import net.hl.compiler.core.HProject;
import net.hl.compiler.core.HTarget;

/**
 *
 * @author vpc
 */
public abstract class AbstractHStage implements HStage{
    @Override
    public boolean isEnabled(HProject project, HL options) {
        Set<HTarget> a = HTarget.expandDependencies(getTargets());
        for (HTarget target : options.getTargets()) {
            if(a.contains(target)){
                return true;
            }
        }
        return false;
    }
}
