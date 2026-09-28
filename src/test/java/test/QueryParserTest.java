package test;

import main.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class QueryParserTest {
    private final QueryParser parser = new QueryParser();

    @Test
    public void singleTermIsLowercased() {
        Assert.assertEquals(parser.parse("Java"), List.of(new Term("java")));
    }

    @Test
    public void recognizesOperators() {
        Assert.assertEquals(parser.parse("machine AND learning"),
                List.of(new Term("machine"), new Operator(OperatorType.AND), new Term("learning")));
        Assert.assertEquals(parser.parse("cat OR dog"),
                List.of(new Term("cat"), new Operator(OperatorType.OR), new Term("dog")));
        Assert.assertEquals(parser.parse("java NOT beginner"),
                List.of(new Term("java"), new Operator(OperatorType.NOT), new Term("beginner")));
    }

    @Test
    public void lowercaseAndIsLiteralTerm() {
        Assert.assertEquals(parser.parse("bread and butter"),
                List.of(new Term("bread"), new Term("and"), new Term("butter")));
    }

    @Test
    public void apostropheIsStripped() {
        Assert.assertEquals(parser.parse("don't stop"),
                List.of(new Term("dont"), new Term("stop")));
    }

    @Test
    public void oneWordQuotedPhraseBecomesTerm() {
        Assert.assertEquals(parser.parse("\"hello\""), List.of(new Term("hello")));
    }

    @Test
    public void multiWordPhraseIsNormalized() {
        Assert.assertEquals(parser.parse("\"Machine Learning\""),
                List.of(new Phrase(List.of("machine", "learning"))));
    }

    @Test
    public void operatorInsideQuotesIsLiteral() {
        Assert.assertEquals(parser.parse("\"cats AND dogs\""),
                List.of(new Phrase(List.of("cats", "and", "dogs"))));
    }

    @Test
    public void phraseMixedWithOperatorAndTerm() {
        Assert.assertEquals(parser.parse("\"machine learning\" AND python"),
                List.of(new Phrase(List.of("machine", "learning")),
                        new Operator(OperatorType.AND),
                        new Term("python")));
    }

    @Test
    public void unclosedQuoteStillProducesPhrase() {
        Assert.assertEquals(parser.parse("\"machine learning"),
                List.of(new Phrase(List.of("machine", "learning"))));
    }

    @Test
    public void loneQuotesAndEmptyInputProduceNothing() {
        Assert.assertEquals(parser.parse("\" \""), List.of());
        Assert.assertEquals(parser.parse(""), List.of());
    }
}