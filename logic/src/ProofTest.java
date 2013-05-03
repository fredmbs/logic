
import gnu.getopt.Getopt;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import proof.FormulaGenerator;
import proof.LogicalReasoning;
import proof.LogicalReasoning.TruthType;
import proof.patterns.FormulaGeneratorFactory;
import proof.test.CombinatoryFormulaGeneratorFactory;
import proof.test.RandomFormulaGenerator;
import proof.test.RandomFormulaGeneratorFactory;
import proof.utils.LogicalSystemException;

import tableau.Tableau;
import tableau.ke.KeNodeSelectorFactory;
import tableau.ke.KeTableauInferenceFactory;
import tableau.ke.KeTableauNodeClassifierFactory;
import tableau.lemma.LemmaNodeSelectorFactory;
import tableau.lemma.LemmaTableauInferenceFactory;
import tableau.simple.TableauNodeClassifierFactory;
import truthTable.TruthTable;

import ast.LogicalSystem;

public class ProofTest {

    // compilador
    private FormulaCompiler compiler = new FormulaCompiler();
    private LogicalSystem logicalSystem = null;
    private byte[] bytes;
    private long count = 0, acertos = 0, erros = 0, falhas = 0;
    private String filename = "stdout";
    private FormulaGeneratorFactory formulaGeneratorFactory;
    private FormulaGenerator formulaGenerator;
    private int numTermos, numProposicoes;
    private boolean checkContradicition = true;
    private boolean showAcertos = true;
    private boolean showLineNumber = true;
    private PrintStream printer = null, originalPrinter = null;
    private long maxTests = 0, timeOut = 0;
    private StringBuffer line = new StringBuffer();
    private long countResults[];
    private long countTimes[];
    private long countNodes[];
    private long countBrances[];
    private long countTimesBySolver[];
    private long countNodesBySolver[];
    private long countBrancesBySolver[];
    private long countTimesBySolverAndResult[][];
    private long countNodesBySolverAndResult[][];
    private long countBrancesBySolverAndResult[][];
    private boolean dispersion = false;
    
    public ProofTest() {
        super();
        this.numTermos = 4;
        this.numProposicoes = 2;
        int n = TruthType.values().length;
        countResults = new long[n];
        countTimes = new long[n];
        countNodes = new long[n];
        countBrances = new long[n];
        countTimesBySolver = new long[4];
        countNodesBySolver = new long[4];
        countBrancesBySolver = new long[4];
        countTimesBySolverAndResult = new long[4][n];
        countNodesBySolverAndResult = new long[4][n];
        countBrancesBySolverAndResult = new long[4][n];
    }

    public long getCount() {
        return count;
    }

    public long getAcertos() {
        return acertos;
    }

