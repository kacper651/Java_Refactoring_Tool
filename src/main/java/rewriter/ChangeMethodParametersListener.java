package rewriter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStreamRewriter;

import java.util.HashMap;

enum OpType {
    ADD,
    REMOVE,
    CHANGE
}

public class ChangeMethodParametersListener extends JavaParserBaseListener {
    HashMap<String, String> parameterMap;
    TokenStreamRewriter rewriter;
    String interesting = null;
    String methodName;
    OpType opType;

    public ChangeMethodParametersListener(HashMap<String, String> parameterMap,
                                          CommonTokenStream tokens,
                                          String methodName,
                                          OpType opType) {
        this.parameterMap = parameterMap;
        this.rewriter = new TokenStreamRewriter(tokens);
        this.methodName = methodName;
        this.opType = opType;
    }

    @Override
    public void enterFormalParameters(JavaParser.FormalParametersContext ctx) {
        if (interesting != null && interesting.equals(methodName)) {
            if (opType == OpType.ADD) {
                for (String key : parameterMap.keySet()) {
                    rewriter.insertAfter(ctx.LPAREN().getSymbol(), parameterMap.get(key) + ", ");
                }
            } else if (opType == OpType.REMOVE) {
                rewriter.replace(ctx.formalParameterList().start, ctx.formalParameterList().stop, "");
            } else if (opType == OpType.CHANGE) {
                for (String key : parameterMap.keySet()) {
                    rewriter.replace(ctx.start, ctx.stop, parameterMap.get(key));
                }
            }
        }
    }

    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        interesting = ctx.identifier().IDENTIFIER().getText();
    }
}
