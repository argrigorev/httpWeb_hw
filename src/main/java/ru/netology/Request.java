package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.InputStream;
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
    private final Map<String, List<String>> queryParams = new HashMap<>();

    public Request(String method, String fullPath, Map<String, String> headers, InputStream body) {
        this.method = method;
        this.headers = headers;
        this.body = body;

        int queryStart = fullPath.indexOf('?');
        if(queryStart != -1) {
            this.path = fullPath.substring(0, queryStart);
            String query = fullPath.substring(queryStart + 1);
            List<NameValuePair> pairs = URLEncodedUtils.parse(query, StandardCharsets.UTF_8);

            for(NameValuePair pair : pairs) {
                queryParams
                        .computeIfAbsent(pair.getName(), k -> new ArrayList<>())
                        .add(pair.getValue());

            }
        } else {
            this.path = fullPath;
        }

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

    public String getQueryParam(String name) {
        List<String> values = queryParams.get(name);
        return values == null || values.isEmpty() ? null : values.get(0);
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }


}

