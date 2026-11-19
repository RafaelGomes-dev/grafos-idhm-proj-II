package app;

import algorithms.CiclosEuler;
import algorithms.Conectividade;
import builder.ConstrutorSimilaridade;
import io.LeitorMunicipios;
import model.Grafo;
import model.Vertice;

public class Main {
    public static void main(String[] args) throws Exception {

        // ===== GRAFO REAL =====
        long t0 = System.currentTimeMillis();
        Grafo g = LeitorMunicipios.ler("dados/municipios_idhm_2010.csv");
        ConstrutorSimilaridade.conectar(g, 8);
        long t1 = System.currentTimeMillis();

        int min = Integer.MAX_VALUE, max = 0, soma = 0;
        for (int i = 0; i < g.numVertices(); i++) {
            int gr = g.grau(i);
            min = Math.min(min, gr);
            max = Math.max(max, gr);
            soma += gr;
        }
        System.out.println("===== GRAFO REAL =====");
        System.out.println("Vértices: " + g.numVertices());
        System.out.println("Arestas: " + g.numArestas());
        System.out.println("Grau min/max/médio: " + min + " / " + max + " / " + (double) soma / g.numVertices());
        System.out.println("Tempo de construção: " + (t1 - t0) + " ms");
        System.out.println("Conexo? " + Conectividade.isConexo(g));
        System.out.println("Componentes: " + Conectividade.componentes(g).size());
        System.out.println("Cíclico? " + CiclosEuler.isCiclico(g));
        System.out.println("Circuito euleriano? " + CiclosEuler.temCircuitoEuleriano(g));
        System.out.println("Trilha euleriana? " + CiclosEuler.temTrilhaEuleriana(g));

        // ===== GRAFO PEQUENO DESCONEXO: {0,1,2} e {3,4} =====
        Grafo p = new Grafo(false);
        for (int i = 0; i < 5; i++) p.adicionarVertice(new Vertice(i, "V" + i));
        p.adicionarAresta(0, 1, 1);
        p.adicionarAresta(1, 2, 1);
        p.adicionarAresta(3, 4, 1);
        System.out.println("\n===== DESCONEXO =====");
        System.out.println("Conexo? " + Conectividade.isConexo(p));
        System.out.println("Componentes: " + Conectividade.componentes(p));

        // ===== TRIÂNGULO: tem ciclo e circuito euleriano =====
        Grafo tri = new Grafo(false);
        for (int i = 0; i < 3; i++) tri.adicionarVertice(new Vertice(i, "T" + i));
        tri.adicionarAresta(0, 1, 1);
        tri.adicionarAresta(1, 2, 1);
        tri.adicionarAresta(2, 0, 1);
        System.out.println("\n===== TRIÂNGULO =====");
        System.out.println("Cíclico? " + CiclosEuler.isCiclico(tri));
        System.out.println("Circuito? " + CiclosEuler.temCircuitoEuleriano(tri));
        System.out.println("Trilha? " + CiclosEuler.temTrilhaEuleriana(tri));

        // ===== CAMINHO 0-1-2: sem ciclo, só trilha =====
        Grafo cam = new Grafo(false);
        for (int i = 0; i < 3; i++) cam.adicionarVertice(new Vertice(i, "C" + i));
        cam.adicionarAresta(0, 1, 1);
        cam.adicionarAresta(1, 2, 1);
        System.out.println("\n===== CAMINHO =====");
        System.out.println("Cíclico? " + CiclosEuler.isCiclico(cam));
        System.out.println("Circuito? " + CiclosEuler.temCircuitoEuleriano(cam));
        System.out.println("Trilha? " + CiclosEuler.temTrilhaEuleriana(cam));

        // ===== DIRECIONADO: 0->1->2->0 (ciclo) =====
        Grafo dir = new Grafo(true);
        for (int i = 0; i < 3; i++) dir.adicionarVertice(new Vertice(i, "D" + i));
        dir.adicionarAresta(0, 1, 1);
        dir.adicionarAresta(1, 2, 1);
        dir.adicionarAresta(2, 0, 1);
        System.out.println("\n===== DIRECIONADO =====");
        System.out.println("Cíclico? " + CiclosEuler.isCiclico(dir));
        System.out.println("Circuito? " + CiclosEuler.temCircuitoEuleriano(dir));
    }
}