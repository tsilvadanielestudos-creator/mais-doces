package maisdoces.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import maisdoces.model.Produto;

public class ProdutoDAO {

    public List<Produto> listar() throws SQLException {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT p.*, f.nome AS fornecedor FROM produto p "
                + "LEFT JOIN fornecedor f ON f.id = p.fornecedor_id ORDER BY p.nome";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Produto p = new Produto();
                p.setId(rs.getInt("id"));
                p.setNome(rs.getString("nome"));
                p.setCategoria(rs.getString("categoria"));
                p.setPreco(rs.getDouble("preco"));
                p.setQuantidade(rs.getInt("quantidade"));
                Date validade = rs.getDate("validade");
                if (validade != null) {
                    p.setValidade(validade.toLocalDate());
                }
                p.setFornecedorId(rs.getInt("fornecedor_id"));
                p.setFornecedorNome(rs.getString("fornecedor"));
                lista.add(p);
            }
        }
        return lista;
    }

    public void inserir(Produto p) throws SQLException {
        String sql = "INSERT INTO produto (nome, categoria, preco, quantidade, validade, fornecedor_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            preencher(ps, p);
            ps.executeUpdate();
        }
    }

    public void atualizar(Produto p) throws SQLException {
        String sql = "UPDATE produto SET nome = ?, categoria = ?, preco = ?, quantidade = ?, validade = ?, fornecedor_id = ? WHERE id = ?";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            preencher(ps, p);
            ps.setInt(7, p.getId());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement("DELETE FROM produto WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private void preencher(PreparedStatement ps, Produto p) throws SQLException {
        ps.setString(1, p.getNome());
        ps.setString(2, p.getCategoria());
        ps.setDouble(3, p.getPreco());
        ps.setInt(4, p.getQuantidade());
        if (p.getValidade() == null) {
            ps.setNull(5, Types.DATE);
        } else {
            ps.setDate(5, Date.valueOf(p.getValidade()));
        }
        if (p.getFornecedorId() == 0) {
            ps.setNull(6, Types.INTEGER);
        } else {
            ps.setInt(6, p.getFornecedorId());
        }
    }
}
