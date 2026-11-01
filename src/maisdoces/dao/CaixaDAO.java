package maisdoces.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import maisdoces.model.Movimentacao;

public class CaixaDAO {

    // grava usando uma conexao ja aberta (usado junto com a venda)
    public static void inserir(Connection con, String tipo, double valor, String descricao,
                               String formaPagamento, int vendaId) throws SQLException {
        String sql = "INSERT INTO caixa (tipo, valor, data, descricao, forma_pagamento, venda_id) "
                + "VALUES (?, ?, CURDATE(), ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, tipo);
        ps.setDouble(2, valor);
        ps.setString(3, descricao);
        ps.setString(4, formaPagamento);
        if (vendaId == 0) {
            ps.setNull(5, Types.INTEGER);
        } else {
            ps.setInt(5, vendaId);
        }
        ps.executeUpdate();
    }

    // saida de dinheiro (compras, contas, etc.)
    public void lancarSaida(double valor, String descricao, String formaPagamento) throws SQLException {
        try (Connection con = Conexao.conectar()) {
            inserir(con, "Saída", valor, descricao, formaPagamento, 0);
        }
    }

    public List<Movimentacao> listar() throws SQLException {
        List<Movimentacao> lista = new ArrayList<>();
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM caixa ORDER BY data DESC, id DESC")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Movimentacao m = new Movimentacao();
                m.setId(rs.getInt("id"));
                m.setTipo(rs.getString("tipo"));
                m.setValor(rs.getDouble("valor"));
                m.setData(rs.getDate("data").toLocalDate());
                m.setDescricao(rs.getString("descricao"));
                m.setFormaPagamento(rs.getString("forma_pagamento"));
                lista.add(m);
            }
        }
        return lista;
    }

    // soma das entradas ou das saidas
    public double total(String tipo) throws SQLException {
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement("SELECT SUM(valor) FROM caixa WHERE tipo = ?")) {
            ps.setString(1, tipo);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getDouble(1);
        }
    }
}
