package rewriter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

import java.util.HashMap;
import java.util.Map;
public class RenameListener extends JavaParserBaseListener{
    public Map<String, String> variableMap;
    HashMap<String, String> parameterMap;
    String interesting = null;
    String methodName;
    OpType opType;

    public Map<String, String> classOrInterfaceMap;
    HashMap<String, String> methodNameMap;
    RenameType renameType;

    public RenameListener(HashMap<String, String> variableMap,
                          HashMap<String, String> methodNameMap,
                          HashMap<String, String> parameterMap,
                          HashMap<String, String> classOrInterfaceMap,
                          CommonTokenStream tokens,
                          String methodName,
                          RenameType type,
                          OpType opType) {
        super(tokens);
        this.variableMap = variableMap;
        this.classOrInterfaceMap = classOrInterfaceMap;
        this.renameType = type;
        this.methodName = methodName;
        this.opType = opType;
        this.parameterMap = parameterMap;
        this.methodNameMap = methodNameMap;
    }

    // w jakiej funkcji jaka zmienna / xpath do szukania nazwy funkcji / trzeba przechowywac id funkcji
    // mozna tez to wywolac w drzewie wynikowym / parsetreepattern do wyszukiwania regul
    // (w dokumentacji jest opisane to obok siebie)

    @Override
    public void enterVariableDeclarator(JavaParser.VariableDeclaratorContext ctx) {
        String name = ctx.variableDeclaratorId().identifier().IDENTIFIER().getText();
        if (variableMap.containsKey(name)
                && interesting != null
                && interesting.equals(methodName)) {
            rewriter.replace(ctx.variableDeclaratorId().identifier().IDENTIFIER().getSymbol(), variableMap.get(name));
        }
    }

    @Override
    public void enterExpression(JavaParser.ExpressionContext ctx) { // zamienić na primary w expression
        if (ctx.start == null) return;
        var variableName = ctx.start.getText();
        if (variableMap.containsKey(variableName)
                && interesting != null
                && interesting.equals(methodName)) {
            rewriter.replace(ctx.start, variableMap.get(variableName));
        }
    }


    @Override
    public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        String name = ctx.identifier().IDENTIFIER().getText();
        String extendsSomeClass = ctx.EXTENDS() != null ? ctx.EXTENDS().getText() : null;
        String implementsSomeInterfaces = ctx.EXTENDS() != null ? ctx.EXTENDS().getText() : null;

        if (classOrInterfaceMap.containsKey(name) && renameType == RenameType.CLASS){
            rewriter.replace(ctx.identifier().IDENTIFIER().getSymbol(), classOrInterfaceMap.get(name));
        }
        if (extendsSomeClass != null && renameType == RenameType.CLASS){
            String baseClassName = ctx
                    .typeType()
                    .classOrInterfaceType()
                    .typeIdentifier()
                    .IDENTIFIER()
                    .getText();
            if (classOrInterfaceMap.containsKey(baseClassName)){
                rewriter.replace(ctx.typeType().classOrInterfaceType().typeIdentifier().IDENTIFIER().getSymbol(), classOrInterfaceMap.get(baseClassName));
            }
        }
        if (implementsSomeInterfaces != null && renameType == RenameType.INTERFACE){
            for (var typeListElem : ctx.typeList()){
                for (var typeTypeContext : typeListElem.typeType()){
                    String interfaceName = typeTypeContext.classOrInterfaceType().typeIdentifier().IDENTIFIER().getText();
                    if (classOrInterfaceMap.containsKey(interfaceName)){
                        rewriter.replace(typeTypeContext.classOrInterfaceType().typeIdentifier().IDENTIFIER().getSymbol(), classOrInterfaceMap.get(interfaceName));
                    }
                }
            }
        }
    }

    // basic case for interfaces
    @Override
    public void enterInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) {
        String name = ctx.identifier().IDENTIFIER().getText();
        String extendsSomeInterfaces = ctx.EXTENDS() != null ? ctx.EXTENDS().getText() : null;

        if (classOrInterfaceMap.containsKey(name) && renameType == RenameType.INTERFACE){
            rewriter.replace(ctx.identifier().IDENTIFIER().getSymbol(), classOrInterfaceMap.get(name));
        }
        if (extendsSomeInterfaces != null && renameType == RenameType.INTERFACE){
            for (var typeListElem : ctx.typeList()) {
                for (var typeTypeContext : typeListElem.typeType()) {
                    String interfaceName = typeTypeContext.classOrInterfaceType().typeIdentifier().IDENTIFIER().getText();
                    if (classOrInterfaceMap.containsKey(interfaceName)) {
                        rewriter.replace(typeTypeContext.classOrInterfaceType().typeIdentifier().IDENTIFIER().getSymbol(), classOrInterfaceMap.get(interfaceName));
                    }
                }
            }

        }
    }

    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        interesting = ctx.identifier().IDENTIFIER().getText();

        String methodName = ctx.identifier().IDENTIFIER().getText();
        if (methodNameMap.containsKey(methodName)) {
            rewriter.replace(
                    ctx.identifier().IDENTIFIER().getSymbol(),
                    methodNameMap.get(methodName)
            );
        }
    }

    @Override
    public void enterFormalParameters(JavaParser.FormalParametersContext ctx) {
        if (interesting != null && interesting.equals(methodName)) {
            if (opType == OpType.ADD) {
                for (String key : parameterMap.keySet()) {
                    rewriter.insertAfter(
                            ctx.formalParameterList().formalParameter(ctx.formalParameterList().formalParameter().size() - 1).stop,
                            ", " + parameterMap.get(key)
                    );
                }
            } else if (opType == OpType.REMOVE) {
                rewriter.replace(
                        ctx.formalParameterList().start,
                        ctx.formalParameterList().stop,
                        ""
                );
            } else if (opType == OpType.CHANGE) {
                for (String key : parameterMap.keySet()) {
                    for (int i = 0; i < ctx.formalParameterList().formalParameter().size(); i++) {
                        String paramName = ctx.formalParameterList()
                                .formalParameter(i)
                                .typeType()
                                .getText()
                                + " " + ctx.formalParameterList()
                                .formalParameter(i)
                                .variableDeclaratorId()
                                .getText();
                        if (paramName.equals(key)) {
                            rewriter.replace(
                                    ctx.formalParameterList().formalParameter(i).start,
                                    ctx.formalParameterList().formalParameter(i).stop,
                                    parameterMap.get(key)
                            );
                        }
                    }
                }
            }
        }
    }

    @Override
    public void enterMethodCall(JavaParser.MethodCallContext ctx) {
        String methodName = ctx.identifier().IDENTIFIER().getText();
        if (methodNameMap.containsKey(methodName)) {
            rewriter.replace(
                    ctx.identifier().IDENTIFIER().getSymbol(),
                    methodNameMap.get(methodName)
            );
        }
    }
}
