package app;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import algorithms.Centralidades;
import algorithms.CiclosEuler;
import algorithms.Conectividade;
import builder.ConstrutorSimilaridade;
import generator.GeradorAleatorio;
import io.LeitorMunicipios;
import io.PajekIO;
import model.Grafo;

public class Main {

    static final String CSV = "dados/municipios_idhm_2010.csv";
    static final int K = 8;
    static Grafo grafo = null;
    static Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n===== Sistema de Análise de IDHM - Grafos =====");
            System.out.println("Grafo atual: " + (grafo == null ? "nenhum"
                : grafo.numVertices() + " vértices, " + grafo.numArestas() + " arestas"));
            System.out.println(" 1. Importar municípios e gerar grafo (k-NN, k=8)");
            System.out.println(" 2. Salvar grafo em Pajek");
            System.out.println(" 3. Carregar grafo do Pajek");
            System.out.println(" 4. Estatísticas do grafo");
            System.out.println(" 5. Conectividade e componentes");
            System.out.println(" 6. Ciclos e propriedade euleriana");
            System.out.println(" 7. Centralidade de proximidade (top 10)");
            System.out.println(" 8. Centralidade de intermediação (top 10)");
            System.out.println(" 9. Gerar grafo aleatório");
            System.out.println("10. Teste de desempenho");
            System.out.println(" 0. Sair");
            System.out.print("Opção: ");

