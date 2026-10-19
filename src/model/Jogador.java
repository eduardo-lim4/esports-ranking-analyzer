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
 *       combo máximo decrescente, nome crescente e ID crescente.</li>
 *   <li><b>Corrida</b>: tempo de prova crescente, posição de chegada
 *       crescente, voltas decrescente, nome crescente e ID crescente.</li>
 *   <li><b>Luta</b>: aproveitamento decrescente, vitórias decrescente,
 *       derrotas crescentes, nome crescente e ID crescente.</li>
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
        if (!Double.isFinite(precisao) || precisao < 0.0 || precisao > 100.0) {
            throw new IllegalArgumentException(
                    "A precisão deve ser um valor finito entre 0.0 e 100.0."
            );
        }
        if (vitorias < 0 || derrotas < 0) {
            throw new IllegalArgumentException(
                    "Vitórias e derrotas não podem ser negativas."
            );
        }
        switch (categoria) {
            case DANCA -> {
                if (pontuacao < 0 || comboMaximo < 0) {
                    throw new IllegalArgumentException(
                            "Pontuação e combo máximo de Dança não podem ser negativos."
                    );
                }
            }
            case CORRIDA -> {
                if (tempoProvaMs <= 0 || voltas <= 0 || posicaoChegada <= 0) {
                    throw new IllegalArgumentException(
                            "Tempo, voltas e posição de Corrida devem ser maiores que zero."
                    );
                }
            }
            case LUTA -> {
                // Vitórias e derrotas já foram validadas acima.
            }
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
        long total = (long) vitorias + derrotas;
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
                && Double.compare(jogador.precisao, precisao) == 0
                && pontuacao == jogador.pontuacao
                && comboMaximo == jogador.comboMaximo
                && tempoProvaMs == jogador.tempoProvaMs
                && voltas == jogador.voltas
                && posicaoChegada == jogador.posicaoChegada
                && vitorias == jogador.vitorias
                && derrotas == jogador.derrotas
                && Objects.equals(nome, jogador.nome)
                && categoria == jogador.categoria;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, categoria, pontuacao, precisao,
                comboMaximo, tempoProvaMs, voltas, posicaoChegada,
                vitorias, derrotas);
    }

    @Override
    public String toString() {
        return String.format(
                "Jogador{id=%d, nome='%s', categoria=%s}",
                id, nome, categoria
        );
    }

    private static int compararAproveitamentoDescendente(Jogador a, Jogador b) {
        long totalA = (long) a.vitorias + a.derrotas;
        long totalB = (long) b.vitorias + b.derrotas;
        // Aproveitamento de 0/0 é definido como zero no modelo.
        if (totalA == 0) totalA = 1;
        if (totalB == 0) totalB = 1;

        long aproveitamentoB = (long) b.vitorias * totalA;
        long aproveitamentoA = (long) a.vitorias * totalB;
        return Long.compare(aproveitamentoB, aproveitamentoA);
    }

    /**
     * Retorna um comparador que ordena jogadores de acordo com a categoria
     * informada. Empates são resolvidos deterministicamente pelo nome e,
     * por fim, pelo identificador do jogador.
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
                    .thenComparing(Jogador::getNome)
                    .thenComparingInt(Jogador::getId);
            case CORRIDA -> Comparator
                    .comparingInt(Jogador::getTempoProvaMs)
                    .thenComparingInt(Jogador::getPosicaoChegada)
                    .thenComparing(Comparator.comparingInt(Jogador::getVoltas).reversed())
                    .thenComparing(Jogador::getNome)
                    .thenComparingInt(Jogador::getId);
            case LUTA -> (a, b) -> {
                int resultado = compararAproveitamentoDescendente(a, b);
                if (resultado == 0) {
                    resultado = Integer.compare(b.vitorias, a.vitorias);
                }
                if (resultado == 0) {
                    resultado = Integer.compare(a.derrotas, b.derrotas);
                }
                if (resultado == 0) {
                    resultado = a.nome.compareTo(b.nome);
                }
                if (resultado == 0) {
                    resultado = Integer.compare(a.id, b.id);
                }
                return resultado;
            };
        };
    }
}
