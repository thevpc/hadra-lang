package net.hl.lang.ext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IOExtensions {

    public static Path newPath(String first) {
        return Paths.get(first);
    }

    public static Path newPath(String first, String... more) {
        return Paths.get(first, more);
    }

    public static Path newPath(URI uri) {
        return Paths.get(uri);
    }

    public static Path write(URL url, Path to, OpenOption... options){
        //FIX ME LATER
        return write(to,newPrimitiveByteArray(url),options);
    }

    public static String readString(URL url){
        return new String(newPrimitiveByteArray(url));
    }

    public static Path write(Path path, byte[] bytes, OpenOption... options){
        try {
            return Files.write(path, bytes,options);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static byte[] newPrimitiveByteArray(URL url) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream is=url.openStream()){
            byte[] byteChunk = new byte[8192];
            int n;
            while ((n = is.read(byteChunk)) > 0) {
                baos.write(byteChunk, 0, n);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
