package model;

import java.util.Comparator;
import java.util.Objects;

/**
 * Representa um jogador de e-sports no sistema.
 *
 * O modelo é unificado e armazena os atributos relevantes para as três
 * categorias suportadas. Os campos que não se aplicam a uma categoria
 * específica são ignorados durante a ordenação daquela categoria.
 *
 * Critérios de ordenação por categoria:
 *
 * <ul>
 *   <li><b>Dança</b>: pontuação decrescente, precisão decrescente,
 *       combo máximo decrescente, nome crescente.</li>
 *   <li><b>Corrida</b>: tempo de prova crescente, posição de chegada
 *       crescente, voltas decrescente, nome crescente.</li>
 *   <li><b>Luta</b>: aproveitamento decrescente, vitórias decrescente,
 *       derrotas crescente, nome crescente.</li>
 * </ul>
 */
public class Jogador {

    private final int id;
    private final String nome;
    private final Categoria categoria;

    // Dança
    private final int pontuacao;
    private final double precisao;
    private final int comboMaximo;

    // Corrida
    private final int tempoProvaMs;
    private final int voltas;
    private final int posicaoChegada;

    // Luta
    private final int vitorias;
    private final int derrotas;

    /**
     * Construtor completo. Campos não aplicáveis a uma categoria devem ser
     * preenchidos com valores neutros (zero).
     *
     * @param id               identificador único do jogador
     * @param nome             nome do jogador (não pode ser nulo nem vazio)
     * @param categoria        categoria do jogo
     * @param pontuacao        pontuação obtida (Dança)
     * @param precisao         precisão percentual, de 0.0 a 100.0 (Dança)
     * @param comboMaximo      maior combo alcançado (Dança)
     * @param tempoProvaMs     tempo total da prova em milissegundos (Corrida)
     * @param voltas           quantidade de voltas completadas (Corrida)
     * @param posicaoChegada   posição final de chegada (Corrida)
     * @param vitorias         número de vitórias (Luta)
     * @param derrotas         número de derrotas (Luta)
     */
    public Jogador(int id,
                   String nome,
                   Categoria categoria,
                   int pontuacao,
                   double precisao,
                   int comboMaximo,
                   int tempoProvaMs,
                   int voltas,
                   int posicaoChegada,
                   int vitorias,
                   int derrotas) {
        if (id < 0) {
            throw new IllegalArgumentException(
                    "O identificador do jogador não pode ser negativo."
            );
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do jogador é obrigatório."
            );
        }
        if (categoria == null) {
            throw new IllegalArgumentException(
                    "A categoria do jogador é obrigatória."
            );
        }
        if (precisao < 0.0 || precisao > 100.0) {
            throw new IllegalArgumentException(
                    "A precisão deve estar entre 0.0 e 100.0."
            );
        }
        if (vitorias < 0 || derrotas < 0) {
            throw new IllegalArgumentException(
                    "Vitórias e derrotas não podem ser negativas."
            );
        }

        this.id = id;
        this.nome = nome.trim();
        this.categoria = categoria;
        this.pontuacao = pontuacao;
        this.precisao = precisao;
        this.comboMaximo = comboMaximo;
        this.tempoProvaMs = tempoProvaMs;
        this.voltas = voltas;
        this.posicaoChegada = posicaoChegada;
        this.vitorias = vitorias;
        this.derrotas = derrotas;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public double getPrecisao() {
        return precisao;
    }

    public int getComboMaximo() {
        return comboMaximo;
    }

    public int getTempoProvaMs() {
        return tempoProvaMs;
    }

    public int getVoltas() {
        return voltas;
    }

    public int getPosicaoChegada() {
        return posicaoChegada;
    }

    public int getVitorias() {
        return vitorias;
    }

    public int getDerrotas() {
        return derrotas;
    }

    /**
     * Retorna o aproveitamento de vitórias, calculado como
     * {@code vitórias / (vitórias + derrotas) * 100}. Se o total de partidas
     * for zero, o aproveitamento é zero.
     *
     * @return aproveitamento percentual
     */
    public double getAproveitamento() {
        int total = vitorias + derrotas;
        if (total == 0) {
            return 0.0;
        }
        return (vitorias * 100.0) / total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Jogador)) return false;
        Jogador jogador = (Jogador) o;
        return id == jogador.id
                && categoria == jogador.categoria;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, categoria);
    }

    @Override
    public String toString() {
        return String.format(
                "Jogador{id=%d, nome='%s', categoria=%s}",
                id, nome, categoria
        );
    }

    /**
     * Retorna um comparador que ordena jogadores de acordo com a categoria
     * informada. Empates são resolvidos de forma determinística pelo nome.
     *
     * @param categoria categoria cujo critério de ordenação será usado
     * @return comparador correspondente
     */
    public static Comparator<Jogador> comparadorPorCategoria(Categoria categoria) {
        return switch (categoria) {
            case DANCA -> Comparator
                    .comparingInt(Jogador::getPontuacao).reversed()
                    .thenComparing(Comparator.comparingDouble(Jogador::getPrecisao).reversed())
                    .thenComparing(Comparator.comparingInt(Jogador::getComboMaximo).reversed())
                    .thenComparing(Jogador::getNome);
            case CORRIDA -> Comparator
                    .comparingInt(Jogador::getTempoProvaMs)
                    .thenComparingInt(Jogador::getPosicaoChegada)
                    .thenComparing(Comparator.comparingInt(Jogador::getVoltas).reversed())
                    .thenComparing(Jogador::getNome);
            case LUTA -> Comparator
                    .comparingDouble(Jogador::getAproveitamento).reversed()
                    .thenComparing(Comparator.comparingInt(Jogador::getVitorias).reversed())
                    .thenComparingInt(Jogador::getDerrotas)
                    .thenComparing(Jogador::getNome);
        };
    }
}
