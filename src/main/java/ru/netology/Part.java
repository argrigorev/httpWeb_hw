package ru.netology;

import org.apache.commons.fileupload.FileItem;

public class Part {
    private final FileItem fileItem;

    public Part(FileItem fileItem) {
        this.fileItem = fileItem;
    }

    public String getName() {
        return fileItem.getFieldName();
    }

    public String getFilename() {
        return fileItem.getName(); // null если это обычное поле
    }

    public String getContentType() {
        return fileItem.getContentType();
    }

    public byte[] getContent() {
        return fileItem.get();
    }

    public boolean isFile() {
        return !fileItem.isFormField();
    }

    @Override
    public String toString() {
        return "Part{" +
                "name='" + getName() + '\'' +
                ", filename='" + getFilename() + '\'' +
                ", contentType='" + getContentType() + '\'' +
                ", content=" + getContent().length + " bytes" +
                '}';
    }
}