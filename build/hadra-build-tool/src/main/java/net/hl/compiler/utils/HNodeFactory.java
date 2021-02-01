/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.hl.compiler.utils;

import net.hl.compiler.ast.HNDeclareIdentifier;
import net.hl.compiler.ast.HNDeclareTokenIdentifier;
import net.hl.compiler.ast.HNTypeToken;
import net.hl.compiler.core.elements.HNElementIdentifierDef;
import net.hl.compiler.core.elements.HNElementLocalVar;
import net.thevpc.jeep.JType;
import net.thevpc.jeep.JTypes;

/**
 *
 * @author vpc
 */
public class HNodeFactory {

    private JTypes types;

    public HNodeFactory(JTypes types) {
        this.types = types;
    }

    public HNDeclareIdentifier createMethodArg(String name, String type) {
        HNDeclareTokenIdentifier argsTokenIdentifier = new HNDeclareTokenIdentifier(HTokenUtils.createToken(name));
        JType jType = types.forName(type);
        argsTokenIdentifier.setElement(new HNElementIdentifierDef(jType));
        HNDeclareIdentifier id = new HNDeclareIdentifier(
                argsTokenIdentifier,
                null,
                new HNTypeToken(jType, null),
                null,
                null,
                null
        );
        HNElementLocalVar hnElementLocalVar = new HNElementLocalVar(name, argsTokenIdentifier, null);
        hnElementLocalVar.setEffectiveType(jType);
        id.setElement(hnElementLocalVar);
        return id;
    }
}
