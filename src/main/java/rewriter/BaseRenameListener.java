package rewriter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

public class BaseRenameListener extends JavaParserBaseListener {

    protected final TokenStreamRewriter rewriter;

    public BaseRenameListener(CommonTokenStream tokens) {
        this.rewriter = new TokenStreamRewriter(tokens);
    }
}
