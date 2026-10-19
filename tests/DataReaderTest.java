import data.DataReader;
import model.Categoria;
import model.Jogador;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

        testeArquivoValido(arquivoValido);
        testeArquivoVazio(arquivoVazio);
        testeCategoriaIncompativel(arquivoIncompativel);
        testeArquivoMalformado(arquivoMalformado);

        arquivoValido.delete();
        arquivoVazio.delete();
        arquivoIncompativel.delete();
        arquivoMalformado.delete();

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

    private static File criarArquivoValido() throws IOException {
        File arquivo = File.createTempFile("danca_valido", ".txt");
        try (FileWriter writer = new FileWriter(arquivo)) {
            writer.write("CATEGORIA=DANCA\n");
            writer.write("ID;NOME;PONTUACAO;PRECISAO;COMBO_MAXIMO\n");
            writer.write("1;Ana Silva;95000;98,5;120\n");
            writer.write("2;Bruno Costa;88000;95,2;98\n");
            writer.write("3;Carla Souza;92000;97,1;110\n");
        }
        return arquivo;
    }

    private static File criarArquivoVazio() throws IOException {
        File arquivo = File.createTempFile("vazio", ".txt");
        arquivo.createNewFile();
        return arquivo;
    }

    private static File criarArquivoIncompativel() throws IOException {
        File arquivo = File.createTempFile("incompativel", ".txt");
        try (FileWriter writer = new FileWriter(arquivo)) {
            writer.write("CATEGORIA=DANCA\n");
            writer.write("ID;NOME;PONTUACAO;PRECISAO;COMBO_MAXIMO\n");
            writer.write("1;Ana;95000;98,5;120\n");
        }
        return arquivo;
    }

    private static File criarArquivoMalformado() throws IOException {
        File arquivo = File.createTempFile("malformado", ".txt");
        try (FileWriter writer = new FileWriter(arquivo)) {
            writer.write("CATEGORIA=DANCA\n");
            writer.write("ID;NOME;PONTUACAO;PRECISAO;COMBO_MAXIMO\n");
            writer.write("abc;Ana;95000;98,5;120\n");
        }
        return arquivo;
    }
}
