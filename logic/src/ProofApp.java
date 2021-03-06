
import java.io.FileNotFoundException;

import proof.utils.LogicalSystemException;
import tableau.Tableau;
import tableau.ke.KeNodeSelectorFactory;
import tableau.ke.KeTableauInferenceFactory;
import tableau.ke.KeTableauNodeClassifierFactory;
import tableau.lemma.LemmaNodeSelectorFactory;
import tableau.lemma.LemmaTableauInferenceFactory;
import tableau.simple.TableauNodeClassifierFactory;
import truthTable.TruthTable;
import ast.*;
import gnu.getopt.Getopt;

class ProofApp {
    
    static boolean do_debug_parse = false;
    
    boolean showSymTable = false;
    String fileName = "";
    String dotFile = "";
    
    // controle de opções de linha de comando
    Getopt getOpt;
    int defaultOptionsIndex = 0;
    String argv[];
    
    // compilador
    FormulaCompiler compiler = new FormulaCompiler();
    
    public ProofApp(String argv[]) {
        this.argv = argv;
    }
    
    public boolean hasFileName() {
        return defaultOptionsIndex < argv.length;
    }
    
    public String getNextFileName() {
        return argv[defaultOptionsIndex++];
    }
    
    public boolean isShowSymTable() {
        return showSymTable;
    }
    
    public String getDotFile() {
        return dotFile;
    }
    
    private void processCommands() {
        
        // tratador das opções da linha de comando
        getOpt = new Getopt("prover", argv, ":nsltho:");
        getOpt.setOpterr(false);
        int c;
        // verifica as opções
        while ((c = getOpt.getopt()) != -1)
        {
            switch(c)
            {
            case 's':
                compiler.setDebugSyn(true);
                break;
            case 'l':
                compiler.setDebugLex(true);
                break;
            case 't':
                showSymTable = true;
                break;
            case 'n':
                compiler.setDoSyntactic(false);
                break;
            case 'o':
                dotFile = getOpt.getOptarg();
                break;
            case 'h':
                System.out.println("ProofApp");
                System.out.println("");
                System.out.println("USO: ProofApp [opcoes] [arquivos com a formula]");
                System.out.println("");
                System.out.println("Opções da linha de comando:");
                System.out.println("  -s  = Debug da analise sintática");
                System.out.println("  -l  = Debug da analise léxica");
                System.out.println("  -t  = Mostra a tabela de símbolos");
                System.out.println("  -n  = Nao realiza a analise sintática, apenas a léxica");
                System.out.println("  -o  = Arquivo de saída do grafo da solução");
                System.out.println("  -h  = Help");
                System.exit(0);
                break;
            case ':':
                System.err.println("Argumento ausente na opcao '" +
                        (char)getOpt.getOptopt()+"'.");
                break;
            case '?':
                System.err.println("Opcao '" + (char)getOpt.getOptopt() + 
                        "' invalida ignorada.");
                break;
            default:
                System.err.println("Erro na opcao = '" + c + "'.");
                break;
            }
        }
        // opções default (listagem de nomes de arquivos)
        defaultOptionsIndex = getOpt.getOptind(); 
    }
    
