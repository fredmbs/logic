
import gnu.getopt.Getopt;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import proof.LogicalReasoning.TruthType;
import proof.utils.LogicalSystemException;

import tableau.Tableau;
import tableau.ke.KeNodeSelectorFactory;
import tableau.ke.KeTableauInferenceFactory;
import tableau.ke.KeTableauNodeClassifierFactory;
import tableau.lemma.LemmaTableauInferenceFactory;
import tableau.patterns.PriorityNodeSelectorFactory;
import tableau.patterns.TableauNodeClassifierFactory;
import truthTable.TruthTable;

import ast.LogicalSystem;
import ast.utils.FormulaGenerator;

public class ProofTest {
    
    // compilador
    private FormulaCompiler compiler = new FormulaCompiler();
    private LogicalSystem logicalSystem = null;
    private byte[] bytes;
    private int count = 0, acertos = 0, erros = 0, falhas = 0;
    private String filename = "stdout";
    private FormulaGenerator amostragem;
    private int numTermos, numProposicoes;
    private boolean notCheckContradicition = false;
    private PrintStream printer = null, originalPrinter = null;
    
    
    public ProofTest() {
        super();
        this.numTermos = 4;
        this.numProposicoes = 2;
    }

    public ProofTest(int numTermos, int numProposicoes) {
        super();
        this.numTermos = numTermos;
        this.numProposicoes = numProposicoes;
        this.amostragem = new FormulaGenerator(numTermos, numProposicoes);
    }

    public int getCount() {
        return count;
    }

    public int getAcertos() {
        return acertos;
    }

    public int getErros() {
        return erros;
    }

    public void redirectToFile(String filename) {
        try {
            if (originalPrinter == null)
                originalPrinter = System.out; 
            printer = new PrintStream(
                    new BufferedOutputStream(
                    new FileOutputStream(filename)));
            System.setOut(printer);
            this.filename = filename;
        } catch (FileNotFoundException e) {
            System.err.println("Arquivo não encontrado : \""+filename+"\"");
            e.printStackTrace();
        }
    }
    
    public void close() {
        if (originalPrinter != null)
            System.setOut(originalPrinter);
        if (printer != null)
            printer.close();
    }
    
    private boolean doSolveAndLog(Tableau t, TruthType baseResult) {
        long spentTime;
        if (notCheckContradicition)
            t.setCheckContradiction(false);
        spentTime = t.solve();
        System.out.print(";" + t.getResultName()); //6,14,22
        System.out.print(";" + t.getOpenBranchesCount());//7,15,23
        System.out.print(";" + t.getClosedBranchesCount());//8,16,24
        System.out.print(";" + t.getAbandonedBranches());//9,17,25
        System.out.print(";" + t.getBranchesCount());//10,18,26
        System.out.print(";" + t.getNodesCount());//11,19,27
        System.out.print(";" + spentTime);//12,20,28
        if (t.compareResult(baseResult)) {//13,21,29
            System.out.print(";PASS");
            return true;
        } else {
            System.out.print(";FAIL");
            return false;
        }
    }
    
    private boolean verify(String formula) {
        boolean ok = true, falha = false;
        bytes = formula.getBytes();
        long time = System.nanoTime();
        logicalSystem = compiler.compile(new ByteArrayInputStream(bytes));
        time = System.nanoTime() - time;
        System.out.print(++count);  //1
        if (logicalSystem == null) {
            System.err.println("Falha na compilação da fórmula " + formula);
            falha = true;
        } else {
            TruthType baseResult = TruthType.UNKNOWN;
            TruthTable ttt;
            try {
                ttt = new TruthTable(logicalSystem);
                System.out.print(";" + ttt.getFormula()); //2
                System.out.print(";" + time);  //3
                long spentTime = ttt.solve2();
                baseResult = ttt.getResult();
                System.out.print(";" + ttt.getResultName()); //4
                System.out.print(";" + spentTime);  //5
            } catch (LogicalSystemException e1) {
                System.err.println(e1.getMessage());
                System.err.println("EERO Tabela Verdade em "+ count + " = " + formula);
                falha = true;
            } catch (Exception e1) {
                e1.printStackTrace();
                System.err.println("EERO Tabela Verdade em "+ count + " = " + formula);
                falha = true;
            }
            if (!falha) {
                try {
                    ok = ok & doSolveAndLog(new Tableau(logicalSystem), baseResult);
                } catch (LogicalSystemException e1) {
                    System.err.println(e1.getMessage());
                    System.err.println("EERO Tableau Smullyan em "+ count + " = " + formula);
                    falha = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("EERO Tableau Smullyan em "+ count + " = " + formula);
                    falha = true;
                }
                
                try {
                    ok = ok & doSolveAndLog(new Tableau(logicalSystem, 
                            new LemmaTableauInferenceFactory(),
                            new TableauNodeClassifierFactory(),
                            new PriorityNodeSelectorFactory()),
                            baseResult);
                } catch (LogicalSystemException e1) {
                    System.err.println(e1.getMessage());
                    System.err.println("EERO Tableau com lema em "+ count + " = " + formula);
                    falha = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("EERO Tableau com lema em "+ count + " = " + formula);
                    falha = true;
                }
                
                try {
                    ok = ok &  doSolveAndLog(new Tableau(logicalSystem, 
                            new KeTableauInferenceFactory(),
                            new KeTableauNodeClassifierFactory(),
                            new KeNodeSelectorFactory()),
                            baseResult);
                } catch (LogicalSystemException e1) {
                    System.err.println(e1.getMessage());
                    System.err.println("EERO Tableau KE em "+ count + " = " + formula);
                    falha = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("EERO Tableau KE em "+ count + " = " + formula);
                    falha = true;
                }           
            }
        }
        if (falha) {
            falhas++;
            System.out.print("EXCEPTION("+formula+")");
            return false;
        } else {
            if (ok) {
                acertos++;
            } else {
                erros++;
            }
        }
        System.out.println();
        return ok;
    }
    
