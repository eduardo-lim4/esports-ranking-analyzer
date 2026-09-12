import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DuplaBubbleSort extends JFrame {

    static class Jogador {
        String nome;
        int pontuacao;

        Jogador(String nome, int pontuacao) {
            this.nome = nome;
            this.pontuacao = pontuacao;
        }
    }

    private final List<Jogador> jogadores = new ArrayList<>();

    private final DefaultTableModel modeloTabela;
    private final JTable tabela;

    private final JTextField campoNome;
    private final JTextField campoPontuacao;

    public DuplaBubbleSort() {

        setTitle("Dupla Bubble Sort - Ranking de Jogadores");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        jogadores.add(new Jogador("Carlos", 850));
        jogadores.add(new Jogador("Julio", 1200));
        jogadores.add(new Jogador("Ana", 950));
        jogadores.add(new Jogador("Pedro", 1500));
        jogadores.add(new Jogador("Mariana", 1100));
        jogadores.add(new Jogador("Lucas", 700));

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel(
                "RANKING DE JOGADORES - DUPLA BUBBLE SORT",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        painelPrincipal.add(titulo, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel(
                new Object[]{"Posição", "Jogador", "Pontuação"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(modeloTabela);

        tabela.setRowHeight(28);
        tabela.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 14)
        );

        atualizarTabela();

        painelPrincipal.add(
                new JScrollPane(tabela),
                BorderLayout.CENTER
        );

        JPanel painelCadastro = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 5)
        );

        campoNome = new JTextField(10);
        campoPontuacao = new JTextField(6);

        JButton botaoAdicionar = new JButton("Adicionar Jogador");

        painelCadastro.add(new JLabel("Nome:"));
        painelCadastro.add(campoNome);

        painelCadastro.add(new JLabel("Pontuação:"));
        painelCadastro.add(campoPontuacao);

        painelCadastro.add(botaoAdicionar);

        JPanel painelBotoes = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 5)
        );

        JButton botaoOrdenar = new JButton(
                "Ordenar com Dupla Bubble Sort"
        );

        JButton botaoTeste = new JButton(
                "Testar Ordenação"
        );

        JButton botaoLimpar = new JButton(
                "Limpar Jogadores"
        );

        painelBotoes.add(botaoOrdenar);
        painelBotoes.add(botaoTeste);
        painelBotoes.add(botaoLimpar);

        JPanel painelInferior = new JPanel(
                new BorderLayout()
        );

        painelInferior.add(
                painelCadastro,
                BorderLayout.NORTH
        );

        painelInferior.add(
                painelBotoes,
                BorderLayout.SOUTH
        );

        painelPrincipal.add(
                painelInferior,
                BorderLayout.SOUTH
        );

        add(painelPrincipal);

        botaoAdicionar.addActionListener(e -> adicionarJogador());

        botaoOrdenar.addActionListener(e -> {

            if (jogadores.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Não existem jogadores para ordenar."
                );
                return;
            }

            duplaBubbleSort();

            atualizarTabela();

            JOptionPane.showMessageDialog(
                    this,
                    "Jogadores ordenados com sucesso!"
            );
        });

        botaoTeste.addActionListener(e -> testarOrdenacao());

        botaoLimpar.addActionListener(e -> {

            jogadores.clear();
            atualizarTabela();

            JOptionPane.showMessageDialog(
                    this,
                    "Lista de jogadores limpa."
            );
        });
    }

    private void adicionarJogador() {

        String nome = campoNome.getText().trim();
        String textoPontuacao = campoPontuacao.getText().trim();

        if (nome.isEmpty() || textoPontuacao.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Preencha o nome e a pontuação."
            );

            return;
        }

        try {

            int pontuacao = Integer.parseInt(textoPontuacao);

            if (pontuacao < 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "A pontuação não pode ser negativa."
                );

                return;
            }

            jogadores.add(
                    new Jogador(nome, pontuacao)
            );

            atualizarTabela();

            campoNome.setText("");
            campoPontuacao.setText("");

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Digite uma pontuação válida."
            );
        }
    }

    private void duplaBubbleSort() {

        if (jogadores.size() < 2) {
            return;
        }

        boolean trocou = true;
        int inicio = 0;
        int fim = jogadores.size() - 1;

        while (trocou && inicio < fim) {

            trocou = false;

            for (int i = inicio; i < fim; i++) {

                if (jogadores.get(i).pontuacao < jogadores.get(i + 1).pontuacao) {

                    Jogador temp = jogadores.get(i);
                    jogadores.set(i, jogadores.get(i + 1));
                    jogadores.set(i + 1, temp);

                    trocou = true;
                }
            }

            fim--;

            if (!trocou) {
                break;
            }

            for (int i = fim; i > inicio; i--) {

                if (jogadores.get(i).pontuacao > jogadores.get(i - 1).pontuacao) {

                    Jogador temp = jogadores.get(i);
                    jogadores.set(i, jogadores.get(i - 1));
                    jogadores.set(i - 1, temp);

                    trocou = true;
                }
            }

            inicio++;
        }
    }

    private void testarOrdenacao() {

        if (jogadores.size() < 2) {

            JOptionPane.showMessageDialog(
                    this,
                    "É necessário ter pelo menos 2 jogadores."
            );

            return;
        }

        duplaBubbleSort();

        boolean correto = true;

        for (int i = 0; i < jogadores.size() - 1; i++) {

            if (jogadores.get(i).pontuacao < jogadores.get(i + 1).pontuacao) {

                correto = false;
                break;
            }
        }

        atualizarTabela();

        if (correto) {

            JOptionPane.showMessageDialog(
                    this,
                    """
                    TESTE APROVADO!

                    A lista está corretamente ordenada pela pontuação, da maior para a menor.
                    """
            );

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    """
                    TESTE FALHOU!

                    A ordenação está incorreta.
                    """
            );
        }
    }

    private void atualizarTabela() {

        modeloTabela.setRowCount(0);

        for (int i = 0; i < jogadores.size(); i++) {

            Jogador jogador = jogadores.get(i);

            modeloTabela.addRow(
                    new Object[]{
                            i + 1,
                            jogador.nome,
                            jogador.pontuacao
                    }
            );
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            DuplaBubbleSort programa =
                    new DuplaBubbleSort();

            programa.setVisible(true);
        });
    }
}