    public static void main(String argv[]) {
        
        long spentTime;
        String fileName;
        ProofApp proofApp = new ProofApp(argv);
        proofApp.processCommands();
        
        if (!proofApp.hasFileName()) {
            System.err.println("Não foi informado um arquivo com a fórmula.");
            return;
        }
        
        while (proofApp.hasFileName()) {
            fileName = proofApp.getNextFileName();
            LogicalSystem logicalSystem = null;
            try {
                logicalSystem = proofApp.compiler.compile(new java.io.FileInputStream(fileName));
            } catch (FileNotFoundException e2) {
                // TODO Auto-generated catch block
                System.err.println("Arquivo não encontrado : \""+fileName+"\"");
            } 
            if (logicalSystem == null) {
                System.err.println("Falha na compilação.");
                return;
            } else {
                System.out.print("Sistema:");
                System.out.println(logicalSystem);
                
                switch(logicalSystem.getLogicType()) {
                case UNKNOWN:
                    System.out.println("==> Lógica desconhecida");
                    break;
                case PROPOSITIONAL:
                    System.out.println("==> Lógica Proposicional");
                    break;
                case PREDICATE:
                    System.out.println("==> Lógica de Predicado");
                    break;
                default:
                    System.out.println("==> Falha na definição do tipo de lógica");
                    break;
                    
                }
                
                switch(logicalSystem.getLogicSystemType()) {
                case UNKNOWN:
                    System.out.println("==> Sistema lógico desconhecido");
                    break;
                case FORMULA:
                    System.out.println("==> Sistema lógico de prova simples (uma única fórmula)");
                    break;
                case DERIVATION:
                    System.out.println("==> Sistema lógico de derivação");
                    break;
                case ENTAILMENT:
                    System.out.println("==> Sistema lógico SAT");
                    break;
                default:
                    System.out.println("==> Falha na definição do tipo de sistema lógico");
                    break;
                    
                }
                
                if (proofApp.isShowSymTable()) 
                {
                    logicalSystem.getSymbolTable().print();
                }
                System.out.println("-------------------------------------");
                System.out.println("Cópia (clone) do sistema lógico:");
                LogicalSystem sys = logicalSystem.clone();
                System.out.print("Sistema:");
                System.out.println(sys);
                TruthTable ttt;
                try {
                    ttt = new TruthTable(sys);
                    System.out.print("Fórmula da tabela verdade:");
                    System.out.println(ttt.getFormula());
                    spentTime = ttt.solve();
                    ttt.print();
                    System.out.println("Solução = " + ttt.getResultName());
                    System.out.println("Solução por Tabela Verdade (com shortCut)' em " + spentTime + " ns");
                } catch (LogicalSystemException e1) {
                    System.err.println(e1.getMessage());
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                
                System.out.println("-------------------------------------");
                System.out.println("Segunda cópia do sistema lógico:");
                LogicalSystem sys2 = logicalSystem.clone();
                System.out.println(sys2);
                TruthTable ttt2;
                try {
                    ttt2 = new TruthTable(sys2);
                    System.out.print("Fórmula da tabela verdade:");
                    System.out.println(ttt2.getFormula());
                    spentTime = ttt2.solve2();
                    System.out.println("Solução = " + ttt2.getResultName());
                    System.out.println("Solução por Tabela Verdade (sem otimização)' em " + spentTime + " ns");
                } catch (LogicalSystemException e1) {
                    System.err.println(e1.getMessage());
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                
                
                System.out.println("-------------------------------------");
                System.out.println("Tableau:");
                Tableau t;
                try {
                    t = new Tableau(logicalSystem);
                    spentTime = t.solve();
                    t.print();
                    System.out.println("Solução = " + t.getResultName());
                    System.out.println("Solução do Tableau de Smullyan em " + spentTime + " ns");
                    System.out.println("Salvando o arquivo " + proofApp.getDotFile() + "_simple.gv");
                    t.toDot(proofApp.getDotFile() + "_simple.gv");
                } catch (LogicalSystemException e1) {
                    System.err.println(e1.getMessage());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                System.out.println("-------------------------------------");
                System.out.println("Tableau:");
                Tableau t2;
                try {
                    t2 = new Tableau(logicalSystem, 
                            new LemmaTableauInferenceFactory(),
                            new TableauNodeClassifierFactory(),
                            new LemmaNodeSelectorFactory());
                    spentTime = t2.solve();
                    t2.print();
                    System.out.println("Solução = " + t2.getResultName());
                    System.out.println("Solução do Tableau com Lema em " + spentTime + " ns");
                    System.out.println("Salvando o arquivo " + proofApp.getDotFile() + "_lemma.gv");
                    t2.toDot(proofApp.getDotFile() + "_lemma.gv");
                } catch (LogicalSystemException e1) {
                    System.err.println(e1.getMessage());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                System.out.println("-------------------------------------");
                System.out.println("Tableau:");
                Tableau t3;
                try {
                    t3 = new Tableau(logicalSystem, 
                            new KeTableauInferenceFactory(),
                            new KeTableauNodeClassifierFactory(),
                            new KeNodeSelectorFactory());
                    spentTime = t3.solve();
                    t3.print();
                    System.out.println("Solução = " + t3.getResultName());
                    System.out.println("Solução do Tableau KE em " + spentTime + " ns");
                    System.out.println("Salvando o arquivo " + proofApp.getDotFile() + "_ke.gv");
                    t3.toDot(proofApp.getDotFile() + "_ke.gv");
                } catch (LogicalSystemException e1) {
                    System.err.println(e1.getMessage());
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
            }
        }
    }
    
}