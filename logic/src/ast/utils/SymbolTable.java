package ast.utils;
/**
 * 
 */

import java.util.*;

import ast.Symbol;

/**
 * @author dev
 *
 */
public  class SymbolTable<T extends Symbol>  {

    private SymbolTable<? extends Symbol> context;
    HashMap<String, Occurrence<? extends Symbol>> table;
    
    public SymbolTable(){
        this.context = null;
        this.table = new HashMap<String, Occurrence<? extends Symbol>>();
    }
    
    public SymbolTable(SymbolTable<? extends Symbol> context){
        this.context = context;
    }
    
    
    // cria e/ou retorna um objeto Symbol (usando Reflection) 
    // de acordo com o identificador especificado
    // adicionado, se não existir, o elemento na tabela de símbolos
    @SuppressWarnings("unchecked")
    public T occurrence(Class<? extends Symbol> tClass, String id) 
            throws Exception {
        Occurrence<? extends Symbol> occur = getContextOccurrence(id);
        if (occur == null) {
            Symbol obj;
            try {
                obj = tClass.newInstance();
                obj.setLexeme(id);
                occur = new Occurrence<T>((T)obj);
                table.put(id, occur);
            } catch ( Exception e ) {
                //} catch ( InstantiationException
                //        | IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
            if (!obj.getClass().isAssignableFrom(tClass)) {
                throw new Exception("Símbolo '" + id + "' usado como " + 
                        tClass.getSimpleName() + " já ocorre como " + 
                        obj.getClass().getSimpleName());
            }
        } else {
            occur.add();
        }
        return (T)occur.getSymbol();
    }

    private Occurrence<? extends Symbol> getContextOccurrence(String id) {
        SymbolTable<? extends Symbol> searchContext = this;
        Occurrence<? extends Symbol> occur = null;
        while (occur == null && searchContext != null) {
            occur = searchContext.table.get(id);
            searchContext = searchContext.context;
        }
        return occur;
    }
    
    // mostra a tabela de símbolos
    public void print() {
        System.out.println ("/-----------------------------------------------------\\");
        System.out.println ("|                 Tabela de símbolos                  |");
        System.out.println ("|--------+------------+--------+----------------------|");
        System.out.println ("| Escopo | Tipo       | Ocorr. | Lexema               |");
        System.out.println ("|--------+------------+--------+----------------------|");
        print(0);
        System.out.println("\\--------+------------+--------+----------------------/");
    }
    
    private int print(int level) {
        if (context != null)
            level = context.print(level);
        Symbol symb;
        for (Occurrence<? extends Symbol> occur: this.table.values()) {
            symb = occur.getSymbol();
            System.out.println(String.format(
                    "| %6s | %-10s | %6d | %-20s |",
                    level,
                    symb.getClass().getSimpleName(),
                    occur.getCount(),
                    symb.getLexeme()));
        }
        return level+1;
    }
    
    public Symbol get(String key) {
        Occurrence<? extends Symbol> occur = table.get(key);
        return occur.getSymbol();
    }

    public Set<String> keySet() {
        return table.keySet();
    }
    
}
