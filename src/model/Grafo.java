package model;

import java.util.ArrayList;
import java.util.List;

/**
 * GRAFO guardado como LISTA DE ADJACÊNCIA.
 *
 * Por que lista e não matriz? O grafo é ESPARSO (cerca de 10 vizinhos por
 * município). Uma matriz 5565 x 5565 teria 31 milhões de células quase todas
 * vazias; a lista guarda só as arestas que existem.
 *
 * Convenção: u e v são os ids de dois vértices (u = origem, v = destino).
 */
public class Grafo {

    private final boolean direcionado;   // false no nosso grafo; true se o Pajek tiver *Arcs

    // Posição na lista = id do vértice (vertices.get(3) é o vértice de id 3)
    private final List<Vertice> vertices = new ArrayList<>();

    // adjacencia.get(u) = lista das arestas que SAEM do vértice u
    private final List<List<Aresta>> adjacencia = new ArrayList<>();

    private int numArestas = 0;

    public Grafo(boolean direcionado) {
        this.direcionado = direcionado;
    }

    /**
     * Adiciona um vértice. O id dele tem que ser a próxima posição livre
     * (0, depois 1, depois 2...). Isso garante que vertices.get(id) é ele,
     * e os algoritmos podem usar vetor[id].
     */
    public void adicionarVertice(Vertice v) {
        if (v.getId() != vertices.size()) {
            throw new IllegalArgumentException(
                "Id esperado " + vertices.size() + ", recebido " + v.getId());
        }
        vertices.add(v);
        adjacencia.add(new ArrayList<>());   // lista de vizinhos dele começa vazia
    }

    /**
     * Adiciona a aresta u -> v. Devolve false (e não faz nada) se for laço
     * (u == v) ou se a aresta já existir. Assim o grafo é sempre SIMPLES.
     */
    public boolean adicionarAresta(int u, int v, double peso) {
        validarId(u);
        validarId(v);
        if (u == v || existeAresta(u, v)) {
            return false;
        }
        adjacencia.get(u).add(new Aresta(u, v, peso));      // u -> v
        if (!direcionado) {
            adjacencia.get(v).add(new Aresta(v, u, peso));  // v -> u (volta)
        }
        // Fora do if: no não direcionado são 2 entradas, mas UMA aresta só.
        numArestas++;
        return true;
    }

    // Percorre os vizinhos de u procurando v. Custo O(grau), máximo 16 aqui.
    public boolean existeAresta(int u, int v) {
        for (Aresta a : adjacencia.get(u)) {
            if (a.getDestino() == v) {
                return true;
            }
        }
        return false;
    }

    // Arestas que saem de u: é o que todos os algoritmos percorrem
    public List<Aresta> getAdjacentes(int u) {
        validarId(u);
        return adjacencia.get(u);
    }

    // Grau = quantidade de arestas que saem de u (no direcionado: grau de saída)
    public int grau(int u) {
        return getAdjacentes(u).size();
    }

    public Vertice getVertice(int id) {
        validarId(id);
        return vertices.get(id);
    }

    public int numVertices() { return vertices.size(); }
    public int numArestas() { return numArestas; }
    public boolean isDirecionado() { return direcionado; }

    // Auxiliar interno: erro claro se alguém pedir um vértice que não existe
    private void validarId(int id) {
        if (id < 0 || id >= vertices.size()) {
            throw new IllegalArgumentException("Vértice inexistente: " + id);
        }
    }
}
