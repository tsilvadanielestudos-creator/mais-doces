package maisdoces.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import maisdoces.dao.ClienteDAO;
import maisdoces.dao.ProdutoDAO;
import maisdoces.dao.VendaDAO;
import maisdoces.model.Cliente;
import maisdoces.model.ItemVenda;
import maisdoces.model.Produto;
import maisdoces.model.Venda;
import maisdoces.util.Sessao;
import maisdoces.util.Util;

/**
 * Tela de vendas. Serve para venda de balcao e para encomendas
 * (quando escolhe um cliente e informa a data de entrega).
 */
public class TelaVendas extends JFrame {

    private Venda venda = new Venda();
    private JComboBox<Produto> cbProduto = new JComboBox<>();
    private JSpinner spQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private JComboBox<Object> cbCliente = new JComboBox<>();
    private JTextField txtEntrega = new JTextField();
    private JComboBox<String> cbOcasiao = new JComboBox<>(new String[]{"Nenhuma", "Aniversário", "Casamento",
        "Dia dos Namorados", "Dia das Mães", "Outra"});
    private JComboBox<String> cbPagamento = new JComboBox<>(new String[]{"Dinheiro", "PIX", "Cartão de débito",
        "Cartão de crédito", "Pagar depois"});
    private JLabel lblTotal = new JLabel("Total: R$ 0,00");
    private DefaultTableModel modelo;
    private JTable tabela;

