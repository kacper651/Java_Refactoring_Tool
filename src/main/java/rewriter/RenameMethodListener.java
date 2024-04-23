package rewriter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

public class RenameMethodListener extends JavaParserBaseListener {
    String methodName;
    TokenStreamRewriter rewriter;

    public RenameMethodListener(CommonTokenStream tokens,
                                String methodName) {
        this.rewriter = new TokenStreamRewriter(tokens);
        this.methodName = methodName;
    }

    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        if (ctx.identifier().IDENTIFIER().getText().equals(methodName)) {
            rewriter.replace(ctx.identifier().IDENTIFIER().getSymbol(), "new_" + methodName);
        }
    }
}
