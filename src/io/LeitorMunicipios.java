package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import model.Grafo;
import model.Vertice;

/**
 * Lê o CSV dos municípios e devolve um Grafo SÓ COM OS VÉRTICES.
 * As arestas são criadas depois, pelo ConstrutorSimilaridade
 * (cada classe com uma responsabilidade só).
 *
 * Formato do CSV (separador ;):
 *   codigo;municipio;uf;idhm;idhm_e;idhm_l;idhm_r
 */
public class LeitorMunicipios {

    // static: não precisa criar objeto, basta chamar LeitorMunicipios.ler(...)
    public static Grafo ler(String caminho) throws IOException {
        Grafo grafo = new Grafo(false);   // nosso grafo é NÃO direcionado

        // try-with-resources: fecha o arquivo sozinho no final, mesmo se der erro.
        // UTF-8 para ler os acentos (SÃO PAULO, GOIÂNIA...).
        try (BufferedReader leitor = Files.newBufferedReader(Path.of(caminho), StandardCharsets.UTF_8)) {
            String linha = leitor.readLine();   // descarta o cabeçalho
            int id = 0;                         // 1º município = vértice 0

            // Lê uma linha por vez até o fim do arquivo (readLine devolve null)
            while ((linha = leitor.readLine()) != null) {
                if (linha.isBlank()) continue;

                String[] c = linha.split(";");   // quebra nas 7 colunas
                if (c.length != 7) {
                    // id + 2: a linha 1 é o cabeçalho e o id começa em 0
                    throw new IOException("Linha " + (id + 2) + " inválida: " + linha);
                }

                grafo.adicionarVertice(new Vertice(
                    id,
                    Integer.parseInt(c[0]),        // codigo IBGE
                    c[1].toUpperCase(),            // nome (padroniza em maiúsculas)
                    c[2],                          // uf
                    Double.parseDouble(c[3]),      // idhm
                    Double.parseDouble(c[4]),      // idhm_e (educação)
                    Double.parseDouble(c[5]),      // idhm_l (longevidade)
                    Double.parseDouble(c[6])));    // idhm_r (renda)
                id++;
            }
        }
        return grafo;
    }
}
