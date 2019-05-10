package fr.progilone.pgcn.web.filter.gzip;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

class GZipServletOutputStream extends ServletOutputStream {
    private final OutputStream stream;

    public GZipServletOutputStream(final OutputStream output) throws IOException {
        super();
        this.stream = output;
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }

    @Override
    public void flush() throws IOException {
        this.stream.flush();
    }

    @Override
    public void write(final byte b[]) throws IOException {
        this.stream.write(b);
    }

    @Override
    public void write(final byte b[], final int off, final int len) throws IOException {
        this.stream.write(b, off, len);
    }

    @Override
    public void write(final int b) throws IOException {
        this.stream.write(b);
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(final WriteListener listener) {

    }
}
