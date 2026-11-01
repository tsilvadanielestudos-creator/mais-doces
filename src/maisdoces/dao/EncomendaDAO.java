package maisdoces.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import maisdoces.model.Encomenda;

public class EncomendaDAO {

    public List<Encomenda> listar(boolean somentePendentes) throws SQLException {
        List<Encomenda> lista = new ArrayList<>();
        String sql = "SELECT e.id, e.data_entrega, e.custo, e.status, i.quantidade, i.subtotal, "
                + "p.nome AS produto, v.ocasiao, c.nome AS cliente "
                + "FROM encomenda e "
                + "JOIN item_venda i ON i.id = e.item_venda_id "
                + "JOIN produto p ON p.id = i.produto_id "
                + "JOIN venda v ON v.id = i.venda_id "
                + "LEFT JOIN cliente c ON c.id = v.cliente_id ";
        if (somentePendentes) {
            sql += "WHERE e.status <> 'Entregue' ";
        }
        sql += "ORDER BY e.data_entrega, e.id";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Encomenda e = new Encomenda();
                e.setId(rs.getInt("id"));
                e.setDataEntrega(rs.getDate("data_entrega").toLocalDate());
                e.setCusto(rs.getDouble("custo"));
                e.setStatus(rs.getString("status"));
                e.setQuantidade(rs.getInt("quantidade"));
                e.setValor(rs.getDouble("subtotal"));
                e.setProduto(rs.getString("produto"));
                e.setOcasiao(rs.getString("ocasiao"));
                e.setCliente(rs.getString("cliente"));
                lista.add(e);
            }
        }
        return lista;
    }

    public void atualizarCusto(int id, double custo) throws SQLException {
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement("UPDATE encomenda SET custo = ? WHERE id = ?")) {
            ps.setDouble(1, custo);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void atualizarStatus(int id, String status) throws SQLException {
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement("UPDATE encomenda SET status = ? WHERE id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public int contarPendentes() throws SQLException {
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM encomenda WHERE status <> 'Entregue'")) {
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }
}
