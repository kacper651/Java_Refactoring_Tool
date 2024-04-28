package rewriter;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.xpath.XPath;

import java.io.*;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Scanner;

public class RefactoringTool {

    public static void main(String[] args) {
        boolean shouldContinue = true;
        int choice;
        String inputPath = "";
        Scanner scanner = new Scanner(System.in);
        String inputDir = "src/main/java/input/";
        // read input file
        CharStream input = null;
        try {
            inputPath = inputDir + "Input.java";
            input = CharStreams.fromFileName(inputPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        HashMap<String, String> variableMap = new HashMap<>();
        HashMap<String, String> methodNameMap = new HashMap<>();
        HashMap<String, String> parameterMap = new HashMap<>();
        HashMap<String, String> classOrInterfaceMap = new HashMap<>();


        String configFile = "refactor_config.txt";
        String line;
        String splitBy = ",";
        int lineCounter = 0;
        String methodName = null;
        RenameType renameType = RenameType.CLASS;
        OpType opType = OpType.CHANGE;

        // create lexer, parser, and parse tree
        JavaLexer lexer = new JavaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);

        // create walker and listener
        ParseTree tree = parser.compilationUnit();
        ParseTreeWalker walker = new ParseTreeWalker();

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
                    } catch (NoSuchFileException e){
                        System.out.println("Nie znaleziono pliku");
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case 2 -> {
                    System.out.println("Podaj nazwę pliku: ");
                    configFile = scanner.nextLine();
                }

                case 3 -> {
                    lineCounter = 0;

                    try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
                        String[] options = br.readLine().split(splitBy);
                        methodName = options[0];
                        renameType = RenameType.valueOf(options[1]);
                        opType = OpType.valueOf(options[2]);

                        lineCounter++;
                        while ((line = br.readLine()) != null) {
                            String[] data = line.split(splitBy);
                            lineCounter++;

                            if(isMadeOfDigits(data[1]) || isMadeOfDigits(data[2])){
                                System.out.println("Uwaga linia " + lineCounter + ": zmienna nie może być liczbą");
                                continue;
                            }

                            if (data.length == 3) {
                                switch (data[0]){
                                    case "var" -> variableMap.put(data[1], data[2]);
                                    case "method" -> methodNameMap.put(data[1], data[2]);
                                    case "class" -> classOrInterfaceMap.put(data[1], data[2]);
                                    case "param" -> parameterMap.put(data[1], data[2]);
                                }
                            }
                        }


                        JavaParserBaseListener renameListener = new RenameListener(variableMap, methodNameMap,
                                                                            parameterMap, classOrInterfaceMap,
                                                                            tokens, methodName,
                                                                            renameType, opType);

                        walker.walk(renameListener, tree);

                        XPath.findAll(tree, "//expression", parser).forEach(ctx -> {
                            System.out.println(ctx.getText());
                        });

                        try (FileWriter writer = new FileWriter(inputPath)){
                            writer.write(renameListener.rewriter.getText());
                            //System.out.println(renamerParam.rewriter.getText());
                            System.out.println("Zapisano zmiany w pliku: " +  inputPath);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } catch (FileNotFoundException e){
                        System.out.println("Nie znaleziono pliku " + configFile);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
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
