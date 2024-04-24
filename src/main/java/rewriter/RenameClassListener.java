package rewriter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

import java.util.HashMap;
import java.util.Map;

public class RenameClassListener extends JavaParserBaseListener{
    public Map<String, String> classMap = new HashMap<>();
    private final CommonTokenStream tokens;
    TokenStreamRewriter rewriter;
    String interesting = null;
//    String scope;

    public RenameClassListener(HashMap<String, String> classMap, CommonTokenStream tokens) {
        this.classMap = classMap;
        this.tokens = tokens;
        this.rewriter = new TokenStreamRewriter(tokens);
    }

    //  basic case
    @Override public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        String name = ctx.identifier().IDENTIFIER().getText();
        if (classMap.containsKey(name)){
            rewriter.replace(ctx.identifier().IDENTIFIER().getSymbol(), classMap.get(name));
        }
    }

    // extends / implements type shit
    @Override
    public void enterTypeIdentifier(JavaParser.TypeIdentifierContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        if (classMap.containsKey(name)){
            rewriter.replace(ctx.IDENTIFIER().getSymbol(), classMap.get(name));
        }
    }

    @Override
    public void enterExpression(JavaParser.ExpressionContext ctx) {
        if (ctx.start == null) return;
        var className = ctx.start.getText();
        System.out.println(className);
        if (classMap.containsKey(className)
                && interesting != null) {
            rewriter.replace(ctx.start, classMap.get(className));
        }
    }

}
