@echo off
REM POC - Ci�ncia da Computa��o - UNI-BH 
REM Gerador de compilador de f�rmula em "fisrt-order logic"
REM Aluno: Frederico Martins Biber Sampaio
REM 01/12/2012
cls

@REM Ajuste o caminho para o Java SDK do computador espec�fico
set JAVA_HOME="C:\Program Files\Java\jdk1.7.0_01"

cd ..
set CUP_LIB="%CD%\lib\java-cup-11a.jar"
set JFLEX_LIB="%CD%\lib\JFlex.jar"
set GETOPT_LIB="%CD%\lib\java-getopt-1.0.9.jar"
@REM set CLASSPATH=%JFLEX_LIB%;%CUP_LIB%;%GETOPT_LIB%
cd src

set LANG_NAME="FolFormula"
set COMP_LEX="Yylex"
set COMP_SYM="LogicalSym"
set COMP_SEM="LogicalCup"

@echo ========================================================================
@echo Iniciando: Excluindo arquivos gerados anteriormente
@echo ------------------------------------------------------------------------
IF EXIST "sym.java" del "sym.java" 
IF EXIST "%COMP_LEX%.java" del "%COMP_LEX%.java"
IF EXIST "%COMP_SEM%.java" del "%COMP_SEM%.java"
IF EXIST "sym.class" del "sym.class" 
IF EXIST "%COMP_LEX%.class" del %COMP_LEX%.class
IF EXIST "%COMP_SEM%.class" del "%COMP_SEM%.class"
IF EXIST "%COMP_SYM%.class" del "%COMP_SYM%.class"
IF EXIST "CUP$parser$actions.class" del "CUP$parser$actions.class"

IF "%1"=="clean" GOTO FIM

@echo ========================================================================
@echo JFLEX: Criando o programa fonte do analisador lexico
@echo ------------------------------------------------------------------------
%JAVA_HOME%\bin\java -jar %JFLEX_LIB% %LANG_NAME%.lex

@echo ========================================================================
@echo CUP: Criando o programa  fonte do analisador sintatico
@echo ------------------------------------------------------------------------
REM %JAVA_HOME%\bin\java -jar java-cup-11a.jar -expect 1 %LANG_NAME%.cup
%JAVA_HOME%\bin\java -jar %CUP_LIB% -symbols %COMP_SYM% -parser %COMP_SEM% %LANG_NAME%.cup

:FIM
IF EXIST "*~" del "*~"
pause