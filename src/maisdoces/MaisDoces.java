package maisdoces;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import maisdoces.dao.Conexao;
import maisdoces.view.TelaLogin;

/**
 * Mais Doces - Sistema para confeitaria
 * Projeto Integrador - Daniel Teixeira Silva
 *
 * Usuarios para teste (senha 123): admin, atendente, confeiteiro, financeiro
 */
public class MaisDoces {

    public static void main(String[] args) {
        // visual Nimbus (o mesmo que o NetBeans usa nos projetos com JFrame)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Nao foi possivel usar o Nimbus");
        }

        SwingUtilities.invokeLater(() -> {
            // testa a conexao com o MySQL (e cria o banco se ainda nao existir)
            if (Conexao.iniciar()) {
                new TelaLogin().setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}
