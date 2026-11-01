package maisdoces.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import maisdoces.model.ItemVenda;
import maisdoces.model.Venda;

public class VendaDAO {

    /**
     * Salva a venda e tudo que depende dela:
     * - os itens;
     * - a baixa no estoque (venda de balcao) ou a encomenda para a producao;
     * - a entrada no caixa, se a venda foi paga.
     * Usa transacao: se der erro no meio, nada e gravado.
     */
    public void salvar(Venda v) throws SQLException {
        Connection con = Conexao.conectar();
        try {
            con.setAutoCommit(false);

            String sqlVenda = "INSERT INTO venda (cliente_id, funcionario_id, data, data_entrega, total, forma_pagamento, status, ocasiao) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sqlVenda, Statement.RETURN_GENERATED_KEYS);
            if (v.getCliente() == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, v.getCliente().getId());
            }
            ps.setInt(2, v.getFuncionarioId());
            ps.setDate(3, Date.valueOf(v.getData()));
            if (v.getDataEntrega() == null) {
                ps.setNull(4, Types.DATE);
            } else {
                ps.setDate(4, Date.valueOf(v.getDataEntrega()));
            }
            ps.setDouble(5, v.getTotal());
            ps.setString(6, v.getFormaPagamento());
            ps.setString(7, v.getStatus());
            ps.setString(8, v.getOcasiao());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            v.setId(rs.getInt(1));

            for (ItemVenda item : v.getItens()) {
                PreparedStatement psItem = con.prepareStatement(
                        "INSERT INTO item_venda (venda_id, produto_id, quantidade, preco, subtotal) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                psItem.setInt(1, v.getId());
                psItem.setInt(2, item.getProduto().getId());
                psItem.setInt(3, item.getQuantidade());
                psItem.setDouble(4, item.getPreco());
                psItem.setDouble(5, item.getSubtotal());
                psItem.executeUpdate();
                ResultSet rsItem = psItem.getGeneratedKeys();
                rsItem.next();
                int idItem = rsItem.getInt(1);

                if (v.isEncomenda()) {
                    // encomenda: vai para a producao
                    PreparedStatement psEnc = con.prepareStatement(
                            "INSERT INTO encomenda (item_venda_id, data_entrega) VALUES (?, ?)");
                    psEnc.setInt(1, idItem);
                    psEnc.setDate(2, Date.valueOf(v.getDataEntrega()));
                    psEnc.executeUpdate();
                } else {
                    // venda de balcao: tira do estoque
                    PreparedStatement psEst = con.prepareStatement(
                            "UPDATE produto SET quantidade = quantidade - ? WHERE id = ?");
                    psEst.setInt(1, item.getQuantidade());
                    psEst.setInt(2, item.getProduto().getId());
                    psEst.executeUpdate();
                }
            }

            if (v.getStatus().equals("Pago")) {
                String nome = v.getCliente() == null ? "Balcão" : v.getCliente().getNome();
                CaixaDAO.inserir(con, "Entrada", v.getTotal(), "Venda " + v.getId() + " - " + nome,
                        v.getFormaPagamento(), v.getId());
            }

            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.close();
        }
    }

    // vendas que ainda nao foram pagas
    public List<Venda> listarPendentes() throws SQLException {
        List<Venda> lista = new ArrayList<>();
        String sql = "SELECT v.id, v.data, v.total, c.nome FROM venda v "
                + "LEFT JOIN cliente c ON c.id = v.cliente_id WHERE v.status = 'Pendente' ORDER BY v.id";
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Venda v = new Venda();
                v.setId(rs.getInt("id"));
                v.setData(rs.getDate("data").toLocalDate());
                v.setTotal(rs.getDouble("total"));
                String nome = rs.getString("nome");
                v.setClienteNome(nome == null ? "Balcão" : nome);
                lista.add(v);
            }
        }
        return lista;
    }

    // recebe o pagamento de uma venda pendente e lanca no caixa
    public void receber(Venda v, String formaPagamento) throws SQLException {
        Connection con = Conexao.conectar();
        try {
            con.setAutoCommit(false);
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE venda SET status = 'Pago', forma_pagamento = ? WHERE id = ?");
            ps.setString(1, formaPagamento);
            ps.setInt(2, v.getId());
            ps.executeUpdate();
            CaixaDAO.inserir(con, "Entrada", v.getTotal(), "Venda " + v.getId() + " - " + v.getClienteNome(),
                    formaPagamento, v.getId());
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.close();
        }
    }

    public double totalVendidoHoje() throws SQLException {
        try (Connection con = Conexao.conectar();
             PreparedStatement ps = con.prepareStatement("SELECT SUM(total) FROM venda WHERE data = CURDATE()")) {
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getDouble(1);
        }
    }
}
