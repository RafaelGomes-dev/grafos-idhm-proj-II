package generator;

import java.util.Random;

import model.Grafo;
import model.Vertice;


public class GeradorAleatorio {

    public static Grafo gerar(int n, int m, boolean conexo, long semente) {
        validar(n, m, conexo);
        Random rnd = new Random(semente);

        Grafo g = new Grafo(false);
        for (int i = 0; i < n; i++) g.adicionarVertice(new Vertice(i, "V" + (i + 1)));

        int[] grupo = new int[n]; // no conexo todos ficam no grupo 0

        if (conexo) {
            // árvore aleatória: cada vértice liga em alguém que já entrou
            int[] ordem = embaralhar(n, rnd);
            for (int i = 1; i < n; i++) {
                int u = ordem[i];
                int v = ordem[rnd.nextInt(i)];
                g.adicionarAresta(u, v, pesoAleatorio(rnd));
            }
        } else {
            // divide em 2 grupos: primeiros 'a' da ordem no grupo 0, resto no grupo 1
            int a = escolherDivisao(n, m, rnd);
            int[] ordem = embaralhar(n, rnd);
            for (int i = a; i < n; i++) grupo[ordem[i]] = 1;
        }

        // completa até m arestas (só dentro do mesmo grupo)
        while (g.numArestas() < m) {
            int u = rnd.nextInt(n);
            int v = rnd.nextInt(n);
            if (grupo[u] == grupo[v]) {
                g.adicionarAresta(u, v, pesoAleatorio(rnd)); // laço/duplicata é recusado pelo Grafo
            }
        }
        return g;
    }

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

    // sorteia o tamanho do grupo 0 de forma que caibam m arestas
    private static int escolherDivisao(int n, int m, Random rnd) {
        for (int t = 0; t < 100; t++) {
            int a = 1 + rnd.nextInt(n - 1); // 1..n-1
            if (capacidade(a) + capacidade(n - a) >= m) return a;
        }
        return n - 1; // maior capacidade: um vértice isolado
    }

    private static long capacidade(int k) {
        return (long) k * (k - 1) / 2;
    }


    // Fisher-Yates: embaralha 0..n-1
    private static int[] embaralhar(int n, Random rnd) {
        int[] v = new int[n];
        for (int i = 0; i < n; i++) v[i] = i;
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int tmp = v[i]; v[i] = v[j]; v[j] = tmp;
        }
        return v;
    }

    private static double pesoAleatorio(Random rnd) {
        return 1 + rnd.nextInt(100); // 1..100
    }
    
}
