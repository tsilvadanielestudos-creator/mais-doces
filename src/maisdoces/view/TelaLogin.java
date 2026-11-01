package maisdoces.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import maisdoces.dao.FuncionarioDAO;
import maisdoces.model.Funcionario;
import maisdoces.util.Sessao;

public class TelaLogin extends JFrame {

    private JTextField txtLogin = new JTextField();
    private JPasswordField txtSenha = new JPasswordField();

    public TelaLogin() {
        setTitle("Mais Doces - Login");
        setSize(420, 340);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // titulo
        JLabel lblTitulo = new JLabel("Mais Doces", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(102, 51, 0));
        JLabel lblSub = new JLabel("Sistema da confeitaria", SwingConstants.CENTER);
        JPanel topo = new JPanel(new GridLayout(2, 1));
        topo.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        topo.add(lblTitulo);
        topo.add(lblSub);
        add(topo, BorderLayout.NORTH);

        // campos
        JPanel campos = new JPanel(new GridLayout(2, 2, 10, 10));
        campos.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        campos.add(new JLabel("Usuário:"));
        campos.add(txtLogin);
        campos.add(new JLabel("Senha:"));
        campos.add(txtSenha);
        add(campos, BorderLayout.CENTER);

        // botao e dica com os usuarios de teste
        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.addActionListener(e -> entrar());
        getRootPane().setDefaultButton(btnEntrar);   // Enter faz o login

        // login de teste ja preenchido para facilitar
        txtLogin.setText("atendente");
        txtSenha.setText("123");

        JLabel lblDica = new JLabel("<html><center>Usuários para teste (senha 123):<br>"
                + "admin (gerente), atendente, confeiteiro, financeiro</center></html>", SwingConstants.CENTER);
        lblDica.setForeground(Color.DARK_GRAY);

        JPanel rodape = new JPanel(new BorderLayout(5, 8));
        rodape.setBorder(BorderFactory.createEmptyBorder(0, 40, 15, 40));
        rodape.add(btnEntrar, BorderLayout.NORTH);
        rodape.add(lblDica, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private void entrar() {
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha o usuário e a senha.");
            return;
        }

        try {
            Funcionario f = new FuncionarioDAO().login(login, senha);
            if (f == null) {
                JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos.", "Login", JOptionPane.WARNING_MESSAGE);
                txtSenha.setText("");
                return;
            }
            Sessao.usuario = f;
            new TelaMenu().setVisible(true);
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao acessar o banco: " + e.getMessage());
        }
    }
}
