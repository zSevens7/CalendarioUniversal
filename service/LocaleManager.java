package service;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

public class LocaleManager {
    // Locale padrão (Português do Brasil)
    private static Locale currentLocale = new Locale.Builder().setLanguage("pt").setRegion("BR").build();
    // Nome base dos arquivos de propriedades (ex: mensagens_pt_BR.properties)
    private static final String BUNDLE_BASE_NAME = "mensagens";
    private static ResourceBundle bundle;

    static {
        System.out.println("DEBUG: LocaleManager - Inicializando com Locale padrão: " + currentLocale.toLanguageTag());
        loadBundle(currentLocale); // Tenta carregar o bundle inicial
    }

    /**
     * Tenta carregar o ResourceBundle para o Locale fornecido.
     * Se falhar, tenta carregar para o Locale padrão (pt-BR).
     * @param targetLocale O Locale para o qual tentar carregar o bundle.
     */
    private static void loadBundle(Locale targetLocale) {
        try {
            System.out.println("DEBUG: LocaleManager - Tentando carregar ResourceBundle para: " + targetLocale.toLanguageTag());
            bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, targetLocale);
            System.out.println("DEBUG: LocaleManager - ResourceBundle carregado com sucesso para: " + targetLocale.toLanguageTag());
        } catch (MissingResourceException e) {
            System.err.println("ERRO: LocaleManager - ResourceBundle NÃO encontrado para o locale: " + targetLocale.toLanguageTag() +
                               ". Detalhes: " + e.getMessage());
            // Se o bundle para o targetLocale não for encontrado, tenta com o locale padrão
            try {
                System.out.println("DEBUG: LocaleManager - Tentando carregar fallback ResourceBundle para pt-BR.");
                Locale defaultLocale = new Locale.Builder().setLanguage("pt").setRegion("BR").build();
                bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, defaultLocale);
                currentLocale = defaultLocale; // Define o currentLocale para o padrão
                System.out.println("DEBUG: LocaleManager - Fallback ResourceBundle carregado com sucesso para pt-BR.");
            } catch (MissingResourceException ex) {
                System.err.println("ERRO CRÍTICO: LocaleManager - ResourceBundle padrão (pt-BR) também NÃO encontrado. " +
                                   "O aplicativo pode não exibir textos corretamente. Detalhes: " + ex.getMessage());
            }
        }
    }

    /**
     * Define o Locale atual do aplicativo e recarrega o ResourceBundle.
     * @param newLocale O novo Locale a ser usado.
     */
    public static void setLocale(Locale newLocale) {
        System.out.println("DEBUG: LocaleManager - setLocale chamado. Novo Locale: " + newLocale.toLanguageTag());
        currentLocale = newLocale;
        
        // <<<----- LINHA ADICIONADA AQUI -----<<<
        // Limpa o cache para garantir que o novo arquivo de propriedades seja lido do disco.
        ResourceBundle.clearCache(); 
        
        loadBundle(newLocale); // Tenta carregar o bundle para o novo Locale
    }

    /**
     * Retorna o Locale atualmente em uso.
     * @return O Locale atual.
     */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Retorna a string traduzida para a chave fornecida.
     * @param key A chave da string no arquivo de propriedades.
     * @return A string traduzida.
     */
    public static String getString(String key) {
        try {
            if (bundle == null) {
                 // Garante que o bundle não seja nulo, caso o carregamento inicial tenha falhado.
                loadBundle(currentLocale);
            }
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            System.err.println("ERRO: LocaleManager - Chave de tradução NÃO encontrada para o locale " + currentLocale.toLanguageTag() + ": " + key);
            return "MISSING_KEY_" + key; // Retorna uma string de erro para depuração
        }
    }
}
