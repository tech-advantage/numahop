package fr.progilone.pgcn.service.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

/**
 * Utilitaire pour tarer (detarer) un dossier
 * en conservant son arborescence ou non.
 *
 * @author Emmanuel RIZET
 *
 */
public class TarUtils {

    public TarUtils() {
    }

    /**
     * Construction du tar en conservant la structure du repertoire racine.
     *
     * Pour tout archiver à plat, laisser basePath à null.
     *
     * @param name
     *            tar name
     * @param basePath
     *            root
     * @param files
     * @throws IOException
     */
    public static void compress(final String name, final Path basePath, final File... files) throws IOException {
        try (TarArchiveOutputStream out = getTarArchiveOutputStream(name)) {
            for (final File file : files) {
                final String entry;
                if (basePath == null) {
                    entry = "." + File.separator
                            + file.getName();
                } else {
                    final Path p = basePath.relativize(file.toPath());
                    entry = p.getParent() == null ? null
                                                  : p.getParent().toString();
                }
                addToArchiveCompression(out, file, entry);
            }
        }
    }

    public static void decompress(final String in, final File out) throws IOException {
        try (TarArchiveInputStream fin = new TarArchiveInputStream(new FileInputStream(in))) {
            TarArchiveEntry entry;
            while ((entry = fin.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                final File curfile = new File(out, entry.getName());
                final File parent = curfile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                IOUtils.copy(fin, new FileOutputStream(curfile));
            }
        }
    }

    private static TarArchiveOutputStream getTarArchiveOutputStream(final String name) throws IOException {
        final TarArchiveOutputStream archout = new TarArchiveOutputStream(new FileOutputStream(name));
        // pour s'affranchir de la limite par defaut à 8GiB
        archout.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);
        // pour support longs noms de fichiers
        archout.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
        archout.setAddPaxHeadersForNonAsciiNames(true);
        return archout;
    }

    private static void addToArchiveCompression(final TarArchiveOutputStream out, final File file, final String dir) throws IOException {

        final String entry = dir == null ? file.getName()
                                         : dir + File.separator
                                           + file.getName();
        if (file.isFile()) {
            out.putArchiveEntry(new TarArchiveEntry(file, entry));
            try (FileInputStream in = new FileInputStream(file)) {
                IOUtils.copy(in, out);
            }
            out.closeArchiveEntry();
        }
    }

}
