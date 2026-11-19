package app;

import algorithms.Conectividade;
import generator.GeradorAleatorio;
import model.Grafo;

public class Main {
    public static void main(String[] args) {
        Grafo a = GeradorAleatorio.gerar(1000, 5000, true, 42);
        System.out.println("Conexo pedido -> arestas: " + a.numArestas()
            + " | conexo? " + Conectividade.isConexo(a));

        Grafo b = GeradorAleatorio.gerar(1000, 5000, false, 42);
        System.out.println("Desconexo pedido -> arestas: " + b.numArestas()
            + " | conexo? " + Conectividade.isConexo(b)
            + " | componentes: " + Conectividade.componentes(b).size());

        Grafo c = GeradorAleatorio.gerar(10, 9, true, 7);
        System.out.println("Árvore (10 vértices, 9 arestas) -> conexo? " + Conectividade.isConexo(c)
            + " | cíclico? " + algorithms.CiclosEuler.isCiclico(c));

        try {
            GeradorAleatorio.gerar(10, 5, true, 1);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro esperado: " + e.getMessage());
        }
        try {
            GeradorAleatorio.gerar(10, 37, false, 1);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro esperado: " + e.getMessage());
        }
    }
}