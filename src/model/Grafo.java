package model;

import java.util.ArrayList;
import java.util.List;

public class Grafo {
    private final boolean direcionado;
    private final List<Vertice> vertices = new ArrayList<>();
    private final List<List<Aresta>> adjacencia = new ArrayList<>();
    private int numArestas = 0;

    public Grafo(boolean direcionado) {
        this.direcionado = direcionado;
    }

    //o id do vertice precisa ser o proxima posição vazia (0, 1, 2, 3...)

    public void adicionarVertice(Vertice v) {
        if (v.getID() != vertices.size()) {
            throw new IllegalArgumentException(
                "Id esperado " + vertices.size() + ", recebido " + v.getID()
            );
        }
        vertices.add(v);
        adjacencia.add(new ArrayList<>());
    }

    //retorna false se for laço ou se aresta ja existir
    public boolean adicionarAresta(int u, int v, double peso) {
        validarId(u);
        validarId(v);
        if (u == v || existeAresta(u, v)) {
            return false;
        }
        adjacencia.get(u).add(new Aresta(u, v, peso));
        if (!direcionado) {
            adjacencia.get(v).add(new Aresta(v, u, peso));
        }

        numArestas++;
        return true;
    }

    public boolean existeAresta(int u, int v) {
        for (Aresta a : adjacencia.get(u)) {
            if (a.getDestino()==v) {
                return true;
            }
        }
        return false;
    }

    public List<Aresta> getAdjacentes(int u) {
        validarId(u);
        return adjacencia.get(u);
    }

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

    private void validarId(int id) {
        if(id < 0 || id >= vertices.size()) {
            throw new IllegalArgumentException("vertice inexistente: " + id);
        }
    }
}
