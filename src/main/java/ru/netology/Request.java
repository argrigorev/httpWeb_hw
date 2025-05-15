package ru.netology;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final InputStream body;

    private final Map<String, String> formParameters = new HashMap<>();
    //private final Map<String, FileItem> fileItems = new HashMap<>();
    private final Map<String, List<FileItem>> fileItems = new HashMap<>();

    public Request(String method, String path,
                   Map<String, String> headers, InputStream body) throws Exception {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;

        if ("POST".equalsIgnoreCase(method)) {
            String contentType = headers.getOrDefault("Content-Type", "").toLowerCase();
            if (contentType.startsWith("application/x-www-form-urlencoded")) {
                parseFormUrlEncoded(body);
            } else if (contentType.startsWith("multipart/form-data")) {
                parseMultipart(contentType, body);
            }
        }
    }

    private void parseFormUrlEncoded(InputStream body) throws IOException {
        String bodyStr = new String(body.readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("Form URLEncoded body: " + bodyStr); // Логирование для отладки
        String[] pairs = bodyStr.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String value = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            formParameters.put(key, value);
        }
    }

    private void parseMultipart(String contentType, InputStream body) throws Exception {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");

        byte[] bodyBytes = body.readAllBytes();
        System.out.println("Multipart body size: " + bodyBytes.length); // Логирование

        RequestContext requestContext = new RequestContextImpl(
                bodyBytes.length,
                contentType,
                new ByteArrayInputStream(bodyBytes)
        );

        List<FileItem> items = upload.parseRequest(requestContext);
        System.out.println("Found " + items.size() + " items"); // Логирование
        for (FileItem item : items) {
            System.out.println("Item: " + item.getFieldName() + ", isFormField: " + item.isFormField());
            if (item.isFormField()) {
                String value = item.getString("UTF-8");
                System.out.println("Text field: " + item.getFieldName() + " = " + value);
                formParameters.put(item.getFieldName(), item.getString("UTF-8"));
            } else {
                System.out.println("File: " + item.getFieldName() + ", filename: " + item.getName());
                fileItems.computeIfAbsent(item.getFieldName(), k -> new ArrayList<>()).add(item);
            }
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path.split("\\?")[0];
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public InputStream getBody() {
        return body;
    }

    public String getFormParam(String name) {
        return formParameters.get(name);
    }

    public Map<String, String> getFormParams() {
        return formParameters;
    }

    public FileItem getFile(String fieldName) {
        List<FileItem> items = fileItems.get(fieldName);
        return (items != null && !items.isEmpty()) ? items.get(0) : null;
    }

    public Map<String, List<FileItem>> getFiles() {
        return fileItems;
    }

    public Part getPart(String name) {
        List<FileItem> items = fileItems.get(name);
        return (items != null && !items.isEmpty()) ? new Part(items.get(0)) : null;
    }

    public List<Part> getParts() {
        List<Part> parts = new ArrayList<>();
        for (List<FileItem> items : fileItems.values()) {
            for (FileItem item : items) {
                parts.add(new Part(item));
            }
        }
        return parts;
    }
}
