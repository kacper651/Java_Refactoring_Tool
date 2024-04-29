package rewriter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

//  this listener works on the basis of an assumption, that no local variable
//  will have the same name as a class field
public class RenameClassFieldListener extends JavaParserBaseListener{

    private final Map<String, String> classFieldMap;
    private final String classScope;

    // guards against renaming some local variable with enterExpression - works on the basis of the fact,
    // that the class fields and constructors come first in a class, before random methods
    private final HashSet<String> foundClassFieldsSet = new HashSet<>();

    // stack to keep track of the current class - I push the name of the class as I start traversing it,
    // and I pop it at the end - it makes sure, that I don't accidentaly rename something in an innner/outer class
    // against my will if something will match with the classFieldMap
    private final Stack<String> currentClassStack = new Stack<>();


    public RenameClassFieldListener(Map<String, String> classFieldMap, CommonTokenStream tokens, String classScope){
        super(tokens);
        this.classFieldMap = classFieldMap;
        this.classScope = classScope;
    }

    @Override
    public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        String className = ctx.identifier().IDENTIFIER().getText();
        currentClassStack.push(className);
    }

    @Override
    public void enterFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        if (!currentClassStack.isEmpty() &&
                currentClassStack.peek().equals(classScope) &&
                ctx.variableDeclarators().variableDeclarator() != null){
            for (var variableDeclarator : ctx.variableDeclarators().variableDeclarator()){
                String name = variableDeclarator.variableDeclaratorId().identifier().IDENTIFIER().getText();
                if (name != null){
                    if (classFieldMap.containsKey(name)){
                        foundClassFieldsSet.add(name);
                        rewriter.replace(variableDeclarator.variableDeclaratorId().identifier().IDENTIFIER().getSymbol(),
                                classFieldMap.get(name));
                    }
                }
            }
        }
    }

    @Override
    public void enterConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx){
        if (!currentClassStack.isEmpty() &&
                currentClassStack.peek().equals(classScope) &&
                ctx.block().blockStatement() != null){
            for (var blockStatementElem : ctx.block().blockStatement()){
                if (blockStatementElem.statement().expression() != null){

                    for (var expressionElem : blockStatementElem.statement().expression()){
                        if (expressionElem.expression() != null){

                            for (var innerExpression : expressionElem.expression()){
                                if (innerExpression.expression() != null
                                    && innerExpression.identifier() != null){
                                        String name = innerExpression.identifier().IDENTIFIER().getText();
                                        foundClassFieldsSet.add(name);
                                        rewriter.replace(innerExpression.identifier().IDENTIFIER().getSymbol(),
                                                classFieldMap.get(name));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void enterFormalParameter(JavaParser.FormalParameterContext ctx) {
        if (ctx.variableDeclaratorId() != null && ctx.variableDeclaratorId().identifier() != null){
            String name = ctx.variableDeclaratorId().identifier().IDENTIFIER().getText();
            if (classFieldMap.containsKey(name) &&
                    !currentClassStack.isEmpty() &&
                    currentClassStack.peek().equals(classScope) &&
                    foundClassFieldsSet.contains(name)){
                rewriter.replace(ctx.variableDeclaratorId().identifier().IDENTIFIER().getSymbol(),
                        classFieldMap.get(name));
            }
        }
    }

    @Override
    public void enterExpression(JavaParser.ExpressionContext ctx) {
        if (ctx.start == null) return;
        var variableName = ctx.start.getText();
        if (classFieldMap.containsKey(variableName) &&
                !currentClassStack.isEmpty() &&
                currentClassStack.peek().equals(classScope) &&
                foundClassFieldsSet.contains(variableName)) {
            rewriter.replace(ctx.start, classFieldMap.get(variableName));
        }
    }

    @Override
    public void exitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        currentClassStack.pop();
    }
}


