/**
 * 
 */
package proof.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import tableau.Node;
import tableau.Tree;

/**
 * @author dev
 *
 */
public class DotTreeGenerator {

    private Tree tree;
    private int count = 0;
    
    public DotTreeGenerator(Tree t) {
        this.tree = t;
    }
    
    public boolean generate(String arq) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(arq));
            writer.write("digraph g {");
            writer.newLine();
            writer.write("graph[nodesep=0.1, ranksep=0.2];");
            writer.newLine();
            writer.write("node[shape=plaintext, fontname=\"Courier\", fontsize=10];");
            writer.newLine();
            writer.write("edge[arrowsize=0.0, arrowhead=none,  penwidth=0.75];");
            writer.newLine();
            this.toDot(writer, tree.getRoot());
            writer.write("}");
            writer.close();
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            return false;
        }
    }
    
    
    private String toDot (BufferedWriter writer, Node node) 
            throws IOException {
        while (node != null) {
            count++;
            String nodeName = "node" + count; 
            writer.write(nodeName);
            writer.write("[label=\"");
            writer.write(node.getLabel2());
            while (node.getBranch() == null) {
                node = node.getNext();
                if (node == null)
                    break;
                writer.write("\\l");
                writer.write(node.getLabel2());
            }
            writer.write("\\l\"];");
            writer.newLine();
            if (node != null) {
                if (node.getNext() != null) {
                    String left = toDot(writer, node.getNext());
                    writer.write(nodeName + " -> " + left + ";");
                    writer.newLine();
                }
                if (node.getBranch() != null) {
                    String right = toDot(writer, node.getBranch());
                    writer.write(nodeName + " -> " + right + ";");
                    writer.newLine();
                }
            }
            return nodeName;
        }
        return "";
    }
        
}