    public TelaVendas() {
        setTitle("Mais Doces - Nova venda");
        setSize(900, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // escolher produto
        JPanel painelProduto = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        painelProduto.setBorder(BorderFactory.createTitledBorder("Adicionar produto"));
        painelProduto.add(new JLabel("Produto:"));
        painelProduto.add(cbProduto);
        painelProduto.add(new JLabel("Quantidade:"));
        painelProduto.add(spQuantidade);
        JButton btnAdicionar = new JButton("Adicionar");
        btnAdicionar.addActionListener(e -> adicionarItem());
        painelProduto.add(btnAdicionar);
        add(painelProduto, BorderLayout.NORTH);

        // itens da venda
        modelo = new DefaultTableModel(new String[]{"Produto", "Quantidade", "Preço", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        tabela = new JTable(modelo);
        JButton btnRemover = new JButton("Remover item selecionado");
        btnRemover.addActionListener(e -> removerItem());
        JPanel painelItens = new JPanel(new BorderLayout(5, 5));
        painelItens.setBorder(BorderFactory.createTitledBorder("Itens da venda"));
        painelItens.add(new JScrollPane(tabela), BorderLayout.CENTER);
        JPanel painelRemover = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelRemover.add(btnRemover);
        painelItens.add(painelRemover, BorderLayout.SOUTH);
        add(painelItens, BorderLayout.CENTER);

        // dados da venda e total
        JPanel painelDados = new JPanel(new GridLayout(0, 1, 5, 5));
        painelDados.setBorder(BorderFactory.createTitledBorder("Dados da venda"));
        painelDados.add(new JLabel("Cliente:"));
        painelDados.add(cbCliente);
        painelDados.add(new JLabel("Data de entrega (só encomenda):"));
        painelDados.add(txtEntrega);
        painelDados.add(new JLabel("Ocasião:"));
        painelDados.add(cbOcasiao);
        painelDados.add(new JLabel("Pagamento:"));
        painelDados.add(cbPagamento);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        painelDados.add(lblTotal);
        JButton btnFinalizar = new JButton("Finalizar venda");
        btnFinalizar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnFinalizar.addActionListener(e -> finalizar());
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> limpar());
        painelDados.add(btnFinalizar);
        painelDados.add(btnCancelar);
        add(painelDados, BorderLayout.EAST);

        carregarProdutos();
        carregarClientes();
    }

    private void carregarProdutos() {
        cbProduto.removeAllItems();
        try {
            for (Produto p : new ProdutoDAO().listar()) {
                if (!p.isVencido()) {   // produto vencido nao aparece para venda
                    cbProduto.addItem(p);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage());
        }
    }

    private void carregarClientes() {
        cbCliente.removeAllItems();
        cbCliente.addItem("Venda de balcão (sem cliente)");
        try {
            for (Cliente c : new ClienteDAO().listar()) {
                cbCliente.addItem(c);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + e.getMessage());
        }
    }

    private void adicionarItem() {
        Produto p = (Produto) cbProduto.getSelectedItem();
        if (p == null) {
            return;
        }
        int quantidade = (Integer) spQuantidade.getValue();

        // se o produto ja esta na lista, so soma a quantidade
        boolean achou = false;
        for (ItemVenda item : venda.getItens()) {
            if (item.getProduto().getId() == p.getId()) {
                item.setQuantidade(item.getQuantidade() + quantidade);
                achou = true;
            }
        }
        if (!achou) {
            venda.getItens().add(new ItemVenda(p, quantidade));
        }
        venda.calcularTotal();
        atualizarTabela();
        spQuantidade.setValue(1);
    }

    private void removerItem() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione o item que deseja remover.");
            return;
        }
        venda.getItens().remove(linha);
        venda.calcularTotal();
        atualizarTabela();
    }

    private void atualizarTabela() {
        modelo.setRowCount(0);
        for (ItemVenda item : venda.getItens()) {
            modelo.addRow(new Object[]{item.getProduto().getNome(), item.getQuantidade(),
                Util.moeda(item.getPreco()), Util.moeda(item.getSubtotal())});
        }
        lblTotal.setText("Total: " + Util.moeda(venda.getTotal()));
    }

    private void finalizar() {
        if (venda.getItens().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Adicione pelo menos um produto.");
            return;
        }

        // cliente
        if (cbCliente.getSelectedItem() instanceof Cliente) {
            venda.setCliente((Cliente) cbCliente.getSelectedItem());
        } else {
            venda.setCliente(null);
        }

        // data de entrega (encomenda)
        try {
            venda.setDataEntrega(Util.lerData(txtEntrega.getText()));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data de entrega inválida. Use dd/mm/aaaa.");
            return;
        }
        if (venda.getDataEntrega() != null && venda.getCliente() == null) {
            JOptionPane.showMessageDialog(this, "Para encomenda é preciso escolher o cliente.");
            return;
        }
        if (venda.getDataEntrega() != null && venda.getDataEntrega().isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "A data de entrega não pode ser no passado.");
            return;
        }

        // venda de balcao: confere o estoque
        if (!venda.isEncomenda()) {
            for (ItemVenda item : venda.getItens()) {
                if (item.getQuantidade() > item.getProduto().getQuantidade()) {
                    JOptionPane.showMessageDialog(this, "Estoque insuficiente de " + item.getProduto().getNome()
                            + ". Disponível: " + item.getProduto().getQuantidade());
                    return;
                }
            }
        }

        // pagamento
        String pagamento = (String) cbPagamento.getSelectedItem();
        if (pagamento.equals("Pagar depois")) {
            venda.setStatus("Pendente");
            venda.setFormaPagamento(null);
        } else {
            venda.setStatus("Pago");
            venda.setFormaPagamento(pagamento);
        }
        venda.setOcasiao((String) cbOcasiao.getSelectedItem());
        venda.setFuncionarioId(Sessao.usuario.getId());

        try {
            new VendaDAO().salvar(venda);
            String msg = "Venda " + venda.getId() + " registrada! Total: " + Util.moeda(venda.getTotal());
            if (venda.isEncomenda()) {
                msg += "\nA encomenda foi enviada para a produção.";
            }
            if (!venda.getOcasiao().equals("Nenhuma")) {
                msg += "\nOcasião especial: preparar cartão e embalagem personalizados.";
            }
            JOptionPane.showMessageDialog(this, msg);
            limpar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar a venda: " + e.getMessage());
        }
    }

    private void limpar() {
        venda = new Venda();
        atualizarTabela();
        txtEntrega.setText("");
        cbCliente.setSelectedIndex(0);
        cbOcasiao.setSelectedIndex(0);
        cbPagamento.setSelectedIndex(0);
        carregarProdutos();   // recarrega para mostrar o estoque atualizado
    }
}
