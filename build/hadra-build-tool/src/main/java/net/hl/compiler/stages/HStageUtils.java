package net.hl.compiler.stages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import net.thevpc.jeep.JField;
import net.thevpc.jeep.JMethod;
import net.hl.compiler.core.invokables.HLJCompilerContext;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.hl.compiler.utils.DepIdAndFile;
import net.thevpc.jeep.JShouldNeverHappenException;

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
            throw new JShouldNeverHappenException("not supported");
        }
    }

    public static DepIdAndFile[] resolveLangPaths(DepIdAndFile[] deps,String[] dependencyFiles, boolean checkClassLoader, boolean checkCompilerVM, boolean checkContent) {
        LinkedHashSet<DepIdAndFile> found = new LinkedHashSet<>();
        //check depdencies
        if (deps != null) {
            for (DepIdAndFile dependencyFile : deps) {
                if(dependencyFile.getId().startsWith("net.thevpc.hl:hadra-lang#")){
                    found.add(dependencyFile);
                }
            }
        }
        if (dependencyFiles != null) {
            for (String dependencyFile : dependencyFiles) {
                DepIdAndFile e = hadraLangDepIdAndFileForPath(dependencyFile);
                if (e != null) {
                    found.add(e);
                }
            }
        }
        if (checkClassLoader) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl instanceof URLClassLoader) {
                URLClassLoader ucl = (URLClassLoader) cl;
                for (URL url : ucl.getURLs()) {
                    String s = url.toString();
                    DepIdAndFile e = hadraLangDepIdAndFileForPath(s);
                    if (e != null) {
                        found.add(e);
                    }
                }
            }
        }
        if (checkCompilerVM) {
            //this happens when using sunfire under netbeans!
            String cp = System.getProperty("java.class.path");
            if (cp != null) {
                for (String s : cp.split(File.pathSeparator)) {
                    DepIdAndFile e = hadraLangDepIdAndFileForPath(s);
                    if (e != null) {
                        found.add(e);
                    }
                }
            }
        }
        return found.toArray(new DepIdAndFile[0]);
    }

    public static DepIdAndFile hadraLangDepIdAndFileForPath(String path) {
        String s0 = path;
        path = path.replace("\\", "/");
        if (path.endsWith("/hadra-lang/target/classes/") || path.endsWith("/hadra-lang/target/classes")) {
            String pomPath = (path.endsWith("/"))
                    ? (path.substring(0, path.length() - 8) + "maven-archiver/pom.properties")
                    : (path.substring(0, path.length() - 7) + "maven-archiver/pom.properties");
            File pomFile = new File(pomPath.replace('/', File.separatorChar));
            if (pomFile.exists()) {
                Properties p = new Properties();
                try (Reader r = new FileReader(pomFile)) {
                    p.load(r);
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
                //version=0.1.0
                //groupId=net.thevpc.hl
                //artifactId=hadra-lang
                String version = p.getProperty("version");
                if (version == null) {
                    return null;
                }
                String groupId = p.getProperty("groupId");
                if (groupId == null) {
                    return null;
                }
                String artifactId = p.getProperty("artifactId");
                if (artifactId == null) {
                    return null;
                }
                return new DepIdAndFile(groupId + ":" + artifactId + "#" + version, s0);
            }
            return null;
        } else if (path.matches(".*/hadra-lang-[a-z0-9.-]+[.]jar")) {
            try (InputStream is = new FileInputStream(s0)) {
                try (ZipInputStream zis = new ZipInputStream(is)) {
                    //get the zipped file list entry
                    ZipEntry ze = zis.getNextEntry();
                    final ZipInputStream finalZis = zis;
                    InputStream entryInputStream = new InputStream() {
                        @Override
                        public int read() throws IOException {
                            return finalZis.read();
                        }

                        @Override
                        public int read(byte[] b) throws IOException {
                            return finalZis.read(b);
                        }

                        @Override
                        public int read(byte[] b, int off, int len) throws IOException {
                            return finalZis.read(b, off, len);
                        }

                        @Override
                        public void close() throws IOException {
                            finalZis.closeEntry();
                        }
                    };

                    while (ze != null) {
                        String fileName = ze.getName();
                        if (fileName.equals("META-INF/maven/net.thevpc.hl/hadra-lang/pom.properties")) {
                            Properties p = new Properties();
                            try (Reader r = new InputStreamReader(entryInputStream)) {
                                p.load(r);
                            } catch (IOException ex) {
                                throw new UncheckedIOException(ex);
                            }
                            //version=0.1.0
                            //groupId=net.thevpc.hl
                            //artifactId=hadra-lang
                            String version = p.getProperty("version");
                            if (version == null) {
                                return null;
                            }
                            String groupId = p.getProperty("groupId");
                            if (groupId == null) {
                                return null;
                            }
                            String artifactId = p.getProperty("artifactId");
                            if (artifactId == null) {
                                return null;
                            }
                            return new DepIdAndFile(groupId + ":" + artifactId + "#" + version, s0);
                        }
                        ze = zis.getNextEntry();
                    }
                }
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
        return null;
    }

}
