import data.DataReader;
import model.Categoria;
import model.Jogador;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Testes para o leitor de arquivos de texto.
 */
public class DataReaderTest {

    public static void main(String[] args) throws IOException {
        System.out.println("Executando DataReaderTest...");

        File arquivoValido = criarArquivoValido();
        File arquivoVazio = criarArquivoVazio();
        File arquivoIncompativel = criarArquivoIncompativel();
        File arquivoMalformado = criarArquivoMalformado();
        File arquivoIdsDuplicados = criarArquivoIdsDuplicados();
        File arquivoAcentos = criarArquivoComAcentos();
        File arquivoCabecalhoIncompleto = criarArquivoCabecalhoIncompleto();

        testeArquivoValido(arquivoValido);
        testeArquivoVazio(arquivoVazio);
        testeCategoriaIncompativel(arquivoIncompativel);
        testeArquivoMalformado(arquivoMalformado);
        testeIdsDuplicados(arquivoIdsDuplicados);
        testeAcentosUtf8(arquivoAcentos);
        testeCabecalhoIncompleto(arquivoCabecalhoIncompleto);

        arquivoValido.delete();
        arquivoVazio.delete();
        arquivoIncompativel.delete();
        arquivoMalformado.delete();
        arquivoIdsDuplicados.delete();
        arquivoAcentos.delete();
        arquivoCabecalhoIncompleto.delete();

        System.out.println("DataReaderTest: OK");
    }

    private static void testeArquivoValido(File arquivo) throws IOException {
        DataReader leitor = new DataReader();
        List<Jogador> jogadores = leitor.ler(arquivo, Categoria.DANCA);
        assert jogadores.size() == 3 : "Deveriam ser lidos 3 jogadores";
        assert jogadores.get(0).getNome().equals("Ana Silva")
                : "Nome do primeiro jogador incorreto";
    }

    private static void testeArquivoVazio(File arquivo) {
        DataReader leitor = new DataReader();
        try {
            leitor.ler(arquivo, Categoria.DANCA);
            assert false : "Arquivo vazio deveria lançar exceção";
        } catch (IOException | IllegalArgumentException e) {
            // esperado
        }
    }

    private static void testeCategoriaIncompativel(File arquivo) {
        DataReader leitor = new DataReader();
        try {
            leitor.ler(arquivo, Categoria.CORRIDA);
            assert false : "Categoria incompatível deveria lançar exceção";
        } catch (IOException | IllegalArgumentException e) {
            assert e.getMessage().contains("incompatível")
                    : "Mensagem de erro deveria mencionar incompatibilidade";
        }
    }

    private static void testeArquivoMalformado(File arquivo) {
        DataReader leitor = new DataReader();
        try {
            leitor.ler(arquivo, Categoria.DANCA);
            assert false : "Arquivo malformado deveria lançar exceção";
        } catch (IOException | IllegalArgumentException e) {
            // esperado
        }
    }

    private static void testeIdsDuplicados(File arquivo) {
        DataReader leitor = new DataReader();
        try {
            leitor.ler(arquivo, Categoria.DANCA);
            assert false : "IDs duplicados deveriam lançar exceção";
        } catch (IOException | IllegalArgumentException e) {
            assert e.getMessage().contains("duplicado")
                    : "Mensagem de erro deveria mencionar duplicidade";
        }
    }

    private static void testeCabecalhoIncompleto(File arquivo) {
        DataReader leitor = new DataReader();
        try {
            leitor.ler(arquivo, Categoria.DANCA);
            assert false : "Cabeçalho com campo ausente deve ser rejeitado";
        } catch (IOException | IllegalArgumentException e) {
            assert e.getMessage().contains("Cabeçalho inválido")
                    : "Erro deve identificar cabeçalho inválido";
        }
    }

    private static void testeAcentosUtf8(File arquivo) throws IOException {
        DataReader leitor = new DataReader();
        List<Jogador> jogadores = leitor.ler(arquivo, Categoria.DANCA);
        assert jogadores.get(0).getNome().equals("João Vitória")
                : "Nome com acentos deveria ser lido corretamente em UTF-8. "
                + "Encontrado: " + jogadores.get(0).getNome();
    }

    /** Cria um arquivo temporário escrito em UTF-8. */
    private static File criarArquivo(String prefixo, String conteudo)
            throws IOException {
        File arquivo = File.createTempFile(prefixo, ".txt");
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(arquivo), StandardCharsets.UTF_8)) {
            writer.write(conteudo);
        }
        return arquivo;
    }

    private static File criarArquivoValido() throws IOException {
        return criarArquivo("danca_valido",
                "CATEGORIA=DANCA\n"
                + "ID;NOME;PONTUACAO;PRECISAO;COMBO_MAXIMO\n"
                + "1;Ana Silva;95000;98,5;120\n"
                + "2;Bruno Costa;88000;95,2;98\n"
                + "3;Carla Souza;92000;97,1;110\n");
    }

    private static File criarArquivoVazio() throws IOException {
        File arquivo = File.createTempFile("vazio", ".txt");
        arquivo.createNewFile();
        return arquivo;
    }

    private static File criarArquivoIncompativel() throws IOException {
        return criarArquivo("incompativel",
                "CATEGORIA=DANCA\n"
                + "ID;NOME;PONTUACAO;PRECISAO;COMBO_MAXIMO\n"
                + "1;Ana;95000;98,5;120\n");
    }

    private static File criarArquivoMalformado() throws IOException {
        return criarArquivo("malformado",
                "CATEGORIA=DANCA\n"
                + "ID;NOME;PONTUACAO;PRECISAO;COMBO_MAXIMO\n"
                + "abc;Ana;95000;98,5;120\n");
    }

    private static File criarArquivoIdsDuplicados() throws IOException {
        return criarArquivo("ids_duplicados",
                "CATEGORIA=DANCA\n"
                + "ID;NOME;PONTUACAO;PRECISAO;COMBO_MAXIMO\n"
                + "1;Ana;95000;98,5;120\n"
                + "1;Bruno;88000;95,2;98\n");
    }

    private static File criarArquivoCabecalhoIncompleto() throws IOException {
        return criarArquivo("cabecalho_incompleto",
                "CATEGORIA=DANCA\n"
                + "ID;NOME;PONTUACAO;PRECISAO\n"
                + "1;Ana;95000;98,5\n");
    }

    private static File criarArquivoComAcentos() throws IOException {
        return criarArquivo("acentos",
                "\uFEFFCATEGORIA=DANCA\n"
                + "ID;NOME;PONTUACAO;PRECISAO;COMBO_MAXIMO\n"
                + "1;João Vitória;95000;98,5;120\n");
    }
}
