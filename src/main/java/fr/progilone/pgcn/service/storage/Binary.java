package fr.progilone.pgcn.service.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.inject.Inject;

public class Binary implements Serializable {
	
    protected final String digest;

    protected transient File file;

    protected long length;
    
    @Inject
    BinaryStorageManager bm;

    public Binary(final File file, final String digest) {
        this.file = file;
        this.digest = digest;
        length = file.length();
    }

    /**
     * Gets the length of the binary.
     *
     * @return the length of the binary
     */
    public long getLength() {
        return length;
    }

    /**
     * Gets a string representation of the hex digest of the binary.
     *
     * @return the digest, characters are in the range {@code [0-9a-f]}
     */
    public String getDigest() {
        return digest;
    }

    /**
     * Gets an input stream for the binary.
     *
     * @return the input stream
     * @throws IOException
     */
    public InputStream getStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + digest + ')';
    }

    private void writeObject(final java.io.ObjectOutputStream oos)
            throws IOException, ClassNotFoundException {
        oos.defaultWriteObject();
        oos.writeObject(file);
    }

}
