package ru.netology;

import org.apache.commons.fileupload.FileItem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


public class Main {
    public static void main(String[] args) {
        final Server server = new Server();

        server.addHandler("GET", "/messages", (request, responseStream) -> {
            String responseContent = "Hello, this is a message.";
            try {
                responseStream.write("HTTP/1.1 200 OK\r\n".getBytes());
                responseStream.write("Content-Type: text/plain\r\n".getBytes());
                responseStream.write("Content-Length: ".getBytes());
                responseStream.write(String.valueOf(responseContent.length()).getBytes());
                responseStream.write("\r\n\r\n".getBytes());

                responseStream.write(responseContent.getBytes());
                responseStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.addHandler("POST", "/messages", ((request, responseStream) -> {
            try {
                StringBuilder responseContent = new StringBuilder("Received POST data:\n");

                Map<String, String> formParams = request.getFormParams();
                if (!formParams.isEmpty()) {
                    responseContent.append("Form parameters:\n");
                    for (Map.Entry<String, String> entry : formParams.entrySet()) {
                        responseContent.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                    }
                    responseContent.append("\n");
                }

                List<Part> parts = request.getParts();
                if (!parts.isEmpty()) {
                    responseContent.append("Files:\n");
                    for (Part part : parts) {
                        responseContent.append("Name: ").append(part.getName()).append("\n");
                        if (part.getFilename() != null) {
                            responseContent.append("Filename: ").append(part.getFilename()).append("\n");
                            responseContent.append("Content-Type: ").append(part.getContentType()).append("\n");
                            responseContent.append("Size: ").append(part.getContent().length).append(" bytes\n");
                        }
                        responseContent.append("\n");
                    }
                }

                byte[] contentBytes = responseContent.toString().getBytes(StandardCharsets.UTF_8);
                responseStream.write("HTTP/1.1 200 OK\r\n".getBytes());
                responseStream.write("Content-Type: text/plain\r\n".getBytes());
                responseStream.write(("Content-Length: " + contentBytes.length + "\r\n").getBytes());
                responseStream.write("\r\n".getBytes());
                responseStream.write(contentBytes);
                responseStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        server.listen(9999);
    }
}