package rewriter;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileWriter;
import java.io.IOException;

public class RefactoringTool {

    public static void main(String[] args) {

        // read input file
        CharStream input = null;
        try {
            input = CharStreams.fromFileName("src/main/java/rewriter/InsertDocListener.java");
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
        InsertDocListener inserter = new InsertDocListener(tokens);
        walker.walk(inserter,tree);
        System.out.println(inserter.rewriter.getText());
        try {
            var writer = new FileWriter("Out.java");
            writer.write(inserter.rewriter.getText());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
