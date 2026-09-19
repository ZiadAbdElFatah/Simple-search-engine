package test;

import main.Document;
import main.SearchEngine;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SearchEngineTest {
    @Test
    public void testSearch() throws IOException {
        final SearchEngine searchEngine = new SearchEngine();
        Path tempDirectory = Files.createTempDirectory("test");

        Path file1 = tempDirectory.resolve("file1.txt");
        Files.writeString(file1, "This is the content of file 1.");

        Path file2 = tempDirectory.resolve("file2.txt");
        Files.writeString(file2, "This is the content of file 2.");

        searchEngine.indexDirectory(tempDirectory);
        List<Document> actualResults = searchEngine.search("file 1");

        List<String> actualFileNames = actualResults.stream().map(Document::toString).toList();
        Assert.assertEquals(actualFileNames, List.of("file1.txt", "file2.txt"));
    }
}
