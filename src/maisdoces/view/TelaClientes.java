package maisdoces.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import maisdoces.dao.ClienteDAO;
import maisdoces.model.Cliente;

public class TelaClientes extends JFrame {

    private ClienteDAO clienteDAO = new ClienteDAO();
    private JTextField txtNome = new JTextField();
    private JTextField txtTelefone = new JTextField();
    private JTextField txtEmail = new JTextField();
    private JTextField txtEndereco = new JTextField();
    private JTextField txtObservacao = new JTextField();
    private DefaultTableModel modelo;
    private JTable tabela;
    private List<Cliente> clientes = new ArrayList<>();
    private int idSelecionado = 0;

    public TelaClientes() {
        setTitle("Mais Doces - Clientes");
        setSize(900, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(3, 4, 10, 8));
        form.setBorder(BorderFactory.createTitledBorder("Dados do cliente"));
        form.add(new JLabel("Nome:"));
        form.add(txtNome);
        form.add(new JLabel("Telefone:"));
        form.add(txtTelefone);
        form.add(new JLabel("E-mail:"));
        form.add(txtEmail);
        form.add(new JLabel("Endereço:"));
        form.add(txtEndereco);
        form.add(new JLabel("Observação:"));
        form.add(txtObservacao);

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

        modelo = new DefaultTableModel(new String[]{"ID", "Nome", "Telefone", "E-mail", "Endereço", "Observação"}, 0) {
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
        scroll.setBorder(BorderFactory.createTitledBorder("Clientes cadastrados (clique para editar)"));
        add(scroll, BorderLayout.CENTER);

        carregarTabela();
    }

    private void carregarTabela() {
        modelo.setRowCount(0);
        try {
            clientes = clienteDAO.listar();
            for (Cliente c : clientes) {
                modelo.addRow(new Object[]{c.getId(), c.getNome(), c.getTelefone(), c.getEmail(),
                    c.getEndereco(), c.getObservacao()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + e.getMessage());
        }
    }

    private void preencherCampos() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        Cliente c = clientes.get(linha);
        idSelecionado = c.getId();
        txtNome.setText(c.getNome());
        txtTelefone.setText(c.getTelefone());
        txtEmail.setText(c.getEmail());
        txtEndereco.setText(c.getEndereco());
        txtObservacao.setText(c.getObservacao());
    }

    private void salvar() {
        if (txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome do cliente.");
            return;
        }
        Cliente c = new Cliente(idSelecionado, txtNome.getText().trim(), txtTelefone.getText().trim(),
                txtEmail.getText().trim(), txtEndereco.getText().trim(), txtObservacao.getText().trim());
        try {
            if (idSelecionado == 0) {
                clienteDAO.inserir(c);
                JOptionPane.showMessageDialog(this, "Cliente cadastrado!");
            } else {
                clienteDAO.atualizar(c);
                JOptionPane.showMessageDialog(this, "Cliente alterado!");
            }
            limpar();
            carregarTabela();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
        }
    }

    private void excluir() {
        if (idSelecionado == 0) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela.");
            return;
        }
        int r = JOptionPane.showConfirmDialog(this, "Excluir o cliente " + txtNome.getText() + "?",
                "Excluir", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            clienteDAO.excluir(idSelecionado);
            limpar();
            carregarTabela();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Não é possível excluir: este cliente já tem compras registradas.");
        }
    }

    private void limpar() {
        idSelecionado = 0;
        txtNome.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        txtEndereco.setText("");
        txtObservacao.setText("");
        tabela.clearSelection();
        txtNome.requestFocus();
    }
}
