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

    @Test
    public void unsupportedFileTypeIsSkipped() throws IOException {
        final SearchEngine searchEngine = new SearchEngine();
        Path tempDirectory = Files.createTempDirectory("test");

        Files.writeString(tempDirectory.resolve("notes.txt"), "this is a searchable document");
        Files.writeString(tempDirectory.resolve("image.jpg"), "not real jpg bytes, irrelevant for this test");

        searchEngine.indexDirectory(tempDirectory);
        List<Document> actualResults = searchEngine.search("searchable");

        List<String> actualFileNames = actualResults.stream().map(Document::getFileName).toList();
        Assert.assertEquals(actualFileNames, List.of("notes.txt"));
    }

    @Test
    public void unsupportedFileDoesNotCorruptSubsequentDocIds() throws IOException {
        final SearchEngine searchEngine = new SearchEngine();
        Path tempDirectory = Files.createTempDirectory("test");

        Files.writeString(tempDirectory.resolve("image.jpg"), "irrelevant bytes");
        Files.writeString(tempDirectory.resolve("first.txt"), "apple");
        Files.writeString(tempDirectory.resolve("second.txt"), "banana");

        searchEngine.indexDirectory(tempDirectory);

        List<String> appleResults = searchEngine.search("apple").stream().map(Document::getFileName).toList();
        List<String> bananaResults = searchEngine.search("banana").stream().map(Document::getFileName).toList();

        Assert.assertEquals(appleResults, List.of("first.txt"));
        Assert.assertEquals(bananaResults, List.of("second.txt"));
    }
}
