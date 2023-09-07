package fr.progilone.pgcn.service.storage;

import java.io.File;

public interface BinaryScrambler {

    /**
     * Scramble a buffer at the given offset for n bytes.
     */
    void scrambleBuffer(byte[] buf, int off, int n);

    /**
     * Unscramble a buffer at the given offset for n bytes.
     */
    void unscrambleBuffer(byte[] buf, int off, int n);

    /**
     * Gets an unscrambled {@link Binary} for the given file.
     */
    Binary getUnscrambledBinary(File file, String digets);

    /**
     * Skips n bytes during unscrambling.
     */
    void skip(long n);

    /**
     * Reset scrambling from start.
     */
    void reset();

}
