package test;

import main.QueryParser;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class QueryParserTest {

    private final QueryParser parser = new QueryParser();

    @Test
    public void singleTermIsLowercased() {
        Assert.assertEquals(parser.parse("Java"), List.of("java"));
    }

    @Test
    public void recognizesAndOperator() {
        Assert.assertEquals(parser.parse("machine AND learning"), List.of("machine", "AND", "learning"));
    }

    @Test
    public void recognizesOrOperator() {
        Assert.assertEquals(parser.parse("cat OR dog"), List.of("cat", "OR", "dog"));
    }

    @Test
    public void recognizesNotOperator() {
        Assert.assertEquals(parser.parse("java NOT beginner"), List.of("java", "NOT", "beginner"));
    }

    @Test
    public void lowercaseAndIsTreatedAsLiteralTerm() {
        Assert.assertEquals(parser.parse("bread and butter"), List.of("bread", "and", "butter"));
    }

    @Test
    public void singleWordQuotedPhrase() {
        Assert.assertEquals(parser.parse("\"hello\""), List.of("hello"));
    }

    @Test
    public void multiWordQuotedPhrasePreservesCaseInsideQuotes() {
        Assert.assertEquals(parser.parse("\"Machine Learning\""), List.of("Machine", "Learning"));
    }

    @Test
    public void quotedPhraseFollowedByOperatorAndTerm() {
        Assert.assertEquals(
                parser.parse("\"machine learning\" AND python"),
                List.of("machine", "learning", "AND", "python")
        );
    }

    @Test
    public void operatorWordInsideQuotesIsTreatedAsLiteralTerm() {
        Assert.assertEquals(
                parser.parse("\"cats AND dogs\""),
                List.of("cats", "AND", "dogs")
        );
    }

    @Test
    public void emptyInputProducesNoTokens() {
        Assert.assertEquals(parser.parse(""), List.of());
    }
}