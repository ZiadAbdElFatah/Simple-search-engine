package test;

import main.PdfTextExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PdfTextExtractorTest {

    private final PdfTextExtractor extractor = new PdfTextExtractor();

    private Path createTestPdf(String text) throws IOException {
        Path tempDirectory = Files.createTempDirectory("pdf-test");
        Path pdfFile = tempDirectory.resolve("test.pdf");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(25, 700);
                contentStream.showText(text);
                contentStream.endText();
            }

            document.save(pdfFile.toFile());
        }

        return pdfFile;
    }

    @Test
    public void supportsPdfExtension() throws IOException {
        Path pdfFile = createTestPdf("irrelevant");
        Assert.assertTrue(extractor.supports(pdfFile));
    }

    @Test
    public void doesNotSupportTxtExtension() {
        Path txtFile = Path.of("document.txt");
        Assert.assertFalse(extractor.supports(txtFile));
    }

    @Test
    public void supportsUppercaseExtension() {
        Path pdfFile = Path.of("Report.PDF");
        Assert.assertTrue(extractor.supports(pdfFile));
    }

    @Test
    public void extractsKnownText() throws IOException {
        Path pdfFile = createTestPdf("The aardvark searches at midnight");
        String extracted = extractor.extractText(pdfFile);
        Assert.assertTrue(extracted.contains("aardvark"));
    }
}
