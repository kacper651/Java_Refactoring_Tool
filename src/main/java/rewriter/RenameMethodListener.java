package rewriter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

public class RenameMethodListener {
    private final CommonTokenStream tokens;
    private final String methodName;
    TokenStreamRewriter rewriter;

    public RenameMethodListener(CommonTokenStream tokens, String methodName) {
        this.tokens = tokens;
        this.rewriter = new TokenStreamRewriter(tokens);
        this.methodName = methodName;
    }
}
