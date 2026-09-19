package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PlainTextExtractor implements TextExtractor {
    @Override
    public boolean supports(Path file) {
        return file.toString().endsWith(".txt");
    }

    @Override
    public String extractText(Path file) throws IOException {
        return Files.readString(file);
    }
}