    public void showConfig() {
        System.err.println("######################################");
        System.err.println("Configurações:");
        System.err.println("Número de termos      = " + numTermos);
        System.err.println("Número de proposições = " + numProposicoes);
        System.err.println("Número de fórmulas    = " + this.amostragem.getNumFormulas());
        System.err.println("Saída de dados        = " + filename);
    }
    
    public void showStatistics() {
        System.err.println("--------------------------------------");
        System.err.println("Fórmulas:");
        System.err.println("Acertos = " + acertos);
        System.err.println("Erros   = " + erros);
        System.err.println("Falhas  = " + falhas);
        System.err.println("Total   = " + count);
        System.err.println("--------------------------------------");
        System.err.println("Amostra do relógio do sistema:");
        long time, sumTime = 0, loops = 1000, efectiveLoops = 0, maxTime = -1, minTime = 1000000;
        for (int i = 0; i < loops; i++) {
            time = System.nanoTime();
            time = System.nanoTime() - time;
            if (time > 0) {
                efectiveLoops++;
                sumTime += time;
                if (time > maxTime)
                    maxTime = time;
                if (time < minTime)
                    minTime = time;
            }
        }
        System.err.println("Unidade de tempo média  = " + (sumTime/efectiveLoops) + " ns");
        System.err.println("Unidade de tempo máxima = " + maxTime + " ns");
        System.err.println("Unidade de tempo mínima = " + minTime + " ns");
    }
    
    public void execute() {
        amostragem = new FormulaGenerator(numTermos, numProposicoes);
        showConfig();
        while (amostragem.hasFormula()) {
            verify(amostragem.nextFormula().toString());
        }
        showStatistics();
    }
    
    private void processCommands(String argv[]) {
        
        // tratador das opções da linha de comando
        Getopt getOpt = new Getopt("tester", argv, ":t:p:nh");
        getOpt.setOpterr(false);
        int c;
        // verifica as opções
        while ((c = getOpt.getopt()) != -1)
        {
            switch(c)
            {
            case 't':
                this.numTermos = Integer.parseInt(getOpt.getOptarg());
                break;
            case 'p':
                this.numProposicoes  = Integer.parseInt(getOpt.getOptarg());
                break;
            case 'h':
                System.out.println("ProofTest");
                System.out.println("");
                System.out.println("USO: ProofTest [opcoes] [arquivos com dados]");
                System.out.println("");
                System.out.println("Opções da linha de comando:");
                System.out.println("  -t  = número de termos da fórmula");
                System.out.println("  -p  = número de variáveis (proposições)");
                System.out.println("  -n  = não faz verificação de contradição");
                System.out.println("  -h  = Help");
                System.exit(0);
                break;
            case ':':
                System.err.println("Argumento ausente na opção '" +
                        (char)getOpt.getOptopt()+"'.");
                break;
            case '?':
                System.err.println("Opção '" + (char)getOpt.getOptopt() + 
                        "' inválida ignorada.");
                break;
            default:
                System.err.println("Erro na opção = '" + c + "'.");
                break;
            }
        }
        // opções default (nome de arquivo de saída)
        int defaultOpt = getOpt.getOptind();
        if (defaultOpt < argv.length)
            redirectToFile(argv[defaultOpt]); 
    }
    
    public static void main(String argv[]) {
        long time = System.nanoTime();
        ProofTest test = new ProofTest();
        test.processCommands(argv);
        test.execute();
        test.close();
        time = (System.nanoTime() - time);
        System.err.println("Tempo de teste          = " + time + " ns");
        System.err.println("Tempo médio por fórmula = " + (time/test.getCount()) + " ns");
    }
    
}

