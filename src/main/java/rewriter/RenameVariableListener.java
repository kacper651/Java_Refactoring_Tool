package rewriter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

import java.util.HashMap;
import java.util.Map;

public class RenameVariableListener extends JavaParserBaseListener {
    public Map<String, String> variableMap;
    TokenStreamRewriter rewriter;
    String interesting = null;
    String methodName;

    public RenameVariableListener(HashMap<String, String> variableMap,
                                  CommonTokenStream tokens,
                                  String methodName) {
        this.variableMap = variableMap;
        this.rewriter = new TokenStreamRewriter(tokens);
        this.methodName = methodName;
    }

    // w jakiej funkcji jaka zmienna / xpath do szukania nazwy funkcji / trzeba przechowywac id funkcji
    // mozna tez to wywolac w drzewie wynikowym / parsetreepattern do wyszukiwania regul
    // (w dokumentacji jest opisane to obok siebie)

    @Override
    public void enterVariableDeclarator(JavaParser.VariableDeclaratorContext ctx) {
        String name = ctx.variableDeclaratorId().identifier().IDENTIFIER().getText();
        if (variableMap.containsKey(name)
                && interesting != null
                && interesting.equals(methodName)) {
            rewriter.replace(ctx.variableDeclaratorId().identifier().IDENTIFIER().getSymbol(), variableMap.get(name));
        }
    }

    @Override
    public void enterExpression(JavaParser.ExpressionContext ctx) { // zamienić na primary w expression
        if (ctx.start == null) return;
        var variableName = ctx.start.getText();
        if (variableMap.containsKey(variableName)
                && interesting != null
                && interesting.equals(methodName)) {
            rewriter.replace(ctx.start, variableMap.get(variableName));
        }
    }

    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        interesting = ctx.identifier().IDENTIFIER().getText();
    }
}
