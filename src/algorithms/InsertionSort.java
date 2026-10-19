package algorithms;

import java.util.Comparator;
import java.util.List;

/**
 * Implementação do algoritmo Insertion Sort.
 *
 * O algoritmo constrói a porção ordenada da lista um elemento de cada vez,
 * inserindo cada novo elemento na posição correta em relação aos já
 * ordenados.
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
public class InsertionSort<T> implements SortingAlgorithm<T> {

    @Override
    public void sort(List<T> lista, Comparator<? super T> comparador) {
        if (lista == null) {
            throw new IllegalArgumentException("A lista não pode ser nula.");
        }
        if (comparador == null) {
            throw new IllegalArgumentException("O comparador não pode ser nulo.");
        }

        int n = lista.size();
        for (int i = 1; i < n; i++) {
            T chave = lista.get(i);
            int j = i - 1;
            while (j >= 0
                    && comparador.compare(lista.get(j), chave) > 0) {
                lista.set(j + 1, lista.get(j));
                j--;
            }
            lista.set(j + 1, chave);
        }
    }

    @Override
    public String getName() {
        return "Insertion Sort";
    }
}
