package rewriter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

import java.util.HashMap;
import java.util.Map;


public class RenameClassListener extends JavaParserBaseListener{
    public Map<String, String> classMap = new HashMap<>();
    private final CommonTokenStream tokens;
    TokenStreamRewriter rewriter;
    String interesting = null;
//    String scope;
    RenameType renameType;

    public RenameClassListener(HashMap<String, String> classMap, CommonTokenStream tokens, RenameType type){
        this.classMap = classMap;
        this.tokens = tokens;
        this.rewriter = new TokenStreamRewriter(tokens);
        this.renameType = type;
    }

    //  basic case for classes
    @Override
    public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        String name = ctx.identifier().IDENTIFIER().getText();
        String isDerived = ctx.EXTENDS() != null ? ctx.EXTENDS().getText() : null;
        if (classMap.containsKey(name)){
            rewriter.replace(ctx.identifier().IDENTIFIER().getSymbol(), classMap.get(name));
        }
        if (isDerived != null && renameType == RenameType.CLASS){
            String baseClassName = ctx
                    .typeType()
                    .classOrInterfaceType()
                    .typeIdentifier()
                    .IDENTIFIER()
                    .getText();
            System.out.println("BASE CLASS NAME: " + baseClassName);
            if (classMap.containsKey(baseClassName)){
                rewriter.replace(ctx.typeType().classOrInterfaceType().typeIdentifier().IDENTIFIER().getSymbol(), classMap.get(baseClassName));
            }
        }

    }

    // basic case for interfaces
    @Override
    public void enterInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) {
        String name = ctx.identifier().IDENTIFIER().getText();
        String implementsSomething = ctx.EXTENDS() != null ? ctx.EXTENDS().getText() : null;

        if (implementsSomething != null && renameType == RenameType.INTERFACE){
//            for (var typeListElem : ctx.typeList()) {
//                typeListElem.typeType().stream().map(
//                        typeTypeContext -> typeTypeContext.classOrInterfaceType().typeIdentifier().IDENTIFIER().getText()
//                ).forEach(
//                        interfaceName -> {
//                            System.out.println("INTERFACE NAME: " + interfaceName);
//                            if (classMap.containsKey(interfaceName)){
//                                rewriter.replace(typeListElem.typeType().get(0).classOrInterfaceType().typeIdentifier().IDENTIFIER().getSymbol(), classMap.get(interfaceName));
//                            }
//                        }
//                );
//            }
            System.out.println(ctx.typeList().size());
            for (var x : ctx.typeList()){
                System.out.println("> " + x.getText());
            }
            for (var typeListElem : ctx.typeList()) {
                for (var typeTypeContext : typeListElem.typeType()) {
                    String interfaceName = typeTypeContext.classOrInterfaceType().typeIdentifier().IDENTIFIER().getText();
//                    System.out.println("INTERFACE NAME: " + interfaceName);
                    if (classMap.containsKey(interfaceName)) {
                        rewriter.replace(typeTypeContext.classOrInterfaceType().typeIdentifier().IDENTIFIER().getSymbol(), classMap.get(interfaceName));
                    }
                }
            }

        }
    }

    // extends / implements type shit
//    @Override
//    public void enterTypeIdentifier(JavaParser.TypeIdentifierContext ctx) {
//        String name = ctx.IDENTIFIER().getText();
//        if (classMap.containsKey(name)){
//            rewriter.replace(ctx.IDENTIFIER().getSymbol(), classMap.get(name));
//        }
//    }
//
//    @Override
//    public void enterExpression(JavaParser.ExpressionContext ctx) {
//        if (ctx.start == null) return;
//        var className = ctx.start.getText();
//        System.out.println(className);
//        if (classMap.containsKey(className)
//                && interesting != null) {
//            rewriter.replace(ctx.start, classMap.get(className));
//        }
//    }

}
