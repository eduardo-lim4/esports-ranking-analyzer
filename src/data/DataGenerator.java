package data;

import model.Categoria;
import model.Jogador;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gera conjuntos de jogadores para os testes de ordenação.
 *
 * A geração é totalmente independente da ordenação e do benchmark. O método
 * {@link #gerar(Categoria, int, long)} permite reproduzir um conjunto de
 * dados através de uma semente aleatória.
 */
public class DataGenerator {

    private static final String[] NOMES = {
            "Ana", "Bruno", "Carlos", "Diana", "Eduardo", "Fernanda",
            "Gabriel", "Helena", "Igor", "Juliana", "Kevin", "Larissa",
            "Marcos", "Natalia", "Otavio", "Patricia", "Quentin", "Rafaela",
            "Sergio", "Tatiane", "Ursula", "Vitor", "Wagner", "Xenia",
            "Yuri", "Zilda", "Alice", "Bernardo", "Cecilia", "Daniel"
    };

    private static final String[] SOBRENOMES = {
            "Silva", "Santos", "Oliveira", "Souza", "Pereira", "Costa",
            "Carvalho", "Almeida", "Ferreira", "Rodrigues", "Martins",
            "Lima", "Araujo", "Fernandes", "Barbosa", "Ribeiro", "Gomes",
            "Monteiro", "Mendes", "Freitas", "Pinto", "Machado", "Araújo"
    };

    /**
     * Gera uma lista de jogadores para a categoria e quantidade informadas,
     * utilizando a semente fornecida para tornar o resultado reprodutível.
     *
     * @param categoria  categoria dos jogadores
     * @param quantidade quantidade de jogadores a ser gerada
     * @param semente    semente para o gerador de números aleatórios
     * @return lista de jogadores gerada
     * @throws IllegalArgumentException se a categoria for nula ou a
     *                                  quantidade for menor ou igual a zero
     */
    public List<Jogador> gerar(Categoria categoria, int quantidade, long semente) {
        if (categoria == null) {
            throw new IllegalArgumentException("A categoria é obrigatória.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade de jogadores deve ser maior que zero."
            );
        }

        Random random = new Random(semente);
        List<Jogador> jogadores = new ArrayList<>(quantidade);

        for (int i = 0; i < quantidade; i++) {
            String nome = gerarNome(random);
            jogadores.add(gerarPorCategoria(i + 1, nome, categoria, random));
        }

        return jogadores;
    }

    /**
     * Gera uma lista de jogadores para a categoria e quantidade informadas,
     * utilizando uma semente fixa padrão.
     *
     * @param categoria  categoria dos jogadores
     * @param quantidade quantidade de jogadores a ser gerada
     * @return lista de jogadores gerada
     */
    public List<Jogador> gerar(Categoria categoria, int quantidade) {
        return gerar(categoria, quantidade, 42L);
    }

    private Jogador gerarPorCategoria(int id,
                                      String nome,
                                      Categoria categoria,
                                      Random random) {
        return switch (categoria) {
            case DANCA -> new Jogador(
                    id,
                    nome,
                    categoria,
                    50_000 + random.nextInt(950_001),      // pontuação: 50k a 1M
                    70.0 + random.nextDouble() * 30.0,     // precisão: 70% a 100%
                    random.nextInt(500),                   // combo máximo: 0 a 499
                    0, 0, 0, 0, 0
            );
            case CORRIDA -> new Jogador(
                    id,
                    nome,
                    categoria,
                    0, 0.0, 0,
                    60_000 + random.nextInt(240_001),      // tempo: 60s a 300s
                    3 + random.nextInt(48),                // voltas: 3 a 50
                    1 + random.nextInt(20),                // posição: 1 a 20
                    0, 0
            );
            case LUTA -> new Jogador(
                    id,
                    nome,
                    categoria,
                    0, 0.0, 0, 0, 0, 0,
                    random.nextInt(100),                   // vitórias: 0 a 99
                    random.nextInt(50)                     // derrotas: 0 a 49
            );
        };
    }

    private String gerarNome(Random random) {
        return NOMES[random.nextInt(NOMES.length)]
                + " "
                + SOBRENOMES[random.nextInt(SOBRENOMES.length)];
    }
}
