package net.hl.compiler.core;

import java.text.Normalizer;
import java.util.regex.Pattern;
import net.thevpc.jeep.util.JStringUtils;

public class JModuleId {

    public static final JModuleId DEFAULT_MODULE_ID() {
        String z = System.getProperty("user.name").toLowerCase();
        //normalize the name

        String nfdNormalizedString = Normalizer.normalize(z, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        z = pattern.matcher(nfdNormalizedString).replaceAll("");

        StringBuilder sb = new StringBuilder();
        for (char c : z.toCharArray()) {
            if (c >= '0' && c <= '9') {
                if (sb.length() == 0) {
                    sb.append('_');
                }
                sb.append(c);
            } else if (c == '_' || c == '.' || (c >= 'a' && c <= 'z')) {
                sb.append(c);
            } else {
                sb.append('_');
            }
        }
        if (sb.toString().equals("_")) {
            sb.append("unknown");
        }
        return new JModuleId("local."
                + sb.toString(),
                "no-name", "1.0.0-SNAPSHOT");
    }
    private final String groupId;
    private final String artifactId;
    private final String version;

    public JModuleId(String groupId, String artifactId, String version) {
        this.groupId = groupId == null ? "" : groupId;
        this.artifactId = artifactId == null ? "" : artifactId;
        this.version = version == null ? "" : version;
    }

    public static JModuleId valueOf(String s) {
        if (s == null) {
            return new JModuleId("", "", "");
        }
        String group = "";
        String artifact;
        String version = "";
        int colon = s.indexOf(':');
        if (colon >= 0) {
            group = s.substring(0, colon);
            s = s.substring(colon + 1);
        }
        int sharp = s.indexOf('#');
        if (sharp >= 0) {
            artifact = s.substring(0, sharp);
            version = s.substring(sharp + 1);
        } else {
            artifact = s;
        }
        return new JModuleId(group, artifact, version);
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        if (groupId.isEmpty() && artifactId.isEmpty() && version.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        if (!groupId.isEmpty()) {
            sb.append(groupId).append(':');
        }
        sb.append(artifactId);
        if (!version.isEmpty()) {
            sb.append('#').append(version);
        }
        return sb.toString();
    }

    public static JModuleId replaceBlanks(JModuleId moduleId, JModuleId defaultId) {
        if (defaultId == null) {
            defaultId = DEFAULT_MODULE_ID();
        }
        if (moduleId == null) {
            moduleId = defaultId;
        }
        if (JStringUtils.isBlank(moduleId.getGroupId()) && !JStringUtils.isBlank(defaultId.getGroupId())) {
            moduleId = new JModuleId(defaultId.getGroupId(), moduleId.getArtifactId(), moduleId.getVersion());
        }
        if (JStringUtils.isBlank(moduleId.getArtifactId()) && !JStringUtils.isBlank(defaultId.getArtifactId())) {
            moduleId = new JModuleId(moduleId.getGroupId(), defaultId.getArtifactId(), moduleId.getVersion());
        }
        if (JStringUtils.isBlank(moduleId.getVersion())) {
            moduleId = new JModuleId(moduleId.getGroupId(), moduleId.getArtifactId(), defaultId.getVersion());
        }
        return moduleId;
    }
}
