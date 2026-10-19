import benchmark.Benchmark;
import benchmark.BenchmarkResult;
import data.DataGenerator;
import model.Categoria;
import model.Jogador;

import java.util.List;

/**
 * Smoke de escala do benchmark, deliberadamente limitado a 1.000 e 10.000
 * registros para não submeter os algoritmos quadráticos a um volume excessivo.
 */
public class BenchmarkScaleTest {

    public static void main(String[] args) {
        System.out.println("Executando BenchmarkScaleTest...");
        DataGenerator gerador = new DataGenerator();
        Benchmark benchmark = new Benchmark();

        for (int quantidade : new int[]{1_000, 10_000}) {
            List<Jogador> jogadores = gerador.gerar(
                    Categoria.DANCA, quantidade, 2026L
            );
            List<BenchmarkResult> resultados = benchmark.executar(
                    jogadores,
                    Jogador.comparadorPorCategoria(Categoria.DANCA)
            );

            assert resultados.size() == 3;
            System.out.println("  Registros: " + quantidade);
            for (BenchmarkResult resultado : resultados) {
                assert resultado.isValido()
                        : resultado.getAlgoritmo() + " falhou: " + resultado.getStatus();
                System.out.printf("    %-15s %.3f ms%n",
                        resultado.getAlgoritmo(), resultado.getTempoMs());
            }
        }

        System.out.println("BenchmarkScaleTest: OK");
    }
}
