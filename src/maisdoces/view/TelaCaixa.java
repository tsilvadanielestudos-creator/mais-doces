package maisdoces.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import maisdoces.dao.CaixaDAO;
import maisdoces.dao.VendaDAO;
import maisdoces.model.Movimentacao;
import maisdoces.model.Venda;
import maisdoces.util.Util;

public class TelaCaixa extends JFrame {

    private CaixaDAO caixaDAO = new CaixaDAO();
    private VendaDAO vendaDAO = new VendaDAO();
    private JLabel lblEntradas = new JLabel();
    private JLabel lblSaidas = new JLabel();
    private JLabel lblSaldo = new JLabel();
    private DefaultTableModel modeloCaixa;
    private DefaultTableModel modeloPendentes;
    private JTable tabelaPendentes;
    private List<Venda> pendentes = new ArrayList<>();

    public TelaCaixa() {
        setTitle("Mais Doces - Caixa");
        setSize(1000, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // totais
        JPanel totais = new JPanel(new GridLayout(1, 3, 10, 10));
        totais.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        Font fonte = new Font("Segoe UI", Font.BOLD, 18);
        lblEntradas.setFont(fonte);
        lblSaidas.setFont(fonte);
        lblSaldo.setFont(fonte);
        lblEntradas.setForeground(new Color(0, 120, 0));
        lblSaidas.setForeground(new Color(170, 0, 0));
        totais.add(lblEntradas);
        totais.add(lblSaidas);
        totais.add(lblSaldo);
        add(totais, BorderLayout.NORTH);

        // movimentacoes do caixa
        modeloCaixa = new DefaultTableModel(new String[]{"Data", "Tipo", "Descrição", "Pagamento", "Valor"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        JTable tabelaCaixa = new JTable(modeloCaixa);
        JButton btnSaida = new JButton("Lançar saída");
        btnSaida.addActionListener(e -> lancarSaida());
        JPanel painelCaixa = new JPanel(new BorderLayout(5, 5));
        painelCaixa.setBorder(BorderFactory.createTitledBorder("Movimentações"));
        painelCaixa.add(new JScrollPane(tabelaCaixa), BorderLayout.CENTER);
        JPanel bCaixa = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bCaixa.add(btnSaida);
        painelCaixa.add(bCaixa, BorderLayout.SOUTH);

        // vendas que ainda nao foram pagas
        modeloPendentes = new DefaultTableModel(new String[]{"Venda", "Cliente", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        tabelaPendentes = new JTable(modeloPendentes);
        JButton btnReceber = new JButton("Receber pagamento");
        btnReceber.addActionListener(e -> receber());
        JPanel painelPendentes = new JPanel(new BorderLayout(5, 5));
        painelPendentes.setBorder(BorderFactory.createTitledBorder("Vendas a receber"));
        painelPendentes.add(new JScrollPane(tabelaPendentes), BorderLayout.CENTER);
        JPanel bPend = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bPend.add(btnReceber);
        painelPendentes.add(bPend, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new GridLayout(1, 2, 10, 10));
        centro.add(painelCaixa);
        centro.add(painelPendentes);
        add(centro, BorderLayout.CENTER);

        carregarDados();
    }

    private void carregarDados() {
        try {
            double entradas = caixaDAO.total("Entrada");
            double saidas = caixaDAO.total("Saída");
            lblEntradas.setText("Entradas: " + Util.moeda(entradas));
            lblSaidas.setText("Saídas: " + Util.moeda(saidas));
            lblSaldo.setText("Saldo: " + Util.moeda(entradas - saidas));

            modeloCaixa.setRowCount(0);
            for (Movimentacao m : caixaDAO.listar()) {
                modeloCaixa.addRow(new Object[]{Util.data(m.getData()), m.getTipo(), m.getDescricao(),
                    m.getFormaPagamento(), Util.moeda(m.getValor())});
            }

            modeloPendentes.setRowCount(0);
            pendentes = vendaDAO.listarPendentes();
            for (Venda v : pendentes) {
                modeloPendentes.addRow(new Object[]{v.getId(), v.getClienteNome(), Util.moeda(v.getTotal())});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar o caixa: " + e.getMessage());
        }
    }

    private void receber() {
        int linha = tabelaPendentes.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione a venda que será paga.");
            return;
        }
        Venda v = pendentes.get(linha);
        JComboBox<String> cbForma = new JComboBox<>(new String[]{"Dinheiro", "PIX", "Cartão de débito", "Cartão de crédito"});
        int r = JOptionPane.showConfirmDialog(this, new Object[]{"Venda " + v.getId() + " - " + v.getClienteNome()
            + " - " + Util.moeda(v.getTotal()), "Forma de pagamento:", cbForma}, "Receber pagamento",
                JOptionPane.OK_CANCEL_OPTION);
        if (r != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            vendaDAO.receber(v, (String) cbForma.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Pagamento recebido e lançado no caixa!");
            carregarDados();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void lancarSaida() {
        String descricao = JOptionPane.showInputDialog(this, "Descrição da saída (ex.: compra de embalagens):");
        if (descricao == null || descricao.trim().isEmpty()) {
            return;
        }
        String texto = JOptionPane.showInputDialog(this, "Valor (R$):");
        if (texto == null) {
            return;
        }
        try {
            double valor = Util.lerValor(texto);
            if (valor <= 0) {
                JOptionPane.showMessageDialog(this, "O valor deve ser maior que zero.");
                return;
            }
            caixaDAO.lancarSaida(valor, descricao.trim(), "Dinheiro");
            carregarDados();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor inválido. Ex.: 85,00");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }
}
