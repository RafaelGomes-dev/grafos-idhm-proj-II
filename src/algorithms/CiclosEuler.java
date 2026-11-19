package algorithms;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

import model.Aresta;
import model.Grafo;

/**
 * Verifica se o grafo é CÍCLICO e se é EULERIANO.
 */
public class CiclosEuler {

    // ===================== CICLOS =====================

    public static boolean isCiclico(Grafo g) {
        return g.isDirecionado() ? ciclicoDirecionado(g) : ciclicoNaoDirecionado(g);
    }

    /**
     * Não direcionado: busca em profundidade guardando o PAI de cada vértice
     * (de onde a busca chegou nele). As arestas que descobrem vértices novos
     * formam uma árvore. Qualquer OUTRA aresta para um vértice já visitado é
     * um segundo caminho até ele -> forma ciclo.
     *
     * Pilha explícita (ArrayDeque) em vez de recursão: com milhares de vértices
     * a recursão poderia estourar a pilha do Java (StackOverflowError).
     */
    private static boolean ciclicoNaoDirecionado(Grafo g) {
        int n = g.numVertices();
        boolean[] visitado = new boolean[n];
        int[] pai = new int[n];
        Arrays.fill(pai, -1);   // -1 = sem pai (raiz da busca)
        ArrayDeque<Integer> pilha = new ArrayDeque<>();

        for (int s = 0; s < n; s++) {          // cobre todos os componentes
            if (visitado[s]) continue;
            visitado[s] = true;
            pilha.push(s);
            while (!pilha.isEmpty()) {
                int u = pilha.pop();
                for (Aresta a : g.getAdjacentes(u)) {
                    int v = a.getDestino();
                    if (!visitado[v]) {
                        visitado[v] = true;
                        pai[v] = u;
                        pilha.push(v);
                    } else if (pai[u] != v && pai[v] != u) {
                        // v já visitado e a aresta u–v NÃO é a ligação pai–filho
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Direcionado: algoritmo de KAHN. Remove repetidamente quem tem grau de
     * entrada 0. Num grafo sem ciclo sempre existe alguém assim; se sobrar
     * vértice que nunca chegou a 0, ele está num ciclo.
     */
    private static boolean ciclicoDirecionado(Grafo g) {
        int n = g.numVertices();
        int[] entrada = grausEntrada(g);
        ArrayDeque<Integer> fila = new ArrayDeque<>();
        for (int u = 0; u < n; u++) if (entrada[u] == 0) fila.add(u);

        int removidos = 0;
        while (!fila.isEmpty()) {
            int u = fila.poll();
            removidos++;
            for (Aresta a : g.getAdjacentes(u)) {
                // "remove" u: os vizinhos perdem uma seta chegando
                if (--entrada[a.getDestino()] == 0) fila.add(a.getDestino());
            }
        }
        return removidos < n;   // sobrou alguém -> tem ciclo
    }

    // ===================== EULER =====================
    // Teorema de Euler: um percurso que usa cada aresta uma vez entra e sai
    // de cada vértice gastando as arestas AOS PARES -> graus precisam ser pares.

    /** Circuito euleriano: passa por todas as arestas uma vez e VOLTA ao início. */
    public static boolean temCircuitoEuleriano(Grafo g) {
        if (!arestasNumComponente(g)) return false;   // condição 1: conectividade
        int[] entrada = grausEntrada(g);
        for (int u = 0; u < g.numVertices(); u++) {
            // condição 2 (não direcionado): todo grau par
            if (!g.isDirecionado() && g.grau(u) % 2 != 0) return false;
            // condição 2 (direcionado): entrada = saída em todos
            if (g.isDirecionado() && g.grau(u) != entrada[u]) return false;
        }
        return true;
    }

    /** Trilha euleriana aberta: passa por todas, mas termina em OUTRO vértice. */
    public static boolean temTrilhaEuleriana(Grafo g) {
        if (!arestasNumComponente(g)) return false;
        int[] entrada = grausEntrada(g);

        if (!g.isDirecionado()) {
            // Exatamente 2 vértices de grau ímpar: são o início e o fim
            int impares = 0;
            for (int u = 0; u < g.numVertices(); u++) if (g.grau(u) % 2 != 0) impares++;
            return impares == 2;
        }
        // Direcionado: 1 com saída = entrada + 1 (início), 1 com entrada = saída + 1 (fim)
        int inicio = 0, fim = 0;
        for (int u = 0; u < g.numVertices(); u++) {
            int dif = g.grau(u) - entrada[u];   // saída - entrada
            if (dif == 1) inicio++;
            else if (dif == -1) fim++;
            else if (dif != 0) return false;
        }
        return inicio == 1 && fim == 1;
    }

    /**
     * Todos os vértices que têm aresta estão no mesmo componente?
     * Vértices isolados (grau 0) não atrapalham: não têm aresta para percorrer.
     * Reaproveita a BFS da Conectividade.
     */
    private static boolean arestasNumComponente(Grafo g) {
        int[] entrada = grausEntrada(g);
        int compsComAresta = 0;
        for (List<Integer> comp : Conectividade.componentes(g)) {
            for (int v : comp) {
                if (g.grau(v) > 0 || entrada[v] > 0) {
                    compsComAresta++;
                    break;   // basta um vértice com aresta
                }
            }
        }
        return compsComAresta <= 1;
    }

    // entrada[v] = quantas arestas chegam em v
    private static int[] grausEntrada(Grafo g) {
        int[] entrada = new int[g.numVertices()];
        for (int u = 0; u < g.numVertices(); u++) {
            for (Aresta a : g.getAdjacentes(u)) entrada[a.getDestino()]++;
        }
        return entrada;
    }
    // Complexidade de tudo nesta classe: O(V + E).
}
