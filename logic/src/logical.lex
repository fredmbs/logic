import java_cup.runtime.*;

%%
%unicode
%line
%column
%cup
/* %cupdebug */
%standalone 
 
%{

  // flag de debug mode
  boolean debug = true;
  // define o debug mode
  public void setDebug(boolean has) { debug = has; };
  public boolean getDebug() { return debug; };
  public boolean isEOF() { return zzAtEOF; };
  
  // variável de contagem de erros léxicos
  Integer lexicalErrors = 0;
  // verifica se existe erro
  public boolean hasError() { return lexicalErrors > 0; };
  // retorna o número de erros léxicos encontrados até o momento
  public Integer getNumErrors() { return lexicalErrors; };

  // registra a ocorrência de um erro léxico
  // mostrando mensagem no console de erro
  public void yyerror(String error) {
    lexicalErrors++;
    System.err.println("!> Erro léxico @(" + (yyline+1) + "," + (yycolumn+1) 
                       + ") Lexema: " + error);
  }
  
  //mostra na saída do console um token encontrado
  private void printToken(int type, String lexema) {
    System.out.println("Token = "+String.format("%5s",type)+" Lexema = "+lexema);
  }

  // cria um objeto Symbol do tipo de token especificado
  private Symbol symbol(int type) {
    if (debug) {
        printToken(type, yytext());
    }
    return new Symbol(type, yyline, yycolumn, yytext());
  }

  // cria um objeto Symbol do tipo de token especificado
  // adicionado o símbolo na tabela de símbolos
  private Symbol symbolTable(int type, String text) {
    if (debug) {
        printToken(type, yytext());
    }
    return new Symbol(type, yyline, yycolumn, yytext());
  }

  // cria um objeto Symbol do tipo de token especificado
  // associando o símbolo a um objeto JAVA (value)
  /*
  private Symbol symbol(int type, Object value) {
    if (debug) {
        printToken(type, yytext());
    }
    return new Symbol(type, yyline, yycolumn, value);
  }
  */
  
  // mostra resultado final da análise léxica
  public void showSummary() {
    if (hasError()) {
        System.err.println("----------------------------------------------------");
        if (getNumErrors() == 1) {
            System.err.println("!!! A expressão POSSUI 1 ERRO léxico !!!");
        } else {
            System.err.println("!!! A expressão POSSUI " + getNumErrors() + " ERROS LEXICOS !!!");
        }
        System.err.println("----------------------------------------------------");
    } else if (debug) {
        System.out.println("----------------------------------------------------");
        System.out.println(" A expressão passou sem erro léxico !!!");
        System.out.println("----------------------------------------------------");
    }
  } 
 
%}

/* Definições */

line_terminator = \r|\n|\r\n
nulos           = [ \n\r\t]
lower_letter    = [a-z]
upper_letter    = [A-Z]
letter          = [a-zA-Z]
digit           = [0-9]
identifier      = {lower_letter}({lower_letter}|{digit}|"_")*
predicate       = {upper_letter}({letter}|{digit}|"_")*
comment_content = [^{]
comment         = {comment_content}* | {nulos}*

%x COMMENT

%%

/*Regras*/

{nulos}            { }
"{"{comment}"}"    { }

"("                { return symbol(LogicalSym.LPAREN); }
")"                { return symbol(LogicalSym.RPAREN); }

"="                { return symbol(LogicalSym.EQUALITY); }
 
"^"                { return symbol(LogicalSym.AND); }
"˄"                { return symbol(LogicalSym.AND); }
"*"                { return symbol(LogicalSym.AND); }
"&"                { return symbol(LogicalSym.AND); }
"and"              { return symbol(LogicalSym.AND); }

"v"                { return symbol(LogicalSym.OR); }
"˅"                { return symbol(LogicalSym.OR); }
"+"                { return symbol(LogicalSym.OR); }
"|"                { return symbol(LogicalSym.OR); }
"||"               { return symbol(LogicalSym.OR); }
"or"               { return symbol(LogicalSym.OR); }

"not"              { return symbol(LogicalSym.NOT); }
"!"                { return symbol(LogicalSym.NOT); }
"~"                { return symbol(LogicalSym.NOT); }

"->"               { return symbol(LogicalSym.IMPLIES); }
"=>"               { return symbol(LogicalSym.IMPLIES); }
/*
"→"                { return symbol(LogicalSym.IMPLIES); }
*/

"<-"               { return symbol(LogicalSym.REV_IMPLIES); }
"<="               { return symbol(LogicalSym.REV_IMPLIES); }

"<->"              { return symbol(LogicalSym.EQUIVALENT); }
"<=>"              { return symbol(LogicalSym.EQUIVALENT); }
"↔"                { return symbol(LogicalSym.EQUIVALENT); }

"|="               { return symbol(LogicalSym.ENTAILMENT); }
"|-"               { return symbol(LogicalSym.DERIVATION); }
 
/*
"TRUE"             { return symbol(LogicalSym.TRUE); }
"FALSE"            { return symbol(LogicalSym.FALSE); }
*/

","                { return symbol(LogicalSym.COMMA); }
":"                { return symbol(LogicalSym.COLON); }

"Fa"               { return symbol(LogicalSym.FORALL); } 
"Ex"               { return symbol(LogicalSym.EXISTS); } 
"Forall"           { return symbol(LogicalSym.FORALL); } 
"Exists"           { return symbol(LogicalSym.EXISTS); } 

{identifier}       { return symbolTable(LogicalSym.IDENTIFIER, yytext()); }
{predicate}        { return symbolTable(LogicalSym.PREDICATE, yytext()); }
<<EOF>>            { Symbol eofSymbol = symbol(LogicalSym.EOF);
                     debug = false;
                     return eofSymbol; 
                   }
{line_terminator}  { } 
.                  { yyerror(yytext()); } 
