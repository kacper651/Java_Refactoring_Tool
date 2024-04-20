package rewriter;

import org.antlr.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

import java.util.HashMap;
import java.util.Map;

public class RenameVariableListener extends JavaParserBaseListener {
    private Map<String, String> variableMap = new HashMap<>();
    private final CommonTokenStream tokenStream;
    TokenStreamRewriter rewriter;

    // w jakiej funkcji jaka zmienna / xpath do szukania nazwy funkcji / trzeba przechowywac id funkcji
    // mozna tez to wywolac w drzewie wynikowym / parsetreepattern do wyszukiwania regul
    // (w dokumentacji jest opisane to obok siebie)


    @Override
    public void exitVariableDeclaratorId(JavaParser.VariableDeclaratorIdContext ctx) {

    }

    @Override
    public void exitIdentifier(JavaParser.IdentifierContext ctx) {

    }
}

