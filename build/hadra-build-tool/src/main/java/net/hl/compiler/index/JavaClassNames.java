package net.hl.compiler.index;

import net.thevpc.jeep.JShouldNeverHappenException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class JavaClassNames {
    public String simpleName;
    public String simpleName2;
    public String fullName;
    public String declaringName;
    public String packageName;

    public static JavaClassNames parseFullNameFromEncoded(String name) {
        return parseFullNameFromPath(readInternalForm(new StringReader(name)));
    }
    public static JavaClassNames parseFullNameFromPath(String name) {
        JavaClassNames cn = new JavaClassNames();
        String s = name.replace('/', '.');
        int x = s.lastIndexOf('$');
        if (x >= 0) {
            cn.simpleName = s.substring(x + 1);
            cn.fullName = s.replace('$', '.');
            int y = s.lastIndexOf('.');
            if (y >= 0) {
                cn.simpleName2 = cn.fullName.substring(y + 1);
            } else {
                cn.simpleName2 = cn.fullName;
            }
            cn.declaringName = cn.fullName.substring(0, x);
            if (y >= 0) {
                cn.packageName = cn.fullName.substring(0, y);
            } else {
                cn.packageName = "";
            }
        } else {
            x = s.lastIndexOf('.');
            if (x >= 0) {
                cn.fullName = s;
                cn.simpleName = s.substring(x + 1);
                cn.packageName = s.substring(0, x);
//                    cn.declaringName=null;
            } else {
                cn.packageName = "";
                cn.simpleName = s;
                cn.fullName = s;
            }
            cn.simpleName2 = cn.simpleName;
        }
        return cn;
    }



    private String parseSimpleName(String name) {
        int x = name.lastIndexOf('/');
        if (x >= 0) {
            name = name.substring(x + 1);
        }
        return name;
    }

    private static String readInternalForm(Reader name) {
        int a = 0;
        StringBuilder ename = new StringBuilder();
        try {
            int c = name.read();
            if (c == -1) {
                return null;
            }
            while (c == '[') {
                a++;
                c = name.read();
            }
            switch (c) {
                case 'Z': {
                    ename.append("boolean");
                    break;
                }
                case 'C': {
                    ename.append("char");
                    break;
                }
                case 'B': {
                    ename.append("byte");
                    break;
                }
                case 'S': {
                    ename.append("short");
                    break;
                }
                case 'I': {
                    ename.append("int");
                    break;
                }
                case 'F': {
                    ename.append("float");
                    break;
                }
                case 'J': {
                    ename.append("long");
                    break;
                }
                case 'D': {
                    ename.append("double");
                    break;
                }
                case 'V': {
                    ename.append("void");
                    break;
                }
                case 'L': {
                    boolean loop = true;
                    while (loop) {
                        try {
                            c = name.read();
                        } catch (IOException e) {
                            c = -1;
                        }
                        switch (c) {
                            case -1:
                            case ';': {
                                loop = false;
                                break;
                            }
                            case '/':
                            case '$': {
                                ename.append('.');
                                break;
                            }
                            default: {
                                ename.append((char) c);
                            }
                        }
                    }
                    break;
                }
                default: {
                    throw new JShouldNeverHappenException("unexpected " + ((char) c));
                }
            }
            if (a > 0) {
                for (int i = 0; i < a; i++) {
                    ename.append("[]");
                }
            }
            return ename.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error");
        }
    }

    public static String[] parseInternalFormList(String name) {
        List<String> all = new ArrayList<>();
        String s;
        StringReader r = new StringReader(name);
        while ((s = readInternalForm(r)) != null) {
            all.add(s);
        }
        return all.toArray(new String[0]);
    }

    public static String parseInternalForm(String name) {
        StringReader r = new StringReader(name);
        return readInternalForm(r);
    }


}
