package rewriter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

import java.util.HashMap;

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
                    rewriter.insertAfter(
                            ctx.formalParameterList().formalParameter(ctx.formalParameterList().formalParameter().size() - 1).stop,
                            ", " + parameterMap.get(key)
                    );
                }
            } else if (opType == OpType.REMOVE) {
                rewriter.replace(
                        ctx.formalParameterList().start,
                        ctx.formalParameterList().stop,
                        ""
                );
            } else if (opType == OpType.CHANGE) {
                for (String key : parameterMap.keySet()) {
                    for (int i = 0; i < ctx.formalParameterList().formalParameter().size(); i++) {
                        String paramName = ctx.formalParameterList()
                                .formalParameter(i)
                                .typeType()
                                .getText()
                                + " " + ctx.formalParameterList()
                                        .formalParameter(i)
                                        .variableDeclaratorId()
                                        .getText();
                        if (paramName.equals(key)) {
                            rewriter.replace(
                                    ctx.formalParameterList().formalParameter(i).start,
                                    ctx.formalParameterList().formalParameter(i).stop,
                                    parameterMap.get(key)
                            );
                        }
                    }
                }
            }
        }
    }

    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        interesting = ctx.identifier().IDENTIFIER().getText();
    }
}
