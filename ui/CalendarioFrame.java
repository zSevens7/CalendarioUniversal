package ui;

import model.Pessoa;
import service.CalendarioService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

public class CalendarioFrame extends JFrame {
    private JPanel calendarioPanel;
    private JLabel mesAnoLabel;
    private LocalDate dataAtual;
    private CalendarioService calendarioService;

    // Lista de cores para pessoas (pode ajustar ou expandir conforme precisar)
    private final List<Color> cores = Arrays.asList(
        new Color(200, 230, 255), // azul claro
        new Color(255, 230, 230), // rosa claro
        new Color(230, 255, 200), // verde claro
        new Color(255, 255, 200), // amarelo claro
        new Color(220, 200, 255), // roxo claro
        new Color(255, 210, 190)  // salmão claro
    );

    public CalendarioFrame(CalendarioService service) {
        this.calendarioService = service;

        setTitle("📅 Calendário de Revezamento");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        dataAtual = LocalDate.now();

        // Topo com nome do mês e botões
        JPanel topo = new JPanel(new BorderLayout());
        JButton anteriorBtn = new JButton("◀");
        JButton proximoBtn = new JButton("▶");
        mesAnoLabel = new JLabel("", SwingConstants.CENTER);
        mesAnoLabel.setFont(new Font("Arial", Font.BOLD, 20));

        topo.add(anteriorBtn, BorderLayout.WEST);
        topo.add(mesAnoLabel, BorderLayout.CENTER);
        topo.add(proximoBtn, BorderLayout.EAST);

        add(topo, BorderLayout.NORTH);

        // Painel dos dias do mês
        calendarioPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        add(calendarioPanel, BorderLayout.CENTER);

        anteriorBtn.addActionListener(e -> {
            dataAtual = dataAtual.minusMonths(1);
            atualizarCalendario();
        });

        proximoBtn.addActionListener(e -> {
            dataAtual = dataAtual.plusMonths(1);
            atualizarCalendario();
        });

        atualizarCalendario();
        setVisible(true);
    }

    private void atualizarCalendario() {
        calendarioPanel.removeAll();

        YearMonth anoMes = YearMonth.from(dataAtual);
        int diasNoMes = anoMes.lengthOfMonth();
        LocalDate primeiroDia = anoMes.atDay(1);
        int inicioSemana = primeiroDia.getDayOfWeek().getValue(); // 1 = segunda

        // Atualiza label do mês
        String nomeMes = dataAtual.getMonth().name().substring(0,1).toUpperCase() +
                         dataAtual.getMonth().name().substring(1).toLowerCase();
        mesAnoLabel.setText(nomeMes + " " + dataAtual.getYear());

        // Preenche espaço vazio até o primeiro dia
        for (int i = 1; i < inicioSemana; i++) {
            calendarioPanel.add(new JLabel(""));
        }

        // Preenche os dias e mostra responsável e cor
        for (int dia = 1; dia <= diasNoMes; dia++) {
            LocalDate dataDia = anoMes.atDay(dia);
            Pessoa responsavel = calendarioService.getResponsavel(dataDia);

            JLabel diaLabel = new JLabel(
                "<html><center>" + dia + "<br>" + responsavel.getNome() + "</center></html>",
                SwingConstants.CENTER
            );
            diaLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            int index = calendarioService.getIndexDaPessoa(responsavel);
            Color cor = cores.get(index % cores.size());

            diaLabel.setBackground(cor);
            diaLabel.setOpaque(true);
            calendarioPanel.add(diaLabel);
        }

        calendarioPanel.revalidate();
        calendarioPanel.repaint();
    }
}
