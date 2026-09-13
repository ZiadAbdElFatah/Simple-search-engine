package test;

import main.Tokenizer;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class TokenizerTest {
    private final Tokenizer tokenizer = new Tokenizer();

    @Test
    public void normalTest() {
        String input = "Hello, World! This is a test.";
        List<String> expectedTokens = List.of("hello", "world", "this", "is", "a", "test");
        List<String> actualTokens = tokenizer.tokenize(input);
        Assert.assertEquals(actualTokens, expectedTokens);
    }

    @Test
    public void emptyStringTest() {
        String input = "";
        List<String> actualTokens = tokenizer.tokenize(input);
        Assert.assertTrue(actualTokens.isEmpty(), "Tokens should be empty for an empty string");
    }

    @Test
    public void apostropheTest() {
        String input = "don't stop";
        List<String> expectedTokens = List.of("dont", "stop");
        List<String> actualTokens = tokenizer.tokenize(input);
        Assert.assertEquals(actualTokens, expectedTokens);
    }

    @Test
    public void hyphensTest() {
        String input = "state-of-the-art";
        List<String> expectedTokens = List.of("state", "of", "the", "art");
        List<String> actualTokens = tokenizer.tokenize(input);
        Assert.assertEquals(actualTokens, expectedTokens);
    }

    @Test
    public void spacesTest() {
        String input = "  Leading spaces  ";
        List<String> expectedTokens = List.of("leading", "spaces");
        List<String> actualTokens = tokenizer.tokenize(input);
        Assert.assertEquals(actualTokens, expectedTokens);
    }

    @Test
    public void lonelyApostropheTest() {
        String input = "' hello '' world";
        List<String> expectedTokens = List.of("hello", "world");
        List<String> actualTokens = tokenizer.tokenize(input);
        Assert.assertEquals(actualTokens, expectedTokens);
    }
}
