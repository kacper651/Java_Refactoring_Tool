package rewriter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

import java.util.HashMap;
import java.util.Map;

public class RenameVariableListener extends JavaParserBaseListener {
    public Map<String, String> variableMap = new HashMap<>();
    private final CommonTokenStream tokens;
    TokenStreamRewriter rewriter;
    String interesting = null;
    String jakaMetodaWariacie;

    public RenameVariableListener(HashMap<String, String> variableMap, CommonTokenStream tokens, String jakaMetodaWariacie) {
        this.variableMap = variableMap;
        this.tokens = tokens;
        this.rewriter = new TokenStreamRewriter(tokens);
        this.jakaMetodaWariacie = jakaMetodaWariacie;
    }

    // w jakiej funkcji jaka zmienna / xpath do szukania nazwy funkcji / trzeba przechowywac id funkcji
    // mozna tez to wywolac w drzewie wynikowym / parsetreepattern do wyszukiwania regul
    // (w dokumentacji jest opisane to obok siebie)

    @Override
    public void enterVariableDeclaratorId(JavaParser.VariableDeclaratorIdContext ctx) {
        String name = ctx.identifier().IDENTIFIER().getText();
        if (variableMap.containsKey(name)
                && interesting != null
                && interesting.equals(jakaMetodaWariacie)) {
            rewriter.replace(ctx.identifier().IDENTIFIER().getSymbol(), variableMap.get(name));
        }
    }

    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        interesting = ctx.identifier().IDENTIFIER().getText();
    }
}
