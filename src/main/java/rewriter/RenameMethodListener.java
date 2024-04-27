package rewriter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

import java.util.HashMap;

public class RenameMethodListener extends JavaParserBaseListener {
    HashMap<String, String> methodNameMap;
    TokenStreamRewriter rewriter;

    public RenameMethodListener(HashMap<String, String> methodNameMap,
                                CommonTokenStream tokens) {
        this.rewriter = new TokenStreamRewriter(tokens);
        this.methodNameMap = methodNameMap;
    }

    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        String methodName = ctx.identifier().IDENTIFIER().getText();
        if (methodNameMap.containsKey(methodName)) {
            rewriter.replace(
                    ctx.identifier().IDENTIFIER().getSymbol(),
                    methodNameMap.get(methodName)
            );
        }
    }

    @Override
    public void enterMethodCall(JavaParser.MethodCallContext ctx) {
        String methodName = ctx.identifier().IDENTIFIER().getText();
        if (methodNameMap.containsKey(methodName)) {
            rewriter.replace(
                    ctx.identifier().IDENTIFIER().getSymbol(),
                    methodNameMap.get(methodName)
            );
        }
    }
}

