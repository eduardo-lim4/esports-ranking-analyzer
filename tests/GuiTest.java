import ui.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Teste automatizado da interface gráfica.
 *
 * Requer ambiente com suporte gráfico (não headless). O teste:
 *
 * 1. Abre a janela principal e captura uma imagem de evidência.
 * 2. Verifica que "Ver ranking" antes de uma análise exibe um aviso.
 * 3. Executa uma análise (Dança, dados gerados, 1.000 jogadores) clicando
 *    no botão real e verifica que a tabela é preenchida com tempos medidos,
 *    que todos os status são "Concluído" e que o vencedor corresponde ao
 *    menor tempo.
 * 4. Abre o ranking e captura a janela de evidência.
 * 5. Repete a análise com a categoria Luta para confirmar a troca de
 *    categoria e a atualização dos resultados.
 * 6. Importa o arquivo de exemplo de Corrida pelo seletor real e verifica que
 *    o ranking corresponde aos registros do arquivo.
 * 7. Cancela outra seleção de arquivo e verifica que nenhum benchmark inicia.
 *
 * As capturas de tela são salvas no diretório {@code results/}.
 */
public class GuiTest {

    private static Main janela;
    private static final String DIRETORIO = "results";

    public static void main(String[] args) throws Exception {
        System.out.println("Executando GuiTest...");

        new File(DIRETORIO).mkdirs();

        // 1. Janela inicial
        SwingUtilities.invokeAndWait(() -> {
            janela = new Main();
            janela.setVisible(true);
            janela.toFront();
        });
        aguardar(1500);
        capturarJanelaPrincipal("gui_1_janela_inicial");

        // 2. Ver ranking antes de qualquer análise (deve mostrar aviso)
        SwingUtilities.invokeLater(() -> clicarBotao("VER RANKING"));
        aguardar(1200);
        fecharDialogos();
        aguardar(300);

        // 3. Primeira análise: Dança, dados gerados, 1.000 jogadores
        SwingUtilities.invokeAndWait(() -> clicarBotao("EXECUTAR ANÁLISE"));
        aguardarConclusaoBenchmark(60);
        verificarResultados();
        verificarBotaoHabilitado();
        aguardar(600);
        capturarJanelaPrincipal("gui_2_resultados_danca");
        System.out.println("  Análise Dança: tabela atualizada com tempos reais.");

        // 4. Ranking
        SwingUtilities.invokeLater(() -> clicarBotao("VER RANKING"));
        aguardar(1500);
        capturarDialogo("gui_3_ranking_danca");
        fecharDialogos();
        aguardar(300);

        // 5. Segunda análise: categoria Luta
        SwingUtilities.invokeAndWait(() -> selecionarItemCombo("Luta"));
        SwingUtilities.invokeAndWait(() -> clicarBotao("EXECUTAR ANÁLISE"));
        aguardarConclusaoBenchmark(60);
        verificarResultados();
        verificarCategoriaExibida("Luta");
        aguardar(600);
        capturarJanelaPrincipal("gui_4_resultados_luta");
        System.out.println("  Análise Luta: tabela atualizada com tempos reais.");

        // 6. Vol. de 10k para comprovar que a EDT continua disponível durante
        // um benchmark demorado e que os controles ficam bloqueados.
        SwingUtilities.invokeAndWait(() -> {
            selecionarItemCombo("Dança");
            selecionarItemCombo("Dados gerados pelo sistema");
            selecionarItemCombo("10.000");
            clicarBotao("EXECUTAR ANÁLISE");
        });
        aguardar(150);
        verificarResponsividadeDuranteBenchmark();
        aguardarConclusaoBenchmark(60);
        verificarResultados();
        System.out.println("  10.000 jogadores: EDT responsiva e controles bloqueados durante a ordenação.");

        // 7. Importa arquivo selecionado através do JFileChooser Swing real.
        SwingUtilities.invokeAndWait(() -> {
            selecionarItemCombo("Corrida");
            selecionarItemCombo("Arquivo .txt");
        });
        File arquivoCorrida = new File("data", "corrida_exemplo.txt");
        SwingUtilities.invokeLater(() -> clicarBotao("EXECUTAR ANÁLISE"));
        aguardarDialogoChooser(10);
        selecionarArquivoNoChooser(arquivoCorrida);
        aguardarConclusaoBenchmark(60);
        verificarResultados();
        SwingUtilities.invokeLater(() -> clicarBotao("VER RANKING"));
        aguardarDialogoRanking(10);
        verificarLinhasRanking(10);
        capturarDialogo("gui_5_ranking_arquivo_corrida");
        fecharDialogos();
        System.out.println("  Importação pelo JFileChooser: ranking contém 10 jogadores do arquivo.");

        // 7. Cancela a próxima seleção e verifica que nenhum novo benchmark inicia.
        SwingUtilities.invokeLater(() -> clicarBotao("EXECUTAR ANÁLISE"));
        aguardarDialogoChooser(10);
        fecharDialogoChooserPorCancelamento();
        aguardarDialogoMensagem(10);
        fecharDialogos();
        verificarTabelaAguardando();
        System.out.println("  Cancelamento do seletor: análise não iniciada.");

        // 8. Arquivo de categoria incorreta deve exibir erro legível sem
        // encerrar a aplicação ou reutilizar resultados anteriores.
        SwingUtilities.invokeAndWait(() -> selecionarItemCombo("Dança"));
        SwingUtilities.invokeLater(() -> clicarBotao("EXECUTAR ANÁLISE"));
        aguardarDialogoChooser(10);
        selecionarArquivoNoChooser(arquivoCorrida);
        aguardarTituloDialogo("Erro", 10,
                "Erro de incompatibilidade não foi apresentado");
        verificarMensagemErro("incompatível");
        fecharDialogos();
        aguardar(300);
        verificarTabelaAguardando();
        verificarBotaoHabilitado();
        System.out.println("  Categoria incompatível: erro legível, aplicação continua ativa.");

        SwingUtilities.invokeAndWait(() -> janela.dispose());
        System.out.println("GuiTest: OK");
        System.exit(0);
    }

