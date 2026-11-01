package maisdoces.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.format.DateTimeParseException;
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
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import maisdoces.dao.FornecedorDAO;
import maisdoces.dao.ProdutoDAO;
import maisdoces.model.Fornecedor;
import maisdoces.model.Produto;
import maisdoces.util.Util;

public class TelaProdutos extends JFrame {

    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private JTextField txtNome = new JTextField();
    private JComboBox<String> cbCategoria = new JComboBox<>(
            new String[]{"Docinhos", "Bolos", "Tortas", "Brownies", "Cookies", "Outros"});
    private JTextField txtPreco = new JTextField();
    private JTextField txtQuantidade = new JTextField();
    private JTextField txtValidade = new JTextField();
    private JComboBox<Object> cbFornecedor = new JComboBox<>();
    private DefaultTableModel modelo;
    private JTable tabela;
    private List<Produto> produtos = new ArrayList<>();
    private int idSelecionado = 0;   // 0 = produto novo

    public TelaProdutos() {
        setTitle("Mais Doces - Produtos");
        setSize(950, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // formulario
        JPanel form = new JPanel(new GridLayout(3, 4, 10, 8));
        form.setBorder(BorderFactory.createTitledBorder("Dados do produto"));
        form.add(new JLabel("Nome:"));
        form.add(txtNome);
        form.add(new JLabel("Categoria:"));
        form.add(cbCategoria);
        form.add(new JLabel("Preço (R$):"));
        form.add(txtPreco);
        form.add(new JLabel("Quantidade:"));
        form.add(txtQuantidade);
        form.add(new JLabel("Validade (dd/mm/aaaa):"));
        form.add(txtValidade);
        form.add(new JLabel("Fornecedor:"));
        form.add(cbFornecedor);

        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limpar());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(btnLimpar);
        botoes.add(btnExcluir);
        botoes.add(btnSalvar);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(form, BorderLayout.CENTER);
        topo.add(botoes, BorderLayout.SOUTH);
        add(topo, BorderLayout.NORTH);

        // tabela
        modelo = new DefaultTableModel(new String[]{"ID", "Nome", "Categoria", "Preço", "Qtd.",
            "Validade", "Fornecedor", "Situação"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        tabela = new JTable(modelo);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                preencherCampos();
            }
        });
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Produtos cadastrados (clique em um produto para editar)"));
        add(scroll, BorderLayout.CENTER);

        carregarFornecedores();
        carregarTabela();
    }

    private void carregarFornecedores() {
        cbFornecedor.removeAllItems();
        cbFornecedor.addItem("Nenhum");
        try {
            for (Fornecedor f : new FornecedorDAO().listar()) {
                cbFornecedor.addItem(f);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar fornecedores: " + e.getMessage());
        }
    }

    private void carregarTabela() {
        modelo.setRowCount(0);
        try {
            produtos = produtoDAO.listar();
            for (Produto p : produtos) {
                modelo.addRow(new Object[]{p.getId(), p.getNome(), p.getCategoria(), Util.moeda(p.getPreco()),
                    p.getQuantidade(), Util.data(p.getValidade()),
                    p.getFornecedorNome() == null ? "-" : p.getFornecedorNome(), p.getSituacao()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage());
        }
    }

    private void preencherCampos() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        Produto p = produtos.get(linha);
        idSelecionado = p.getId();
        txtNome.setText(p.getNome());
        cbCategoria.setSelectedItem(p.getCategoria());
        txtPreco.setText(String.format("%.2f", p.getPreco()).replace(".", ","));
        txtQuantidade.setText(String.valueOf(p.getQuantidade()));
        txtValidade.setText(Util.data(p.getValidade()));
        cbFornecedor.setSelectedIndex(0);
        for (int i = 1; i < cbFornecedor.getItemCount(); i++) {
            Fornecedor f = (Fornecedor) cbFornecedor.getItemAt(i);
            if (f.getId() == p.getFornecedorId()) {
                cbFornecedor.setSelectedIndex(i);
            }
        }
    }

    private void salvar() {
        // validacoes
        if (txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do produto.");
            return;
        }
        Produto p = new Produto();
        try {
            p.setPreco(Util.lerValor(txtPreco.getText()));
            p.setQuantidade(Integer.parseInt(txtQuantidade.getText().trim()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preço e quantidade devem ser números. Ex.: 3,50 e 20");
            return;
        }
        if (p.getPreco() <= 0) {
            JOptionPane.showMessageDialog(this, "O preço deve ser maior que zero.");
            return;
        }
        if (p.getQuantidade() < 0) {
            JOptionPane.showMessageDialog(this, "A quantidade não pode ser negativa.");
            return;
        }
        try {
            p.setValidade(Util.lerData(txtValidade.getText()));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data de validade inválida. Use o formato dd/mm/aaaa.");
            return;
        }

        p.setId(idSelecionado);
        p.setNome(txtNome.getText().trim());
        p.setCategoria((String) cbCategoria.getSelectedItem());
        if (cbFornecedor.getSelectedItem() instanceof Fornecedor) {
            p.setFornecedorId(((Fornecedor) cbFornecedor.getSelectedItem()).getId());
        }

        try {
            if (idSelecionado == 0) {
                produtoDAO.inserir(p);
                JOptionPane.showMessageDialog(this, "Produto cadastrado!");
            } else {
                produtoDAO.atualizar(p);
                JOptionPane.showMessageDialog(this, "Produto alterado!");
            }
            limpar();
            carregarTabela();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
        }
    }

    private void excluir() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela.");
            return;
        }
        int r = JOptionPane.showConfirmDialog(this, "Excluir o produto " + txtNome.getText() + "?",
                "Excluir", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            produtoDAO.excluir(idSelecionado);
            limpar();
            carregarTabela();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Não é possível excluir: este produto já foi usado em vendas.");
        }
    }

    private void limpar() {
        idSelecionado = 0;
        txtNome.setText("");
        txtPreco.setText("");
        txtQuantidade.setText("");
        txtValidade.setText("");
        cbCategoria.setSelectedIndex(0);
        cbFornecedor.setSelectedIndex(0);
        tabela.clearSelection();
        txtNome.requestFocus();
    }
}
