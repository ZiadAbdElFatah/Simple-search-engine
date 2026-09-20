package main;

import java.util.ArrayList;
import java.util.List;

public class QueryParser {
    private static final String TOKEN_REGEX = "[^a-zA-Z0-9'\"]+";

    public List<String> parse(String input) {
        List<String> tokens = new ArrayList<>();
        boolean inQuotes = false;
        for (String token : input.split(TOKEN_REGEX)) {
            switch (token) {
                case "AND" -> tokens.add("AND");
                case "OR" -> tokens.add("OR");
                case "NOT" -> tokens.add("NOT");
                default -> {
                    if (token.startsWith("\"") && token.endsWith("\"") && token.length() > 1) {
                        tokens.add(token.substring(1, token.length() - 1));
                        continue;
                    } else if (token.endsWith("\"")) {
                        tokens.add(token.substring(0, token.length() - 1));
                        inQuotes = false;
                        continue;
                    } else if (token.startsWith("\"")) {
                        tokens.add(token.substring(1));
                        inQuotes = true;
                        continue;
                    }
                    if (inQuotes) {
                        tokens.add(token);
                    } else {
                        tokens.add(token.toLowerCase());
                    }
                }
            }
            if (tokens.getLast().isEmpty()) {
                tokens.remove(token);
            }
        }
        return tokens;
    }
}