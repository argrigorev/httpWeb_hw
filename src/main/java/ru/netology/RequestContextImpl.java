package ru.netology;

import org.apache.commons.fileupload.RequestContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class RequestContextImpl implements RequestContext {
    private int contentLength;
    private String contentType;
    private InputStream inputStream;

    public RequestContextImpl(int contentLength, String contentType, InputStream inputStream) {
        this.contentLength = contentLength;
        this.contentType = contentType;
        this.inputStream = inputStream;
    }

    @Override
    public String getCharacterEncoding() {
        return Charset.defaultCharset().name();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public int getContentLength() {
        return contentLength;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }
}
