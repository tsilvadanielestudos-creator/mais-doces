package maisdoces.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import maisdoces.model.Cliente;

public class ClienteDAO {

    public List<Cliente> listar() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM cliente ORDER BY nome")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Cliente(rs.getInt("id"), rs.getString("nome"), rs.getString("telefone"),
                        rs.getString("email"), rs.getString("endereco"), rs.getString("observacao")));
            }
        }
        return lista;
    }

    public void inserir(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (nome, telefone, email, endereco, observacao) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getTelefone());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getEndereco());
            ps.setString(5, c.getObservacao());
            ps.executeUpdate();
        }
    }

    public void atualizar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET nome = ?, telefone = ?, email = ?, endereco = ?, observacao = ? WHERE id = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getTelefone());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getEndereco());
            ps.setString(5, c.getObservacao());
            ps.setInt(6, c.getId());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement("DELETE FROM cliente WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