    // ==============================
    // VERIFICAÇÕES
    // ==============================

    private static void verificarResultados() throws Exception {
        double[] tempos = new double[3];
        int vencedores = 0;
        int indiceVencedor = -1;
        double menor = Double.MAX_VALUE;

        for (int i = 0; i < 3; i++) {
            Object tempoCell = valorTabela(i, 1);
            Object statusCell = valorTabela(i, 2);

            assert !"Aguardando".equals(statusCell)
                    : "Status ainda em Aguardando na linha " + i;
            assert statusCell.toString().contains("Concluído")
                    : "Status inesperado: " + statusCell;

            double tempo = Double.parseDouble(
                    tempoCell.toString().replace(',', '.'));
            assert tempo > 0 : "Tempo não positivo: " + tempo;
            tempos[i] = tempo;

            if (statusCell.toString().contains("Vencedor")) {
                vencedores++;
                indiceVencedor = i;
            }
            if (tempo < menor) {
                menor = tempo;
            }
        }

        assert vencedores == 1 : "Deveria haver exatamente 1 vencedor, haviam " + vencedores;
        assert tempos[indiceVencedor] == menor
                : "Vencedor exibido não corresponde ao menor tempo medido";

        System.out.printf("  Tempos medidos: Bubble=%.3f ms, Insertion=%.3f ms, Merge=%.3f ms%n",
                tempos[0], tempos[1], tempos[2]);
        System.out.println("  Vencedor correto (menor tempo): linha " + indiceVencedor);
    }

    private static void verificarResponsividadeDuranteBenchmark()
            throws Exception {
        final boolean[] responsivo = new boolean[1];
        SwingUtilities.invokeAndWait(() -> {
            JButton analisar = encontrarBotao("EXECUTAR ANÁLISE");
            List<JComboBox> combos = coletar(getContentPane(), JComboBox.class);
            responsivo[0] = analisar != null && !analisar.isEnabled()
                    && combos.size() == 3;
            for (JComboBox<?> combo : combos) {
                responsivo[0] &= !combo.isEnabled();
            }
        });
        assert responsivo[0]
                : "EDT deve responder e controles devem estar desabilitados durante o benchmark";
    }

