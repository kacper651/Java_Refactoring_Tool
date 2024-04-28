package rewriter;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStreamRewriter;

import java.util.Map;

public class RenameClassFieldListener extends JavaParserBaseListener{

    public Map<String, String> classFieldMap;
    TokenStreamRewriter rewriter;
    String classScope;
    String interesting = null;

    public RenameClassFieldListener(Map<String, String> classFieldMap, CommonTokenStream tokens, String classScope){
        this.classFieldMap = classFieldMap;
        this.rewriter = new TokenStreamRewriter(tokens);
        this.classScope = classScope;
    }

    @Override
    public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        interesting = ctx.identifier().IDENTIFIER().getText();
    }

    @Override
    public void enterFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        if (interesting.equals(classScope) &&
            ctx.variableDeclarators().variableDeclarator() != null){
            for (var variableDeclarator : ctx.variableDeclarators().variableDeclarator()){
                String name = variableDeclarator.variableDeclaratorId().identifier().IDENTIFIER().getText();
                if (name != null){
                    if (classFieldMap.containsKey(name)){
                        rewriter.replace(variableDeclarator.variableDeclaratorId().identifier().IDENTIFIER().getSymbol(),
                                classFieldMap.get(name));
                    }
                }
            }
        }
    }

    @Override
    public void enterConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx){
        if (interesting.equals(classScope) &&
            ctx.block().blockStatement() != null){
            for (var blockStatementElem : ctx.block().blockStatement()){
                if (blockStatementElem.statement().expression() != null){

                    for (var expressionElem : blockStatementElem.statement().expression()){
                        if (expressionElem.expression() != null){

                            for (var innerExpression : expressionElem.expression()){
                                if (innerExpression.expression() != null
                                    && innerExpression.identifier() != null){
                                        rewriter.replace(innerExpression.identifier().IDENTIFIER().getSymbol(),
                                                classFieldMap.get(innerExpression.identifier().IDENTIFIER().getText()));
                                }

                            }

                        }
                    }

                }
            }

        }
    }


    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx){
        // TODO: implement changing class fields in methods
    }

    // TODO: guard from inner classes bullshit

}


