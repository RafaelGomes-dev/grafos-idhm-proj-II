package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import model.Grafo;
import model.Vertice;

public abstract class LeitorMunicipios {
    public static Grafo ler(String caminho) throws IOException {
        Grafo grafo = new Grafo(false);

        try (BufferedReader leitor = Files.newBufferedReader(Path.of(caminho), StandardCharsets.UTF_8)) {
            String linha = leitor.readLine();
            int id = 0;

            while ((linha = leitor.readLine()) != null) {
                if (linha.isBlank()) continue;
                String[] c = linha.split(";");
                if (c.length != 7) {
                    throw new IOException("Linha " + (id + 2) + " inválida: " + linha);
                }
                grafo.adicionarVertice(new Vertice(
                    id,
                    Integer.parseInt(c[0]), c[1].toUpperCase(), c[2],
                    Double.parseDouble(c[3]), Double.parseDouble(c[4]),
                    Double.parseDouble(c[5]), Double.parseDouble(c[6])));
                id++;
                    
            
            }
        }

        return grafo;
    }
}