    public long getErros() {
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
    
    private boolean doSolveAndLog(Tableau t, TruthType baseResult, int solver) {
        long spentTime;
        t.setCheckContradiction(checkContradicition);
        spentTime = t.solve();
        line.append(";" + t.getResultName()); 
        line.append(";" + t.getOpenBranchesCount());
        line.append(";" + t.getClosedBranchesCount());
        line.append(";" + t.getAbandonedBranches());
        line.append(";" + t.getBranchesCount());
        line.append(";" + t.getNodesCount());
        line.append(";" + spentTime);
        int n = t.getResult().ordinal();
        countTimes[n] += spentTime;
        countNodes[n] += t.getNodesCount();
        countBrances[n] += t.getBranchesCount();
        countNodesBySolver[solver] += t.getNodesCount();
        countBrancesBySolver[solver] += t.getBranchesCount();
        countTimesBySolver[solver] += spentTime;
        countNodesBySolverAndResult[solver][n] += t.getNodesCount();
        countBrancesBySolverAndResult[solver][n] += t.getBranchesCount();
        countTimesBySolverAndResult[solver][n] += spentTime;
        if (t.compareResult(baseResult)) {
            line.append(";PASS");
            return true;
        } else {
            line.append(";FAIL");
            return false;
        }
    }
    
    private boolean verify(String formula) {
        boolean ok = true, falha = false;
        bytes = formula.getBytes();
        long time = System.nanoTime();
        logicalSystem = compiler.compile(new ByteArrayInputStream(bytes));
        time = System.nanoTime() - time;
        line.setLength(0);
        if (showLineNumber)
            line.append(++count);  //1
        if (logicalSystem == null) {
            System.err.println("Falha na compilação da fórmula " + formula);
            falha = true;
        } else {
            TruthType baseResult = TruthType.UNKNOWN;
            TruthTable ttt;
            try {
                ttt = new TruthTable(logicalSystem);
                line.append(";" + ttt.getFormula()); 
                line.append(";" + time);  
                long spentTime = ttt.solve2();
                baseResult = ttt.getResult();
                line.append(";" + ttt.getResultName()); 
                line.append(";" + ttt.getTrueCount()); 
                line.append(";" + ttt.getFalseCount()); 
                line.append(";" + spentTime);  
                countResults[ttt.getResult().ordinal()]++;
                countTimes[ttt.getResult().ordinal()] += spentTime;
                countTimesBySolver[0] += spentTime;
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
                    ok = ok & doSolveAndLog(new Tableau(logicalSystem), baseResult, 1);
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
                            new LemmaNodeSelectorFactory()),
                            baseResult, 2);
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
                            baseResult, 3);
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
            line.append("EXCEPTION("+formula+")");
            System.out.println(line);
            return false;
        } else {
            if (ok) {
                acertos++;
                if (this.showAcertos)
                    System.out.println(line);
            } else {
                erros++;
                System.out.println(line);
            }
        }
        return ok;
    }
    
    public void showConfig() {
        for (int i = 0; i < TruthType.values().length; i++)
            countResults[i] = 0;
        System.err.println("######################################");
        System.err.println("Configurações:");
        System.err.println("Gerador de fórmulas   = " + this.formulaGenerator.getName());
        System.err.println("Número de termos      = " + numTermos);
        System.err.println("Número de proposições = " + numProposicoes);
        System.err.println("Número de fórmulas    = " + this.formulaGenerator.getFormulas());
        System.err.println("Número de testes      = " + this.formulaGenerator.getMaxTests());
        System.err.println("Timeout               = " + this.formulaGenerator.getTimeOut() + "ms");
        System.err.println("Saída de dados        = " + filename);
    }
    
    public void showStatistics() {
        long total = this.formulaGenerator.getCount();
        System.err.println("--------------------------------------");
        System.err.println("Fórmulas:");
        System.err.println("  Acertos = " + acertos);
        System.err.println("  Erros   = " + erros);
        System.err.println("  Falhas  = " + falhas);
        System.err.println("  Total   = " + total);
        System.err.println("--------------------------------------");
        System.err.println("Estatísticas básicas por solução:");
        for (int i = 0; i < TruthType.values().length; i++) {
            System.err.println(LogicalReasoning.getResultName(i) + ":");
            System.err.println("  Ocorrências  = " + countResults[i]);
            System.err.println("  Total de nós   = " + countNodes[i]);
            System.err.println("  Total de ramos = " + countBrances[i]);
            System.err.println("  Total de tempo = " + countTimes[i] + "ns");
            if (countResults[i] != 0) {
                System.err.println("  Média de nós   = " + countNodes[i]/countResults[i]);
                System.err.println("  Média de ramos = " + countBrances[i]/countResults[i]);
                System.err.println("  Médio de tempo = " + (countTimes[i])/countResults[i] + "ns");
            }
        }
        System.err.println("Estatísticas básicas por métodos:");
        final String[] solver = {"Tabela verdade", "Tableau Smullyan", "Tableau com Lema", "TableauKE"};
        for (int i = 0; i < 4; i++) {
            System.err.println(solver[i] + ":");
            System.err.println("  Tempo gasto    = " + countTimesBySolver[i] + "ns");
            System.err.println("  Total de nós   = " + countNodesBySolver[i]);
            System.err.println("  Total de ramos = " + countBrancesBySolver[i]);
            if (total != 0) {
                System.err.println("  Média de nós   = " + countNodesBySolver[i]/total);
                System.err.println("  Média de ramos = " + countBrancesBySolver[i]/total);
                System.err.println("  Tempo médio    = " + (countTimesBySolver[i])/total + "ns");
            }
            System.err.println("  Por solução:");
            for (int j = 0; j < TruthType.values().length; j++) {
                System.err.println("    " + LogicalReasoning.getResultName(j) + ":");
                if (countResults[j] != 0) {
                    System.err.println("      Média de nós   = " + countNodesBySolverAndResult[i][j]/countResults[j]);
                    System.err.println("      Média de ramos = " + countBrancesBySolverAndResult[i][j]/countResults[j]);
                    System.err.println("      Médio de tempo = " + (countTimesBySolverAndResult[i][j])/countResults[j] + "ns");
                }
            }
        }
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
        System.err.println("  Unidade de tempo média  = " + (sumTime/efectiveLoops) + "ns");
        System.err.println("  Unidade de tempo máxima = " + maxTime + "ns");
        System.err.println("  Unidade de tempo mínima = " + minTime + "ns");
    }
    
    private void generateRandomFormula() {
        formulaGenerator = new RandomFormulaGenerator(numTermos, numProposicoes);
        formulaGenerator.setMaxTests(this.maxTests);
        formulaGenerator.setTimeOut(this.timeOut);
        showConfig();
        while (formulaGenerator.hasFormula()) {
            System.out.println(formulaGenerator.nextFormula().toString());
        }
    }
    
    private void test() {
        formulaGenerator = formulaGeneratorFactory.newFormulaGenerator(numTermos, numProposicoes);
        formulaGenerator.setMaxTests(this.maxTests);
        formulaGenerator.setTimeOut(this.timeOut);
        showConfig();
        while (formulaGenerator.hasFormula()) {
            verify(formulaGenerator.nextFormula().toString());
        }
        showStatistics();
    }
    
    public void execute() {
        if (dispersion)
            generateRandomFormula();
        else
            test();
    }
    
    private void processCommands(String argv[]) {
        
        // tratador das opções da linha de comando
        Getopt getOpt = new Getopt("tester", argv, ":t:p:m:l:nrdcsfh");
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
            case 'm':
                this.maxTests  = Long.parseLong(getOpt.getOptarg());
                break;
            case 'l':
                this.timeOut  = Long.parseLong(getOpt.getOptarg());
                break;
            case 'n':
                checkContradicition = false;
                break;
            case 'f':
                showAcertos = false; 
                break;
            case 's':
                showLineNumber = false;
                break;
            case 'c':
                formulaGeneratorFactory = new CombinatoryFormulaGeneratorFactory();
                break;
            case 'r':
                formulaGeneratorFactory = new RandomFormulaGeneratorFactory();
                break;
            case 'd':
                dispersion = true; 
                break;
            case 'h':
                System.out.println("ProofTest");
                System.out.println("");
                System.out.println("USO: ProofTest [opcoes] [arquivos com dados]");
                System.out.println("");
                System.out.println("Opções da linha de comando:");
                System.out.println("  -t  = número de termos da fórmula");
                System.out.println("  -p  = número de variáveis (proposições)");
                System.out.println("  -c  = teste combinatório (exponencial)");
                System.out.println("  -r  = teste aleatório");
                System.out.println("  -n  = não faz verificação de contradição");
                System.out.println("  -m  = número máximo de testes");
                System.out.println("  -l  = timeout (mínimo)");
                System.out.println("  -f  = apresenta apenas as falhas");
                System.out.println("  -s  = não apresnta os número das linhas");
                System.out.println("  -d  = gera dados para análise de dispersão");
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
        
        if (formulaGeneratorFactory == null)
            formulaGeneratorFactory = new RandomFormulaGeneratorFactory();
    }
    
    public static void main(String argv[]) {
        long time = System.nanoTime();
        ProofTest test = new ProofTest();
        test.processCommands(argv);
        test.execute();
        test.close();
        time = (System.nanoTime() - time);
        System.err.println("  Tempo de teste          = " + time + "ns");
        if (test.getCount() != 0) 
            System.err.println("  Tempo médio por fórmula = " + (time/test.getCount()) + "ns");
    }
    
}

