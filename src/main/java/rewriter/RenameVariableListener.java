package rewriter;

import java.util.HashMap;
import java.util.Map;

public class RenameVariableListener extends JavaParserBaseListener {
    private Map<String, String> variableMap = new HashMap<>();

    // Override methods to capture variable declarations and references
    // Implement logic to rename variables as needed

    public void renameVariable(String oldName, String newName) {
        variableMap.put(oldName, newName);
    }

    @Override
    public void exitVariableDeclaratorId(JavaParser.VariableDeclaratorIdContext ctx) {
        String oldName = ctx.identifier().getText();
        if (variableMap.containsKey(oldName)) {
            ctx.identifier().setText(variableMap.get(oldName));
        }
    }

    @Override
    public void exitIdentifier(JavaParser.IdentifierContext ctx) {
        String oldName = ctx.getText();
        if (variableMap.containsKey(oldName)) {
            ctx.IDENTIFIER().setText(variableMap.get(oldName));
        }
    }
}

