package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final InputStream body;
    private final Map<String, List<String>> postParams = new HashMap<>();

    public Request(String method, String path, Map<String, String> headers, InputStream body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
        parsePostParams();
    }

    private void parsePostParams() {
        if (!"POST".equalsIgnoreCase(method)) return;
        if (body == null) return;

        String contentType = headers.getOrDefault("Content-Type", "");
        if (!contentType.equalsIgnoreCase("application/x-www-form-urlencoded")) return;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(body, StandardCharsets.UTF_8))) {
            StringBuilder bodyText = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                bodyText.append(line);
            }

            List<NameValuePair> parsed = URLEncodedUtils.parse(bodyText.toString(), StandardCharsets.UTF_8);
            for (NameValuePair pair : parsed) {
                postParams
                        .computeIfAbsent(pair.getName(), k -> new ArrayList<>())
                        .add(pair.getValue());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPostParam(String name) {
        List<String> values = postParams.get(name);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    public Map<String, List<String>> getPostParams() {
        return postParams;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public InputStream getBody() {
        return body;
    }
}

