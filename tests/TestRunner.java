/**
 * Executor simples de todos os testes do projeto.
 *
 * Não utiliza framework externo. Cada classe de teste possui um método
 * {@code main} que lança {@link AssertionError} em caso de falha.
 */
public class TestRunner {

    public static void main(String[] args) throws Exception {
        System.out.println("====================================");
        System.out.println("  Esports Ranking Analyzer - Testes");
        System.out.println("====================================\n");

        try {
            SortingTest.main(args);
            DataGeneratorTest.main(args);
            DataReaderTest.main(args);
            BenchmarkTest.main(args);

            System.out.println("\n====================================");
            System.out.println("  TODOS OS TESTES PASSARAM");
            System.out.println("====================================");
        } catch (AssertionError e) {
            System.err.println("\nFALHA: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
