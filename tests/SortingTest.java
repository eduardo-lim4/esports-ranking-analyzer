import algorithms.BubbleSort;
import algorithms.InsertionSort;
import algorithms.MergeSort;
import algorithms.SortingAlgorithm;
import model.Categoria;
import model.Jogador;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Testes para os algoritmos de ordenação.
 *
 * Verifica cenários como lista vazia, um único elemento, ordem aleatória,
 * ordem inversa, dados já ordenados e empates. Garante que os três
 * algoritmos produzam resultados equivalentes para a mesma entrada.
 */
public class SortingTest {

    public static void main(String[] args) {
        System.out.println("Executando SortingTest...");

        testeListaVazia();
        testeUmElemento();
        testeOrdenacaoAleatoria();
        testeDadosJaOrdenados();
        testeOrdemInversa();
        testeEmpates();
        testeCategorias();
        testeResultadosEquivalentes();

        System.out.println("SortingTest: OK");
    }

    private static void testeListaVazia() {
        List<Jogador> lista = new ArrayList<>();
        executarTodos(lista, Categoria.DANCA);
        assert lista.isEmpty() : "Lista vazia deve permanecer vazia";
    }

    private static void testeUmElemento() {
        List<Jogador> lista = List.of(criarDanca(1, "Ana", 1000, 90.0, 10));
        List<Jogador> copia = new ArrayList<>(lista);
        executarTodos(copia, Categoria.DANCA);
        assert copia.get(0).getId() == 1 : "Lista unitária deve permanecer inalterada";
    }

    private static void testeOrdenacaoAleatoria() {
        List<Jogador> lista = List.of(
                criarDanca(1, "Ana", 5000, 95.0, 50),
                criarDanca(2, "Bruno", 9000, 98.0, 120),
                criarDanca(3, "Carlos", 7000, 92.0, 80),
                criarDanca(4, "Diana", 9000, 99.0, 150)
        );
        List<Jogador> copia = new ArrayList<>(lista);
        executarTodos(copia, Categoria.DANCA);
        assert copia.get(0).getId() == 4 : "Maior pontuação/precisão deve ficar em primeiro";
        assert copia.get(1).getId() == 2 : "Desempate por precisão";
        assert copia.get(3).getId() == 1 : "Menor pontuação deve ficar em último";
    }

    private static void testeDadosJaOrdenados() {
        List<Jogador> lista = List.of(
                criarDanca(1, "Ana", 9000, 99.0, 100),
                criarDanca(2, "Bruno", 8000, 95.0, 80),
                criarDanca(3, "Carlos", 7000, 90.0, 60)
        );
        List<Jogador> copia = new ArrayList<>(lista);
        executarTodos(copia, Categoria.DANCA);
        assertOrdenado(copia, Categoria.DANCA);
    }

    private static void testeOrdemInversa() {
        List<Jogador> lista = List.of(
                criarDanca(1, "Ana", 1000, 80.0, 10),
                criarDanca(2, "Bruno", 2000, 85.0, 20),
                criarDanca(3, "Carlos", 3000, 90.0, 30)
        );
        List<Jogador> copia = new ArrayList<>(lista);
        executarTodos(copia, Categoria.DANCA);
        assert copia.get(0).getId() == 3 : "Ordem inversa deve ser corrigida";
        assert copia.get(2).getId() == 1 : "Menor elemento deve ficar em último";
    }

    private static void testeEmpates() {
        List<Jogador> lista = List.of(
                criarDanca(1, "Ana", 5000, 90.0, 50),
                criarDanca(2, "Bruno", 5000, 90.0, 50),
                criarDanca(3, "Carlos", 5000, 90.0, 50)
        );
        List<Jogador> copia = new ArrayList<>(lista);
        executarTodos(copia, Categoria.DANCA);
        assertOrdenado(copia, Categoria.DANCA);
    }

    private static void testeCategorias() {
        List<Jogador> corridas = List.of(
                criarCorrida(1, "Ana", 120000, 10, 2),
                criarCorrida(2, "Bruno", 115000, 10, 1),
                criarCorrida(3, "Carlos", 130000, 10, 3)
        );
        List<Jogador> copia = new ArrayList<>(corridas);
        executarTodos(copia, Categoria.CORRIDA);
        assert copia.get(0).getId() == 2 : "Menor tempo de prova deve vencer";

        List<Jogador> lutas = List.of(
                criarLuta(1, "Ana", 40, 10),
                criarLuta(2, "Bruno", 45, 5),
                criarLuta(3, "Carlos", 36, 4)
        );
        copia = new ArrayList<>(lutas);
        executarTodos(copia, Categoria.LUTA);
        assert copia.get(0).getId() == 2 : "Maior aproveitamento deve vencer";
    }

    private static void testeResultadosEquivalentes() {
        List<Jogador> lista = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            lista.add(criarDanca(i, "Jogador " + i, i * 100, i % 100, i));
        }
        Collections.shuffle(lista);

        Comparator<Jogador> comparador = Jogador.comparadorPorCategoria(Categoria.DANCA);

        List<Jogador> bubble = new ArrayList<>(lista);
        List<Jogador> insertion = new ArrayList<>(lista);
        List<Jogador> merge = new ArrayList<>(lista);

        new BubbleSort<Jogador>().sort(bubble, comparador);
        new InsertionSort<Jogador>().sort(insertion, comparador);
        new MergeSort<Jogador>().sort(merge, comparador);

        assert bubble.equals(insertion) : "Bubble e Insertion devem produzir resultados iguais";
        assert insertion.equals(merge) : "Insertion e Merge devem produzir resultados iguais";
    }

    private static void executarTodos(List<Jogador> lista, Categoria categoria) {
        Comparator<Jogador> comparador = Jogador.comparadorPorCategoria(categoria);
        new BubbleSort<Jogador>().sort(lista, comparador);
        assertOrdenado(lista, categoria);

        List<Jogador> insertion = new ArrayList<>(lista);
        new InsertionSort<Jogador>().sort(insertion, comparador);
        assertOrdenado(insertion, categoria);

        List<Jogador> merge = new ArrayList<>(lista);
        new MergeSort<Jogador>().sort(merge, comparador);
        assertOrdenado(merge, categoria);
    }

    private static void assertOrdenado(List<Jogador> lista, Categoria categoria) {
        Comparator<Jogador> comparador = Jogador.comparadorPorCategoria(categoria);
        for (int i = 1; i < lista.size(); i++) {
            assert comparador.compare(lista.get(i - 1), lista.get(i)) <= 0
                    : "Lista não está ordenada corretamente";
        }
    }

    private static Jogador criarDanca(int id, String nome, int pontuacao,
                                      double precisao, int combo) {
        return new Jogador(id, nome, Categoria.DANCA, pontuacao, precisao, combo,
                0, 0, 0, 0, 0);
    }

    private static Jogador criarCorrida(int id, String nome, int tempoMs,
                                        int voltas, int posicao) {
        return new Jogador(id, nome, Categoria.CORRIDA, 0, 0.0, 0,
                tempoMs, voltas, posicao, 0, 0);
    }

    private static Jogador criarLuta(int id, String nome, int vitorias, int derrotas) {
        return new Jogador(id, nome, Categoria.LUTA, 0, 0.0, 0,
                0, 0, 0, vitorias, derrotas);
    }
}
