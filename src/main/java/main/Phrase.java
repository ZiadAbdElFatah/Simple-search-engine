package main;

import java.util.List;

public record Phrase(List<String> words) implements QueryElement {}
