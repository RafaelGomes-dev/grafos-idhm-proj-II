package builder;

import model.Grafo;
import model.Vertice;

public class ConstrutorSimilaridade {
    public static void conectar(Grafo g, int k) {
        int n = g.numVertices();

        for (int i =0; i < n; i++) {
            Vertice vi = g.getVertice(i);
            int[] melhorD = new int[k]; // distâncias² do top k (ordenadas)
            int[] melhorJ = new int[k]; // índices dos vizinhos do top k
            int qtd = 0;

            for (int j = 0; j < n; j++) {
                if (j == i) continue;
                int d = vi.distanciaQuadrada(g.getVertice(j));

                // entra se o top ainda não está cheio ou se é melhor que o último  
                if ( qtd < k || d < melhorD[k - 1]) {
                   int pos = (qtd < k) ? qtd : k - 1;
                   // empurra para a direita quem for MAIOR (empate não passa na frente)
                   while (pos > 0 && melhorD[pos - 1] > d) {
                        melhorD[pos] = melhorD[pos - 1];
                        melhorJ[pos] = melhorJ[pos - 1];
                        pos--;
                   }
                   melhorD[pos] = d;
                   melhorJ[pos] = j;
                   if (qtd < k) qtd++;
                }
            }

            for (int t = 0; t < qtd; t++) {
                double peso = 1 + Math.sqrt(melhorD[t]); // = 1 + 1000 * d
                g.adicionarAresta(i, melhorJ[t], peso);  // duplicata é ignorada pelo Grafo
            }
        }
    }
}
