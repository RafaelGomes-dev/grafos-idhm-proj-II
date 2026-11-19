package algorithms;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;

import model.Aresta;
import model.Grafo;

public class CiclosEuler {

    // ---------- CICLOS ----------


    public static boolean isCiclico(Grafo g) {
        return g.isDirecionado() ? ciclicoDirecionado(g) : ciclicoNaoDirecionado(g);
    } 

     // Busca guardando o pai: aresta para visitado que não é pai = ciclo
     private static boolean ciclicoNaoDirecionado(Grafo g) {
        int n = g.numVertices();
        boolean[] visitado = new boolean[n];
        int[] pai = new int[n];
        Arrays.fill(pai, -1);
        ArrayDeque<Integer> pilha = new ArrayDeque<>();

         for (int s = 0; s < n; s++) {
            if (visitado[s]) continue;
            visitado[s] = true;
            pilha.push(s);
            while (!pilha.isEmpty()) {
                int u = pilha.pop();
                for (Aresta a : g.getAdjacentes(u)) {
                    int v = a.getDestino();
                    if (!visitado[v]) {
                        visitado[v] = true;
                        pai[v] = u;
                        pilha.push(v);

                    } else if (pai[u] != v && pai[v] != u) {
                        return true; // aresta fora da árvore fecha um ciclo
                    }
                }
            }
         }

         return false;
     }

    // Kahn: remove quem tem grau de entrada 0; se sobrar alguém, há ciclo
    private static boolean ciclicoDirecionado(Grafo g) {
        int n = g.numVertices();
        int[] entrada = grausEntrada(g);
        ArrayDeque<Integer> fila = new ArrayDeque<>();
        for (int u = 0; u < n; u++) if (entrada[u] == 0) fila.add(u);

        int removidos = 0;
        while (!fila.isEmpty()) {
            int u = fila.poll();
            removidos++;
            for (Aresta a : g.getAdjacentes(u)) {
                if (--entrada[a.getDestino()] == 0) fila.add(a.getDestino());
            }
        }

        return removidos < n;
    }

    // ---------- EULER ----------

    // Circuito euleriano: passa por todas as arestas uma vez e volta ao início
    public static boolean temCircuitoEuleriano(Grafo g) {
        if (!arestasNumComponente(g)) return false;
        int[] entrada = grausEntrada(g);
        for (int u = 0; u < g.numVertices(); u++) {
            if (!g.isDirecionado() && g.grau(u) % 2 != 0) return false;
            if (g.isDirecionado() && g.grau(u) != entrada[u]) return false;
        }
        return true;
    }

    // Trilha euleriana aberta: começa e termina em vértices diferentes
    public static boolean temTrilhaEuleriana(Grafo g) {
        if (!arestasNumComponente(g)) return false;
        int[] entrada = grausEntrada(g);

        if (!g.isDirecionado()) {
            int impares = 0;
            for (int u = 0; u < g.numVertices(); u++) if (g.grau(u) % 2 != 0) impares++;
            return impares == 2;
        }

        int inicio = 0, fim = 0;
        for (int u = 0; u < g.numVertices(); u++) {
            int dif = g.grau(u) - entrada[u]; // saída - entrada
            if (dif == 1) inicio++;
            else if (dif == -1) fim++;
            else if (dif != 0) return false;
        }
        return inicio == 1 && fim == 1;
    }

    // Todos os vértices que têm alguma aresta estão no mesmo componente?
    private static boolean arestasNumComponente(Grafo g) {
        int[] entrada = grausEntrada(g);
        int compsComAresta = 0;
        for (List<Integer> comp : Conectividade.componentes(g)) {
            for (int v : comp) {
                if (g.grau(v) > 0 || entrada[v] > 0) {
                    compsComAresta++;
                    break;
                }
            }
        }

        return compsComAresta <= 1;
    }

    private static int[] grausEntrada(Grafo g) {
        int[] entrada = new int[g.numVertices()];
        for (int u = 0; u < g.numVertices(); u++) {
            for (Aresta a : g.getAdjacentes(u)) entrada[a.getDestino()]++;
        }
        return entrada;
    }



    
}
