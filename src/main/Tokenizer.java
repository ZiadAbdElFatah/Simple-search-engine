package main;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    private static final String TOKEN_REGEX = "[^a-z0-9']+";

    public List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        for (String token : input.toLowerCase().split(TOKEN_REGEX)) {
            token = token.replace("'", "");
            if (!token.isEmpty()) {
                tokens.add(token);
            }
        }
        return tokens;
    }
}
