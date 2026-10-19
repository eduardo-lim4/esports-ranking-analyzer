import data.DataGenerator;
import model.Categoria;
import model.Jogador;

import java.util.List;

/**
 * Testes para o gerador de dados internos.
 */
public class DataGeneratorTest {

    public static void main(String[] args) {
        System.out.println("Executando DataGeneratorTest...");

        testeQuantidades();
        testeCategorias();
        testeReprodutibilidade();
        testeQuantidadeInvalida();

        System.out.println("DataGeneratorTest: OK");
    }

    private static void testeQuantidades() {
        DataGenerator gerador = new DataGenerator();
        int[] quantidades = {1000, 10000, 50000};
        for (int qtd : quantidades) {
            List<Jogador> jogadores = gerador.gerar(Categoria.DANCA, qtd);
            assert jogadores.size() == qtd
                    : "Quantidade gerada incorreta para " + qtd;
        }
    }

    private static void testeCategorias() {
        DataGenerator gerador = new DataGenerator();
        for (Categoria categoria : Categoria.values()) {
            List<Jogador> jogadores = gerador.gerar(categoria, 100);
            for (Jogador j : jogadores) {
                assert j.getCategoria() == categoria
                        : "Categoria do jogador não corresponde";
            }
        }
    }

    private static void testeReprodutibilidade() {
        DataGenerator gerador = new DataGenerator();
        List<Jogador> a = gerador.gerar(Categoria.LUTA, 100, 123L);
        List<Jogador> b = gerador.gerar(Categoria.LUTA, 100, 123L);
        assert a.equals(b) : "Semente igual deve gerar dados iguais";
    }

    private static void testeQuantidadeInvalida() {
        DataGenerator gerador = new DataGenerator();
        try {
            gerador.gerar(Categoria.DANCA, 0);
            assert false : "Quantidade zero deveria lançar exceção";
        } catch (IllegalArgumentException e) {
            // esperado
        }
    }
}
