package ru.netology;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        final List<String> validPaths = List.of("/index.html",
                "/spring.svg", "/spring.png", "/resources.html", "/styles.css",
                "/app.js", "/links.html", "/forms.html", "/classic.html",
                "/events.html", "/events.js"
        );
        final var server = new Server(validPaths,9999);
        server.start();
    }
}


