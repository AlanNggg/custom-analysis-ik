package org.wltea.analyzer.lucene;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.junit.Test;
import org.wltea.analyzer.TestUtils;
import org.wltea.analyzer.cfg.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class IKAnalyzerSpecialCharTests {

    @Test
    public void tokenizeSpecialChars_correctly() {
        Configuration cfg = TestUtils.createFakeConfigurationSub(false);
        String[] values = tokenize(cfg, "@#$%");
        assert values.length == 4;
        assert values[0].equals("@");
        assert values[1].equals("#");
        assert values[2].equals("$");
        assert values[3].equals("%");
    }

    @Test
    public void verifySpecialCharTypes_correctly() {
        Configuration cfg = TestUtils.createFakeConfigurationSub(false);
        String text = "@#$%";
        try (IKAnalyzer analyzer = new IKAnalyzer(cfg)) {
            TokenStream ts = analyzer.tokenStream("text", text);
            ts.reset();

            CharTermAttribute termAttr = ts.getAttribute(CharTermAttribute.class);
            TypeAttribute typeAttr = ts.getAttribute(TypeAttribute.class);

            while(ts.incrementToken()) {
                assert typeAttr.type().equals("SYMBOL");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 连续特殊字符测试
     */
    @Test
    public void tokenizeConsecutiveSpecialChars_correctly() {
        Configuration cfg = TestUtils.createFakeConfigurationSub(false);
        String[] values = tokenize(cfg, "@#$%");
        assert values.length == 4;
        assert values[0].equals("@");
        assert values[1].equals("#");
        assert values[2].equals("$");
        assert values[3].equals("%");
    }

    /**
     * 标点符号测试
     */
    @Test
    public void tokenizePunctuation_correctly() {
        Configuration cfg = TestUtils.createFakeConfigurationSub(false);
        String[] values = tokenize(cfg, "中文，测试。English!Test?");
        assert values.length == 8;
        assert values[0].equals("中文");
        assert values[1].equals(",");
        assert values[2].equals("测试");
        assert values[3].equals("。");
        assert values[4].equals("english");
        assert values[5].equals("!");
        assert values[6].equals("test");
        assert values[7].equals("?");
    }

    /**
     * 特殊字符类型测试
     */
    @Test
    public void tokenizeSpecialCharsType_correctly() {
        Configuration cfg = TestUtils.createFakeConfigurationSub(false);
        TokenInfo[] tokens = tokenizeWithType(cfg, "@#$%");
        assert tokens.length == 4;
        for (TokenInfo token : tokens) {
            assert token.type.equals("SYMBOL");
        }
    }

    // Helper class to store token info
    static class TokenInfo {
        String text;
        String type;

        TokenInfo(String text, String type) {
            this.text = text;
            this.type = type;
        }
    }

    // Modified tokenize method to include type information
    static TokenInfo[] tokenizeWithType(Configuration configuration, String s) {
        ArrayList<TokenInfo> tokens = new ArrayList<>();
        try (IKAnalyzer ikAnalyzer = new IKAnalyzer(configuration)) {
            TokenStream tokenStream = ikAnalyzer.tokenStream("text", s);
            tokenStream.reset();

            CharTermAttribute termAttr = tokenStream.getAttribute(CharTermAttribute.class);
            OffsetAttribute offsetAttr = tokenStream.getAttribute(OffsetAttribute.class);
            TypeAttribute typeAttr = tokenStream.getAttribute(TypeAttribute.class);

            while(tokenStream.incrementToken()) {
                int len = offsetAttr.endOffset() - offsetAttr.startOffset();
                char[] chars = new char[len];
                System.arraycopy(termAttr.buffer(), 0, chars, 0, len);
                tokens.add(new TokenInfo(
                        new String(chars),
                        typeAttr.type()
                ));
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return tokens.toArray(new TokenInfo[0]);
    }

    // Original tokenize method
    static String[] tokenize(Configuration configuration, String s) {
        ArrayList<String> tokens = new ArrayList<>();
        try (IKAnalyzer ikAnalyzer = new IKAnalyzer(configuration)) {
            TokenStream tokenStream = ikAnalyzer.tokenStream("text", s);
            tokenStream.reset();

            while(tokenStream.incrementToken()) {
                CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
                OffsetAttribute offsetAttribute = tokenStream.getAttribute(OffsetAttribute.class);
                int len = offsetAttribute.endOffset()-offsetAttribute.startOffset();
                char[] chars = new char[len];
                System.arraycopy(charTermAttribute.buffer(), 0, chars, 0, len);
                tokens.add(new String(chars));
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return tokens.toArray(new String[0]);
    }
}