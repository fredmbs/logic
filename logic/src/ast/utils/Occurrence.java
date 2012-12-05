/**
 * 
 */
package ast.utils;

import ast.Symbol;

/**
 * @author dev
 *
 */
public class Occurrence<T extends Symbol> {

    private T symbol;;
    private int count;
    
    protected Occurrence(T symbol){
        this.symbol = symbol;
        this.count = 1;
    }

    protected void add(){
        this.count++;
    }

    public int getCount(){
        return this.count;
    }

    public T getSymbol() {
        return symbol;
    }

}
