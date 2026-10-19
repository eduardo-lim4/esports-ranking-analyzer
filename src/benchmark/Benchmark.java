package benchmark;

import algorithms.BubbleSort;
import algorithms.InsertionSort;
import algorithms.MergeSort;
import algorithms.SortingAlgorithm;
import model.Jogador;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
        algoritmo.sort(copia, comparador);
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

    private boolean estaOrdenado(List<Jogador> lista, Comparator<Jogador> comparador) {
        for (int i = 1; i < lista.size(); i++) {
            if (comparador.compare(lista.get(i - 1), lista.get(i)) > 0) {
                return false;
            }
        }
        return true;
    }

    private boolean mesmosElementos(List<Jogador> original, List<Jogador> ordenado) {
        if (original.size() != ordenado.size()) {
            return false;
        }
        List<Jogador> copiaOriginal = new ArrayList<>(original);
        List<Jogador> copiaOrdenado = new ArrayList<>(ordenado);
        copiaOriginal.sort(Jogador.comparadorPorCategoria(original.get(0).getCategoria()));
        copiaOrdenado.sort(Jogador.comparadorPorCategoria(original.get(0).getCategoria()));
        return copiaOriginal.equals(copiaOrdenado);
    }
}
