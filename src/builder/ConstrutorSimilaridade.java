package builder;

import model.Grafo;
import model.Vertice;

/**
 * Cria as arestas do problema real: cada município é ligado aos k municípios
 * mais parecidos (k-NN, "k vizinhos mais próximos"), por força bruta.
 *
 * Com k = 8: cada um cria 8 ligações e cada aresta pode ser criada no máximo
 * pelas suas 2 pontas, então há no mínimo 5565 * 8 / 2 = 22.260 arestas (deu 27.342).
 */
public class ConstrutorSimilaridade {

    public static void conectar(Grafo g, int k) {
        int n = g.numVertices();

        // ---- Para cada município i ----
        for (int i = 0; i < n; i++) {
            Vertice vi = g.getVertice(i);

            // "Top k" de i, sempre ORDENADO do mais perto para o mais longe
            int[] melhorD = new int[k];   // distâncias² dos k melhores
            int[] melhorJ = new int[k];   // ids desses k melhores
            int qtd = 0;                  // quantos já entraram no top

            // ---- Compara i com todos os outros ----
            for (int j = 0; j < n; j++) {
                if (j == i) continue;
                int d = vi.distanciaQuadrada(g.getVertice(j));

                // j entra se o top ainda não encheu OU se é melhor que o último (8º)
                if (qtd < k || d < melhorD[k - 1]) {
                    int pos = (qtd < k) ? qtd : k - 1;   // começa no fim do top

                    // Inserção ordenada (como no insertion sort): empurra para a
                    // direita quem for MAIOR que d. Usa ">" ESTRITO: em empate,
                    // quem já estava (j menor = veio antes no CSV) fica na frente.
                    // Essa é a REGRA DE DESEMPATE que deixa o grafo determinístico.
                    while (pos > 0 && melhorD[pos - 1] > d) {
                        melhorD[pos] = melhorD[pos - 1];
                        melhorJ[pos] = melhorJ[pos - 1];
                        pos--;
                    }
                    melhorD[pos] = d;
                    melhorJ[pos] = j;
                    if (qtd < k) qtd++;   // se estava cheio, o antigo 8º saiu
                }
            }

            // ---- Cria as arestas de i para os seus k melhores ----
            for (int t = 0; t < qtd; t++) {
                // Índices estão x1000, então sqrt(d²) já é 1000 * d.
                // Peso = 1 + 1000 * d. O +1 garante peso > 0 (Dijkstra exige).
                double peso = 1 + Math.sqrt(melhorD[t]);
                // Se j já tinha criado a aresta j–i, o Grafo recusa a duplicata.
                // O peso seria o mesmo: a distância é simétrica.
                g.adicionarAresta(i, melhorJ[t], peso);
            }
        }
        // Complexidade: O(n² * k) -> ~31 milhões de comparações, ~250 ms.
    }
}
