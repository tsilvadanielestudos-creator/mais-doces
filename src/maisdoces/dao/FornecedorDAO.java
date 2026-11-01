package maisdoces.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import maisdoces.model.Fornecedor;

public class FornecedorDAO {

    public List<Fornecedor> listar() throws SQLException {
        List<Fornecedor> lista = new ArrayList<>();
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM fornecedor ORDER BY nome")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Fornecedor(rs.getInt("id"), rs.getString("nome"), rs.getString("telefone"),
                        rs.getString("cidade"), rs.getString("categoria")));
            }
        }
        return lista;
    }

    public void inserir(Fornecedor f) throws SQLException {
        String sql = "INSERT INTO fornecedor (nome, telefone, cidade, categoria) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getNome());
            ps.setString(2, f.getTelefone());
            ps.setString(3, f.getCidade());
            ps.setString(4, f.getCategoria());
            ps.executeUpdate();
        }
    }

    public void atualizar(Fornecedor f) throws SQLException {
        String sql = "UPDATE fornecedor SET nome = ?, telefone = ?, cidade = ?, categoria = ? WHERE id = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getNome());
            ps.setString(2, f.getTelefone());
            ps.setString(3, f.getCidade());
            ps.setString(4, f.getCategoria());
            ps.setInt(5, f.getId());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement("DELETE FROM fornecedor WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
