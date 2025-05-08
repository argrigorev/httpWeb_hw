package ru.netology;

import java.io.IOException;
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

                for (Map.Entry<String, List<String>> entry : request.getPostParams().entrySet()) {
                    for (String value : entry.getValue()) {
                        responseContent
                                .append(entry.getKey())
                                .append(" = ")
                                .append(value)
                                .append("\n");
                    }
                }

                byte[] contentBytes = responseContent.toString().getBytes();
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


