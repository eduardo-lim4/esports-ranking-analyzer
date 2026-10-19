package algorithms;

import java.util.Comparator;
import java.util.List;

/**
 * Implementação do algoritmo Bubble Sort.
 *
 * O algoritmo percorre repetidamente a lista, comparando elementos adjacentes
 * e trocando-os de posição quando estão fora de ordem. A cada passagem, o
 * maior elemento (ou menor, conforme o comparador) "flutua" para sua posição
 * final.
 *
 * <b>Complexidade:</b>
 * <ul>
 *   <li>Melhor caso (lista já ordenada): O(n)</li>
 *   <li>Caso médio e pior caso: O(n²)</li>
 *   <li>Espaço auxiliar: O(1)</li>
 * </ul>
 *
 * @param <T> tipo dos elementos da lista
 */
public class BubbleSort<T> implements SortingAlgorithm<T> {

    @Override
    public void sort(List<T> lista, Comparator<? super T> comparador) {
        if (lista == null) {
            throw new IllegalArgumentException("A lista não pode ser nula.");
        }
        if (comparador == null) {
            throw new IllegalArgumentException("O comparador não pode ser nulo.");
        }

        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            boolean trocou = false;
            for (int j = 0; j < n - i - 1; j++) {
                if (comparador.compare(lista.get(j), lista.get(j + 1)) > 0) {
                    T temp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temp);
                    trocou = true;
                }
            }
            if (!trocou) {
                break;
            }
        }
    }

    @Override
    public String getName() {
        return "Bubble Sort";
    }
}
