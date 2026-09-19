package main;

import java.io.IOException;
import java.nio.file.Path;

public interface TextExtractor {
    boolean supports(Path file);
    String extractText(Path file) throws IOException;
}
