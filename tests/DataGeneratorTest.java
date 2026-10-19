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
        testeCategoriaNulaInvalida();

        System.out.println("DataGeneratorTest: OK");
    }

    private static void testeQuantidades() {
        DataGenerator gerador = new DataGenerator();
        int[] quantidades = {1000, 10000, 50000, 100000};
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
                switch (categoria) {
                    case DANCA -> {
                        assert j.getPontuacao() >= 50_000
                                && j.getPontuacao() <= 1_000_000;
                        assert j.getPrecisao() >= 70.0 && j.getPrecisao() <= 100.0;
                        assert j.getComboMaximo() >= 0 && j.getComboMaximo() < 500;
                    }
                    case CORRIDA -> {
                        assert j.getTempoProvaMs() >= 60_000
                                && j.getTempoProvaMs() <= 300_000;
                        assert j.getVoltas() >= 3 && j.getVoltas() <= 50;
                        assert j.getPosicaoChegada() >= 1 && j.getPosicaoChegada() <= 20;
                    }
                    case LUTA -> {
                        assert j.getVitorias() >= 0 && j.getVitorias() < 100;
                        assert j.getDerrotas() >= 0 && j.getDerrotas() < 50;
                    }
                }
            }
        }
    }

    private static void testeReprodutibilidade() {
        DataGenerator gerador = new DataGenerator();
        List<Jogador> a = gerador.gerar(Categoria.LUTA, 100, 123L);
        List<Jogador> b = gerador.gerar(Categoria.LUTA, 100, 123L);
        assert a.equals(b) : "Semente igual deve gerar dados iguais";
    }

    private static void testeCategoriaNulaInvalida() {
        DataGenerator gerador = new DataGenerator();
        try {
            gerador.gerar(null, 10);
            assert false : "Categoria nula deveria ser rejeitada";
        } catch (IllegalArgumentException e) {
            // esperado
        }
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
