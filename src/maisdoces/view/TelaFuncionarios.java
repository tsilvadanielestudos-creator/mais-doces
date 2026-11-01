package maisdoces.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import maisdoces.dao.FuncionarioDAO;
import maisdoces.model.Funcionario;
import maisdoces.util.Sessao;

public class TelaFuncionarios extends JFrame {

    private FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private JTextField txtNome = new JTextField();
    private JComboBox<String> cbCargo = new JComboBox<>(
            new String[]{"Gerente", "Atendente", "Confeiteiro", "Financeiro"});
    private JTextField txtLogin = new JTextField();
    private JTextField txtSenha = new JTextField();
    private DefaultTableModel modelo;
    private JTable tabela;
    private List<Funcionario> funcionarios = new ArrayList<>();
    private int idSelecionado = 0;

    public TelaFuncionarios() {
        setTitle("Mais Doces - Funcionários");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(2, 4, 10, 8));
        form.setBorder(BorderFactory.createTitledBorder("Dados do funcionário"));
        form.add(new JLabel("Nome:"));
        form.add(txtNome);
        form.add(new JLabel("Cargo:"));
        form.add(cbCargo);
        form.add(new JLabel("Login:"));
        form.add(txtLogin);
        form.add(new JLabel("Senha:"));
        form.add(txtSenha);

        JButton btnSalvar = new JButton("Salvar");
        JButton btnAtivo = new JButton("Ativar / Inativar");
        JButton btnLimpar = new JButton("Limpar");
        btnSalvar.addActionListener(e -> salvar());
        btnAtivo.addActionListener(e -> ativarInativar());
        btnLimpar.addActionListener(e -> limpar());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botoes.add(btnLimpar);
        botoes.add(btnAtivo);
        botoes.add(btnSalvar);

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(form, BorderLayout.CENTER);
        topo.add(botoes, BorderLayout.SOUTH);
        add(topo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"ID", "Nome", "Cargo", "Login", "Situação"}, 0) {
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
        scroll.setBorder(BorderFactory.createTitledBorder("Funcionários (clique para editar)"));
        add(scroll, BorderLayout.CENTER);

        carregarTabela();
    }

    private void carregarTabela() {
        modelo.setRowCount(0);
        try {
            funcionarios = funcionarioDAO.listar();
            for (Funcionario f : funcionarios) {
                modelo.addRow(new Object[]{f.getId(), f.getNome(), f.getCargo(), f.getLogin(),
                    f.isAtivo() ? "Ativo" : "Inativo"});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar funcionários: " + e.getMessage());
        }
    }

    private void preencherCampos() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            return;
        }
        Funcionario f = funcionarios.get(linha);
        idSelecionado = f.getId();
        txtNome.setText(f.getNome());
        cbCargo.setSelectedItem(f.getCargo());
        txtLogin.setText(f.getLogin());
        txtSenha.setText(f.getSenha());
    }

    private void salvar() {
        if (txtNome.getText().trim().isEmpty() || txtLogin.getText().trim().isEmpty()
                || txtSenha.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha nome, login e senha.");
            return;
        }
        Funcionario f = new Funcionario(idSelecionado, txtNome.getText().trim(), (String) cbCargo.getSelectedItem(),
                txtLogin.getText().trim(), txtSenha.getText().trim(), true);
        try {
            if (idSelecionado == 0) {
                funcionarioDAO.inserir(f);
                JOptionPane.showMessageDialog(this, "Funcionário cadastrado!");
            } else {
                funcionarioDAO.atualizar(f);
                JOptionPane.showMessageDialog(this, "Funcionário alterado!");
            }
            limpar();
            carregarTabela();
        } catch (SQLException e) {
            // o login e UNIQUE no banco
            JOptionPane.showMessageDialog(this, "Não foi possível salvar. Verifique se o login já existe.");
        }
    }

    private void ativarInativar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um funcionário na tabela.");
            return;
        }
        Funcionario f = funcionarios.get(linha);
        if (f.getId() == Sessao.usuario.getId()) {
            JOptionPane.showMessageDialog(this, "Você não pode inativar o seu próprio usuário.");
            return;
        }
        try {
            funcionarioDAO.alterarAtivo(f.getId(), !f.isAtivo());
            limpar();
            carregarTabela();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void limpar() {
        idSelecionado = 0;
        txtNome.setText("");
        txtLogin.setText("");
        txtSenha.setText("");
        cbCargo.setSelectedIndex(0);
        tabela.clearSelection();
    }
}
