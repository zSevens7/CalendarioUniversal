package ui;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import model.Configuracao;
import model.Pessoa;
import service.CalendarioService;
import service.ConfigHelper;
import service.LocaleManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarioFrame extends JFrame {
    private JPanel calendarioPanel;
    private JLabel mesAnoLabel;
    private LocalDate dataAtual;
    private CalendarioService calendarioService;
    private JButton fillWindowBtn;
    private JButton salvarBtn;
    private JButton apagarBtn;
    private JButton pdfBtn;
    private JComboBox<String> idiomaComboBox;

    private Map<String, Locale> localeMap;

    public CalendarioFrame(CalendarioService service) {
        this.calendarioService = service;
        setTitle(LocaleManager.getString("app.title"));
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }

        dataAtual = LocalDate.now();

        JPanel topo = new JPanel(new BorderLayout(10, 10));
        topo.setBorder(new EmptyBorder(15, 20, 10, 20));

        JButton anteriorBtn = criarBotaoNavegacao(createArrowIcon(false));
        JButton proximoBtn = criarBotaoNavegacao(createArrowIcon(true));

        fillWindowBtn = criarBotaoNavegacao(createMaximizeIcon());

        salvarBtn = criarBotaoAcao(LocaleManager.getString("button.save"), createSaveIcon());
        apagarBtn = criarBotaoAcao(LocaleManager.getString("button.delete"), createDeleteIcon());
        
        pdfBtn = criarBotaoAcao(LocaleManager.getString("button.save_pdf"), createPdfIcon());

        mesAnoLabel = new JLabel("", SwingConstants.CENTER);
        mesAnoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mesAnoLabel.setForeground(new Color(50, 50, 50));

        JPanel botoesLaterais = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        botoesLaterais.setBorder(new EmptyBorder(10, 0, 15, 0));

        botoesLaterais.add(salvarBtn);
        botoesLaterais.add(apagarBtn);
        botoesLaterais.add(pdfBtn);

        JPanel navigationEastPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        navigationEastPanel.setOpaque(false);
        navigationEastPanel.add(proximoBtn);
        navigationEastPanel.add(fillWindowBtn);

        inicializarIdiomaComboBox();

        idiomaComboBox.addActionListener(e -> {
            String selectedTranslatedName = (String) idiomaComboBox.getSelectedItem();
            Locale newLocale = localeMap.get(selectedTranslatedName);
            if (newLocale != null) {
                LocaleManager.setLocale(newLocale);
                atualizarTextosUI();
            }
        });

        JPanel topEastControls = new JPanel(new BorderLayout());
        topEastControls.setOpaque(false);
        topEastControls.add(navigationEastPanel, BorderLayout.CENTER);
        topEastControls.add(idiomaComboBox, BorderLayout.SOUTH);

        topo.add(anteriorBtn, BorderLayout.WEST);
        topo.add(mesAnoLabel, BorderLayout.CENTER);
        topo.add(topEastControls, BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);
        add(botoesLaterais, BorderLayout.SOUTH);

        calendarioPanel = new JPanel(new GridLayout(0, 7, 8, 8));
        calendarioPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        calendarioPanel.setBackground(new Color(250, 250, 250));

        add(calendarioPanel, BorderLayout.CENTER);

        anteriorBtn.addActionListener(e -> {
            dataAtual = dataAtual.minusMonths(1);
            atualizarCalendario();
        });

        proximoBtn.addActionListener(e -> {
            dataAtual = dataAtual.plusMonths(1);
            atualizarCalendario();
        });

        fillWindowBtn.addActionListener(e -> {
            if (getExtendedState() == JFrame.NORMAL) {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                fillWindowBtn.setIcon(createRestoreIcon());
            } else {
                setExtendedState(JFrame.NORMAL);
                fillWindowBtn.setIcon(createMaximizeIcon());
            }
        });

        salvarBtn.addActionListener(e -> {
            try {
                Configuracao config = calendarioService.getConfiguracao();
                ConfigHelper.salvar(config);
                JOptionPane.showMessageDialog(this, LocaleManager.getString("dialog.save_success"), LocaleManager.getString("dialog.info_title"), JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, LocaleManager.getString("dialog.save_error") + ex.getMessage(), LocaleManager.getString("dialog.error_title"), JOptionPane.ERROR_MESSAGE);
            }
        });

        apagarBtn.addActionListener(e -> {
            int opcao = JOptionPane.showConfirmDialog(this, LocaleManager.getString("dialog.delete_confirm"), LocaleManager.getString("dialog.warning_title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opcao == JOptionPane.YES_OPTION) {
                try {
                    ConfigHelper.apagar();
                    JOptionPane.showMessageDialog(this, LocaleManager.getString("dialog.delete_success"), LocaleManager.getString("dialog.info_title"), JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, LocaleManager.getString("dialog.delete_error") + ex.getMessage(), LocaleManager.getString("dialog.error_title"), JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pdfBtn.addActionListener(e -> salvarCalendarioComoPDF());

        atualizarCalendario();
        setVisible(true);
    }

    private void inicializarIdiomaComboBox() {
        localeMap = new LinkedHashMap<>();
        localeMap.put(LocaleManager.getString("language.pt_BR"), new Locale.Builder().setLanguage("pt").setRegion("BR").build());
        localeMap.put(LocaleManager.getString("language.en"), Locale.ENGLISH);
        localeMap.put(LocaleManager.getString("language.es"), new Locale.Builder().setLanguage("es").build());
        localeMap.put(LocaleManager.getString("language.fr"), Locale.FRENCH);
        localeMap.put(LocaleManager.getString("language.de"), Locale.GERMAN);
        localeMap.put(LocaleManager.getString("language.it"), Locale.ITALIAN);
        localeMap.put(LocaleManager.getString("language.nl"), new Locale.Builder().setLanguage("nl").build());
        localeMap.put(LocaleManager.getString("language.ko"), Locale.KOREAN);
        localeMap.put(LocaleManager.getString("language.ja"), Locale.JAPANESE);
        localeMap.put(LocaleManager.getString("language.pl"), new Locale.Builder().setLanguage("pl").build());
        localeMap.put(LocaleManager.getString("language.ru"), new Locale.Builder().setLanguage("ru").build());
        localeMap.put(LocaleManager.getString("language.ar"), new Locale.Builder().setLanguage("ar").build());
        localeMap.put(LocaleManager.getString("language.zh_CN"), Locale.SIMPLIFIED_CHINESE);

        if (idiomaComboBox == null) {
            idiomaComboBox = new JComboBox<>(localeMap.keySet().toArray(new String[0]));
            idiomaComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        } else {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(localeMap.keySet().toArray(new String[0]));
            idiomaComboBox.setModel(model);
        }

        String currentLocaleKey = "language." + LocaleManager.getCurrentLocale().getLanguage() +
                (LocaleManager.getCurrentLocale().getCountry().isEmpty() ? "" : "_" + LocaleManager.getCurrentLocale().getCountry());
        String currentTranslatedName = LocaleManager.getString(currentLocaleKey);
        if (localeMap.containsKey(currentTranslatedName)) {
            idiomaComboBox.setSelectedItem(currentTranslatedName);
        } else {
            String fallbackName = null;
            for (Map.Entry<String, Locale> entry : localeMap.entrySet()) {
                if (entry.getValue().equals(LocaleManager.getCurrentLocale())) {
                    fallbackName = entry.getKey();
                    break;
                }
            }
            if (fallbackName != null) {
                idiomaComboBox.setSelectedItem(fallbackName);
            } else {
                idiomaComboBox.setSelectedIndex(0);
            }
        }
    }

    private void atualizarTextosUI() {
        setTitle(LocaleManager.getString("app.title"));
        salvarBtn.setText(LocaleManager.getString("button.save"));
        apagarBtn.setText(LocaleManager.getString("button.delete"));
        pdfBtn.setText(LocaleManager.getString("button.save_pdf"));
        inicializarIdiomaComboBox();
        atualizarCalendario();
        revalidate();
        repaint();
    }

    private ImageIcon createArrowIcon(boolean right) {
        int size = 24;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setColor(Color.WHITE);
        Polygon arrow = new Polygon();
        if (right) {
            arrow.addPoint(size / 3, size / 4);
            arrow.addPoint(2 * size / 3, size / 2);
            arrow.addPoint(size / 3, 3 * size / 4);
        } else {
            arrow.addPoint(2 * size / 3, size / 4);
            arrow.addPoint(size / 3, size / 2);
            arrow.addPoint(2 * size / 3, 3 * size / 4);
        }
        g2.fill(arrow);
        g2.dispose();
        return new ImageIcon(image);
    }

    private ImageIcon createMaximizeIcon() {
        int size = 24;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.drawRect(size / 4, size / 4, size / 2, size / 2);
        g2.dispose();
        return new ImageIcon(image);
    }

    private ImageIcon createRestoreIcon() {
        int size = 24;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.drawRect(size / 4, size / 3, size / 2, size / 2);
        g2.drawRect(size / 3, size / 4, size / 2, size / 2);
        g2.dispose();
        return new ImageIcon(image);
    }

    private ImageIcon createSaveIcon() {
        int size = 18;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(size / 4, 0, size / 2, size - (size / 4));
        g2.fillRect(size / 8, size / 2, 3 * size / 4, size / 4);
        g2.fillRect(size / 2 - 2, size / 8, 4, size / 4);
        g2.fillRect(size / 4, size / 2 + 2, size / 2, 2);
        g2.dispose();
        return new ImageIcon(image);
    }

    private ImageIcon createDeleteIcon() {
        int size = 18;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(size / 4, size / 4, size / 2, 3 * size / 4);
        g2.fillRect(size / 8, size / 8, 3 * size / 4, size / 8);
        g2.fillRect(size / 2 - 1, 0, 2, size / 8);
        g2.dispose();
        return new ImageIcon(image);
    }

    private ImageIcon createPdfIcon() {
        int size = 18;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.drawRect(size / 8, size / 8, 6 * size / 8, 7 * size / 8);
        g2.drawLine(size / 8, size / 4, 7 * size / 8, size / 4);
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.drawString("PDF", size / 8 + 2, size - 3);
        g2.dispose();
        return new ImageIcon(image);
    }

    private JButton criarBotaoNavegacao(Icon icon) {
        JButton button = new JButton(icon);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton criarBotaoAcao(String texto, Icon icon) {
        JButton button = new JButton(texto, icon);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(60, 179, 113));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setIconTextGap(8);
        button.setModel(new DefaultButtonModel());
        return button;
    }

    private void salvarCalendarioComoPDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(LocaleManager.getString("dialog.save_pdf_title"));
        fileChooser.setSelectedFile(new File("calendario.pdf"));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }

            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();
                
                // Captura a imagem do painel principal (que contém o cabeçalho e o calendário)
                Container contentPane = this.getContentPane();
                BufferedImage image = new BufferedImage(contentPane.getWidth(), contentPane.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();
                contentPane.printAll(g2d);
                g2d.dispose();

                Image pdfImage = Image.getInstance(image, null);
                pdfImage.scaleToFit(document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin(), document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin());
                pdfImage.setAlignment(Image.ALIGN_CENTER);

                document.add(pdfImage);
                document.close();

                JOptionPane.showMessageDialog(this, LocaleManager.getString("dialog.save_pdf_success"), LocaleManager.getString("dialog.info_title"), JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, LocaleManager.getString("dialog.save_pdf_error") + ex.getMessage(), LocaleManager.getString("dialog.error_title"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void atualizarCalendario() {
        calendarioPanel.removeAll();
        mesAnoLabel.setText("");

        YearMonth anoMes = YearMonth.from(dataAtual);
        int diasNoMes = anoMes.lengthOfMonth();
        LocalDate primeiroDia = anoMes.atDay(1);
        int inicioSemana = primeiroDia.getDayOfWeek().getValue();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", LocaleManager.getCurrentLocale());
        String formattedMonthYear = anoMes.format(formatter);
        if (formattedMonthYear.length() > 0) {
            mesAnoLabel.setText(formattedMonthYear.substring(0, 1).toUpperCase(LocaleManager.getCurrentLocale()) + formattedMonthYear.substring(1));
        } else {
            mesAnoLabel.setText("");
        }

        String[] diasDaSemana = {
                LocaleManager.getString("day.monday"),
                LocaleManager.getString("day.tuesday"),
                LocaleManager.getString("day.wednesday"),
                LocaleManager.getString("day.thursday"),
                LocaleManager.getString("day.friday"),
                LocaleManager.getString("day.saturday"),
                LocaleManager.getString("day.sunday")
        };
        for (String diaSemana : diasDaSemana) {
            JLabel headerLabel = new JLabel(diaSemana, SwingConstants.CENTER);
            headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
            headerLabel.setForeground(new Color(80, 80, 80));
            calendarioPanel.add(headerLabel);
        }

        for (int i = 1; i < inicioSemana; i++) {
            calendarioPanel.add(new JLabel(""));
        }

        for (int dia = 1; dia <= diasNoMes; dia++) {
            LocalDate dataDia = anoMes.atDay(dia);
            Pessoa responsavel = calendarioService.getResponsavel(dataDia);

            JLabel diaLabel = new JLabel(
                    "<html><center><font size='+1'><b>" + dia + "</b></font><br>" + responsavel.getNome() + "</center></html>",
                    SwingConstants.CENTER
            );

            diaLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
            diaLabel.setOpaque(true);
            diaLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            diaLabel.setForeground(new Color(30, 30, 30));

            Color corDaPessoa = responsavel.getCor();
            if (corDaPessoa != null) {
                diaLabel.setBackground(corDaPessoa);
            } else {
                diaLabel.setBackground(Color.LIGHT_GRAY);
            }

            diaLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    List<Pessoa> opcoes = calendarioService.getConfiguracao().getPessoas();
                    Pessoa[] opcoesArray = opcoes.toArray(new Pessoa[0]);

                    Pessoa escolhida = (Pessoa) JOptionPane.showInputDialog(
                            CalendarioFrame.this,
                            LocaleManager.getString("dialog.select_responsible") + dataDia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            LocaleManager.getString("dialog.change_rotation"),
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            opcoesArray,
                            responsavel
                    );

                    if (escolhida != null && !escolhida.equals(responsavel)) {
                        calendarioService.substituirResponsavel(dataDia, escolhida);
                        atualizarCalendario();
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    diaLabel.setBorder(BorderFactory.createLineBorder(new Color(100, 149, 237), 2, true));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    diaLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
                }
            });

            calendarioPanel.add(diaLabel);
        }

        calendarioPanel.revalidate();
        calendarioPanel.repaint();
    }
}