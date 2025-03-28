package org.wltea.analyzer.core;

import java.util.regex.Pattern;

public class SpecialCharacterSegmenter implements ISegmenter {
    @Override
    public void analyze(AnalyzeContext context) {
        if(context.getCurrentCharType() == CharacterUtil.CHAR_SYMBOL){
            Lexeme newLexeme = new Lexeme(context.getBufferOffset(),
                    context.getCursor(),
                    1,
                    Lexeme.TYPE_SYMBOL);
            context.addLexeme(newLexeme);
        }
    }

    @Override
    public void reset() {
        // Nothing to reset
    }
}