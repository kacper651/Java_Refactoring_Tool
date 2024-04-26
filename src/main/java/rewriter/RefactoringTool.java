package rewriter;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.xpath.XPath;

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
        // add verification so stupid user cant add '1' as a key


        HashMap<String, String> classMap = new HashMap<>();
        classMap.put("a", "new_a");
        classMap.put("b", "new_b");
        classMap.put("c", "new_c");
        classMap.put("d", "new_d");
        classMap.put("e", "new_e");
        classMap.put("f", "new_f");
        classMap.put("g", "new_g");
        classMap.put("h", "new_h");
        classMap.put("i", "new_i");
        classMap.put("j", "new_j");
        classMap.put("xyz", "new_xyz");

        RenameVariableListener renamer = new RenameVariableListener(variableMap,
                                                                    tokens,
                                                    "method1");
        RenameClassOrInterfaceListener renamer2 = new RenameClassOrInterfaceListener(classMap, tokens, RenameType.INTERFACE);
        walker.walk(renamer2,tree);

        XPath.findAll(tree, "//expression", parser).forEach(ctx -> {
            //System.out.println(ctx.getText());
        });

        System.out.println(renamer2.rewriter.getText());
        try {
            var writer = new FileWriter("Out.java");
            writer.write(renamer.rewriter.getText());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
