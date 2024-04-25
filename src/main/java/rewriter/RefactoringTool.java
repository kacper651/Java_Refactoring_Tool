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
import java.util.Scanner;

public class RefactoringTool {

    public static void main(String[] args) {
        boolean shouldContinue = true;
        int choice;
        String inputPath = "";
        Scanner scanner = new Scanner(System.in);
        Scanner scannerFiles = new Scanner(System.in);
        String inputDir = "src/main/java/input/";
        // read input file
        CharStream input = null;
        try {
            inputPath = inputDir + "Input.java";
            input = CharStreams.fromFileName(inputPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // rename variables map
        HashMap<String, String> variableMap = new HashMap<>();
        HashMap<String, String> methodMap = new HashMap<>();
        HashMap<String, String> classMap = new HashMap<>();

        String configFile = "refactor_vars.txt";
        String line;
        String splitBy = ",";
        int lineCounter = 0;
        String methodName = null;

        while(shouldContinue){
            System.out.println("Plik wejściowy: " + inputPath);
            System.out.println("Plik konfiguracyjny: " + configFile + "\n");
            System.out.println("Wybierz opcję: ");
            System.out.println("0. Wyjdź");
            System.out.println("1. Dodaj plik wejściowy");
            System.out.println("2. Dodaj plik konfiguracyjny");
            System.out.println("3. Wykonaj refaktoryzację");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 0 -> shouldContinue = false;
                case 1 -> {
                    try {
                        System.out.println("Podaj nazwę pliku z katalogu src/main/java/input/: ");
                        String newFile = scanner.nextLine();
                        input = CharStreams.fromFileName(inputDir + newFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case 2 -> {
                    System.out.println("Podaj nazwę pliku: ");
                    configFile = scanner.nextLine();
                }

                case 3 -> {
                    // create lexer, parser, and parse tree
                    JavaLexer lexer = new JavaLexer(input);
                    CommonTokenStream tokens = new CommonTokenStream(lexer);
                    JavaParser parser = new JavaParser(tokens);

                    // create walker and listener
                    ParseTree tree = parser.compilationUnit();
                    ParseTreeWalker walker = new ParseTreeWalker();

                    try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
                        methodName = br.readLine();
                        lineCounter++;
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
                                    case "class" -> classMap.put(data[1], data[2]);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    RenameVariableListener renamer = new RenameVariableListener(variableMap, tokens, methodName);
                    walker.walk(renamer,tree);

                    XPath.findAll(tree, "//expression", parser).forEach(ctx -> {
                        //System.out.println(ctx.getText());
                    });

                    System.out.println(renamer.rewriter.getText());
                    System.out.println("Wynik zapisano w pliku Out.java");
                    try {
                        var writer = new FileWriter("Out.java");
                        writer.write(renamer.rewriter.getText());
                        writer.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        scanner.close();
    }

    public static boolean isMadeOfDigits(String str) {
        return str.matches("\\d+");
    }
}
