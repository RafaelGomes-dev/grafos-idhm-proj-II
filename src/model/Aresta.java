package model;

/**
 * ARESTA = ligação de um vértice (origem) para outro (destino), com um peso.
 *
 * origem e destino SÃO os ids dos vértices (int), não os objetos Vertice.
 * Assim os algoritmos usam o id direto como índice: visitado[destino], dist[destino].
 *
 * No grafo NÃO direcionado, uma ligação A–B vira duas Arestas (A->B e B->A).
 * Quem cria as duas é o Grafo, não esta classe.
 */
public class Aresta {

    private final int origem;    // id do vértice de onde a aresta sai (o "u")
    private final int destino;   // id do vértice onde a aresta chega (o "v")
    private final double peso;   // no nosso grafo: 1 + 1000 * distância

    public Aresta(int origem, int destino, double peso) {
        this.origem = origem;
        this.destino = destino;
        this.peso = peso;
    }

    public int getOrigem() { return origem; }
    public int getDestino() { return destino; }
    public double getPeso() { return peso; }

    // Ex.: "0 -> 1 (323.92)"
    @Override
    public String toString() {
        return origem + " -> " + destino + " (" + peso + ")";
    }
}
