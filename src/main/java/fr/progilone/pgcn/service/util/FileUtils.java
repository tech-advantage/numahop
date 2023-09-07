package fr.progilone.pgcn.service.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class FileUtils {

    private FileUtils() {
    }

    /**
     * Simple copie de fichiers par streams.
     *
     * @param src
     * @param dest
     * @throws IOException
     */
    public static void copyFile(final File src, final File dest) throws IOException {

        try (final InputStream is = new FileInputStream(src); final OutputStream os = new FileOutputStream(dest);) {
            final byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    /**
     * Génère le checksum d'un fichier, selon l'algorithme donné
     *
     * @param file
     * @param type
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    public static String checkSum(final File file, final CheckSumType type) throws NoSuchAlgorithmException, IOException {
        if (type == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder("");

        try (final FileInputStream fis = new FileInputStream(file);) {
            final MessageDigest md = MessageDigest.getInstance(type.getValue());
            final byte[] dataBytes = new byte[1024];
            int nread = 0;

            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }

            final byte[] mdbytes = md.digest();

            // convert the byte to hex format
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
        }

        return sb.toString();
    }

    public enum CheckSumType {

        MD5("MD5"),
        SHA1("SHA-1");

        private final String value;

        private CheckSumType(final String v) {
            value = v;
        }

        public String getValue() {
            return value;
        }
    }
}
