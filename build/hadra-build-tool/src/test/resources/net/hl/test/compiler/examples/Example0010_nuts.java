package net.vpc.app.nuts;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NutsInstaller {
    public static String readString(URL url) throws IOException {
        try (InputStream in = url.openStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    public static void main(String[] args) {
        String metadata = readString(new URL("https://raw.github.com/thevpc/nuts/master/meta-data.txt"));
        String apiVersion = null;
        String jarLocation = null;
        for (String line : metadata.split("\n")) {
            line = line.trim();
            if (line.matcher("[^#].*").matches()) {
                String key = null;
                String value = null;
                Matcher matcher = Pattern.compile("[:=]").matcher(line);
                if (matcher.find()) {
                    key = line.substring(0, matcher.start());
                    value = line.substring(matcher.start() + 1);
                }
                switch (key != null ? key.trim() : "") {
                    case "apiVersion": {
                        apiVersion = value.trim();
                        break;
                    }
                    case "jarLocation": {
                        jarLocation = value.trim();
                        break;
                    }
                }
            }
        }
        if (jarLocation == null) {
            apiVersion = "0.6.0";
            jarLocation = MessageFormat.format("https://github.com/thevpc/vpc-public-maven/raw/master/net/vpc/app/nuts/nuts/{0}/nuts-{0}.jar", apiVersion);
        }
        Path nj = Paths.get("nuts.jar");
        try (OutputStream out = Files.newOutputStream(nj)) {
            try (InputStream in = new URL(jarLocation).openStream()) {
                byte[] buffer = new byte[1024];
                int r = 0;
                while ((r = in.read(buffer)) != 0) {
                    out.write(buffer, 0, r);
                }
            }
        }
        ProcessBuilder proc = new ProcessBuilder("java", "-jar", nj.toString(), "--gui");
        proc.inheritIO().start().waitFor();
    }
}