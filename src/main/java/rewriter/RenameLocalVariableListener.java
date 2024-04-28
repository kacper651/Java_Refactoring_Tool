package rewriter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

import java.util.HashMap;
import java.util.Map;

public class RenameLocalVariableListener extends JavaParserBaseListener {
    public Map<String, String> variableMap = new HashMap<>();
    private final CommonTokenStream tokens;
    TokenStreamRewriter rewriter;
    String interesting = null;
    String methodScope;

    public RenameLocalVariableListener(HashMap<String, String> variableMap, CommonTokenStream tokens, String jakaMetodaWariacie) {
        this.variableMap = variableMap;
        this.tokens = tokens;
        this.rewriter = new TokenStreamRewriter(tokens);
        this.methodScope = jakaMetodaWariacie;
    }

    @Override
    public void enterVariableDeclarator(JavaParser.VariableDeclaratorContext ctx) {
        String name = ctx.variableDeclaratorId().identifier().IDENTIFIER().getText();
        if (variableMap.containsKey(name)
                && interesting != null
                && interesting.equals(methodScope)) {
            rewriter.replace(ctx.variableDeclaratorId().identifier().IDENTIFIER().getSymbol(), variableMap.get(name));
        }
    }

    @Override
    public void enterExpression(JavaParser.ExpressionContext ctx) {
        if (ctx.start == null) return;
        var variableName = ctx.start.getText();
        System.out.println(variableName);
        if (variableMap.containsKey(variableName)
                && interesting != null
                && interesting.equals(methodScope)) {
            rewriter.replace(ctx.start, variableMap.get(variableName));
        }
    }

    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        interesting = ctx.identifier().IDENTIFIER().getText();
    }
}