            String op = in.nextLine().trim();
            try {
                switch (op) {
                    case "1" -> importar();
                    case "2" -> salvarPajek();
                    case "3" -> carregarPajek();
                    case "4" -> estatisticas();
                    case "5" -> conectividade();
                    case "6" -> ciclosEuler();
                    case "7" -> proximidade();
                    case "8" -> intermediacao();
                    case "9" -> gerarAleatorio();
                    case "10" -> desempenho();
                    case "0" -> { System.out.println("Até mais!"); return; }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    // ---------- OPÇÕES ----------

    static void importar() throws Exception {
        long t0 = System.currentTimeMillis();
        grafo = LeitorMunicipios.ler(CSV);
        ConstrutorSimilaridade.conectar(grafo, K);
        long t1 = System.currentTimeMillis();
        System.out.println("Grafo gerado: " + grafo.numVertices() + " vértices, "
            + grafo.numArestas() + " arestas (" + (t1 - t0) + " ms)");
    }

    static void salvarPajek() throws Exception {
        if (!temGrafo()) return;
        System.out.print("Arquivo (Enter = resultados/grafo.net): ");
        String c = in.nextLine().trim();
        if (c.isEmpty()) c = "resultados/grafo.net";
        PajekIO.gravar(grafo, c);
        System.out.println("Salvo em " + c);
    }

    static void carregarPajek() throws Exception {
        System.out.print("Arquivo (Enter = resultados/grafo.net): ");
        String c = in.nextLine().trim();
        if (c.isEmpty()) c = "resultados/grafo.net";
        grafo = PajekIO.carregar(c);
        System.out.println("Carregado: " + grafo.numVertices() + " vértices, "
            + grafo.numArestas() + " arestas, " + (grafo.isDirecionado() ? "direcionado" : "não direcionado"));
    }

    static void estatisticas() {
        if (!temGrafo()) return;
        int min = Integer.MAX_VALUE, max = 0;
        long soma = 0;
        for (int i = 0; i < grafo.numVertices(); i++) {
            int gr = grafo.grau(i);
            min = Math.min(min, gr);
            max = Math.max(max, gr);
            soma += gr;
        }
        System.out.println("Tipo: " + (grafo.isDirecionado() ? "direcionado" : "não direcionado"));
        System.out.println("Vértices: " + grafo.numVertices());
        System.out.println("Arestas: " + grafo.numArestas());
        System.out.printf("Grau mínimo / máximo / médio: %d / %d / %.2f%n",
            min, max, (double) soma / grafo.numVertices());
    }

    static void conectividade() {
        if (!temGrafo()) return;
        List<List<Integer>> comps = Conectividade.componentes(grafo);
        System.out.println("Conexo? " + (comps.size() == 1 ? "SIM" : "NÃO"));
        if (comps.size() > 1) {
            System.out.println("Componentes: " + comps.size()
                + (grafo.isDirecionado() ? " (fracamente conectados)" : ""));
            for (int i = 0; i < comps.size(); i++) {
                List<Integer> c = comps.get(i);
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < Math.min(5, c.size()); j++) {
                    sb.append(grafo.getVertice(c.get(j)).getRotulo()).append("; ");
                }
                System.out.println("  Componente " + (i + 1) + " (" + c.size() + " vértices): "
                    + sb + (c.size() > 5 ? "..." : ""));
            }
        }
    }

    static void ciclosEuler() {
        if (!temGrafo()) return;
        System.out.println("Cíclico? " + (CiclosEuler.isCiclico(grafo) ? "SIM" : "NÃO"));
        boolean circuito = CiclosEuler.temCircuitoEuleriano(grafo);
        System.out.println("Euleriano (circuito)? " + (circuito ? "SIM" : "NÃO"));
        if (!circuito) {
            System.out.println("Possui trilha euleriana aberta? "
                + (CiclosEuler.temTrilhaEuleriana(grafo) ? "SIM" : "NÃO"));
        }
    }

    static void proximidade() {
        if (!temGrafo()) return;
        System.out.println("Calculando (pode levar alguns minutos)...");
        long t0 = System.currentTimeMillis();
        double[] v = Centralidades.proximidade(grafo);
        System.out.println("Tempo: " + (System.currentTimeMillis() - t0) + " ms");
        imprimirTop(v, 10);
    }

    static void intermediacao() {
        if (!temGrafo()) return;
        System.out.println("Calculando (pode levar alguns minutos)...");
        long t0 = System.currentTimeMillis();
        double[] v = Centralidades.intermediacao(grafo);
        System.out.println("Tempo: " + (System.currentTimeMillis() - t0) + " ms");
        imprimirTop(v, 10);
    }

    static void gerarAleatorio() {
        System.out.print("Nº de vértices: ");
        int n = Integer.parseInt(in.nextLine().trim());
        System.out.print("Nº de arestas: ");
        int m = Integer.parseInt(in.nextLine().trim());
        System.out.print("Conexo? (s/n): ");
        boolean conexo = in.nextLine().trim().equalsIgnoreCase("s");
        grafo = GeradorAleatorio.gerar(n, m, conexo, System.currentTimeMillis());
        System.out.println("Gerado: " + grafo.numVertices() + " vértices, " + grafo.numArestas()
            + " arestas | conexo? " + (Conectividade.isConexo(grafo) ? "SIM" : "NÃO"));
    }

    // Mede o tempo das centralidades com os primeiros N municípios do CSV
    static void desempenho() throws Exception {
        Grafo completo = LeitorMunicipios.ler(CSV);
        int[] tamanhos = {100, 500, 1000, 3000, completo.numVertices()};
        System.out.println("    N | arestas | proximidade (ms) | intermediação (ms)");
        for (int n : tamanhos) {
            Grafo sub = new Grafo(false);
            for (int i = 0; i < n; i++) sub.adicionarVertice(completo.getVertice(i));
            ConstrutorSimilaridade.conectar(sub, K);

            long t0 = System.currentTimeMillis();
            Centralidades.proximidade(sub);
            long t1 = System.currentTimeMillis();
            Centralidades.intermediacao(sub);
            long t2 = System.currentTimeMillis();
            System.out.printf("%5d | %7d | %16d | %18d%n", n, sub.numArestas(), t1 - t0, t2 - t1);
        }
    }

    // ---------- AUXILIARES ----------

    static boolean temGrafo() {
        if (grafo == null) {
            System.out.println("Nenhum grafo carregado. Use a opção 1, 3 ou 9.");
            return false;
        }
        return true;
    }

    static void imprimirTop(double[] valores, int k) {
        Integer[] idx = new Integer[valores.length];
        for (int i = 0; i < idx.length; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> Double.compare(valores[b], valores[a]));
        for (int i = 0; i < Math.min(k, idx.length); i++) {
            System.out.printf("  %2d. %-40s %.6f%n", i + 1,
                grafo.getVertice(idx[i]).getRotulo(), valores[idx[i]]);
        }
    }
}