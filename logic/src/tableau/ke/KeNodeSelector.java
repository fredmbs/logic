/**
 * 
 */
package tableau.ke;

import java.util.ArrayList;
import tableau.BranchEngine;
import tableau.Node;
import tableau.simple.PriorityNodeSelector;

/**
 * @author dev
 *
 */
public class KeNodeSelector extends PriorityNodeSelector {
    
    private ArrayList<Node> openBetas;
    private BranchEngine engine;
    
    public KeNodeSelector(BranchEngine engine) {
        super(engine);
        this.engine = engine;
        this.openBetas = new ArrayList<Node>(); 
    }
    
    public KeNodeSelector(BranchEngine engine, KeNodeSelector from) {
        super(engine, (PriorityNodeSelector)from);
        this.engine = engine;
        this.openBetas = new ArrayList<Node>(from.openBetas); 
    }
    
    public void regressOpenBetas() {
        // retorna os nós beta abertos
        int numOpenBetas = openBetas.size();
        //System.err.println("Regressando nós betas abertos:" + numOpenBetas);
        if (numOpenBetas > 0) {
            Node returned;
            for (int i = 0; i < numOpenBetas; i++) {
                returned = openBetas.get(i);
                if (returned != null) {
                    //System.err.println("Regressando nó betas abertos:" + returned);
                    super.add(returned);
                }
            }
            openBetas.clear();
        }
    }
    
    @Override
    public void add(Node node) {
        //System.err.println("Adicionando nó:" + node);
        regressOpenBetas();
        // adiciona o nó na fila de prioridade
        super.add(node);
    }
    
    @Override
    public Node select() {
        //System.err.println("--- Selecionando nó");
        Node node = super.select();
        if (node == null) {
            if (!openBetas.isEmpty()) {
                //System.err.println("Tentando um PB entre os BETA aberto");
                KePbSelector selector = new KePbSelector(openBetas, engine);
                if (selector.select()) {
                    node = selector.getSelectedPB();
                    //System.err.println("Encontrou PB:" + node);
                    if (node != null) 
                        regressOpenBetas();
                }
            } 
            else { 
                //System.err.println("Não existem nós BETA abertos...");
            }
        }
        //System.err.println("--- Nó selecionado = "  + node);
        return node;
    }
    
    @Override
    public void regress(Node node) {
        //System.err.println("--- Regressando o nó " + node);
        Node.Type type = node.getType();
        if (type == Node.Type.BETA) {
            openBetas.add(node);
        } else { 
            super.regress(node);
        }
    }
    
}
