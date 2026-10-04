package test;

import main.Document;
import main.SearchEngine;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class BooleanSearchTest {
    private SearchEngine engine;

    @BeforeClass
    public void setUp() throws IOException {
        Path dir = Files.createTempDirectory("boolean-test");
        Files.writeString(dir.resolve("d1.txt"), "java python spring");
        Files.writeString(dir.resolve("d2.txt"), "java only");
        Files.writeString(dir.resolve("d3.txt"), "python only");
        Files.writeString(dir.resolve("d4.txt"), "the quick brown fox");
        Files.writeString(dir.resolve("d5.txt"), "machine learning is fun");
        Files.writeString(dir.resolve("d6.txt"), "learning machine is hard");
        engine = new SearchEngine();
        engine.indexDirectory(dir);
    }

    private Set<String> names(String query) {
        return engine.search(query).stream().map(Document::getFileName).collect(java.util.stream.Collectors.toSet());
    }

    @Test
    public void andIntersects() {
        Assert.assertEquals(names("java AND python"), Set.of("d1.txt"));
    }

    @Test
    public void orUnions() {
        Assert.assertEquals(names("java OR python"), Set.of("d1.txt", "d2.txt", "d3.txt"));
    }

    @Test
    public void notSubtracts() {
        Assert.assertEquals(names("java NOT python"), Set.of("d2.txt"));
    }

    @Test
    public void leadingNotComplementsAgainstAllDocuments() {
        Assert.assertEquals(names("NOT java"), Set.of("d3.txt", "d4.txt", "d5.txt", "d6.txt"));
    }

    @Test
    public void precedenceDiffersFromLeftToRight() {
        // standard: fox OR (java AND python) = {d4, d1}. Left-to-right would give only {d1}.
        Assert.assertEquals(names("fox OR java AND python"), Set.of("d1.txt", "d4.txt"));
    }

    @Test
    public void phraseRequiresAdjacencyAndOrder() {
        Assert.assertEquals(names("\"machine learning\""), Set.of("d5.txt"));
        Assert.assertEquals(names("\"learning machine\""), Set.of("d6.txt"));
        Assert.assertEquals(names("machine AND learning"), Set.of("d5.txt", "d6.txt"));
    }

    @Test
    public void phraseWorksWithStopwords() {
        Assert.assertEquals(names("\"the quick brown\""), Set.of("d4.txt"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void malformedQueryThrows() {
        engine.search("java AND");
    }
}