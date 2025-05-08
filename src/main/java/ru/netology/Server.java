package ru.netology;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int TREAD_POOL_SIZE = 64;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(TREAD_POOL_SIZE);
    private final Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();

    public void addHandler(String method, String path, Handler handler) {
        handlers
                .computeIfAbsent(method, key -> new HashMap<>())
                .put(path, handler);
    }

    public void listen(int port) {
        try (final var serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен, порт: " + port);
            while (true) {
                final var socket = serverSocket.accept();
                threadPool.submit(() -> handleConnection(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleConnection(Socket socket) {
        try (
                socket;
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {
            final var requestLine = in.readLine();
            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            final var parts = requestLine.split(" ");
            if (parts.length != 3) {
                return;
            }

            final var method = parts[0];
            final var path = parts[1];

            Map<String, String> headers = new ConcurrentHashMap<>();
            String line;
            while (!(line = in.readLine()).equals("")) {
                final var separatorIndex = line.indexOf(": ");
                if (separatorIndex != -1) {
                    final var headerName = line.substring(0, separatorIndex);
                    final var headerValue = line.substring(separatorIndex + 2);
                    headers.put(headerName, headerValue);
                }
            }

            InputStream body = null;
            if ("POST".equalsIgnoreCase(method)) {
                int contentLength = 0;
                contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));

                char[] bodyChars = new char[contentLength];

                if (contentLength > 0) {
                    in.read(bodyChars);
                }
                String bodyText = new String(bodyChars);
                body = new ByteArrayInputStream(bodyText.getBytes(StandardCharsets.UTF_8));
            }

            Request request = new Request(method, path, headers, body);

            Handler handler = handlers.getOrDefault(method, new ConcurrentHashMap<>()).get(path);

            if (handler != null) {
                handler.handle(request, out);
            } else {
                out.write(("HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n").getBytes());
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
