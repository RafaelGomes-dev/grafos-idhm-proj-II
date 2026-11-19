package generator;

import java.util.Random;

import model.Grafo;
import model.Vertice;

/**
 * Gera um grafo aleatório NÃO direcionado com exatamente n vértices e m arestas,
 * conexo ou desconexo. É separado da aplicação real (o grafo de municípios
 * NÃO usa o gerador, como o enunciado exige).
 */
public class GeradorAleatorio {

    /**
     * @param semente mesma semente = mesmo grafo (resultado reproduzível)
     */
    public static Grafo gerar(int n, int m, boolean conexo, long semente) {
        validar(n, m, conexo);   // recusa pedidos impossíveis (senão travaria)
        Random rnd = new Random(semente);

        Grafo g = new Grafo(false);
        for (int i = 0; i < n; i++) g.adicionarVertice(new Vertice(i, "V" + (i + 1)));

        // grupo[v]: só se criam arestas entre vértices do MESMO grupo.
        // No conexo, todos ficam no grupo 0.
        int[] grupo = new int[n];

        if (conexo) {
            // ÁRVORE ALEATÓRIA: embaralha e liga cada vértice (a partir do 2º)
            // a um sorteado entre os que já entraram. Resultado: n-1 arestas,
            // conexo e sem ciclo, porque cada novo se pendura em alguém já ligado.
            int[] ordem = embaralhar(n, rnd);
            for (int i = 1; i < n; i++) {
                int u = ordem[i];
                int v = ordem[rnd.nextInt(i)];   // algum entre ordem[0..i-1]
                g.adicionarAresta(u, v, pesoAleatorio(rnd));
            }
        } else {
            // DOIS GRUPOS: os 'a' primeiros da ordem ficam no grupo 0, o resto no 1.
            // Como nunca há aresta entre grupos, a desconexão é GARANTIDA.
            int a = escolherDivisao(n, m, rnd);
            int[] ordem = embaralhar(n, rnd);
            for (int i = a; i < n; i++) grupo[ordem[i]] = 1;
        }

        // Completa até m arestas sorteando pares do mesmo grupo.
        // O Grafo recusa laço e duplicata, então só para com m arestas válidas.
        while (g.numArestas() < m) {
            int u = rnd.nextInt(n);
            int v = rnd.nextInt(n);
            if (grupo[u] == grupo[v]) {
                g.adicionarAresta(u, v, pesoAleatorio(rnd));
            }
        }
        return g;
    }

    /**
     * Conexo:    n-1 <= m <= n(n-1)/2   (mínimo = árvore; máximo = grafo completo)
     * Desconexo: n >= 2 e m <= (n-1)(n-2)/2  (completo em n-1 vértices + 1 isolado)
     * Usa long porque n*n pode estourar o int.
     */
    private static void validar(int n, int m, boolean conexo) {
        if (n < 1 || m < 0) throw new IllegalArgumentException("n deve ser >= 1 e m >= 0");
        long max = (long) n * (n - 1) / 2;
        if (conexo) {
            if (m < n - 1) throw new IllegalArgumentException("Conexo precisa de pelo menos n-1 = " + (n - 1) + " arestas");
            if (m > max) throw new IllegalArgumentException("Máximo possível: " + max + " arestas");
        } else {
            if (n < 2) throw new IllegalArgumentException("Desconexo precisa de pelo menos 2 vértices");
            long maxDesc = (long) (n - 1) * (n - 2) / 2;
            if (m > maxDesc) throw new IllegalArgumentException("Desconexo aceita no máximo " + maxDesc + " arestas");
        }
    }

    // Sorteia o tamanho do grupo 0 de forma que caibam m arestas dentro dos grupos
    private static int escolherDivisao(int n, int m, Random rnd) {
        for (int t = 0; t < 100; t++) {
            int a = 1 + rnd.nextInt(n - 1);   // 1..n-1
            if (capacidade(a) + capacidade(n - a) >= m) return a;
        }
        return n - 1;   // maior capacidade possível: um vértice isolado
    }

    // Máximo de arestas entre k vértices: k(k-1)/2
    private static long capacidade(int k) {
        return (long) k * (k - 1) / 2;
    }

    // FISHER-YATES: embaralha 0..n-1 em O(n); toda permutação tem a mesma chance
    private static int[] embaralhar(int n, Random rnd) {
        int[] v = new int[n];
        for (int i = 0; i < n; i++) v[i] = i;
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);              // posição sorteada em 0..i
            int tmp = v[i]; v[i] = v[j]; v[j] = tmp; // troca
        }
        return v;
    }

    private static double pesoAleatorio(Random rnd) {
        return 1 + rnd.nextInt(100);   // inteiro de 1 a 100
    }
}
