import benchmark.Benchmark;
import benchmark.BenchmarkResult;
import data.DataReader;
import model.Categoria;
import model.Jogador;

import java.io.File;
import java.util.List;

/**
 * Teste de integração simples que lê os arquivos de exemplo e executa o
 * benchmark.
 */
public class SmokeTest {

    public static void main(String[] args) throws Exception {
        System.out.println("Executando SmokeTest...");

        for (Categoria categoria : Categoria.values()) {
            String nomeArquivo = categoria.name().toLowerCase() + "_exemplo.txt";
            File arquivo = new File("data", nomeArquivo);
            System.out.println("Lendo " + arquivo.getPath() + "...");

            DataReader leitor = new DataReader();
            List<Jogador> jogadores = leitor.ler(arquivo, categoria);
            System.out.println("  Jogadores lidos: " + jogadores.size());

            Benchmark benchmark = new Benchmark();
            List<BenchmarkResult> resultados = benchmark.executar(
                    jogadores,
                    Jogador.comparadorPorCategoria(categoria)
            );

            for (BenchmarkResult r : resultados) {
                System.out.printf("  %-15s: %8.3f ms - %s%n",
                        r.getAlgoritmo(), r.getTempoMs(), r.getStatus());
            }
        }

        System.out.println("SmokeTest: OK");
    }
}