    private static void verificarBotaoHabilitado() throws Exception {
        final boolean[] habilitado = new boolean[1];
        SwingUtilities.invokeAndWait(() -> {
            JButton botao = encontrarBotao("EXECUTAR ANÁLISE");
            habilitado[0] = botao != null && botao.isEnabled();
        });
        assert habilitado[0] : "Botão de análise deveria ser reativado após o benchmark";
    }

    private static void verificarCategoriaExibida(String esperado) throws Exception {
        final String[] texto = new String[1];
        SwingUtilities.invokeAndWait(() -> {
            for (JLabel label : encontrarLabels()) {
                if (label.getText().equals(esperado)) {
                    texto[0] = label.getText();
                    return;
                }
            }
        });
        assert esperado.equals(texto[0])
                : "Categoria exibida deveria ser " + esperado;
    }

    // ==============================
    // ACESSO AOS COMPONENTES
    // ==============================

    private static void clicarBotao(String texto) {
        JButton botao = encontrarBotao(texto);
        assert botao != null : "Botão não encontrado: " + texto;
        botao.doClick();
    }

    private static JButton encontrarBotao(String texto) {
        for (JButton botao : coletar(getContentPane(), JButton.class)) {
            if (botao.getText().trim().contains(texto)) {
                return botao;
            }
        }
        return null;
    }

    private static List<JLabel> encontrarLabels() {
        return coletar(getContentPane(), JLabel.class);
    }

