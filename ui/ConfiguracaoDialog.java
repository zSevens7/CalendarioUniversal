package ui;

import model.Configuracao;
import model.Pessoa;
import service.LocaleManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

public class ConfiguracaoDialog extends JDialog {
    private List<Pessoa> pessoas;
    private LocalDate dataInicial;
    private boolean confirmado = false;

    private JTextField qtdPessoasField;
    private JFormattedTextField dataInicialField;
    private JPanel nomesPanel;
    private JScrollPane nomesScrollPane;
    private JComboBox<String> idiomaComboBox;

    // Campos da classe para os JLabels e JButtons
    private JLabel titleLabel;
    private JLabel qtdPessoasLabel;
    private JLabel dataInicialLabel;
    private JButton gerarCamposBtn;
    private JButton confirmarBtn;
    private JButton cancelarBtn;

    // Mapa de chaves de cor para objetos Color
    private final Map<String, Color> coresMap = new LinkedHashMap<>();
    // Mapa auxiliar para mapear nomes traduzidos de volta para chaves de cor
    private final Map<String, String> translatedColorNameToKeyMap = new LinkedHashMap<>();

    // Mapeia o nome traduzido do idioma para o objeto Locale (AGORA CAMPO DA CLASSE)
    private Map<String, Locale> localeMap;


    public ConfiguracaoDialog(JFrame parent) {
        super(parent, LocaleManager.getString("config.title"), true);
        setSize(700, 750);
        setLocationRelativeTo(parent);
        setResizable(true);

        coresMap.put("light_blue", new Color(173, 216, 230));
        coresMap.put("light_pink", new Color(255, 182, 193));
        coresMap.put("light_green", new Color(144, 238, 144));
        coresMap.put("peach", new Color(255, 223, 186));
        coresMap.put("plum", new Color(221, 160, 221));
        coresMap.put("light_steel_blue", new Color(176, 196, 222));
        coresMap.put("soft_yellow", new Color(255, 255, 204));
        coresMap.put("lavender", new Color(230, 230, 250));
        coresMap.put("light_cyan", new Color(224, 255, 255));
        coresMap.put("light_salmon", new Color(255, 160, 122));

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        mainPanel.setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        titleLabel = new JLabel(LocaleManager.getString("config.title"), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(40, 40, 40));
        
        // --- Lógica para o seletor de idioma com nomes traduzidos ---
        // Inicializa o localeMap e o idiomaComboBox no construtor
        inicializarIdiomaComboBox(); // CHAMADA AO NOVO MÉTODO

        idiomaComboBox.addActionListener(e -> {
            String selectedTranslatedName = (String) idiomaComboBox.getSelectedItem();
            Locale newLocale = localeMap.get(selectedTranslatedName);
            if (newLocale != null) {
                LocaleManager.setLocale(newLocale);
                atualizarTextosUI();
            }
        });
        // --- Fim da lógica do seletor de idioma ---

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(idiomaComboBox, gbc);

        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 8, 10, 8);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        qtdPessoasLabel = new JLabel(LocaleManager.getString("config.quantity_label"));
        qtdPessoasLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(qtdPessoasLabel, gbc);

        qtdPessoasField = new JTextField(8);
        qtdPessoasField.setFont(new Font("Arial", Font.PLAIN, 15));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(qtdPessoasField, gbc);

        dataInicialLabel = new JLabel(LocaleManager.getString("config.date_label"));
        dataInicialLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(dataInicialLabel, gbc);

        try {
            MaskFormatter dateFormatter = new MaskFormatter("##/##/####");
            dateFormatter.setPlaceholderCharacter('_');
            dataInicialField = new JFormattedTextField(dateFormatter);
            dataInicialField.setColumns(12);
            dataInicialField.setFont(new Font("Arial", Font.PLAIN, 15));
            dataInicialField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } catch (ParseException e) {
            e.printStackTrace();
            dataInicialField = new JFormattedTextField();
            JOptionPane.showMessageDialog(this, LocaleManager.getString("dialog.config_error") + " " + LocaleManager.getString("dialog.error_creating_mask"), LocaleManager.getString("dialog.error_title"), JOptionPane.ERROR_MESSAGE);
        }
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(dataInicialField, gbc);

