package algorithms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import model.Aresta;
import model.Grafo;

public class Centralidades {

    private static final double EPS = 1e-9; // tolerância para comparar distâncias double

    // Dijkstra a partir de s: devolve a menor distância até cada vértice
    public static double[] dijkstra(Grafo g, int s) {
        int n = g.numVertices();
        double[] dist = new double[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        boolean[] finalizado = new boolean[n];
        PriorityQueue<double[]> fila = new PriorityQueue<>((a, b) -> Double.compare(a[0], b[0]));

        dist[s] = 0;
        fila.add(new double[]{0, s});
        while (!fila.isEmpty()) {
            int u = (int) fila.poll()[1];
            if (finalizado[u]) continue; // entrada antiga na fila
            finalizado[u] = true;
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

    // Proximidade de todos os vértices (com correção para desconexos)
    public static double[] proximidade(Grafo g) {
        int n = g.numVertices();
        double[] c = new double[n];
        for (int s = 0; s < n; s++) {
            double[] dist = dijkstra(g, s);
            double soma = 0;
            int alcancados = 0; // conta o próprio s
            for (int v = 0; v < n; v++) {
                if (dist[v] != Double.POSITIVE_INFINITY) {
                    soma += dist[v];
                    alcancados++;
                }
            }
            if (alcancados > 1 && soma > 0) {
                double r1 = alcancados - 1;
                c[s] = (r1 / (n - 1)) * (r1 / soma);
            }
        }
        return c;
    }

    // Intermediação de todos os vértices (Brandes com Dijkstra)
    public static double[] intermediacao(Grafo g) {
        int n = g.numVertices();
        double[] cb = new double[n];

        for (int s = 0; s < n; s++) {
            double[] dist = new double[n];
            double[] sigma = new double[n];   // nº de caminhos mínimos de s até v
            double[] delta = new double[n];   // dependência acumulada
            boolean[] finalizado = new boolean[n];
            List<List<Integer>> pred = new ArrayList<>();
            for (int i = 0; i < n; i++) pred.add(new ArrayList<>());
            Arrays.fill(dist, Double.POSITIVE_INFINITY);

            ArrayDeque<Integer> ordem = new ArrayDeque<>(); // pilha: ordem de finalização
            PriorityQueue<double[]> fila = new PriorityQueue<>((a, b) -> Double.compare(a[0], b[0]));
            dist[s] = 0;
            sigma[s] = 1;
            fila.add(new double[]{0, s});

            // 1) Dijkstra contando caminhos mínimos e guardando predecessores
            while (!fila.isEmpty()) {
                int u = (int) fila.poll()[1];
                if (finalizado[u]) continue;
                finalizado[u] = true;
                ordem.push(u);
                for (Aresta a : g.getAdjacentes(u)) {
                    int v = a.getDestino();
                    double nd = dist[u] + a.getPeso();
                    if (nd < dist[v] - EPS) {            // caminho estritamente melhor
                        dist[v] = nd;
                        sigma[v] = sigma[u];
                        pred.get(v).clear();
                        pred.get(v).add(u);
                        fila.add(new double[]{nd, v});
                    } else if (Math.abs(nd - dist[v]) <= EPS) { // empate: outro caminho mínimo
                        sigma[v] += sigma[u];
                        pred.get(v).add(u);
                    }
                }
            }

            // 2) Acumula dependências do mais longe para o mais perto
            while (!ordem.isEmpty()) {
                int w = ordem.pop();
                for (int v : pred.get(w)) {
                    delta[v] += (sigma[v] / sigma[w]) * (1 + delta[w]);
                }
                if (w != s) cb[w] += delta[w];
            }
        }

        if (!g.isDirecionado()) {
            for (int i = 0; i < n; i++) cb[i] /= 2; // cada par contado 2x
        }
        return cb;
    }
}