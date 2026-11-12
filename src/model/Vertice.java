package model;

public class Vertice {
    private final int id;
    private final String rotulo;

    //dados do municipio - vazios quando o grafo vem do pajek

    private final int codigo;
    private final String uf;
    private final double idhm;
    private final int educacao;
    private final int longevidade;
    private final int renda;

    //construtor para montar o grafo a partir do csv

    public Vertice(int id, int codigo, String nome, String uf, double idhm, double idhmE, double idhmL, double idhmR) {
        this.id = id;
        this.codigo = codigo;
        this.uf = uf;
        this.rotulo = nome + " (" + uf + ") ";
        this.idhm = idhm;
        this.educacao = (int) Math.round(idhmE * 1000);
        this.longevidade = (int) Math.round(idhmL * 1000);
        this.renda = (int) Math.round(idhmR * 1000);
    }

    //construtor simples - usado pelo pajek e pelo gerador aleatorio
    public Vertice(int id, String rotulo) {
        this.id = id;
        this.rotulo = rotulo;
        this.codigo = 0;
        this.uf = "";
        this.idhm = 0;
        this.educacao = 0;
        this.longevidade = 0;
        this.renda = 0;
    }

    //distancia ao quadrado até outro municipio
    public int distanciaQuadrada(Vertice outro) {
        int dE = this.educacao - outro.educacao;
        int dL = this.longevidade - outro.longevidade;
        int dR = this.renda - outro.renda;
        return dE * dE + dL * dL + dR * dR;
    }

    public int getID() { return id; }
    public String getRotulo() { return rotulo; }
    public int getCodigo() { return codigo; }
    public String getUf() { return uf; }
    public double getIdhm() { return idhm; }

    @Override 
    public String toString() {
        return rotulo;
    }


}
