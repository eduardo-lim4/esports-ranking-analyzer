package algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementação do algoritmo Merge Sort.
 *
 * O algoritmo divide recursivamente a lista pela metade até que cada parte
 * contenha um único elemento, e então mescla (merge) as partes de forma
 * ordenada.
 *
 * <b>Complexidade:</b>
 * <ul>
 *   <li>Melhor, médio e pior caso: O(n log n)</li>
 *   <li>Espaço auxiliar: O(n)</li>
 * </ul>
 *
 * @param <T> tipo dos elementos da lista
 */
public class MergeSort<T> implements SortingAlgorithm<T> {

    @Override
    public void sort(List<T> lista, Comparator<? super T> comparador) {
        if (lista == null) {
            throw new IllegalArgumentException("A lista não pode ser nula.");
        }
        if (comparador == null) {
            throw new IllegalArgumentException("O comparador não pode ser nulo.");
        }

        if (lista.size() <= 1) {
            return;
        }
        List<T> auxiliar = new ArrayList<>(lista);
        mergeSort(lista, auxiliar, 0, lista.size() - 1, comparador);
    }

    private void mergeSort(List<T> lista,
                           List<T> auxiliar,
                           int esquerda,
                           int direita,
                           Comparator<? super T> comparador) {
        if (esquerda < direita) {
            int meio = esquerda + (direita - esquerda) / 2;
            mergeSort(lista, auxiliar, esquerda, meio, comparador);
            mergeSort(lista, auxiliar, meio + 1, direita, comparador);
            merge(lista, auxiliar, esquerda, meio, direita, comparador);
        }
    }

    private void merge(List<T> lista,
                       List<T> auxiliar,
                       int esquerda,
                       int meio,
                       int direita,
                       Comparator<? super T> comparador) {
        for (int i = esquerda; i <= direita; i++) {
            auxiliar.set(i, lista.get(i));
        }

        int i = esquerda;
        int j = meio + 1;
        int k = esquerda;

        while (i <= meio && j <= direita) {
            if (comparador.compare(auxiliar.get(i), auxiliar.get(j)) <= 0) {
                lista.set(k, auxiliar.get(i));
                i++;
            } else {
                lista.set(k, auxiliar.get(j));
                j++;
            }
            k++;
        }

        while (i <= meio) {
            lista.set(k, auxiliar.get(i));
            i++;
            k++;
        }

        while (j <= direita) {
            lista.set(k, auxiliar.get(j));
            j++;
            k++;
        }
    }

    @Override
    public String getName() {
        return "Merge Sort";
    }
}
