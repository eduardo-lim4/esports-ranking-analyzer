package ui;

import benchmark.Benchmark;
import benchmark.BenchmarkResult;
import data.DataGenerator;
import data.DataReader;
import model.Categoria;
import model.Jogador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main extends JFrame {

    // ==============================
    // CORES DA INTERFACE
    // ==============================

    private static final Color BACKGROUND = new Color(18, 20, 26);
    private static final Color PANEL = new Color(27, 30, 38);
    private static final Color PANEL_LIGHT = new Color(36, 40, 50);

    private static final Color ACCENT = new Color(75, 130, 255);

    private static final Color TEXT = new Color(235, 238, 245);
    private static final Color MUTED = new Color(155, 162, 176);

    // ==============================
    // COMPONENTES
    // ==============================

    private JComboBox<String> categoriaCombo;
    private JComboBox<String> fonteCombo;
    private JComboBox<String> tamanhoCombo;

    private JLabel categoriaSelecionada;

    private DefaultTableModel tabelaModel;

    // ==============================
    // LÓGICA
    // ==============================

    private final DataGenerator gerador = new DataGenerator();
    private final DataReader leitor = new DataReader();
    private final Benchmark benchmark = new Benchmark();

    private File arquivoSelecionado;
    private List<BenchmarkResult> ultimosResultados = new ArrayList<>();

    public Main() {

        setTitle("Esports Ranking Analyzer");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1200, 750);

        setLocationRelativeTo(null);

        criarInterface();
    }

    // ==============================
    // INTERFACE PRINCIPAL
    // ==============================

    private void criarInterface() {

        JPanel principal = new JPanel(new BorderLayout());

        principal.setBackground(BACKGROUND);

        principal.setBorder(
                new EmptyBorder(20, 20, 20, 20)
        );

        setContentPane(principal);

        // HEADER

        principal.add(criarHeader(), BorderLayout.NORTH);

        // CONTEÚDO

        JPanel conteudo = new JPanel(new BorderLayout(20, 20));

        conteudo.setOpaque(false);

        conteudo.setBorder(
                new EmptyBorder(20, 0, 0, 0)
        );

        conteudo.add(
                criarPainelConfiguracao(),
                BorderLayout.WEST
        );

        conteudo.add(
                criarPainelResultados(),
                BorderLayout.CENTER
        );

        principal.add(
                conteudo,
                BorderLayout.CENTER
        );
    }

    // ==============================
    // HEADER
    // ==============================

    private JPanel criarHeader() {

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(PANEL);

        header.setBorder(
                new EmptyBorder(20, 25, 20, 25)
        );

        JPanel textos = new JPanel();

        textos.setOpaque(false);

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titulo = new JLabel(
                "ESPORTS RANKING ANALYZER"
        );

        titulo.setForeground(TEXT);

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        25
                )
        );

        JLabel subtitulo = new JLabel(
                "Análise de desempenho de algoritmos de ordenação"
        );

        subtitulo.setForeground(MUTED);

        subtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        textos.add(titulo);

        textos.add(
                Box.createVerticalStrut(5)
        );

        textos.add(subtitulo);

        header.add(
                textos,
                BorderLayout.WEST
        );

        JLabel java = new JLabel("JAVA • BENCHMARK");

        java.setForeground(ACCENT);

        java.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        header.add(
                java,
                BorderLayout.EAST
        );

        return header;
    }

    // ==============================
    // CONFIGURAÇÃO
    // ==============================

    private JPanel criarPainelConfiguracao() {

        JPanel painel = criarCard();

        painel.setPreferredSize(
                new Dimension(300, 0)
        );

        JPanel conteudo = new JPanel();

        conteudo.setOpaque(false);

        conteudo.setLayout(
                new BoxLayout(
                        conteudo,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titulo = tituloSecao(
                "CONFIGURAÇÃO"
        );

        conteudo.add(titulo);

        conteudo.add(
                Box.createVerticalStrut(20)
        );

        // CATEGORIA

        conteudo.add(
                label("Categoria do jogo")
        );

        categoriaCombo = new JComboBox<>(
                new String[]{
                        "Dança",
                        "Corrida",
                        "Luta"
                }
        );

        estilizarCombo(categoriaCombo);

        conteudo.add(categoriaCombo);

        conteudo.add(
                Box.createVerticalStrut(15)
        );

        // FONTE

        conteudo.add(
                label("Fonte dos dados")
        );

        fonteCombo = new JComboBox<>(
                new String[]{
                        "Dados gerados pelo sistema",
                        "Arquivo .txt"
                }
        );

        estilizarCombo(fonteCombo);

        conteudo.add(fonteCombo);

        conteudo.add(
                Box.createVerticalStrut(15)
        );

        // QUANTIDADE

        conteudo.add(
                label("Quantidade de jogadores")
        );

        tamanhoCombo = new JComboBox<>(
                new String[]{
                        "1.000",
                        "10.000",
                        "50.000",
                        "100.000",
                        "500.000"
                }
        );

        estilizarCombo(tamanhoCombo);

        conteudo.add(tamanhoCombo);

        conteudo.add(
                Box.createVerticalStrut(25)
        );

        // BOTÃO

        JButton analisar = new JButton(
                "  EXECUTAR ANÁLISE"
        );

        estilizarBotaoPrincipal(analisar);

        analisar.addActionListener(
                e -> executarAnalise()
        );

        conteudo.add(analisar);

        conteudo.add(
                Box.createVerticalStrut(10)
        );

        JButton ranking = new JButton(
                "VER RANKING"
        );

        estilizarBotaoSecundario(ranking);

        ranking.addActionListener(
                e -> abrirRanking()
        );

        conteudo.add(ranking);

        conteudo.add(
                Box.createVerticalStrut(30)
        );

        conteudo.add(
                tituloSecao("ALGORITMOS")
        );

        conteudo.add(
                Box.createVerticalStrut(12)
        );

        conteudo.add(
                algoritmo("Bubble Sort", "O(n²)")
        );

        conteudo.add(
                Box.createVerticalStrut(8)
        );

        conteudo.add(
                algoritmo("Insertion Sort", "O(n²)")
        );

        conteudo.add(
                Box.createVerticalStrut(8)
        );

        conteudo.add(
                algoritmo("Merge Sort", "O(n log n)")
        );

        painel.add(
                conteudo,
                BorderLayout.NORTH
        );

        return painel;
    }

    // ==============================
    // RESULTADOS
    // ==============================

    private JPanel criarPainelResultados() {

        JPanel painel = criarCard();

        JPanel topo = new JPanel(
                new BorderLayout()
        );

        topo.setOpaque(false);

        JPanel textos = new JPanel();

        textos.setOpaque(false);

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        textos.add(
                tituloSecao(
                        "RESULTADOS DO BENCHMARK"
                )
        );

        JLabel descricao = new JLabel(
                "Comparação do tempo de execução dos algoritmos"
        );

        descricao.setForeground(MUTED);

        descricao.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        textos.add(
                Box.createVerticalStrut(4)
        );

        textos.add(descricao);

        topo.add(
                textos,
                BorderLayout.WEST
        );

        categoriaSelecionada = new JLabel(
                "Dança"
        );

        categoriaSelecionada.setForeground(
                ACCENT
        );

        categoriaSelecionada.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        topo.add(
                categoriaSelecionada,
                BorderLayout.EAST
        );

        painel.add(
                topo,
                BorderLayout.NORTH
        );

        painel.add(
                criarTabela(),
                BorderLayout.CENTER
        );

        return painel;
    }

    // ==============================
    // TABELA
    // ==============================

    private JScrollPane criarTabela() {

        tabelaModel = new DefaultTableModel(

                new Object[][]{

                        {
                                "Bubble Sort",
                                "—",
                                "Aguardando"
                        },

                        {
                                "Insertion Sort",
                                "—",
                                "Aguardando"
                        },

                        {
                                "Merge Sort",
                                "—",
                                "Aguardando"
                        }

                },

                new String[]{
                        "Algoritmo",
                        "Tempo (ms)",
                        "Status"
                }

        ) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {

                return false;
            }
        };

        JTable tabela = new JTable(
                tabelaModel
        );

        tabela.setBackground(PANEL);

        tabela.setForeground(TEXT);

        tabela.setGridColor(
                new Color(50, 54, 65)
        );

        tabela.setRowHeight(45);

        tabela.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        tabela.getTableHeader()
                .setBackground(PANEL_LIGHT);

        tabela.getTableHeader()
                .setForeground(MUTED);

        tabela.getTableHeader()
                .setFont(
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                12
                        )
                );

        tabela.getTableHeader()
                .setReorderingAllowed(false);

        // O Windows LAF ignora as cores do cabeçalho; um renderer
        // opaco garante o fundo escuro e o texto legível.
        DefaultTableCellRenderer cabecalhoRenderer =
                new DefaultTableCellRenderer();

        cabecalhoRenderer.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        cabecalhoRenderer.setBackground(PANEL_LIGHT);

        cabecalhoRenderer.setForeground(TEXT);

        cabecalhoRenderer.setOpaque(true);

        cabecalhoRenderer.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        cabecalhoRenderer.setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 1,
                        new Color(50, 54, 65)
                )
        );

        tabela.getTableHeader()
                .setDefaultRenderer(cabecalhoRenderer);

        DefaultTableCellRenderer centralizar =
                new DefaultTableCellRenderer();

        centralizar.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        tabela.getColumnModel()
                .getColumn(1)
                .setCellRenderer(centralizar);

        tabela.getColumnModel()
                .getColumn(2)
                .setCellRenderer(centralizar);

        JScrollPane scroll =
                new JScrollPane(tabela);

        scroll.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scroll.getViewport()
                .setBackground(PANEL);

        return scroll;
    }

    // ==============================
    // ELEMENTOS VISUAIS
    // ==============================

    private JPanel criarCard() {

        JPanel painel =
                new JPanel(new BorderLayout());

        painel.setBackground(PANEL);

        painel.setBorder(
                new EmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        return painel;
    }

    private JLabel tituloSecao(
            String texto
    ) {

        JLabel label =
                new JLabel(texto);

        label.setForeground(TEXT);

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        return label;
    }

    private JLabel label(
            String texto
    ) {

        JLabel label =
                new JLabel(texto);

        label.setForeground(MUTED);

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        label.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        return label;
    }

    private JPanel algoritmo(
            String nome,
            String complexidade
    ) {

        JPanel painel =
                new JPanel(
                        new BorderLayout()
                );

        painel.setBackground(
                PANEL_LIGHT
        );

        painel.setBorder(
                new EmptyBorder(
                        10,
                        12,
                        10,
                        12
                )
        );

        JLabel nomeLabel =
                new JLabel(nome);

        nomeLabel.setForeground(TEXT);

        nomeLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        JLabel complexidadeLabel =
                new JLabel(complexidade);

        complexidadeLabel.setForeground(MUTED);

        complexidadeLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        painel.add(
                nomeLabel,
                BorderLayout.WEST
        );

        painel.add(
                complexidadeLabel,
                BorderLayout.EAST
        );

        return painel;
    }

    private void estilizarCombo(
            JComboBox<String> combo
    ) {

        combo.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        38
                )
        );

        combo.setBackground(
                Color.WHITE
        );

        combo.setForeground(Color.BLACK);

        combo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        combo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        // O Windows LAF ignora as cores do combo; o renderer
        // garante texto preto sobre fundo branco e destaque
        // azul para o item selecionado na lista suspensa.
        combo.setRenderer(
                new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus
                    ) {

                        JLabel item =
                                (JLabel) super
                                        .getListCellRendererComponent(
                                                list,
                                                value,
                                                index,
                                                isSelected,
                                                cellHasFocus
                                        );

                        item.setOpaque(true);

                        if (isSelected) {
                            item.setBackground(ACCENT);
                            item.setForeground(Color.WHITE);
                        } else {
                            item.setBackground(Color.WHITE);
                            item.setForeground(Color.BLACK);
                        }

                        item.setBorder(
                                new EmptyBorder(4, 8, 4, 8)
                        );

                        return item;
                    }
                }
        );
    }

    private void estilizarBotaoPrincipal(
            JButton botao
    ) {

        botao.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42
                )
        );

        botao.setBackground(ACCENT);

        botao.setForeground(Color.WHITE);

        botao.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        botao.setFocusPainted(false);

        botao.setBorderPainted(false);

        botao.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );
    }

    private void estilizarBotaoSecundario(
            JButton botao
    ) {

        botao.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        42
                )
        );

        botao.setBackground(
                PANEL_LIGHT
        );

        botao.setForeground(TEXT);

        botao.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        botao.setFocusPainted(false);

        botao.setBorderPainted(false);

        botao.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );
    }

    // ==============================
    // LÓGICA DA APLICAÇÃO
    // ==============================

    private void executarAnalise() {
        String categoriaTexto = (String) categoriaCombo.getSelectedItem();
        String fonte = (String) fonteCombo.getSelectedItem();
        String quantidadeTexto = (String) tamanhoCombo.getSelectedItem();

        Categoria categoria;
        try {
            categoria = Categoria.fromString(categoriaTexto);
        } catch (IllegalArgumentException e) {
            mostrarErro("Categoria inválida: " + e.getMessage());
            return;
        }

        // Nova análise iniciada: descarta resultados antigos para não exibir
        // valores de uma execução anterior como se fossem atuais.
        ultimosResultados = new ArrayList<>();
        limparTabela();
        categoriaSelecionada.setText(categoriaTexto);

        if ("Arquivo .txt".equals(fonte)) {
            arquivoSelecionado = leitor.selecionarArquivo();
            if (arquivoSelecionado == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Seleção de arquivo cancelada. A análise não foi iniciada.",
                        "Aviso",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
        }

        Comparator<Jogador> comparador = Jogador.comparadorPorCategoria(categoria);
        File arquivo = arquivoSelecionado;
        setBotoesHabilitados(false);

        new SwingWorker<List<BenchmarkResult>, Void>() {
            @Override
            protected List<BenchmarkResult> doInBackground() {
                List<Jogador> jogadores = obterDados(
                        fonte, categoria, quantidadeTexto, arquivo
                );
                if (jogadores == null || jogadores.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Nenhum dado disponível para análise."
                    );
                }
                return benchmark.executar(jogadores, comparador);
            }

            @Override
            protected void done() {
                try {
                    ultimosResultados = get();
                    atualizarTabela(ultimosResultados);
                } catch (Exception e) {
                    Throwable causa = e.getCause() != null ? e.getCause() : e;
                    String mensagem = causa.getMessage();
                    mostrarErro("Erro ao carregar dados ou executar o benchmark: "
                            + (mensagem == null ? causa.getClass().getSimpleName() : mensagem));
                    limparTabela();
                } finally {
                    setBotoesHabilitados(true);
                }
            }
        }.execute();
    }

    private List<Jogador> obterDados(String fonte,
                                     Categoria categoria,
                                     String quantidadeTexto,
                                     File arquivo) {
        if ("Arquivo .txt".equals(fonte)) {
            try {
                return leitor.ler(arquivo, categoria);
            } catch (IOException e) {
                throw new IllegalArgumentException(
                        "Falha ao ler o arquivo: " + e.getMessage(), e
                );
            }
        }

        int quantidade = parseQuantidade(quantidadeTexto);
        return gerador.gerar(categoria, quantidade);
    }

    private int parseQuantidade(String texto) {
        try {
            return Integer.parseInt(texto.replace(".", "").replace(",", ""));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Quantidade de jogadores inválida: " + texto
            );
        }
    }

    private void limparTabela() {
        for (int i = 0; i < tabelaModel.getRowCount(); i++) {
            tabelaModel.setValueAt("—", i, 1);
            tabelaModel.setValueAt("Aguardando", i, 2);
        }
    }

    private void atualizarTabela(List<BenchmarkResult> resultados) {
        BenchmarkResult vencedor = null;
        double menorTempo = Double.MAX_VALUE;

        for (BenchmarkResult resultado : resultados) {
            if (resultado.isValido() && resultado.getTempoMs() < menorTempo) {
                menorTempo = resultado.getTempoMs();
                vencedor = resultado;
            }
        }

        for (int i = 0; i < resultados.size(); i++) {
            BenchmarkResult resultado = resultados.get(i);
            String tempo = String.format("%.6f", resultado.getTempoMs());
            String status = resultado.getStatus();
            if (resultado == vencedor) {
                status = "Vencedor • " + status;
            }
            tabelaModel.setValueAt(tempo, i, 1);
            tabelaModel.setValueAt(status, i, 2);
        }
    }

    private void abrirRanking() {
        if (ultimosResultados == null || ultimosResultados.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Execute uma análise antes de visualizar o ranking.",
                    "Ranking indisponível",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        BenchmarkResult resultadoValido = null;
        for (BenchmarkResult r : ultimosResultados) {
            if (r.isValido()) {
                resultadoValido = r;
                break;
            }
        }

        if (resultadoValido == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Nenhum algoritmo produziu um resultado válido.",
                    "Ranking indisponível",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        List<Jogador> ranking = resultadoValido.getRanking();
        String[] colunas = {"Posição", "ID", "Nome", "Métrica principal"};
        Object[][] dados = new Object[Math.min(ranking.size(), 1000)][colunas.length];

        Categoria categoria = ranking.get(0).getCategoria();
        for (int i = 0; i < dados.length; i++) {
            Jogador j = ranking.get(i);
            dados[i][0] = i + 1;
            dados[i][1] = j.getId();
            dados[i][2] = j.getNome();
            dados[i][3] = formatarMetricaPrincipal(j, categoria);
        }

        DefaultTableModel model = new DefaultTableModel(dados, colunas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabela = new JTable(model);
        tabela.setRowHeight(30);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.setBackground(PANEL);
        tabela.setForeground(TEXT);
        tabela.setGridColor(new Color(50, 54, 65));
        tabela.setSelectionBackground(ACCENT);
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(70);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(70);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(170);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(530);

        DefaultTableCellRenderer cabecalhoRanking =
                new DefaultTableCellRenderer();
        cabecalhoRanking.setBackground(PANEL_LIGHT);
        cabecalhoRanking.setForeground(TEXT);
        cabecalhoRanking.setOpaque(true);
        cabecalhoRanking.setFont(
                new Font("Segoe UI", Font.BOLD, 12)
        );
        cabecalhoRanking.setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 1,
                        new Color(50, 54, 65)
                )
        );
        tabela.getTableHeader()
                .setDefaultRenderer(cabecalhoRanking);
        tabela.getTableHeader()
                .setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new Dimension(860, 400));
        scroll.getViewport().setBackground(PANEL);

        JOptionPane.showMessageDialog(
                this,
                scroll,
                "Ranking - " + categoria.getNomeExibicao()
                        + " (top " + dados.length + ")",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private String formatarMetricaPrincipal(Jogador j, Categoria categoria) {
        return switch (categoria) {
            case DANCA -> String.format(
                    "Pts: %,d | Prec: %.1f%% | Combo: %d",
                    j.getPontuacao(), j.getPrecisao(), j.getComboMaximo()
            );
            case CORRIDA -> String.format(
                    "Tempo: %,d ms | Pos: %d | Voltas: %d",
                    j.getTempoProvaMs(), j.getPosicaoChegada(), j.getVoltas()
            );
            case LUTA -> String.format(
                    "Aprov: %.1f%% | V: %d | D: %d",
                    j.getAproveitamento(), j.getVitorias(), j.getDerrotas()
            );
        };
    }

    private void setBotoesHabilitados(boolean habilitado) {
        for (Component c : getContentPane().getComponents()) {
            habilitarRecursivo(c, habilitado);
        }
    }

    private void habilitarRecursivo(Component componente, boolean habilitado) {
        if (componente instanceof JButton
                || componente instanceof JComboBox<?>) {
            componente.setEnabled(habilitado);
        }
        if (componente instanceof Container) {
            for (Component filho : ((Container) componente).getComponents()) {
                habilitarRecursivo(filho, habilitado);
            }
        }
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(
                this,
                mensagem,
                "Erro",
                JOptionPane.ERROR_MESSAGE
        );
    }

    // ==============================
    // MAIN
    // ==============================

    public static void main(
            String[] args
    ) {

        SwingUtilities.invokeLater(() -> {

            try {

                UIManager.setLookAndFeel(
                        UIManager
                                .getSystemLookAndFeelClassName()
                );

            } catch (Exception ignored) {
            }

            Main janela =
                    new Main();

            janela.setVisible(true);
        });
    }
}
