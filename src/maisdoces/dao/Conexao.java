package maisdoces.dao;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Classe responsavel pela conexao com o MySQL.
 *
 * Por padrao usa o usuario "root" sem senha. Se o MySQL do computador
 * tiver senha, o sistema pede na primeira vez e guarda no arquivo
 * conexao.properties (na pasta do projeto).
 */
public class Conexao {

    private static final String SERVIDOR = "jdbc:mysql://localhost:3306/";
    private static final String BANCO = "mais_doces";
    private static final String OPCOES = "?useSSL=false&allowPublicKeyRetrieval=true"
            + "&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8";
    private static final String ARQUIVO = "conexao.properties";

    private static String usuario = "root";
    private static String senha = "";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(SERVIDOR + BANCO + OPCOES, usuario, senha);
    }

    /**
     * Chamado quando o sistema abre. Testa a conexao e, se precisar,
     * pede a senha do MySQL ou cria o banco. Retorna false se nao der certo.
     */
    public static boolean iniciar() {
        lerArquivo();
        boolean jaCriou = false;
        while (true) {
            try {
                conectar().close();
                if (!bancoAtualizado() && !jaCriou) {
                    // existe um banco "maisdoces" de uma versao antiga, sem as tabelas certas
                    int r = JOptionPane.showConfirmDialog(null,
                            "O banco \"maisdoces\" deste computador é de uma versão antiga do sistema.\n"
                            + "Deseja recriar o banco com os dados de teste?",
                            "Mais Doces", JOptionPane.YES_NO_OPTION);
                    if (r != JOptionPane.YES_OPTION) {
                        return false;
                    }
                    criarBanco();
                    jaCriou = true;
                    JOptionPane.showMessageDialog(null, "Banco de dados atualizado com sucesso!");
                }
                return true;
            } catch (Exception e2) {
                if (!(e2 instanceof SQLException)) {
                    JOptionPane.showMessageDialog(null, "Erro ao criar o banco: " + e2.getMessage());
                    return false;
                }
                SQLException e = (SQLException) e2;
                String msg = String.valueOf(e.getMessage()).toLowerCase();

                if (msg.contains("unknown database") && !jaCriou) {
                    // o MySQL funciona, mas o banco ainda nao existe
                    int r = JOptionPane.showConfirmDialog(null,
                            "O banco de dados \"mais_doces\" ainda não existe neste computador.\n"
                            + "Deseja criar agora com os dados de teste?",
                            "Mais Doces", JOptionPane.YES_NO_OPTION);
                    if (r != JOptionPane.YES_OPTION) {
                        return false;
                    }
                    try {
                        criarBanco();
                        jaCriou = true;
                        JOptionPane.showMessageDialog(null, "Banco de dados criado com sucesso!");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Erro ao criar o banco: " + ex.getMessage());
                        return false;
                    }

                } else if (msg.contains("access denied")) {
                    // usuario ou senha do MySQL errados: pede para digitar
                    if (!pedirSenha()) {
                        return false;
                    }

                } else {
                    JOptionPane.showMessageDialog(null,
                            "Não foi possível conectar ao MySQL.\n"
                            + "Verifique se o MySQL está ligado neste computador.\n\n"
                            + "Detalhe: " + e.getMessage(),
                            "Mais Doces", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }
    }

    // confere se o banco tem as tabelas desta versao e o usuario admin
    private static boolean bancoAtualizado() {
        try (Connection con = conectar();
             Statement st = con.createStatement()) {
            st.executeQuery("SELECT COUNT(*) FROM encomenda");
            java.sql.ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM funcionario WHERE login = 'admin'");
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // mostra uma janela para digitar o usuario e a senha do MySQL
    private static boolean pedirSenha() {
        JTextField txtUsuario = new JTextField(usuario);
        JPasswordField txtSenha = new JPasswordField();
        JPanel painel = new JPanel(new java.awt.GridLayout(0, 1, 4, 4));
        painel.add(new JLabel("Digite o usuário e a senha do MySQL deste computador"));
        painel.add(new JLabel("(os mesmos usados no MySQL Workbench):"));
        painel.add(new JLabel("Usuário:"));
        painel.add(txtUsuario);
        painel.add(new JLabel("Senha:"));
        painel.add(txtSenha);
        int r = JOptionPane.showConfirmDialog(null, painel, "Conexão com o MySQL",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) {
            return false;
        }
        usuario = txtUsuario.getText().trim();
        senha = new String(txtSenha.getPassword());
        salvarArquivo();
        return true;
    }

    private static void lerArquivo() {
        try (FileInputStream in = new FileInputStream(ARQUIVO)) {
            Properties p = new Properties();
            p.load(in);
            usuario = p.getProperty("usuario", "root");
            senha = p.getProperty("senha", "");
        } catch (Exception e) {
            // arquivo ainda nao existe: usa root sem senha
        }
    }

    private static void salvarArquivo() {
        try (FileOutputStream out = new FileOutputStream(ARQUIVO)) {
            Properties p = new Properties();
            p.setProperty("usuario", usuario);
            p.setProperty("senha", senha);
            p.store(out, "Usuario e senha do MySQL usados pelo sistema Mais Doces");
        } catch (Exception e) {
            System.out.println("Nao foi possivel salvar " + ARQUIVO);
        }
    }

    // executa o script banco/maisdoces.sql para criar as tabelas e os dados de teste
    public static void criarBanco() throws Exception {
        String texto = new String(Files.readAllBytes(Paths.get("banco", "maisdoces.sql")), StandardCharsets.UTF_8);
        StringBuilder script = new StringBuilder();
        for (String linha : texto.split("\n")) {
            if (!linha.trim().startsWith("--")) {
                script.append(linha).append("\n");
            }
        }
        try (Connection con = DriverManager.getConnection(SERVIDOR + OPCOES, usuario, senha);
             Statement st = con.createStatement()) {
            for (String comando : script.toString().split(";")) {
                if (!comando.trim().isEmpty()) {
                    st.execute(comando);
                }
            }
        }
    }

    // usado nos testes
    public static void configurar(String novoUsuario, String novaSenha) {
        usuario = novoUsuario;
        senha = novaSenha;
    }
}
