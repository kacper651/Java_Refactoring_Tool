package rewriter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

import java.util.HashMap;
import java.util.Map;


public class RenameClassOrInterfaceListener extends BaseRenameListener {
    public Map<String, String> classOrInterfaceMap;
    RenameType renameType;

    public RenameClassOrInterfaceListener(HashMap<String, String> classMap, CommonTokenStream tokens, RenameType type) {
        super(tokens);
        this.classOrInterfaceMap = classMap;
        this.renameType = type;
    }

    @Override
    public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        String name = ctx.identifier().IDENTIFIER().getText();
        String extendsSomeClass = ctx.EXTENDS() != null ? ctx.EXTENDS().getText() : null;
        String implementsSomeInterfaces = ctx.EXTENDS() != null ? ctx.EXTENDS().getText() : null;

        if (classOrInterfaceMap.containsKey(name) && renameType == RenameType.CLASS) {
            rewriter.replace(ctx.identifier().IDENTIFIER().getSymbol(), classOrInterfaceMap.get(name));
        }
        if (extendsSomeClass != null && renameType == RenameType.CLASS) {
            String baseClassName = ctx
                    .typeType()
                    .classOrInterfaceType()
                    .typeIdentifier()
                    .IDENTIFIER()
                    .getText();
            if (classOrInterfaceMap.containsKey(baseClassName)) {
                rewriter.replace(ctx.typeType().classOrInterfaceType().typeIdentifier().IDENTIFIER().getSymbol(), classOrInterfaceMap.get(baseClassName));
            }
        }
        if (implementsSomeInterfaces != null && renameType == RenameType.INTERFACE) {
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

    // basic case for interfaces
    @Override
    public void enterInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) {
        String name = ctx.identifier().IDENTIFIER().getText();
        String extendsSomeInterfaces = ctx.EXTENDS() != null ? ctx.EXTENDS().getText() : null;

        if (classOrInterfaceMap.containsKey(name) && renameType == RenameType.INTERFACE) {
            rewriter.replace(ctx.identifier().IDENTIFIER().getSymbol(), classOrInterfaceMap.get(name));
        }
        if (extendsSomeInterfaces != null && renameType == RenameType.INTERFACE) {
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
}
