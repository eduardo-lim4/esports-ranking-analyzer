package data;

import model.Categoria;
import model.Jogador;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Lê jogadores a partir de arquivos de texto no formato especificado pelo
 * sistema.
 *
 * O tempo de leitura do arquivo não entra na medição do benchmark.
 *
 * <b>Formato esperado:</b>
 * <ul>
 *   <li>Arquivo codificado em UTF-8</li>
 *   <li>Primeira linha: {@code CATEGORIA=<DANCA|CORRIDA|LUTA>}</li>
 *   <li>Segunda linha: cabeçalho com os nomes dos campos separados por
 *       ponto-e-vírgula</li>
 *   <li>Linhas seguintes: um registro por linha, campos separados por
 *       ponto-e-vírgula</li>
 * </ul>
 */
public class DataReader {

    /**
     * Abre uma janela de seleção de arquivos e retorna o arquivo escolhido.
     *
     * @return arquivo selecionado, ou {@code null} se o usuário cancelar
     */
    public File selecionarArquivo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecionar arquivo de jogadores");
        chooser.setFileFilter(
                new FileNameExtensionFilter("Arquivos de texto (*.txt)", "txt")
        );
        int resultado = chooser.showOpenDialog(null);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    /**
     * Lê jogadores do arquivo informado, validando a compatibilidade com a
     * categoria esperada.
     *
     * @param arquivo          arquivo a ser lido
     * @param categoriaEsperada categoria que deve estar no arquivo
     * @return lista de jogadores lidos
     * @throws IOException              se houver erro de leitura
     * @throws IllegalArgumentException se o arquivo estiver vazio,
     *                                  malformado ou com categoria incompatível
     */
    public List<Jogador> ler(File arquivo, Categoria categoriaEsperada)
            throws IOException {
        if (arquivo == null) {
            throw new IllegalArgumentException("Nenhum arquivo foi selecionado.");
        }
        if (categoriaEsperada == null) {
            throw new IllegalArgumentException("A categoria esperada é obrigatória.");
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(arquivo),
                        StandardCharsets.UTF_8
                )
        )) {
            String linhaCategoria = reader.readLine();
            if (linhaCategoria == null || linhaCategoria.isBlank()) {
                throw new IllegalArgumentException(
                        "O arquivo está vazio ou não possui a linha de categoria."
                );
            }
            // Remove marca de ordem de bytes (BOM), caso presente.
            linhaCategoria = linhaCategoria.replace("\uFEFF", "");

            Categoria categoriaArquivo = extrairCategoria(linhaCategoria);
            if (categoriaArquivo != categoriaEsperada) {
                throw new IllegalArgumentException(String.format(
                        "Categoria incompatível. Esperada: %s, encontrada: %s.",
                        categoriaEsperada.getNomeExibicao(),
                        categoriaArquivo.getNomeExibicao()
                ));
            }

            String cabecalho = reader.readLine();
            if (cabecalho == null || cabecalho.isBlank()) {
                throw new IllegalArgumentException(
                        "O arquivo não possui a linha de cabeçalho."
                );
            }

            List<String> colunas = Arrays.stream(cabecalho.split(";", -1))
                    .map(String::trim)
                    .toList();
            validarCabecalho(colunas, categoriaArquivo);
            List<Jogador> jogadores = new ArrayList<>();
            String linha;
            int numeroLinha = 3;

            while ((linha = reader.readLine()) != null) {
                if (linha.isBlank()) {
                    numeroLinha++;
                    continue;
                }
                jogadores.add(parseLinha(linha, categoriaArquivo, colunas, numeroLinha));
                numeroLinha++;
            }

            if (jogadores.isEmpty()) {
                throw new IllegalArgumentException(
                        "O arquivo não contém registros de jogadores."
                );
            }

            validarIdsUnicos(jogadores);

            return jogadores;
        }
    }

    private void validarCabecalho(List<String> colunas, Categoria categoria) {
        List<String> esperadas = switch (categoria) {
            case DANCA -> List.of(
                    "ID", "NOME", "PONTUACAO", "PRECISAO", "COMBO_MAXIMO"
            );
            case CORRIDA -> List.of(
                    "ID", "NOME", "TEMPO_PROVA_MS", "VOLTAS", "POSICAO_CHEGADA"
            );
            case LUTA -> List.of("ID", "NOME", "VITORIAS", "DERROTAS");
        };
        if (!esperadas.equals(colunas)) {
            throw new IllegalArgumentException(
                    "Cabeçalho inválido para " + categoria.getNomeExibicao()
                            + ". Esperado: " + String.join(";", esperadas)
            );
        }
    }

    private Categoria extrairCategoria(String linha) {
        if (!linha.trim().toUpperCase().startsWith("CATEGORIA=")) {
            throw new IllegalArgumentException(
                    "A primeira linha deve iniciar com 'CATEGORIA='."
            );
        }
        String valor = linha.substring("CATEGORIA=".length()).trim();
        try {
            return Categoria.fromString(valor);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Categoria inválida no arquivo: " + valor
            );
        }
    }

    private Jogador parseLinha(String linha,
                               Categoria categoria,
                               List<String> colunas,
                               int numeroLinha) {
        String[] campos = linha.split(";");
        if (campos.length != colunas.size()) {
            throw new IllegalArgumentException(String.format(
                    "Linha %d: número de campos incorreto. Esperado: %d, encontrado: %d.",
                    numeroLinha, colunas.size(), campos.length
            ));
        }

        int id = obterInteiro(colunas, campos, "ID", numeroLinha);
        String nome = obterString(colunas, campos, "NOME", numeroLinha);

        return switch (categoria) {
            case DANCA -> new Jogador(
                    id,
                    nome,
                    categoria,
                    obterInteiro(colunas, campos, "PONTUACAO", numeroLinha),
                    obterDouble(colunas, campos, "PRECISAO", numeroLinha),
                    obterInteiro(colunas, campos, "COMBO_MAXIMO", numeroLinha),
                    0, 0, 0, 0, 0
            );
            case CORRIDA -> new Jogador(
                    id,
                    nome,
                    categoria,
                    0, 0.0, 0,
                    obterInteiro(colunas, campos, "TEMPO_PROVA_MS", numeroLinha),
                    obterInteiro(colunas, campos, "VOLTAS", numeroLinha),
                    obterInteiro(colunas, campos, "POSICAO_CHEGADA", numeroLinha),
                    0, 0
            );
            case LUTA -> new Jogador(
                    id,
                    nome,
                    categoria,
                    0, 0.0, 0, 0, 0, 0,
                    obterInteiro(colunas, campos, "VITORIAS", numeroLinha),
                    obterInteiro(colunas, campos, "DERROTAS", numeroLinha)
            );
        };
    }

    private String obterString(List<String> colunas,
                               String[] campos,
                               String nome,
                               int numeroLinha) {
        int indice = colunas.indexOf(nome);
        if (indice == -1) {
            throw new IllegalArgumentException(
                    "Cabeçalho ausente: " + nome
            );
        }
        String valor = campos[indice].trim();
        if (valor.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                    "Linha %d: campo '%s' está vazio.",
                    numeroLinha, nome
            ));
        }
        return valor;
    }

    private int obterInteiro(List<String> colunas,
                             String[] campos,
                             String nome,
                             int numeroLinha) {
        String valor = obterString(colunas, campos, nome, numeroLinha);
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format(
                    "Linha %d: campo '%s' deve ser um número inteiro válido.",
                    numeroLinha, nome
            ));
        }
    }

    private double obterDouble(List<String> colunas,
                               String[] campos,
                               String nome,
                               int numeroLinha) {
        String valor = obterString(colunas, campos, nome, numeroLinha);
        try {
            return Double.parseDouble(valor.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format(
                    "Linha %d: campo '%s' deve ser um número decimal válido.",
                    numeroLinha, nome
            ));
        }
    }

    private void validarIdsUnicos(List<Jogador> jogadores) {
        Set<Integer> ids = new HashSet<>();
        for (Jogador j : jogadores) {
            if (!ids.add(j.getId())) {
                throw new IllegalArgumentException(
                        "ID duplicado encontrado no arquivo: " + j.getId()
                );
            }
        }
    }
}
