package main;

import java.util.ArrayList;
import java.util.List;

public class QueryParser {
    private static final String SPLIT_REGEX = "[^a-zA-Z0-9'\"]+";

    public List<QueryElement> parse(String input) {
        List<QueryElement> elements = new ArrayList<>();
        List<String> phraseWords = new ArrayList<>();
        boolean inQuotes = false;

        for (String raw : input.split(SPLIT_REGEX)) {
            if (raw.isEmpty()) {
                continue;
            }

            boolean opens = !inQuotes && raw.startsWith("\"");
            String word;
            if (opens) {
                word = raw.substring(1);
            } else {
                word = raw;
            }
            boolean closes = word.endsWith("\"");
            if (closes) {
                word = word.substring(0, word.length() - 1);
            }

            if (opens) {
                inQuotes = true;
            }

            if (inQuotes) {
                String normalized = normalize(word);
                if (!normalized.isEmpty()) {
                    phraseWords.add(normalized);
                }
                if (closes) {
                    flushPhrase(phraseWords, elements);
                    inQuotes = false;
                }
            } else {
                switch (word) {
                    case "AND" -> elements.add(new Operator(OperatorType.AND));
                    case "OR" -> elements.add(new Operator(OperatorType.OR));
                    case "NOT" -> elements.add(new Operator(OperatorType.NOT));
                    default -> {
                        String normalized = normalize(word);
                        if (!normalized.isEmpty()) {
                            elements.add(new Term(normalized));
                        }
                    }
                }
            }
        }

        if (inQuotes) {
            flushPhrase(phraseWords, elements);
        }
        return elements;
    }

    private String normalize(String word) {
        return word.toLowerCase().replace("'", "");
    }

    private void flushPhrase(List<String> words, List<QueryElement> elements) {
        if (words.size() == 1) {
            elements.add(new Term(words.getFirst()));
        } else if (words.size() > 1) {
            elements.add(new Phrase(List.copyOf(words)));
        }
        words.clear();
    }
}
