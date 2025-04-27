package ru.netology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
            StringBuilder bodyContent = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getBody()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    bodyContent.append(line);
                }

                String responseContent = "Received POST data: " + bodyContent.toString();
                responseStream.write("HTTP/1.1 200 OK\r\n".getBytes());
                responseStream.write("Content-Type: text/plain\r\n".getBytes());
                responseStream.write("Content-Length: ".getBytes());
                responseStream.write(String.valueOf(responseContent.length()).getBytes());
                responseStream.write("\r\n\r\n".getBytes());
                responseStream.write(responseContent.getBytes());
                responseStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        server.listen(9999);
    }
}


