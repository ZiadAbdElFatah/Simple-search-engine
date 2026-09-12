package test;

import main.Index;
import main.Posting;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class IndexTest {
    private Index index;

    @BeforeMethod
    public void setUp() {
        index = new Index();
    }

    @Test
    public void sameTermSameDocTest() {
        index.addTerm("java", 2, 0);
        index.addTerm("java", 2, 1);
        List<Posting> javaPostings = index.getPostings("java");
        Assert.assertEquals(javaPostings.size(), 1);
        Assert.assertEquals(javaPostings.getFirst().getDocId(), 2);
        Assert.assertEquals(javaPostings.getFirst().getPositions(), List.of(0, 1));
        Assert.assertEquals(javaPostings.getFirst().getTermFrequency(), 2);
    }

    @Test
    public void sameTermMultipleDocsTest() {
        index.addTerm("java", 1, 0);
        index.addTerm("java", 2, 1);
        List<Posting> javaPostings = index.getPostings("java");
        Assert.assertEquals(javaPostings.size(), 2);
        Assert.assertEquals(javaPostings.getFirst().getDocId(), 1);
        Assert.assertEquals(javaPostings.getFirst().getPositions(), List.of(0));
        Assert.assertEquals(javaPostings.getFirst().getTermFrequency(), 1);
        Assert.assertEquals(javaPostings.getLast().getDocId(), 2);
        Assert.assertEquals(javaPostings.getLast().getPositions(), List.of(1));
        Assert.assertEquals(javaPostings.getLast().getTermFrequency(), 1);
    }

    @Test
    public void differentTermsTest() {
        index.addTerm("java", 1, 0);
        index.addTerm("python", 1, 1);
        Assert.assertEquals(index.getPostings("java").size(), 1);
        Assert.assertEquals(index.getPostings("python").size(), 1);
    }

    @Test
    public void queryingAbsentTerms() {
        Assert.assertEquals(index.getPostings("absent").size(), 0);
    }
}
