
import ast.LogicalSystem;

public class FormulaCompiler {
    
    // compilador
    LogicalSystem logicalSystem;
    Yylex scanner;
    LogicalCup parser_obj;
    
    // opções de linha de comandos do programa
    boolean debugSyn = false;
    boolean debugLex = false;
    boolean doSyntactic = true;
    
    public boolean isDebugSyn() {
        return debugSyn;
    }
    
    public boolean isDebugLex() {
        return debugLex;
    }
    
    public boolean isDoSyntactic() {
        return doSyntactic;
    }
    
    public LogicalSystem getLogicalSystem() {
        return logicalSystem;
    }
    
    public void setDebugSyn(boolean debugSyn) {
        this.debugSyn = debugSyn;
    }
    
    public void setDebugLex(boolean debugLex) {
        this.debugLex = debugLex;
    }
    
    public void setDoSyntactic(boolean doSyntactic) {
        this.doSyntactic = doSyntactic;
    }
    
    public LogicalSystem compile(java.io.InputStream input) {
        try {
            // Cria o analisador léxico 
            scanner = new Yylex(input);
            scanner.setDebug(debugLex);
            if (!doSyntactic) {
                // só faz a análise léxica
                while(!scanner.isEOF()) 
                    scanner.next_token();
                return null;
            } 
            /* cria um sistema lógico */
            logicalSystem = new LogicalSystem();
            
            /* cria um analisador sintático */
            parser_obj = new LogicalCup(scanner);
            parser_obj.setDebug(debugSyn || debugLex);
            parser_obj.setLogicalSystem(logicalSystem);
            /* cria um objeto de símbolo para o nó raiz da árvore sintática */
            java_cup.runtime.Symbol astRootNode;
            
            if (debugSyn) 
                // faz a análise sintática com debug
                astRootNode = parser_obj.debug_parse();
            else
                // faz a análise sintática SEM debug
                astRootNode = parser_obj.parse();
            //parser_obj.showSummary();
            
            if (!parser_obj.hasError() && astRootNode.value instanceof LogicalSystem) {
                return logicalSystem;
            }
        }     
        catch (java.io.IOException e) {
            System.err.println("Erro de IO no analisador léxico.");
            e.printStackTrace();
        }
        catch (Exception e) {
            System.err.println("Erro desconhecido:");
            e.printStackTrace();
        }
        return null;
    }
}
