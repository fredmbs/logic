POC - Software de prova de fórmulas em sistema de lógica clássica

1) Intalação:

1.1) O projeto foi desenvolvido utilizando:

    a) JAVA SE 1.7 (jdk1.7.0_01)
    b) Eclipse JUNO (Version: 4.2.1 Build id: M20120914-1800e)
    c) Java CUP (11a)
    d) JFlex (Version 1.4.3)
 
    O CUP e o JFLEX estão no diretório LIB e 
    devem ser adicionados nas bibliotecas do proejto.

1.2) O projeto do Eclipse usa um plugin do CUP/JFLEX:
     Para instalar o plugin no Eclipse, existe orientação em:
     http://cup-lex-eclipse.sourceforge.net/installation.html

     O plugin também se encontra na pasta "downloads":
     pi.eclipse.cle.feature-0.1.8.1-20090505-1600.tar.bz2
     
     Mesmo sem o plugin, as classes necessárias para o compilador pode ser
     geradas executando-se o arquivo make.bat (apenas para Windows)

1.3) O software gera arquivos na linguagem "DOT" para criar as imagens 
     das árvores de prova (grafos direcional acíclico)
      
     Por isso, recomenda-se a instalação do GRAPHVIZ 
     (http://www.graphviz.org/Download.php) 

