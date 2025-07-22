package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Configuracao;
// import model.Pessoa; // REMOVIDA: Esta linha não é mais necessária aqui

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate; // Importe LocalDate

import java.util.ArrayList; // Importe ArrayList para inicializar a lista

public class ConfigHelper {
    private static final String ARQUIVO_CONFIG = "data/config.json";

    // Configura Gson com suporte para LocalDate (vai precisar do LocalDateAdapter)
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(java.time.LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting()
            .create();

    public static void salvar(Configuracao config) throws IOException {
        criarPastaSeNaoExistir();
        try (FileWriter writer = new FileWriter(ARQUIVO_CONFIG)) {
            gson.toJson(config, writer);
        }
    }

    public static Configuracao carregar() throws IOException {
        File arquivo = new File(ARQUIVO_CONFIG);
        if (!arquivo.exists()) {
            // Se o arquivo não existe, retornamos null.
            // O App.java vai então solicitar os dados ao usuário.
            return null;
        }

        Configuracao configuracao = null;
        try (FileReader reader = new FileReader(arquivo)) {
            configuracao = gson.fromJson(reader, Configuracao.class);
        } catch (IOException e) {
            // Em caso de erro de leitura (ex: JSON malformado), loga e retorna null
            System.err.println("Erro ao carregar configuração: " + e.getMessage());
            // Opcional: apagar o arquivo corrompido para que um novo seja criado
            // apagar(); // Descomente se quiser apagar arquivos corrompidos automaticamente
            return null; // Retorna null para que o App.java crie uma nova config
        }

        // --- Adicionado: Verificações e valores padrão para evitar NullPointerException ---
        if (configuracao != null) {
            // Garante que a lista de pessoas não seja null. Se for null, cria uma lista vazia.
            if (configuracao.getPessoas() == null) {
                configuracao.setPessoas(new ArrayList<>());
            }
            // Garante que a lista de pessoas não esteja vazia se ela for a única do revezamento
            // (Isso é mais uma regra de negócio, pode ser ajustado)
            if (configuracao.getPessoas().isEmpty() && configuracao.getPessoas().size() == 0) { // Adicionado .size() == 0
                // Esta linha pode ser removida se você já está tratando o caso de .isEmpty()
                // ou se a lógica é garantir que configuracao.getPessoas() nunca é null.
                // A linha configuracao.setPessoas(new ArrayList<>()); já resolve o caso de null.
                // E o App.java agora trata o .isEmpty() antes de chamar o CalendarioService.
            }


            // Garante que dataInicial não seja null. Se for null, define um valor padrão.
            if (configuracao.getDataInicial() == null) {
                // Define uma data inicial padrão, por exemplo, a data atual, ou uma data fixa.
                // Usar a data atual é uma boa opção se o usuário não especificou.
                configuracao.setDataInicial(LocalDate.now());
                System.out.println("Atenção: Data inicial não encontrada ou nula no config.json. Usando a data atual como padrão.");
            }
        }
        // --- Fim das verificações adicionadas ---

        return configuracao;
    }

    public static void apagar() throws IOException {
        File arquivo = new File(ARQUIVO_CONFIG);
        if (arquivo.exists()) {
            if (arquivo.delete()) {
                System.out.println("Arquivo de configuração apagado com sucesso.");
            } else {
                System.err.println("Falha ao apagar o arquivo de configuração.");
            }
        } else {
            System.out.println("Arquivo de configuração não encontrado para apagar.");
        }
    }

    private static void criarPastaSeNaoExistir() {
        File pasta = new File("data");
        if (!pasta.exists()) {
            if (pasta.mkdirs()) {
                System.out.println("Pasta 'data' criada com sucesso.");
            } else {
                System.err.println("Falha ao criar a pasta 'data'.");
            }
        }
    }
}