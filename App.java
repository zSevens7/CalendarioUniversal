import model.Configuracao;
import model.Pessoa;
import service.CalendarioService;
import service.ConfigHelper;
import ui.CalendarioFrame;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
// import java.util.Scanner; // NÃO VAI MAIS PRECISAR AQUI SE USAR try-with-resources

public class App {
    public static void main(String[] args) {
        try {
            Configuracao configuracao = ConfigHelper.carregar();

            // Modificação AQUI: Se a configuração for null OU a lista de pessoas estiver vazia,
            // então, vamos pedir ao usuário para criar uma nova configuração.
            if (configuracao == null || configuracao.getPessoas() == null || configuracao.getPessoas().isEmpty()) {
                System.out.println("Configuração inicial não encontrada ou lista de pessoas vazia. Iniciando configuração...");

                // --- MUDANÇA AQUI: USANDO try-with-resources para o Scanner ---
                try (java.util.Scanner scanner = new java.util.Scanner(System.in)) { // Declara o Scanner dentro do try
                    List<Pessoa> pessoas = new ArrayList<>();

                    System.out.print("Quantas pessoas vão revezar? ");
                    int quantidade = Integer.parseInt(scanner.nextLine());

                    // Validação básica para a quantidade de pessoas
                    if (quantidade <= 0) {
                        // Lança uma exceção para o bloco catch tratar
                        throw new IllegalArgumentException("A quantidade de pessoas deve ser maior que zero.");
                    }

                    for (int i = 1; i <= quantidade; i++) {
                        System.out.print("Digite o nome da pessoa " + i + ": ");
                        pessoas.add(new Pessoa(scanner.nextLine()));
                    }

                    System.out.print("Digite a data inicial do revezamento (dd/MM/yyyy): ");
                    String dataStr = scanner.nextLine();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate dataInicial = LocalDate.parse(dataStr, formatter);

                    configuracao = new Configuracao(pessoas, dataInicial);

                    ConfigHelper.salvar(configuracao); // Salva a nova configuração
                } // Scanner é automaticamente fechado aqui, mesmo que ocorra uma exceção
                // --- FIM DA MUDANÇA ---

            }

            // A partir deste ponto, configuracao.getPessoas()
            // não será nulo nem vazio, e configuracao.getDataInicial() não será nulo.
            CalendarioService service = new CalendarioService(configuracao.getPessoas(), configuracao.getDataInicial());
            new CalendarioFrame(service);

        } catch (IllegalArgumentException e) {
            // Captura erros específicos de argumento inválido, como quantidade de pessoas <= 0
            System.err.println("Erro de validação: " + e.getMessage());
            System.err.println("Por favor, reinicie o aplicativo e forneça uma configuração válida.");
        } catch (Exception e) {
            // Captura outras exceções gerais
            e.printStackTrace();
        }
    }
}