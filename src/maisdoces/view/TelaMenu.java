package maisdoces.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import maisdoces.dao.CaixaDAO;
import maisdoces.dao.EncomendaDAO;
import maisdoces.dao.VendaDAO;
import maisdoces.util.Sessao;
import maisdoces.util.Util;

public class TelaMenu extends JFrame {

    private JLabel lblResumo = new JLabel();

    public TelaMenu() {
        setTitle("Mais Doces - Menu principal");
        setSize(640, 470);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // cabecalho com o usuario e o resumo do dia
        JLabel lblBemVindo = new JLabel("Bem-vindo(a), " + Sessao.usuario.getNome()
                + " (" + Sessao.usuario.getCargo() + ")");
        lblBemVindo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBemVindo.setForeground(new Color(102, 51, 0));
        JPanel topo = new JPanel(new GridLayout(2, 1, 5, 5));
        topo.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 20));
        topo.add(lblBemVindo);
        topo.add(lblResumo);
        add(topo, BorderLayout.NORTH);

        // botoes das telas (so ficam ativos os que o cargo pode usar)
        JPanel botoes = new JPanel(new GridLayout(3, 3, 15, 15));
        botoes.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        String[] telas = {"Vendas", "Encomendas", "Produtos", "Clientes", "Fornecedores", "Caixa", "Funcionários"};
        for (String tela : telas) {
            JButton btn = new JButton(tela);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
            btn.setEnabled(Sessao.usuario.podeAcessar(tela));
            if (!btn.isEnabled()) {
                btn.setToolTipText("Seu cargo não tem acesso a esta tela");
            }
            btn.addActionListener(e -> abrirTela(tela));
            botoes.add(btn);
        }
        JButton btnSair = new JButton("Sair");
        btnSair.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnSair.addActionListener(e -> sair());
        botoes.add(btnSair);
        add(botoes, BorderLayout.CENTER);

        // atualiza o resumo sempre que a janela volta a ficar ativa
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                atualizarResumo();
            }
        });
    }

    private void abrirTela(String tela) {
        switch (tela) {
            case "Vendas":
                new TelaVendas().setVisible(true);
                break;
            case "Encomendas":
                new TelaEncomendas().setVisible(true);
                break;
            case "Produtos":
                new TelaProdutos().setVisible(true);
                break;
            case "Clientes":
                new TelaClientes().setVisible(true);
                break;
            case "Fornecedores":
                new TelaFornecedores().setVisible(true);
                break;
            case "Caixa":
                new TelaCaixa().setVisible(true);
                break;
            case "Funcionários":
                new TelaFuncionarios().setVisible(true);
                break;
        }
    }

    private void atualizarResumo() {
        try {
            double vendas = new VendaDAO().totalVendidoHoje();
            CaixaDAO caixa = new CaixaDAO();
            double saldo = caixa.total("Entrada") - caixa.total("Saída");
            int encomendas = new EncomendaDAO().contarPendentes();
            lblResumo.setText("Vendas de hoje: " + Util.moeda(vendas)
                    + "     |     Saldo do caixa: " + Util.moeda(saldo)
                    + "     |     Encomendas pendentes: " + encomendas);
        } catch (Exception e) {
            lblResumo.setText("Não foi possível carregar o resumo.");
        }
    }

    private void sair() {
        int r = JOptionPane.showConfirmDialog(this, "Deseja sair do sistema?", "Sair", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            Sessao.usuario = null;
            new TelaLogin().setVisible(true);
            dispose();
        }
    }
}
