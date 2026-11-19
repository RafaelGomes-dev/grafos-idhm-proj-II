package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import model.Aresta;
import model.Grafo;
import model.Vertice;


public class PajekIO {
    public static void gravar(Grafo g, String caminho) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(Path.of(caminho), StandardCharsets.UTF_8)) {
            w.write("*Vertices " + g.numVertices());
            w.newLine();
            for (int i = 0; i < g.numVertices(); i++) {
                w.write((i + 1) + " \"" + g.getVertice(i).getRotulo() + "\"");
                w.newLine();
            }

            w.write(g.isDirecionado() ? "*Arcs" : "*Edges");
            w.newLine();
            for (int u = 0; u < g.numVertices(); u++) {
               for (Aresta a : g.getAdjacentes(u)) {
                    int v = a.getDestino();
                    // não direcionado: cada aresta aparece 2x na adjacência, grava só 1x
                    if (g.isDirecionado() || u < v) {
                        w.write((u + 1) + " " + (v + 1) + " " + a.getPeso());
                        w.newLine();
                    }
               } 
            }
        }
    }

    public static Grafo carregar(String caminho) throws IOException {
        String[] rotulos = null;
        Grafo g = null;

        try (BufferedReader r = Files.newBufferedReader(Path.of(caminho), StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = r.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("%")) continue; // vazia ou comentário
                String minus = linha.toLowerCase();

                if (minus.startsWith("*vertices")) {
                    int n = Integer.parseInt(linha.split("\\s+")[1]);
                    rotulos = new String[n];
                    for (int i = 0; i < n; i++) rotulos[i] = String.valueOf(i + 1); // padrão

                } else if (minus.startsWith("*edges") || minus.startsWith("*arcs")) {
                    g = new Grafo(minus.startsWith("*arcs"));
                    for (int i = 0; i < rotulos.length; i++) {
                        g.adicionarVertice(new Vertice(i, rotulos[i]));
                    }
                } else if (g == null) {
                   // linha de vértice: 1 "CURITIBA (PR)"
                   int id = Integer.parseInt(linha.split("\\s+")[0]) - 1;
                   int ini = linha.indexOf('"');
                   int fim = linha.lastIndexOf('"');
                   if (ini >= 0 && fim > ini) rotulos[id] = linha.substring(ini + 1, fim);
                } else {
                    // linha de aresta: 1 2 12.5 (peso opcional)
                    String[] p = linha.split("\\s+");
                    int u = Integer.parseInt(p[0]) - 1;
                    int v = Integer.parseInt(p[1]) - 1;
                    double peso = (p.length >= 3) ? Double.parseDouble(p[2]) : 1.0;
                    g.adicionarAresta(u, v, peso);
                }
            }
        }

        return g;
    }
}
