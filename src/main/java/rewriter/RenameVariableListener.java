package rewriter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

import java.util.HashMap;
import java.util.Map;

public class RenameVariableListener extends JavaParserBaseListener {
    private Map<String, String> variableMap = new HashMap<>();
    private final CommonTokenStream tokenStream;
    TokenStreamRewriter rewriter;

    public RenameVariableListener(CommonTokenStream tokens) {
        this.tokenStream = tokens;
        rewriter = new TokenStreamRewriter(tokens);
    }

    // w jakiej funkcji jaka zmienna / xpath do szukania nazwy funkcji / trzeba przechowywac id funkcji
    // mozna tez to wywolac w drzewie wynikowym / parsetreepattern do wyszukiwania regul
    // (w dokumentacji jest opisane to obok siebie)

    @Override
    public void enterVariableDeclaratorId(JavaParser.VariableDeclaratorIdContext ctx) {
        String oldName = ctx.identifier().IDENTIFIER().getText();
        String newName = "new_" + oldName;
        variableMap.put(oldName, newName);
        rewriter.replace(ctx.identifier().IDENTIFIER().getSymbol(), newName);
    }
}
