package app;

import builder.ConstrutorSimilaridade;
import io.LeitorMunicipios;
import model.Grafo;

public class Main {
    public static void main(String[] args) throws Exception {
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
        System.out.println("Vértices: " + g.numVertices());
        System.out.println("Arestas: " + g.numArestas());
        System.out.println("Grau min/max/médio: " + min + " / " + max + " / " + (double) soma / g.numVertices());
        System.out.println("Tempo: " + (t1 - t0) + " ms");
    }
}