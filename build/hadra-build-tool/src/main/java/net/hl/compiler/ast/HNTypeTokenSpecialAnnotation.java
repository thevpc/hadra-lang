package net.hl.compiler.ast;

import net.thevpc.jeep.*;
import net.thevpc.jeep.core.types.DefaultTypeName;

public class HNTypeTokenSpecialAnnotation extends HNTypeToken {
    private JToken nameToken;
    private HNTypeTokenSpecialAnnotation() {
        super();
    }

    public HNTypeTokenSpecialAnnotation(JToken nameToken) {
        super(nameToken,DefaultTypeName.of(nameToken.image), new HNTypeToken[0],new HNTypeToken[0],new HNTypeToken[0],nameToken,nameToken);
    }

}
