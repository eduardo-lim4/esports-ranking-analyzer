package benchmark;

import model.Jogador;

import java.util.Collections;
import java.util.List;

/**
 * Resultado da execução de um algoritmo no benchmark.
 *
 * Armazena o nome do algoritmo, o tempo medido em milissegundos, o status da
 * execução, se o resultado foi validado e a lista ordenada resultante.
 */
public class BenchmarkResult {

    private final String algoritmo;
    private final double tempoMs;
    private final String status;
    private final boolean valido;
    private final List<Jogador> ranking;

    public BenchmarkResult(String algoritmo,
                           double tempoMs,
                           String status,
                           boolean valido,
                           List<Jogador> ranking) {
        this.algoritmo = algoritmo;
        this.tempoMs = tempoMs;
        this.status = status;
        this.valido = valido;
        this.ranking = Collections.unmodifiableList(ranking);
    }

    public String getAlgoritmo() {
        return algoritmo;
    }

    public double getTempoMs() {
        return tempoMs;
    }

    public String getStatus() {
        return status;
    }

    public boolean isValido() {
        return valido;
    }

    public List<Jogador> getRanking() {
        return ranking;
    }
}
