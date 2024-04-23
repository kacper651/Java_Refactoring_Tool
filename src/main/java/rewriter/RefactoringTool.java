package rewriter;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.xpath.XPath;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

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
        HashMap<String, String> methodMap = new HashMap<>();

        String inputFile = "refactor_vars.txt";
        String line;
        String splitBy = ",";
        int lineCounter = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            while ((line = br.readLine()) != null) {

                String[] data = line.split(splitBy);
                lineCounter++;

                if(isMadeOfDigits(data[1]) || isMadeOfDigits(data[2])){
                    System.out.println("Warning line " + lineCounter + ": variable name cannot be digit");
                    continue;
                }

                if (data.length == 3) {
                    switch (data[0]){
                        case "var" -> variableMap.put(data[1], data[2]);
                        case "method" -> methodMap.put(data[1], data[2]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        RenameVariableListener renamer = new RenameVariableListener(variableMap,
                                                                    tokens,
                                                    "method1");
        walker.walk(renamer,tree);

        XPath.findAll(tree, "//expression", parser).forEach(ctx -> {
            //System.out.println(ctx.getText());
        });

        System.out.println(renamer.rewriter.getText());
        try {
            var writer = new FileWriter("Out.java");
            writer.write(renamer.rewriter.getText());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isMadeOfDigits(String str) {
        return str.matches("\\d+");
    }
}