        gerarCamposBtn = criarBotaoAcao(LocaleManager.getString("config.generate_fields_button"));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 8, 20, 8);
        mainPanel.add(gerarCamposBtn, gbc);

        nomesPanel = new JPanel();
        nomesPanel.setLayout(new BoxLayout(nomesPanel, BoxLayout.Y_AXIS));
        nomesPanel.setBackground(new Color(255, 255, 255));
        nomesPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));

        nomesScrollPane = new JScrollPane(nomesPanel);
        nomesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        nomesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        nomesScrollPane.setPreferredSize(new Dimension(550, 250));
        nomesScrollPane.setBorder(BorderFactory.createEmptyBorder());

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(nomesScrollPane, gbc);

        JPanel botoesAcaoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        botoesAcaoPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        confirmarBtn = criarBotaoAcao(LocaleManager.getString("config.confirm_button"));
        cancelarBtn = criarBotaoAcao(LocaleManager.getString("config.cancel_button"));

        botoesAcaoPanel.add(confirmarBtn);
        botoesAcaoPanel.add(cancelarBtn);

        add(mainPanel, BorderLayout.CENTER);
        add(botoesAcaoPanel, BorderLayout.SOUTH);

        gerarCamposBtn.addActionListener(e -> {
            gerarCamposDeNomesECores();
        });

        confirmarBtn.addActionListener(e -> {
            validarEConfirmar();
        });

        cancelarBtn.addActionListener(e -> {
            confirmado = false;
            dispose();
        });
    }

    // NOVO MÉTODO: Inicializa e popula o JComboBox de idioma
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

        // Se o idiomaComboBox ainda não foi criado, cria. Caso contrário, apenas atualiza o modelo.
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
    System.out.println("Atualizando textos da UI para o idioma: " + LocaleManager.getCurrentLocale());
    
    setTitle(LocaleManager.getString("config.title"));
    titleLabel.setText(LocaleManager.getString("config.title"));
    qtdPessoasLabel.setText(LocaleManager.getString("config.quantity_label"));
    dataInicialLabel.setText(LocaleManager.getString("config.date_label"));
    gerarCamposBtn.setText(LocaleManager.getString("config.generate_fields_button"));
    confirmarBtn.setText(LocaleManager.getString("config.confirm_button"));
    cancelarBtn.setText(LocaleManager.getString("config.cancel_button"));
    
    // Atualiza os nomes traduzidos do combo de idiomas
    inicializarIdiomaComboBox();
    
    // Se o campo quantidade de pessoas não está vazio, gera novamente os campos de nomes e cores
    if (!qtdPessoasField.getText().isEmpty()) {
        gerarCamposDeNomesECores();
    }

    revalidate();
    repaint();
}

    class ColorComboBoxRenderer extends JPanel implements ListCellRenderer<String> {
        private JLabel textLabel;
        private JPanel colorPanel;

        public ColorComboBoxRenderer() {
            super(new BorderLayout(5, 0));
            textLabel = new JLabel();
            colorPanel = new JPanel();
            colorPanel.setPreferredSize(new Dimension(20, 15));
            colorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            add(colorPanel, BorderLayout.WEST);
            add(textLabel, BorderLayout.CENTER);
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
            textLabel.setText(value);
            String colorKey = translatedColorNameToKeyMap.get(value);
            Color color = coresMap.get(colorKey);
            
            if (color != null) {
                colorPanel.setBackground(color);
            } else {
                colorPanel.setBackground(Color.WHITE);
            }

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            return this;
        }
    }

    private JButton criarBotaoAcao(String texto) {
        JButton button = new JButton(texto);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void gerarCamposDeNomesECores() {
        nomesPanel.removeAll();
        translatedColorNameToKeyMap.clear();

        try {
            int qtd = Integer.parseInt(qtdPessoasField.getText());
            if (qtd <= 0) {
                JOptionPane.showMessageDialog(this, LocaleManager.getString("dialog.input_error_quantity"), LocaleManager.getString("dialog.error_title"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<String> translatedColorNames = new ArrayList<>();
            for (Map.Entry<String, Color> entry : coresMap.entrySet()) {
                String key = entry.getKey();
                String translatedName = LocaleManager.getString("color." + key);
                translatedColorNames.add(translatedName);
                translatedColorNameToKeyMap.put(translatedName, key);
            }
            String[] nomesDasCoresTraduzidos = translatedColorNames.toArray(new String[0]);


            for (int i = 1; i <= qtd; i++) {
                JPanel pessoaEntryPanel = new JPanel(new GridBagLayout());
                pessoaEntryPanel.setBorder(new EmptyBorder(8, 0, 8, 0));
                pessoaEntryPanel.setBackground(nomesPanel.getBackground());

                GridBagConstraints innerGbc = new GridBagConstraints();
                innerGbc.insets = new Insets(0, 5, 0, 5);
                innerGbc.anchor = GridBagConstraints.WEST;

                JLabel nomeLabel = new JLabel(LocaleManager.getString("config.person_name_label") + " " + i + ":");
                nomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                innerGbc.gridx = 0;
                innerGbc.gridy = 0;
                pessoaEntryPanel.add(nomeLabel, innerGbc);

                JTextField nomeField = new JTextField(25);
                nomeField.setFont(new Font("Arial", Font.PLAIN, 14));
                nomeField.setName("nomeField" + i);
                innerGbc.gridx = 1;
                innerGbc.weightx = 1.0;
                innerGbc.fill = GridBagConstraints.HORIZONTAL;
                pessoaEntryPanel.add(nomeField, innerGbc);

                JLabel corLabel = new JLabel(LocaleManager.getString("config.color_label"));
                corLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                innerGbc.gridx = 2;
                innerGbc.weightx = 0;
                innerGbc.fill = GridBagConstraints.NONE;
                pessoaEntryPanel.add(corLabel, innerGbc);

                JComboBox<String> corComboBox = new JComboBox<>(nomesDasCoresTraduzidos);
                corComboBox.setRenderer(new ColorComboBoxRenderer());
                corComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
                corComboBox.setName("corComboBox" + i);
                corComboBox.setPreferredSize(new Dimension(150, 25));
                
                int corIndex = (i - 1) % coresMap.size();
                String defaultColorKey = (String) coresMap.keySet().toArray()[corIndex];
                String defaultTranslatedColorName = LocaleManager.getString("color." + defaultColorKey);
                corComboBox.setSelectedItem(defaultTranslatedColorName);


                innerGbc.gridx = 3;
                innerGbc.weightx = 0;
                pessoaEntryPanel.add(corComboBox, innerGbc);
                
                nomesPanel.add(pessoaEntryPanel);
            }
            nomesPanel.revalidate();
            nomesPanel.repaint();
            nomesScrollPane.revalidate();
            nomesScrollPane.repaint();
            revalidate();
            repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, LocaleManager.getString("dialog.input_error_quantity"), LocaleManager.getString("dialog.error_title"), JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private void validarEConfirmar() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dataInicial = LocalDate.parse(dataInicialField.getText(), formatter);

            pessoas = new ArrayList<>();
            Component[] components = nomesPanel.getComponents();
            boolean algumNomeVazio = false;
            
            for (int i = 0; i < components.length; i++) {
                Component comp = components[i];
                if (comp instanceof JPanel pessoaEntryPanel) {
                    JTextField nomeField = null;
                    JComboBox<String> corComboBox = null;

                    for (Component subComp : pessoaEntryPanel.getComponents()) {
                        if (subComp instanceof JTextField && subComp.getName() != null && subComp.getName().startsWith("nomeField")) {
                            nomeField = (JTextField) subComp;
                        } else if (subComp instanceof JComboBox && subComp.getName() != null && subComp.getName().startsWith("corComboBox")) {
                            corComboBox = (JComboBox<String>) subComp;
                        }
                    }

                    if (nomeField != null && corComboBox != null) {
                        String nome = nomeField.getText().trim();
                        String translatedColorName = (String) corComboBox.getSelectedItem();
                        String colorKey = translatedColorNameToKeyMap.get(translatedColorName);
                        Color corSelecionada = coresMap.get(colorKey);

                        if (nome.isEmpty()) {
                            algumNomeVazio = true;
                        }
                        pessoas.add(new Pessoa(nome, corSelecionada));
                    }
                }
            }

            if (pessoas.isEmpty()) {
                JOptionPane.showMessageDialog(this, LocaleManager.getString("dialog.no_names_generated"), LocaleManager.getString("dialog.error_title"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (algumNomeVazio) {
                JOptionPane.showMessageDialog(this, LocaleManager.getString("dialog.empty_names"), LocaleManager.getString("dialog.error_title"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            confirmado = true;
            dispose();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, LocaleManager.getString("dialog.input_error_date"), LocaleManager.getString("dialog.error_title"), JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, LocaleManager.getString("dialog.config_error") + ex.getMessage(), LocaleManager.getString("dialog.error_title"), JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public Configuracao getConfiguracao() {
        return new Configuracao(pessoas, dataInicial);
    }


    
}
