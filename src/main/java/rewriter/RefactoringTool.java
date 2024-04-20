package rewriter;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RefactoringTool {

    public static void main(String[] args) {

        // read input file
        CharStream input = null;
        try {
            input = CharStreams.fromFileName("src/main/java/input/Input.java");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // create lexer, parser, and parse tree
        JavaLexer lexer = new JavaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);

        // create walker and listener
        ParseTree tree = parser.compilationUnit();
        ParseTreeWalker walker = new ParseTreeWalker();

        // rename variables map
        HashMap<String, String> variableMap = new HashMap<>();
        variableMap.put("a", "new_a");
        variableMap.put("b", "new_b");
        variableMap.put("c", "new_c");

        RenameVariableListener renamer = new RenameVariableListener(variableMap, tokens, "method1");
        walker.walk(renamer,tree);

        System.out.println(renamer.rewriter.getText());
        try {
            var writer = new FileWriter("Out.java");
            writer.write(renamer.rewriter.getText());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
