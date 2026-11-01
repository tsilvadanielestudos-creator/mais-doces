package maisdoces.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import maisdoces.model.Funcionario;

public class FuncionarioDAO {

    // retorna o funcionario se login e senha estiverem certos, senao null
    public Funcionario login(String login, String senha) throws SQLException {
        String sql = "SELECT * FROM funcionario WHERE login = ? AND senha = ? AND ativo = TRUE";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, senha);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return montar(rs);
            }
            return null;
        }
    }

    public List<Funcionario> listar() throws SQLException {
        List<Funcionario> lista = new ArrayList<>();
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM funcionario ORDER BY nome")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(montar(rs));
            }
        }
        return lista;
    }

    public void inserir(Funcionario f) throws SQLException {
        String sql = "INSERT INTO funcionario (nome, cargo, login, senha, ativo) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getNome());
            ps.setString(2, f.getCargo());
            ps.setString(3, f.getLogin());
            ps.setString(4, f.getSenha());
            ps.setBoolean(5, f.isAtivo());
            ps.executeUpdate();
        }
    }

    public void atualizar(Funcionario f) throws SQLException {
        String sql = "UPDATE funcionario SET nome = ?, cargo = ?, login = ?, senha = ? WHERE id = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getNome());
            ps.setString(2, f.getCargo());
            ps.setString(3, f.getLogin());
            ps.setString(4, f.getSenha());
            ps.setInt(5, f.getId());
            ps.executeUpdate();
        }
    }

    // funcionario nao e excluido, so fica inativo (para nao perder o historico de vendas)
    public void alterarAtivo(int id, boolean ativo) throws SQLException {
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement("UPDATE funcionario SET ativo = ? WHERE id = ?")) {
            ps.setBoolean(1, ativo);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private Funcionario montar(ResultSet rs) throws SQLException {
        return new Funcionario(rs.getInt("id"), rs.getString("nome"), rs.getString("cargo"),
                rs.getString("login"), rs.getString("senha"), rs.getBoolean("ativo"));
    }
}
