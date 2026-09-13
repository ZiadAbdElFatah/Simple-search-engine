package main;

import java.util.ArrayList;
import java.util.List;

/// Tokenizer class that provides a method to tokenize input strings into a list of tokens.
///
/// It uses a regular expression to split the input string into tokens, removing any non-alphanumeric characters (except for apostrophes) and converting all tokens to lowercase.
///
/// It also removes any empty tokens from the final list.
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
