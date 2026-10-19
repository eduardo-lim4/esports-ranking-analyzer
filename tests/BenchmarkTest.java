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

    private static void testePreservacaoOriginal() {
        DataGenerator gerador = new DataGenerator();
        List<Jogador> original = gerador.gerar(Categoria.LUTA, 100);
        List<Jogador> copia = new java.util.ArrayList<>(original);

        Benchmark benchmark = new Benchmark();
        benchmark.executar(copia, Jogador.comparadorPorCategoria(Categoria.LUTA));

        assert original.equals(copia) : "Lista original não deve ser alterada";
    }
}
