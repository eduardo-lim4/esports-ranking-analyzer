import benchmark.Benchmark;
import benchmark.BenchmarkResult;
import data.DataGenerator;
import model.Categoria;
import model.Jogador;

import java.util.List;

/**
 * Testes para o benchmark.
 */
public class BenchmarkTest {

    public static void main(String[] args) {
        System.out.println("Executando BenchmarkTest...");

        testeComparacaoJusta();
        testeValidacaoOrdenacao();
        testePreservacaoOriginal();
        testeListaVazia();
        testeFalhaDeExecucao();

        System.out.println("BenchmarkTest: OK");
    }

    private static void testeComparacaoJusta() {
        DataGenerator gerador = new DataGenerator();
        List<Jogador> jogadores = gerador.gerar(Categoria.DANCA, 500);

        Benchmark benchmark = new Benchmark();
        List<BenchmarkResult> resultados = benchmark.executar(
                jogadores,
                Jogador.comparadorPorCategoria(Categoria.DANCA)
        );

        assert resultados.size() == 3 : "Benchmark deve retornar 3 resultados";
        for (BenchmarkResult r : resultados) {
            assert r.getTempoMs() >= 0 : "Tempo deve ser não negativo";
            assert r.isValido() : "Todos os resultados devem ser válidos";
        }
    }

    private static void testeValidacaoOrdenacao() {
        DataGenerator gerador = new DataGenerator();
        List<Jogador> jogadores = gerador.gerar(Categoria.CORRIDA, 200);

        Benchmark benchmark = new Benchmark();
        List<BenchmarkResult> resultados = benchmark.executar(
                jogadores,
                Jogador.comparadorPorCategoria(Categoria.CORRIDA)
        );

        for (BenchmarkResult r : resultados) {
            List<Jogador> ranking = r.getRanking();
            for (int i = 1; i < ranking.size(); i++) {
                assert ranking.get(i - 1).getTempoProvaMs() <= ranking.get(i).getTempoProvaMs()
                        : "Ranking não está ordenado";
            }
        }
    }

    private static void testeListaVazia() {
        Benchmark benchmark = new Benchmark();
        List<BenchmarkResult> resultados = benchmark.executar(
                List.of(),
                Jogador.comparadorPorCategoria(Categoria.DANCA)
        );
        assert resultados.size() == 3 : "Lista vazia deve executar os três algoritmos";
        for (BenchmarkResult resultado : resultados) {
            assert resultado.isValido()
                    : "Resultado vazio deve ser válido: " + resultado.getStatus();
            assert resultado.getRanking().isEmpty()
                    : "Ranking da lista vazia deve permanecer vazio";
        }
    }

    private static void testeFalhaDeExecucao() {
        DataGenerator gerador = new DataGenerator();
        List<Jogador> jogadores = gerador.gerar(Categoria.DANCA, 10);
        List<BenchmarkResult> resultados = new Benchmark().executar(
                jogadores,
                (a, b) -> {
                    throw new IllegalStateException("Falha simulada");
                }
        );
        assert resultados.size() == 3
                : "Falha de um algoritmo deve preservar as três linhas de resultado";
        for (BenchmarkResult resultado : resultados) {
            assert !resultado.isValido()
                    : "Execução com comparador defeituoso não pode ser válida";
            assert resultado.getStatus().contains("Falha simulada")
                    : "A falha deve ser explicada no status";
        }
    }

    private static void testePreservacaoOriginal() {
        DataGenerator gerador = new DataGenerator();
        List<Jogador> original = gerador.gerar(Categoria.LUTA, 100);
        List<Jogador> copia = new java.util.ArrayList<>(original);

        Benchmark benchmark = new Benchmark();
        benchmark.executar(copia, Jogador.comparadorPorCategoria(Categoria.LUTA));

        assert original.equals(copia) : "Lista original não deve ser alterada";
    }
}
