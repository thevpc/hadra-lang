package net.vpc.hadralang.compiler;

import net.vpc.app.nuts.*;

public class HLMain extends NutsApplication {
    public static void main(String[] args) {
        // just create an instance and call runAndExit in the main method
//        System.setProperty("nuts.boot.args","-z -y");
//        new net.vpc.hadralang.compiler.core.HadraLanguage().generateTokensClass(System.out);
        new HLMain().runAndExit(args);
    }

    @Override
    public void run(NutsApplicationContext applicationContext) {
        applicationContext.processCommandLine(new NutsCommandLineProcessor() {
            HL hl=HL.create();
            boolean noMoreOptions=false;
            @Override
            public boolean processOption(NutsArgument argument, NutsCommandLine cmdLine) {
                if(!noMoreOptions){
                    return false;
                }
                switch (argument.getStringKey()) {
                    case "--clean": {
                        hl.clean(cmdLine.nextBoolean().getBooleanValue());
                        return true;
                    }
                    case "-i":
                    case "--incremental":{
                        hl.setIncremental(cmdLine.nextBoolean().getBooleanValue());
                        return true;
                    }
                    case "-r":
                    case "--root":{
                        hl.setProjectRoot(cmdLine.nextString().getStringValue());
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean processNonOption(NutsArgument argument, NutsCommandLine cmdLine) {
                String s = argument.getString();
                if(isURL(s)){
                    hl.addSourceFileURL(s);
                }else{
                    hl.addSourceFile(s);
                }
                noMoreOptions=true;
                return true;
            }

            private boolean isURL(String s) {
                return
                        s.startsWith("file:")
                        ||s.startsWith("http:")
                        ||s.startsWith("https:")
                        ;
            }

            @Override
            public void exec() {
                hl.compile();
            }
        });
    }
}
