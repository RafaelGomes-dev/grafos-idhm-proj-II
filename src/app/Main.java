package app;

import model.Aresta;
import model.Vertice;

public class Main {
    public static void main(String[] args) {
        Vertice a = new Vertice(0, 4106902, "CURITIBA", "PR", 0.823, 0.768, 0.855, 0.850);
        Vertice b = new Vertice(1, 1100015, "ALTA FLORESTA D'OESTE", "RO", 0.641, 0.526, 0.763, 0.657);
        System.out.println(a + " x " + b);
        System.out.println("d² = " + a.distanciaQuadrada(b));

        Aresta ab = new Aresta(a.getID(), b.getID(), 1 + Math.sqrt(a.distanciaQuadrada(b)));
        System.out.println(ab);
    }

}