package test;

import main.*;
import main.QueryNode.*;
import org.testng.Assert;
import org.testng.annotations.Test;

public class QueryTreeBuilderTest {
    private final QueryParser parser = new QueryParser();

    private QueryNode build(String query) {
        return new QueryTreeBuilder().build(parser.parse(query));
    }

    @Test
    public void andBindsTighterThanOr() {
        Assert.assertEquals(build("a AND b OR c"),
                new OrNode(new AndNode(new TermNode("a"), new TermNode("b")), new TermNode("c")));
        Assert.assertEquals(build("a OR b AND c"),
                new OrNode(new TermNode("a"), new AndNode(new TermNode("b"), new TermNode("c"))));
    }

    @Test
    public void binaryNotMeansAndNot() {
        Assert.assertEquals(build("a NOT b"),
                new AndNode(new TermNode("a"), new NotNode(new TermNode("b"))));
    }

    @Test
    public void leadingNot() {
        Assert.assertEquals(build("NOT a"), new NotNode(new TermNode("a")));
    }

    @Test
    public void adjacentTermsAreImplicitOr() {
        Assert.assertEquals(build("a b"), new OrNode(new TermNode("a"), new TermNode("b")));
    }

    @Test
    public void phraseBecomesPhraseNode() {
        Assert.assertEquals(build("\"machine learning\""),
                new PhraseNode(java.util.List.of("machine", "learning")));
    }

    @Test
    public void emptyQueryGivesNull() {
        Assert.assertNull(build(""));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void trailingOperatorIsRejected() {
        build("a AND");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void leadingAndIsRejected() {
        build("AND a");
    }
}