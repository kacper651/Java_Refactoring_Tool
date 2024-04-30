package rewriter;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

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

        BaseRenameListener renameListener = null;
        HashMap<String, String> variableMap = new HashMap<>();
        HashMap<String, String> methodNameMap = new HashMap<>();
        HashMap<String, String> parameterMap = new HashMap<>();
        HashMap<String, String> classMap = new HashMap<>();
        HashMap<String, String> interfaceMap = new HashMap<>();
        HashMap<String, String> classFieldMap = new HashMap<>();


        String configFile = "refactor_config.txt";
        String line;
        String splitBy = ",";
        int lineCounter = 0;
        String methodName = null;
        OpType opType = OpType.CHANGE;
        String classScope = "";

        // create lexer, parser, and parse tree
        JavaLexer lexer = new JavaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);

        // create walker and listener
        ParseTree tree = parser.compilationUnit();
        ParseTreeWalker walker = new ParseTreeWalker();

        while(shouldContinue){
            System.out.println("\nPlik wejściowy: " + inputPath);
            System.out.println("Plik konfiguracyjny: " + configFile + "\n");
            System.out.println("Wybierz opcję: ");
            System.out.println("0. Wyjdź");
            System.out.println("1. Dodaj plik wejściowy");
            System.out.println("2. Dodaj plik konfiguracyjny");
            System.out.println("3. Wykonaj refaktoryzację\n");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 0 -> shouldContinue = false;
                case 1 -> {
                    try {
                        System.out.println("\nPodaj nazwę pliku z katalogu src/main/java/input/: ");
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
                    System.out.println("\nPodaj nazwę pliku: ");
                    configFile = scanner.nextLine();
                }

                case 3 -> {
                    lineCounter = 0;

                    try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
                        String[] options = br.readLine().split(splitBy);
                        methodName = options[0];
                        opType = OpType.valueOf(options[2]);
                        classScope = options[3];

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
                                    case "class" -> classMap.put(data[1], data[2]);
                                    case "interface" -> interfaceMap.put(data[1], data[2]);
                                    case "param" -> parameterMap.put(data[1], data[2]);
                                    case "classField" -> classFieldMap.put(data[1], data[2]);
                                }
                            }
                        }

                        System.out.println("\nCo chcesz zmienić: ");
                        System.out.println("0. Cofnij ");
                        System.out.println("1. Parametry metody ");
                        System.out.println("2. Pola klasy ");
                        System.out.println("3. Nazwa klasy ");
                        System.out.println("4. Nazwa zmiennej lokalnej ");
                        System.out.println("5. Nazwa metody ");
                        System.out.println("6. Nazwa interfejsu");

                        int choiceRenamer = scanner.nextInt();
                        scanner.nextLine();

                        if (choiceRenamer == 0)
                            break;
                        else if (choiceRenamer < 0 || choiceRenamer > 6){
                            System.out.println("Niepoprawny wybór");
                            break;
                        }

                        switch (choiceRenamer){
                            case 1 -> renameListener = new ChangeMethodParametersListener(parameterMap, tokens, methodName, opType);
                            case 2 -> renameListener = new RenameClassFieldListener(classFieldMap, tokens, classScope);
                            case 3 -> renameListener = new RenameClassOrInterfaceListener(classMap, tokens, RenameType.CLASS);
                            case 4 -> renameListener = new RenameLocalVariableListener(variableMap, tokens, methodName);
                            case 5 -> renameListener = new RenameMethodListener(methodNameMap, tokens);
                            case 6 -> renameListener = new RenameClassOrInterfaceListener(interfaceMap, tokens, RenameType.INTERFACE);
                        }

                        walker.walk(renameListener, tree);

                        try (FileWriter writer = new FileWriter("Output.java")){
                            assert renameListener != null;
                            writer.write(renameListener.rewriter.getText());
                            System.out.println("Zapisano zmiany w pliku Output.java");
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
                default -> System.out.println("\nNiepoprawny wybór");
            }
        }
        scanner.close();
    }

    public static boolean isMadeOfDigits(String str) {
        return str.matches("\\d+");
    }
}
