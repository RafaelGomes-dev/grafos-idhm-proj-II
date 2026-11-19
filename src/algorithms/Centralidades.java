package algorithms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import model.Aresta;
import model.Grafo;

/**
 * Centralidades usando o MELHOR CAMINHO (menor soma de pesos), via Dijkstra.
 */
public class Centralidades {

    // Tolerância para comparar distâncias double: caminhos matematicamente
    // iguais podem diferir na última casa decimal e devem contar como empate.
    private static final double EPS = 1e-9;

    /**
     * DIJKSTRA: menor distância de s até todos os vértices.
     * Funciona porque todos os pesos são positivos (>= 1): quando um vértice
     * sai da fila, não existe caminho mais curto até ele.
     * Complexidade: O((V + E) log V).
     */
    public static double[] dijkstra(Grafo g, int s) {
        int n = g.numVertices();
        double[] dist = new double[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);   // ninguém alcançado ainda
        boolean[] finalizado = new boolean[n];

        // Fila de prioridade de pares {distância, vértice}, menor distância primeiro
        PriorityQueue<double[]> fila = new PriorityQueue<>((a, b) -> Double.compare(a[0], b[0]));

        dist[s] = 0;
        fila.add(new double[]{0, s});
        while (!fila.isEmpty()) {
            int u = (int) fila.poll()[1];       // o mais próximo ainda não finalizado
            // "Lazy deletion": em vez de atualizar a posição na fila, colocamos
            // uma entrada nova e ignoramos as antigas aqui.
            if (finalizado[u]) continue;
            finalizado[u] = true;

            // RELAXAR as arestas de u: achou caminho melhor até v passando por u?
            for (Aresta a : g.getAdjacentes(u)) {
                int v = a.getDestino();
                double nd = dist[u] + a.getPeso();
                if (nd < dist[v]) {
                    dist[v] = nd;
                    fila.add(new double[]{nd, v});
                }
            }
        }
        return dist;
    }

    /**
     * CENTRALIDADE DE PROXIMIDADE (closeness): quão perto o vértice está de todos.
     *   proximidade = ((r - 1) / (n - 1)) * ((r - 1) / soma das distâncias)
     * r = alcançados (incluindo ele). No grafo conexo r = n e vira (n-1)/soma.
     * O 1º fator corrige grafos desconexos: quem alcança pouca gente não
     * parece "central" à toa.
     * Complexidade: n Dijkstras = O(V * (V + E) log V).
     */
    public static double[] proximidade(Grafo g) {
        int n = g.numVertices();
        double[] c = new double[n];
        for (int s = 0; s < n; s++) {
            double[] dist = dijkstra(g, s);
            double soma = 0;
            int alcancados = 0;   // conta o próprio s
            for (int v = 0; v < n; v++) {
                if (dist[v] != Double.POSITIVE_INFINITY) {
                    soma += dist[v];
                    alcancados++;
                }
            }
            if (alcancados > 1 && soma > 0) {   // não alcança ninguém -> fica 0
                double r1 = alcancados - 1;
                c[s] = (r1 / (n - 1)) * (r1 / soma);
            }
        }
        return c;
    }

    /**
     * CENTRALIDADE DE INTERMEDIAÇÃO (betweenness) — ALGORITMO DE BRANDES.
     * Mede quantos caminhos mínimos entre outros pares passam pelo vértice.
     * Para cada origem s: (1) Dijkstra contando caminhos mínimos;
     * (2) acumulação do mais longe para o mais perto.
     * Complexidade: O(V * (V + E) log V).
     */
    public static double[] intermediacao(Grafo g) {
        int n = g.numVertices();
        double[] cb = new double[n];   // resultado acumulado de todas as origens

        for (int s = 0; s < n; s++) {
            double[] dist = new double[n];
            double[] sigma = new double[n];   // sigma[v] = nº de caminhos mínimos de s até v
            double[] delta = new double[n];   // delta[v] = dependência acumulada de s em v
            boolean[] finalizado = new boolean[n];
            List<List<Integer>> pred = new ArrayList<>();   // pred[v] = quem vem antes de v
            for (int i = 0; i < n; i++) pred.add(new ArrayList<>());
            Arrays.fill(dist, Double.POSITIVE_INFINITY);

            // Pilha com a ordem de finalização (do mais perto ao mais longe)
            ArrayDeque<Integer> ordem = new ArrayDeque<>();
            PriorityQueue<double[]> fila = new PriorityQueue<>((a, b) -> Double.compare(a[0], b[0]));
            dist[s] = 0;
            sigma[s] = 1;   // um caminho de s até ele mesmo
            fila.add(new double[]{0, s});

            // ---- FASE 1: Dijkstra contando caminhos e guardando predecessores ----
            while (!fila.isEmpty()) {
                int u = (int) fila.poll()[1];
                if (finalizado[u]) continue;
                finalizado[u] = true;
                ordem.push(u);
                for (Aresta a : g.getAdjacentes(u)) {
                    int v = a.getDestino();
                    double nd = dist[u] + a.getPeso();
                    if (nd < dist[v] - EPS) {
                        // Caminho ESTRITAMENTE melhor: v herda a contagem de u
                        dist[v] = nd;
                        sigma[v] = sigma[u];
                        pred.get(v).clear();
                        pred.get(v).add(u);
                        fila.add(new double[]{nd, v});
                    } else if (Math.abs(nd - dist[v]) <= EPS) {
                        // EMPATE: mais um caminho mínimo até v, vindo por u
                        sigma[v] += sigma[u];
                        pred.get(v).add(u);
                    }
                }
            }

            // ---- FASE 2: acumulação, do mais longe para o mais perto ----
            // Cada w repassa aos predecessores v a fração sigma[v]/sigma[w]
            // de (1 + delta[w]): o "1" é o caminho até o próprio w,
            // delta[w] são os caminhos que continuam depois de w.
            while (!ordem.isEmpty()) {
                int w = ordem.pop();
                for (int v : pred.get(w)) {
                    delta[v] += (sigma[v] / sigma[w]) * (1 + delta[w]);
                }
                if (w != s) cb[w] += delta[w];   // a origem não conta para si mesma
            }
        }

        // Não direcionado: cada par foi contado duas vezes (s->t e t->s)
        if (!g.isDirecionado()) {
            for (int i = 0; i < n; i++) cb[i] /= 2;
        }
        return cb;
    }
}
