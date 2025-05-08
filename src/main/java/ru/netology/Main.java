package ru.netology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        final Server server = new Server();

        server.addHandler("GET", "/messages", (request, responseStream) -> {
            StringBuilder responseContent = new StringBuilder("Query parameters received:\n");

            Map<String, List<String>> queryParams = request.getQueryParams();
            if (queryParams.isEmpty()) {
                responseContent.append("No query parameters provided.");
            } else {
                queryParams.forEach((key, values) -> {
                    for (String value : values) {
                        responseContent.append(key).append(" = ").append(value).append("\n");
                    }
                });
            }

            try {
                byte[] contentBytes = responseContent.toString().getBytes();
                responseStream.write("HTTP/1.1 200 OK\r\n".getBytes());
                responseStream.write("Content-Type: text/plain\r\n".getBytes());
                responseStream.write(("Content-Length: " + contentBytes.length + "\r\n").getBytes());
                responseStream.write("\r\n".getBytes());
                responseStream.write(contentBytes);
                responseStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.addHandler("POST", "/messages", ((request, responseStream) -> {
            StringBuilder responseContent = new StringBuilder("Received POST data:\n");

            Map<String, java.util.List<String>> queryParams = request.getQueryParams();
            if (!queryParams.isEmpty()) {
                responseContent.append("Query parameters:\n");
                queryParams.forEach((key, values) -> {
                    for (String value : values) {
                        responseContent.append(key).append(" = ").append(value).append("\n");
                    }
                });
            }

            StringBuilder bodyContent = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getBody()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    bodyContent.append(line);
                }

                responseContent.append("\nBody content:\n").append(bodyContent.toString());

                String responseText = responseContent.toString();
                responseStream.write("HTTP/1.1 200 OK\r\n".getBytes());
                responseStream.write("Content-Type: text/plain\r\n".getBytes());
                responseStream.write(("Content-Length: " + responseText.length() + "\r\n").getBytes());
                responseStream.write("\r\n".getBytes());
                responseStream.write(responseText.getBytes());
                responseStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        server.listen(9999);
    }
}


