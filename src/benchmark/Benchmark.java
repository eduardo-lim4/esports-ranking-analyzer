package benchmark;

import algorithms.BubbleSort;
import algorithms.InsertionSort;
import algorithms.MergeSort;
import algorithms.SortingAlgorithm;
import model.Jogador;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Executa o benchmark comparando os três algoritmos de ordenação.
 *
 * O benchmark garante que todos os algoritmos recebam cópias equivalentes do
 * mesmo conjunto inicial de dados e mede exclusivamente o tempo gasto pela
 * ordenação, utilizando {@link System#nanoTime()}.
 */
public class Benchmark {

    private final BubbleSort<Jogador> bubbleSort = new BubbleSort<>();
    private final InsertionSort<Jogador> insertionSort = new InsertionSort<>();
    private final MergeSort<Jogador> mergeSort = new MergeSort<>();

    /**
     * Executa o benchmark sobre a lista de jogadores informada.
     *
     * @param jogadores  lista original de jogadores
     * @param comparador critério de ordenação
     * @return resultados do benchmark para cada algoritmo
     * @throws IllegalArgumentException se a lista ou o comparador forem nulos
     */
    public List<BenchmarkResult> executar(List<Jogador> jogadores,
                                              Comparator<Jogador> comparador) {
        if (jogadores == null) {
            throw new IllegalArgumentException(
                    "A lista de jogadores não pode ser nula."
            );
        }
        if (comparador == null) {
            throw new IllegalArgumentException(
                    "O comparador não pode ser nulo."
            );
        }

        List<BenchmarkResult> resultados = new ArrayList<>();
        resultados.add(medir(bubbleSort, jogadores, comparador));
        resultados.add(medir(insertionSort, jogadores, comparador));
        resultados.add(medir(mergeSort, jogadores, comparador));
        return resultados;
    }

    private BenchmarkResult medir(SortingAlgorithm<Jogador> algoritmo,
                                  List<Jogador> original,
                                  Comparator<Jogador> comparador) {
        List<Jogador> copia = new ArrayList<>(original);

        long inicio = System.nanoTime();
        try {
            algoritmo.sort(copia, comparador);
        } catch (RuntimeException e) {
            long fim = System.nanoTime();
            double tempoMs = (fim - inicio) / 1_000_000.0;
            return new BenchmarkResult(
                    algoritmo.getName(),
                    tempoMs,
                    "Erro na execução: " + mensagemDaExcecao(e),
                    false,
                    copia
            );
        }
        long fim = System.nanoTime();

        double tempoMs = (fim - inicio) / 1_000_000.0;

        boolean ordenado = estaOrdenado(copia, comparador);
        boolean mesmosElementos = mesmosElementos(original, copia);

        String status;
        if (!ordenado) {
            status = "Erro: ordenação inválida";
        } else if (!mesmosElementos) {
            status = "Erro: dados alterados";
        } else {
            status = "Concluído";
        }

        return new BenchmarkResult(
                algoritmo.getName(),
                tempoMs,
                status,
                ordenado && mesmosElementos,
                copia
        );
    }

    private String mensagemDaExcecao(RuntimeException e) {
        String mensagem = e.getMessage();
        return mensagem == null || mensagem.isBlank()
                ? e.getClass().getSimpleName()
                : mensagem;
    }

    private boolean estaOrdenado(List<Jogador> lista, Comparator<Jogador> comparador) {
        for (int i = 1; i < lista.size(); i++) {
            if (comparador.compare(lista.get(i - 1), lista.get(i)) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica se a lista ordenada contém exatamente os mesmos elementos da
     * lista original (mesma multiplicidade), sem perdas, duplicações ou
     * alterações. A comparação é feita por contagem de frequência, fora do
     * intervalo cronometrado e sem utilizar ordenação pronta.
     */
    private boolean mesmosElementos(List<Jogador> original, List<Jogador> ordenado) {
        if (original.size() != ordenado.size()) {
            return false;
        }
        if (original.isEmpty()) {
            return true;
        }
        Map<Jogador, Integer> contagem = new HashMap<>();
        for (Jogador j : original) {
            contagem.merge(j, 1, Integer::sum);
        }
        for (Jogador j : ordenado) {
            Integer restante = contagem.get(j);
            if (restante == null) {
                return false;
            }
            if (restante == 1) {
                contagem.remove(j);
            } else {
                contagem.put(j, restante - 1);
            }
        }
        return contagem.isEmpty();
    }
}
