import net.vpc.hadralang.compiler.HL;
import net.vpc.hadralang.compiler.utils.SetLog;

public class TestCompile {
    public static void main(String[] args) {
        SetLog.prepare();
        HL hl=HL.create();
        hl.addSourceMavenProject("/data/public/git/nuts/nuts-installer");
        hl.generateJavaFolder("/data/public/git/nuts/nuts-installer/target/generated-sources/hl/");
        hl.compile();
    }
}
