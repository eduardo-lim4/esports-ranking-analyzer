package algorithms;

import java.util.Comparator;
import java.util.List;

/**
 * Contrato comum para os algoritmos de ordenação do sistema.
 *
 * Cada implementação deve ordenar a lista fornecida de acordo com o
 * comparador, sem depender de métodos de ordenação prontos da biblioteca
 * padrão do Java.
 *
 * @param <T> tipo dos elementos da lista
 */
public interface SortingAlgorithm<T> {

    /**
     * Ordena a lista de acordo com o comparador informado.
     *
     * @param lista      lista a ser ordenada (modificada in-place)
     * @param comparador critério de ordenação
     */
    void sort(List<T> lista, Comparator<? super T> comparador);

    /**
     * @return nome legível do algoritmo
     */
    String getName();
}
