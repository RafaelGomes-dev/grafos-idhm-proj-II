package algorithms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import model.Aresta;
import model.Grafo;

public class Conectividade {

    public static List<List<Integer>> componentes(Grafo g) {
        int n = g.numVertices();

        // vizinhos ignorando o sentido (no direcionado, adiciona também a volta)
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
            if (visitado[s]) continue;
            List<Integer> comp = new ArrayList<>();
            ArrayDeque<Integer> fila = new ArrayDeque<>();
            fila.add(s);
            visitado[s] = true;

            while (!fila.isEmpty()) {
                int u = fila.poll();
                comp.add(u);
                for (int v : viz.get(u)) {
                    if (!visitado[v]) {
                        visitado[v] = true;
                        fila.add(v);
                    }
                }
            }
            comps.add(comp);
        }
        return comps;
    }

    public static boolean isConexo(Grafo g) {
        return g.numVertices() == 0 || componentes(g).size() == 1;
    }
}