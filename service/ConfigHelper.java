package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import model.Configuracao;
// import model.Pessoa; // Removido: Esta importação não é utilizada diretamente aqui
import java.awt.Color; // Importar Color

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.lang.reflect.Type; // Importar Type para o TypeAdapter

import java.util.ArrayList;

public class ConfigHelper {
    private static final String ARQUIVO_CONFIG = "data/config.json";

    // Configura Gson com suporte para LocalDate e Color
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(Color.class, new ColorTypeAdapter()) // Registrar o ColorTypeAdapter
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
            return null;
        }

        Configuracao configuracao = null;
        try (FileReader reader = new FileReader(arquivo)) {
            configuracao = gson.fromJson(reader, Configuracao.class);
        } catch (IOException e) {
            System.err.println("Erro ao carregar configuração: " + e.getMessage());
            // Opcional: apagar o arquivo corrompido para que um novo seja criado
            // apagar();
            return null;
        } catch (JsonParseException e) { // Adicionado catch para JsonParseException
            System.err.println("Erro de parsing JSON ao carregar configuração: " + e.getMessage());
            // Opcional: apagar o arquivo corrompido para que um novo seja criado
            // apagar();
            return null;
        }

        // --- Adicionado: Verificações e valores padrão para evitar NullPointerException ---
        if (configuracao != null) {
            if (configuracao.getPessoas() == null) {
                configuracao.setPessoas(new ArrayList<>());
            }
            // A verificação de isEmpty() é feita no App.java antes de chamar o CalendarioService.

            if (configuracao.getDataInicial() == null) {
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

    // --- Classe interna para o TypeAdapter de Color ---
    private static class ColorTypeAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {
        @Override
        public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
            // Converte Color para uma string hexadecimal (ex: #RRGGBB)
            String hex = String.format("#%02x%02x%02x", src.getRed(), src.getGreen(), src.getBlue());
            return new JsonPrimitive(hex);
        }

        @Override
        public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            // Converte a string hexadecimal de volta para Color
            String hex = json.getAsString();
            try {
                // Remove o '#' e converte para int
                return Color.decode(hex);
            } catch (NumberFormatException e) {
                throw new JsonParseException("Cor inválida: " + hex, e);
            }
        }
    }
}
