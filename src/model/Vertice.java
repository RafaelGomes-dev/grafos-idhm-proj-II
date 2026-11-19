package model;

/**
 * VÉRTICE = um município.
 *
 * Cada município é visto como um PONTO num espaço 3D, onde cada eixo é um
 * subíndice do IDHM: Educação (E), Longevidade (L) e Renda (R).
 * Municípios parecidos = pontos próximos.
 */
public class Vertice {

    // ---------- CAMPOS ----------
    // "private final": só são preenchidos no construtor e nunca mudam depois.

    private final int id;          // índice interno: 0..n-1 (posição nos vetores dos algoritmos)
    private final String rotulo;   // texto exibido e gravado no Pajek, ex.: "CURITIBA (PR)"

    // Dados do município (ficam zerados quando o grafo vem do Pajek ou do gerador)
    private final int codigo;      // código IBGE (7 dígitos)
    private final String uf;
    private final double idhm;     // IDHM geral, só para exibição

    // Subíndices guardados como INTEIROS x1000 (0.641 -> 641).
    // Motivo: com inteiros a conta da distância é EXATA. Com double, duas
    // distâncias iguais podiam sair diferentes na última casa decimal e mudar
    // quem entra no top 8 nos casos de empate. Os dados têm 3 casas decimais,
    // então multiplicar por 1000 não perde nada.
    private final int educacao;
    private final int longevidade;
    private final int renda;

    // ---------- CONSTRUTORES ----------

    // Construtor completo: usado pelo LeitorMunicipios (dados do CSV)
    public Vertice(int id, int codigo, String nome, String uf,
                   double idhm, double idhmE, double idhmL, double idhmR) {
        this.id = id;
        this.codigo = codigo;
        this.uf = uf;
        this.rotulo = nome + " (" + uf + ")";
        this.idhm = idhm;
        // Math.round e não (int): 0.641 * 1000 pode dar 640.9999... no double,
        // e o (int) sozinho cortaria para 640.
        this.educacao = (int) Math.round(idhmE * 1000);
        this.longevidade = (int) Math.round(idhmL * 1000);
        this.renda = (int) Math.round(idhmR * 1000);
    }

    // Construtor simples: usado pelo PajekIO e pelo GeradorAleatorio,
    // que só conhecem o número e o rótulo do vértice.
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

    // ---------- DISTÂNCIA ----------

    /**
     * Distância AO QUADRADO até outro município (Pitágoras em 3D, sem a raiz).
     *   d² = ΔE² + ΔL² + ΔR²
     * Para comparar quem está mais perto, a raiz não muda a ordem
     * (se d²A < d²B, então dA < dB). Sem raiz, tudo fica em inteiros: exato e rápido.
     * A raiz só é usada no peso da aresta (ConstrutorSimilaridade).
     */
    public int distanciaQuadrada(Vertice outro) {
        int dE = this.educacao - outro.educacao;
        int dL = this.longevidade - outro.longevidade;
        int dR = this.renda - outro.renda;
        return dE * dE + dL * dL + dR * dR;   // SOMA dos quadrados (não multiplicação!)
    }

    // ---------- GETTERS ----------

    public int getId() { return id; }
    public String getRotulo() { return rotulo; }
    public int getCodigo() { return codigo; }
    public String getUf() { return uf; }
    public double getIdhm() { return idhm; }

    // Permite fazer System.out.println(vertice) e ver o rótulo
    @Override
    public String toString() {
        return rotulo;
    }
}
