package test;

import main.PlainTextExtractor;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PlainTextExtractorTest {

    private final PlainTextExtractor extractor = new PlainTextExtractor();

    @Test
    public void supportsTxtExtension() {
        Path txtFile = Path.of("notes.txt");
        Assert.assertTrue(extractor.supports(txtFile));
    }

    @Test
    public void doesNotSupportPdfExtension() {
        Path pdfFile = Path.of("report.pdf");
        Assert.assertFalse(extractor.supports(pdfFile));
    }

    @Test
    public void extractsFileContent() throws IOException {
        Path tempDirectory = Files.createTempDirectory("txt-test");
        Path txtFile = tempDirectory.resolve("notes.txt");
        Files.writeString(txtFile, "hello world");

        String extracted = extractor.extractText(txtFile);
        Assert.assertEquals(extracted, "hello world");
    }
}