    private static void selecionarItemCombo(String item) {
        for (JComboBox<?> combo : coletar(getContentPane(), JComboBox.class)) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                if (item.equals(combo.getItemAt(i))) {
                    combo.setSelectedIndex(i);
                    return;
                }
            }
        }
        throw new AssertionError("Item não encontrado em nenhum combo: " + item);
    }

    private static Object valorTabela(final int linha, final int coluna)
            throws Exception {
        final Object[] valor = new Object[1];
        SwingUtilities.invokeAndWait(() -> {
            List<JTable> tabelas = coletar(getContentPane(), JTable.class);
            assert !tabelas.isEmpty() : "Tabela de resultados não encontrada";
            valor[0] = tabelas.get(0).getModel().getValueAt(linha, coluna);
        });
        return valor[0];
    }

    private static Container getContentPane() {
        return janela.getContentPane();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Component> List<T> coletar(
            Container raiz, Class<T> tipo) {
        List<T> encontrados = new ArrayList<>();
        coletarRecursivo(raiz, tipo, encontrados);
        return encontrados;
    }

    private static <T extends Component> void coletarRecursivo(
            Container container, Class<T> tipo, List<T> encontrados) {
        for (Component filho : container.getComponents()) {
            if (tipo.isInstance(filho)) {
                encontrados.add(tipo.cast(filho));
            }
            if (filho instanceof Container) {
                coletarRecursivo((Container) filho, tipo, encontrados);
            }
        }
    }

    private static void verificarLinhasRanking(int linhas) throws Exception {
        final int[] quantidade = new int[1];
        SwingUtilities.invokeAndWait(() -> {
            for (Window window : Window.getWindows()) {
                if (window instanceof JDialog && window.isVisible()) {
                    List<JTable> tabelas = coletar((Container) window, JTable.class);
                    if (!tabelas.isEmpty()) {
                        JTable tabela = tabelas.get(0);
                        quantidade[0] = tabela.getRowCount();
                        assert tabela.getColumnModel().getColumn(3)
                                        .getPreferredWidth() >= 500
                                : "Atributos do jogador devem caber na coluna de métrica";
                        assert tabela.getValueAt(0, 3).toString().contains("Voltas:")
                                : "Ranking de Corrida deve mostrar atributos específicos";
                        return;
                    }
                }
            }
        });
        assert quantidade[0] == linhas
                : "Esperava " + linhas + " linhas no ranking, encontrado " + quantidade[0];
    }

    private static void verificarTabelaAguardando() throws Exception {
        for (int i = 0; i < 3; i++) {
            assert "Aguardando".equals(valorTabela(i, 2))
                    : "Cancelamento não deveria iniciar benchmark; status "
                    + "inesperado na linha " + i + ": " + valorTabela(i, 2);
        }
    }

    private static void selecionarArquivoNoChooser(File arquivo) throws Exception {
        final boolean[] selecionado = new boolean[1];
        long limite = System.currentTimeMillis() + 10_000;
        while (System.currentTimeMillis() < limite && !selecionado[0]) {
            SwingUtilities.invokeAndWait(() -> {
                for (Window window : Window.getWindows()) {
                    if (window instanceof JDialog && window.isVisible()) {
                        List<JFileChooser> choosers = coletar(
                                (Container) window, JFileChooser.class);
                        if (!choosers.isEmpty()) {
                            JFileChooser chooser = choosers.get(0);
                            chooser.setSelectedFile(arquivo.getAbsoluteFile());
                            chooser.approveSelection();
                            selecionado[0] = true;
                            return;
                        }
                    }
                }
            });
            if (!selecionado[0]) {
                aguardar(100);
            }
        }
        assert selecionado[0] : "JFileChooser não foi aberto para seleção do arquivo";
    }

    private static void aguardarDialogoChooser(long timeoutSegundos)
            throws Exception {
        aguardarTipoDialogo(JFileChooser.class, timeoutSegundos,
                "JFileChooser não abriu");
    }

    private static void fecharDialogoChooserPorCancelamento() throws Exception {
        final boolean[] cancelado = new boolean[1];
        SwingUtilities.invokeAndWait(() -> {
            for (Window window : Window.getWindows()) {
                if (window instanceof JDialog && window.isVisible()) {
                    List<JFileChooser> choosers = coletar(
                            (Container) window, JFileChooser.class);
                    if (!choosers.isEmpty()) {
                        choosers.get(0).cancelSelection();
                        cancelado[0] = true;
                        return;
                    }
                }
            }
        });
        assert cancelado[0] : "JFileChooser não estava aberto para cancelar";
    }

    private static void verificarMensagemErro(String trechoEsperado)
            throws Exception {
        final boolean[] encontrado = new boolean[1];
        SwingUtilities.invokeAndWait(() -> {
            for (Window window : Window.getWindows()) {
                if (window instanceof JDialog && window.isVisible()) {
                    for (JLabel label : coletar((Container) window, JLabel.class)) {
                        if (label.getText() != null
                                && label.getText().contains(trechoEsperado)) {
                            encontrado[0] = true;
                            return;
                        }
                    }
                }
            }
        });
        assert encontrado[0]
                : "Diálogo de erro deveria explicar o problema: " + trechoEsperado;
    }

    private static void aguardarDialogoMensagem(long timeoutSegundos)
            throws Exception {
        aguardarTituloDialogo("Aviso", timeoutSegundos,
                "Mensagem de cancelamento não foi apresentada");
    }

    private static void aguardarDialogoRanking(long timeoutSegundos)
            throws Exception {
        long limite = System.currentTimeMillis() + timeoutSegundos * 1000L;
        while (System.currentTimeMillis() < limite) {
            final boolean[] aberto = new boolean[1];
            SwingUtilities.invokeAndWait(() -> {
                for (Window window : Window.getWindows()) {
                    if (window instanceof JDialog && window.isVisible()
                            && !coletar((Container) window, JTable.class).isEmpty()) {
                        aberto[0] = true;
                        return;
                    }
                }
            });
            if (aberto[0]) return;
            aguardar(100);
        }
        throw new AssertionError("Diálogo do ranking não abriu");
    }

    private static void aguardarDialogoChooserOuMensagem(long timeoutSegundos)
            throws Exception {
        aguardarTipoDialogo(JFileChooser.class, timeoutSegundos,
                "Diálogo do seletor não abriu");
    }

    private static void aguardarTituloDialogo(String titulo, long timeoutSegundos,
                                               String erro) throws Exception {
        long limite = System.currentTimeMillis() + timeoutSegundos * 1000L;
        while (System.currentTimeMillis() < limite) {
            final boolean[] aberto = new boolean[1];
            SwingUtilities.invokeAndWait(() -> {
                for (Window window : Window.getWindows()) {
                    if (window instanceof JDialog && window.isVisible()
                            && titulo.equals(((JDialog) window).getTitle())) {
                        aberto[0] = true;
                        return;
                    }
                }
            });
            if (aberto[0]) return;
            aguardar(100);
        }
        throw new AssertionError(erro);
    }

    private static void aguardarTipoDialogo(
            Class<? extends Component> tipo, long timeoutSegundos, String erro)
            throws Exception {
        long limite = System.currentTimeMillis() + timeoutSegundos * 1000L;
        while (System.currentTimeMillis() < limite) {
            final boolean[] encontrado = new boolean[1];
            SwingUtilities.invokeAndWait(() -> {
                for (Window window : Window.getWindows()) {
                    if (window instanceof JDialog && window.isVisible()
                            && !coletar((Container) window, tipo).isEmpty()) {
                        encontrado[0] = true;
                        return;
                    }
                }
            });
            if (encontrado[0]) return;
            aguardar(100);
        }
        throw new AssertionError(erro);
    }

    // ==============================
    // DIÁLOGOS E ESPERA
    // ==============================

    private static void fecharDialogos() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            for (Window janelaAtual : Window.getWindows()) {
                if (janelaAtual instanceof JDialog
                        && janelaAtual.isVisible()) {
                    janelaAtual.dispose();
                }
            }
        });
    }

    private static void aguardar(long ms) throws InterruptedException {
        Thread.sleep(ms);
    }

    private static void aguardarConclusaoBenchmark(long timeoutSegundos)
            throws Exception {
        long limite = System.currentTimeMillis() + timeoutSegundos * 1000L;
        while (System.currentTimeMillis() < limite) {
            aguardar(200);
            final boolean concluido[] = new boolean[1];
            SwingUtilities.invokeAndWait(() -> {
                List<JTable> tabelas = coletar(getContentPane(), JTable.class);
                if (tabelas.isEmpty()) {
                    concluido[0] = false;
                    return;
                }
                boolean pronto = true;
                for (int i = 0; i < 3; i++) {
                    Object status = tabelas.get(0).getModel().getValueAt(i, 2);
                    if ("Aguardando".equals(status)) {
                        pronto = false;
                    }
                }
                concluido[0] = pronto;
            });
            if (concluido[0]) {
                return;
            }
        }
        throw new AssertionError(
                "Benchmark não concluiu em " + timeoutSegundos + " segundos.");
    }

    // ==============================
    // CAPTURA DE TELA
    // ==============================

    private static void capturarJanelaPrincipal(String nome) throws Exception {
        capturar(janela.getBounds(), nome);
    }

    private static void capturarDialogo(String nome) throws Exception {
        Rectangle limites = null;
        for (Window w : Window.getWindows()) {
            if (w instanceof JDialog && w.isVisible()) {
                limites = w.getBounds();
            }
        }
        assert limites != null : "Nenhum diálogo visível para capturar";
        capturar(limites, nome);
    }

    private static void capturar(Rectangle area, String nome) throws Exception {
        try {
            BufferedImage imagem = new Robot().createScreenCapture(area);
            File arquivo = new File(DIRETORIO, nome + ".png");
            ImageIO.write(imagem, "png", arquivo);
            System.out.println("  Captura salva: " + arquivo.getPath());
        } catch (HeadlessException e) {
            System.out.println("  AVISO: ambiente sem suporte gráfico, "
                    + "captura " + nome + " não gerada.");
        }
    }
}
