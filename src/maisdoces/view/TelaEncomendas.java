package maisdoces.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
import maisdoces.dao.EncomendaDAO;
import maisdoces.model.Encomenda;
import maisdoces.util.Util;

/**
 * Tela da confeitaria: mostra as encomendas e controla a producao.
 * Ordem do status: Em andamento -> Finalizado -> Entregue.
 */
public class TelaEncomendas extends JFrame {

    private EncomendaDAO encomendaDAO = new EncomendaDAO();
    private JComboBox<String> cbFiltro = new JComboBox<>(new String[]{"Pendentes", "Todas"});
    private DefaultTableModel modelo;
    private JTable tabela;
    private List<Encomenda> encomendas = new ArrayList<>();

    public TelaEncomendas() {
        setTitle("Mais Doces - Encomendas");
        setSize(950, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel("Mostrar:"));
        topo.add(cbFiltro);
        cbFiltro.addActionListener(e -> carregarTabela());
        add(topo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"Nº", "Entrega", "Cliente", "Produto", "Qtd.",
            "Ocasião", "Valor", "Custo", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        tabela = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Encomendas (ordenadas pela data de entrega)"));
        add(scroll, BorderLayout.CENTER);

        JButton btnCusto = new JButton("Informar custo");
        JButton btnAvancar = new JButton("Avançar status");
        btnCusto.addActionListener(e -> informarCusto());
        btnAvancar.addActionListener(e -> avancarStatus());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(btnCusto);
        botoes.add(btnAvancar);
        add(botoes, BorderLayout.SOUTH);

        carregarTabela();
    }

    private void carregarTabela() {
        modelo.setRowCount(0);
        try {
            boolean pendentes = cbFiltro.getSelectedIndex() == 0;
            encomendas = encomendaDAO.listar(pendentes);
            for (Encomenda e : encomendas) {
                modelo.addRow(new Object[]{e.getId(), Util.data(e.getDataEntrega()), e.getCliente(), e.getProduto(),
                    e.getQuantidade(), e.getOcasiao(), Util.moeda(e.getValor()),
                    e.getCusto() > 0 ? Util.moeda(e.getCusto()) : "não informado", e.getStatus()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar encomendas: " + ex.getMessage());
        }
    }

    private Encomenda selecionada() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione uma encomenda na tabela.");
            return null;
        }
        return encomendas.get(linha);
    }

    private void informarCusto() {
        Encomenda e = selecionada();
        if (e == null) {
            return;
        }
        String texto = JOptionPane.showInputDialog(this,
                "Custo total para produzir \"" + e.getProduto() + "\" (ingredientes + embalagem):", "Custo",
                JOptionPane.QUESTION_MESSAGE);
        if (texto == null) {
            return;
        }
        try {
            double custo = Util.lerValor(texto);
            if (custo <= 0) {
                JOptionPane.showMessageDialog(this, "O custo deve ser maior que zero.");
                return;
            }
            encomendaDAO.atualizarCusto(e.getId(), custo);
            JOptionPane.showMessageDialog(this, "Custo salvo. Lucro estimado: " + Util.moeda(e.getValor() - custo));
            carregarTabela();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Digite um valor válido. Ex.: 85,00");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private void avancarStatus() {
        Encomenda e = selecionada();
        if (e == null) {
            return;
        }
        if (e.getStatus().equals("Entregue")) {
            JOptionPane.showMessageDialog(this, "Esta encomenda já foi entregue.");
            return;
        }
        if (e.getStatus().equals("Em andamento") && e.getCusto() == 0) {
            JOptionPane.showMessageDialog(this, "Informe o custo antes de finalizar a produção.");
            return;
        }
        int r = JOptionPane.showConfirmDialog(this, "Mudar o status para \"" + e.proximoStatus() + "\"?",
                "Status", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            encomendaDAO.atualizarStatus(e.getId(), e.proximoStatus());
            carregarTabela();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }
}
