
import model.Configuracao;
import service.CalendarioService;
import service.ConfigHelper;
import ui.CalendarioFrame;
import ui.ConfiguracaoDialog;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        try {
            Configuracao configuracao = ConfigHelper.carregar();

            if (configuracao == null || configuracao.getPessoas() == null || configuracao.getPessoas().isEmpty()) {
                // Interface Swing para configuração
                ConfiguracaoDialog dialog = new ConfiguracaoDialog(null);
                dialog.setVisible(true);

                if (!dialog.isConfirmado()) {
                    JOptionPane.showMessageDialog(null, "Configuração cancelada. O aplicativo será encerrado.");
                    System.exit(0);
                }

                configuracao = dialog.getConfiguracao();
                ConfigHelper.salvar(configuracao);
            }

            CalendarioService service = new CalendarioService(
                configuracao.getPessoas(),
                configuracao.getDataInicial(),
                configuracao.getSubstituicoes()
            );

            new CalendarioFrame(service);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
