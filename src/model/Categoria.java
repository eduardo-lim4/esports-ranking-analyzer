package model;

/**
 * Categorias de jogos de e-sports suportadas pelo sistema.
 *
 * Cada categoria define um critério principal de ordenação para o ranking:
 *
 * - {@link #DANCA}: maior pontuação vence. Em empate, maior precisão e,
 *   posteriormente, maior combo máximo desempatam.
 * - {@link #CORRIDA}: menor tempo de prova vence. Em empate, menor posição de
 *   chegada e, posteriormente, maior número de voltas desempatam.
 * - {@link #LUTA}: maior aproveitamento (vitórias / total) vence. Em empate,
 *   maior número absoluto de vitórias e, posteriormente, menor número de
 *   derrotas desempatam.
 */
public enum Categoria {
    DANCA("Dança"),
    CORRIDA("Corrida"),
    LUTA("Luta");

    private final String nomeExibicao;

    Categoria(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    public String getNomeExibicao() {
        return nomeExibicao;
    }

    /**
     * Resolve uma categoria a partir de um texto, aceitando tanto o nome
     * técnico ({@code DANCA}) quanto o nome de exibição ({@code Dança}).
     *
     * @param texto texto a ser resolvido
     * @return a categoria correspondente
     * @throws IllegalArgumentException se o texto não corresponder a nenhuma
     *                                  categoria conhecida
     */
    public static Categoria fromString(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Categoria não informada.");
        }
        String normalizado = texto.trim().toUpperCase()
                .replace("Ç", "C")
                .replace("Ã", "A");
        for (Categoria c : values()) {
            if (c.name().equals(normalizado)
                    || c.nomeExibicao.equalsIgnoreCase(texto.trim())) {
                return c;
            }
        }
        throw new IllegalArgumentException(
                "Categoria desconhecida: " + texto
        );
    }
}
