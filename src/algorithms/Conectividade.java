package algorithms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import model.Aresta;
import model.Grafo;

/**
 * Conectividade com BFS (busca em largura).
 * Uma BFS a partir de um vértice visita tudo que dá para alcançar = 1 componente.
 * Repetindo a partir de cada vértice ainda não visitado, achamos todos.
 * Conexo = exatamente 1 componente.
 */
public class Conectividade {

    public static List<List<Integer>> componentes(Grafo g) {
        int n = g.numVertices();

        // Vizinhos IGNORANDO O SENTIDO. No direcionado adicionamos também a volta,
        // por isso o resultado são componentes FRACAMENTE conectados (pedido no enunciado).
        List<List<Integer>> viz = new ArrayList<>();
        for (int i = 0; i < n; i++) viz.add(new ArrayList<>());
        for (int u = 0; u < n; u++) {
            for (Aresta a : g.getAdjacentes(u)) {
                viz.get(u).add(a.getDestino());
                if (g.isDirecionado()) viz.get(a.getDestino()).add(u);
            }
        }

        boolean[] visitado = new boolean[n];
        List<List<Integer>> comps = new ArrayList<>();

        for (int s = 0; s < n; s++) {
            if (visitado[s]) continue;   // s já pertence a um componente achado

            // ---- BFS a partir de s ----
            List<Integer> comp = new ArrayList<>();
            ArrayDeque<Integer> fila = new ArrayDeque<>();
            fila.add(s);
            visitado[s] = true;

            while (!fila.isEmpty()) {
                int u = fila.poll();     // tira o primeiro da fila
                comp.add(u);
                for (int v : viz.get(u)) {
                    if (!visitado[v]) {
                        // Marca AO ENTRAR na fila (e não ao sair):
                        // assim nenhum vértice entra duas vezes.
                        visitado[v] = true;
                        fila.add(v);
                    }
                }
            }
            comps.add(comp);
        }
        return comps;
        // Complexidade: O(V + E) — cada vértice e cada aresta vistos uma vez.
    }

    public static boolean isConexo(Grafo g) {
        return g.numVertices() == 0 || componentes(g).size() == 1;
    }
}
