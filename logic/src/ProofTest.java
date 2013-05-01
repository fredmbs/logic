
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
    private int count = 0, acertos = 0, erros = 0;
    private String filename = "stdout";
    FormulaGenerator amostragem;
    int numTermos, numProposicoes;
    
    
    
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

    public void redirectToFile(String filename) {
        try {
            System.setOut(new PrintStream(
                    new BufferedOutputStream(
                    new FileOutputStream(filename))));
            this.filename = filename;
        } catch (FileNotFoundException e) {
            System.err.println("Arquivo não encontrado : \""+filename+"\"");
            e.printStackTrace();
        }
    }
    
    private boolean doSolveAndLog(Tableau t, TruthType baseResult) {
        long spentTime;
        spentTime = t.solve();
        System.out.print(";" + t.getResultName());
        System.out.print(";" + t.getOpenBranchesCount());
        System.out.print(";" + t.getClosedBranchesCount());
        System.out.print(";" + t.getAbandonedBranches());
        System.out.print(";" + t.getBranchesCount());
        System.out.print(";" + t.getNodesCount());
        System.out.print(";" + spentTime);
        if (t.compareResult(baseResult)) {
            System.out.print(";PASS");
            return true;
        } else {
            System.out.print(";FAIL");
            return false;
        }
    }
    
    private boolean verify(String formula) {
        boolean ok = true;
        bytes = formula.getBytes();
        long time = System.nanoTime();
        logicalSystem = compiler.compile(new ByteArrayInputStream(bytes));
        time = System.nanoTime() - time;
        if (logicalSystem == null) {
            System.err.println("Falha na compilação da fórmula " + formula);
        } else {
            System.out.print(++count);
            TruthType baseResult = TruthType.UNKNOWN;
            TruthTable ttt;
            try {
                ttt = new TruthTable(logicalSystem);
                System.out.print(";" + ttt.getFormula());
                System.out.print(";" + time);
                long spentTime = ttt.solve2();
                baseResult = ttt.getResult();
                System.out.print(";" + ttt.getResultName());
                System.out.print(";" + spentTime);
            } catch (LogicalSystemException e1) {
                System.err.println(e1.getMessage());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            try {
                ok = ok & doSolveAndLog(new Tableau(logicalSystem), baseResult);
            } catch (LogicalSystemException e1) {
                System.err.println(e1.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            try {
                ok = ok & doSolveAndLog(new Tableau(logicalSystem, 
                        new LemmaTableauInferenceFactory(),
                        new TableauNodeClassifierFactory(),
                        new PriorityNodeSelectorFactory()),
                        baseResult);
            } catch (LogicalSystemException e1) {
                System.err.println(e1.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            try {
                ok = ok &  doSolveAndLog(new Tableau(logicalSystem, 
                        new KeTableauInferenceFactory(),
                        new KeTableauNodeClassifierFactory(),
                        new KeNodeSelectorFactory()),
                        baseResult);
            } catch (LogicalSystemException e1) {
                System.err.println(e1.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }           
        }
        System.out.println();
        if (ok) {
            acertos++;
        } else {
            erros++;
        }
        return ok;
    }
    
    public void showConfig() {
        System.err.println("######################################");
        System.err.println("Configurações:");
        System.err.println("Número de termos      = " + numTermos);
        System.err.println("Número de proposições = " + numProposicoes);
        System.err.println("Saída de dados        = " + filename);
    }
    
    public void showStatistics() {
        System.err.println("--------------------------------------");
        System.err.println("Fórmulas:");
        System.err.println("Acertos = " + acertos);
        System.err.println("Erros   = " + erros);
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
        long time = System.nanoTime();
        showConfig();
        amostragem = new FormulaGenerator(numTermos, numProposicoes);
        while (amostragem.hasFormula()) {
            verify(amostragem.nextFormula().toString());
        }
        showStatistics();
        System.err.println("Tempo de teste = " + (System.nanoTime() - time) + " ns");
    }
    
    private void processCommands(String argv[]) {
        
        // tratador das opções da linha de comando
        Getopt getOpt = new Getopt("tester", argv, ":t:p:h");
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
        ProofTest test = new ProofTest();
        test.processCommands(argv);
        test.execute();
    }
    
}

