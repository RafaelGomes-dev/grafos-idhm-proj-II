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

/**
 * Grava e carrega grafos no FORMATO PAJEK (.net), um arquivo de texto padrão:
 *
 *   *Vertices 3
 *   1 "CURITIBA (PR)"
 *   2 "LONDRINA (PR)"
 *   3 "MARINGA (PR)"
 *   *Edges            <- não direcionado (*Arcs = direcionado)
 *   1 2 12.5          <- origem destino peso
 *
 * ATENÇÃO: o Pajek numera a partir de 1; nosso código, a partir de 0.
 * Por isso: +1 ao gravar, -1 ao carregar.
 */
public class PajekIO {

    public static void gravar(Grafo g, String caminho) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(Path.of(caminho), StandardCharsets.UTF_8)) {

            // 1) Seção de vértices
            w.write("*Vertices " + g.numVertices());
            w.newLine();
            for (int i = 0; i < g.numVertices(); i++) {
                w.write((i + 1) + " \"" + g.getVertice(i).getRotulo() + "\"");   // +1 !
                w.newLine();
            }

            // 2) Seção de arestas
            w.write(g.isDirecionado() ? "*Arcs" : "*Edges");
            w.newLine();
            for (int u = 0; u < g.numVertices(); u++) {
                for (Aresta a : g.getAdjacentes(u)) {
                    int v = a.getDestino();
                    // Não direcionado: cada aresta está 2x na adjacência (u->v e v->u).
                    // Gravando só quando u < v, ela aparece UMA vez no arquivo.
                    if (g.isDirecionado() || u < v) {
                        // Concatenar o double usa Double.toString: sempre com PONTO
                        // e precisão total (String.format em PT-BR usaria vírgula).
                        w.write((u + 1) + " " + (v + 1) + " " + a.getPeso());
                        w.newLine();
                    }
                }
            }
        }
    }

    public static Grafo carregar(String caminho) throws IOException {
        String[] rotulos = null;   // rótulos lidos na seção *Vertices
        Grafo g = null;            // só é criado ao chegar em *Edges/*Arcs

        try (BufferedReader r = Files.newBufferedReader(Path.of(caminho), StandardCharsets.UTF_8)) {
            String linha;
            while ((linha = r.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("%")) continue;   // vazia ou comentário
                String minus = linha.toLowerCase();                       // aceita *VERTICES, *vertices...

                if (minus.startsWith("*vertices")) {
                    // "*Vertices 5565" -> n = 5565
                    int n = Integer.parseInt(linha.split("\\s+")[1]);
                    rotulos = new String[n];
                    for (int i = 0; i < n; i++) rotulos[i] = String.valueOf(i + 1);   // rótulo padrão

                } else if (minus.startsWith("*edges") || minus.startsWith("*arcs")) {
                    // Agora sabemos se é direcionado: cria o grafo e os vértices
                    g = new Grafo(minus.startsWith("*arcs"));
                    for (int i = 0; i < rotulos.length; i++) {
                        g.adicionarVertice(new Vertice(i, rotulos[i]));
                    }

                } else if (g == null) {
                    // Ainda na seção de vértices: 1 "CURITIBA (PR)"
                    int id = Integer.parseInt(linha.split("\\s+")[0]) - 1;   // -1 !
                    int ini = linha.indexOf('"');
                    int fim = linha.lastIndexOf('"');
                    if (ini >= 0 && fim > ini) rotulos[id] = linha.substring(ini + 1, fim);

                } else {
                    // Seção de arestas: 1 2 12.5 (peso opcional, padrão 1.0)
                    String[] p = linha.split("\\s+");
                    int u = Integer.parseInt(p[0]) - 1;   // -1 !
                    int v = Integer.parseInt(p[1]) - 1;
                    double peso = (p.length >= 3) ? Double.parseDouble(p[2]) : 1.0;
                    g.adicionarAresta(u, v, peso);
                }
            }
        }
        return g;
    }
}
