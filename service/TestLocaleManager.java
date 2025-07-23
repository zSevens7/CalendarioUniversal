package service;




import java.util.Locale;
import java.util.Scanner;

public class TestLocaleManager {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("Idioma atual: " + LocaleManager.getCurrentLocale());
        System.out.println("Título atual: " + LocaleManager.getString("config.title"));
        System.out.println("Botão confirmar atual: " + LocaleManager.getString("config.confirm_button"));
        System.out.println();

        while (true) {
            System.out.println("Digite o código do idioma para trocar (ex: pt_BR, en, de, es, fr, ko) ou 'sair' para encerrar:");
            input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("sair")) {
                break;
            }

            Locale newLocale;

            switch (input.toLowerCase()) {
                case "pt_br":
                    newLocale = new Locale.Builder().setLanguage("pt").setRegion("BR").build();
                    break;
                case "en":
                    newLocale = Locale.ENGLISH;
                    break;
                case "de":
                    newLocale = Locale.GERMAN;
                    break;
                case "es":
                    newLocale = new Locale.Builder().setLanguage("es").build();
                    break;
                case "fr":
                    newLocale = Locale.FRENCH;
                    break;
                case "ko":
                    newLocale = Locale.KOREAN;
                    break;
                default:
                    System.out.println("Idioma não suportado. Tente novamente.");
                    continue;
            }

            LocaleManager.setLocale(newLocale);
            System.out.println("Idioma alterado para: " + LocaleManager.getCurrentLocale());
            System.out.println("Título: " + LocaleManager.getString("config.title"));
            System.out.println("Botão confirmar: " + LocaleManager.getString("config.confirm_button"));
            System.out.println();
        }

        scanner.close();
        System.out.println("Teste encerrado.");
    }
}
