package net.hl.compiler.stages;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.file.Paths;
import net.vpc.common.jeep.JField;
import net.vpc.common.jeep.JMethod;
import net.hl.compiler.core.invokables.HLJCompilerContext;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.vpc.common.jeep.JShouldNeverHappenException;

public class HStageUtils {

    public static List<JField> getNoTypeFields(HLJCompilerContext compilerContext) {
        List<JField> list = (List<JField>) compilerContext.getMetaPackageType().getUserObjects().get("NoTypeFields");
        if (list == null) {
            list = new ArrayList<>();
            compilerContext.getMetaPackageType().getUserObjects().put("NoTypeFields", list);
        }
        return list;
    }

    public static List<JMethod> getNoTypeMethods(HLJCompilerContext compilerContext) {
        List<JMethod> list = (List<JMethod>) compilerContext.getMetaPackageType().getUserObjects().get("NoTypeMethods");
        if (list == null) {
            list = new ArrayList<>();
            compilerContext.getMetaPackageType().getUserObjects().put("NoTypeMethods", list);
        }
        return list;
    }

    public static String urlToFilePath(URL url) {
        if ("file".equalsIgnoreCase(url.getProtocol())) {
            String filename = url.getFile().replace('/', File.separatorChar);
            try {
                return URLDecoder.decode(filename, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                throw new IllegalArgumentException(ex);
            }
        } else {
            throw new JShouldNeverHappenException("Not supported");
        }
    }

    public static String[] resolveLangPaths(String[] dependencyFiles, boolean checkClassLoader, boolean checkCompilerVM, boolean checkContent) {
        LinkedHashSet<String> found = new LinkedHashSet<>();
        //check depdencies
        if (dependencyFiles != null) {
            for (String dependencyFile : dependencyFiles) {
                String s = dependencyFile.replace('\\', '/');
                if (s.endsWith("/hadra-lang/target/classes/") || s.endsWith("/hadra-lang/target/classes")) {
                    //this is dev stdlib, include it
                    found.add(dependencyFile);
                } else if (s.matches(".*/hadra-lang-.*[.]jar")) {
                    found.add(dependencyFile);
                }
            }
        }
        if (checkClassLoader) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl instanceof URLClassLoader) {
                URLClassLoader ucl = (URLClassLoader) cl;
                for (URL url : ucl.getURLs()) {
                    String s = url.toString();
                    if (s.endsWith("/hadra-lang/target/classes/") || s.endsWith("/hadra-lang/target/classes")) {
                        //this is dev stdlib, include it
                        found.add(urlToFilePath(url));
                    } else if (s.matches(".*/hadra-lang-.*[.]jar")) {
                        found.add(urlToFilePath(url));
                    }
                }
            }
        }
        if (checkCompilerVM) {
            //this happens when using sunfire under netbeans!
            String cp = System.getProperty("java.class.path");
            if (cp != null) {
                for (String s : cp.split(File.pathSeparator)) {
                    if (s.endsWith("/hadra-lang/target/classes/") || s.endsWith("/hadra-lang/target/classes")) {
                        found.add(s);
                    } else if (s.matches(".*/hadra-lang-.*[.]jar")) {
                        found.add(s);
                    }
                }
            }
        }
        return found.toArray(new String[0]);
    }

}
