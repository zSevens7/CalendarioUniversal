package service;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken; // Importar JsonToken
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE; // Formato padrão: YYYY-MM-DD

    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        if (value == null) {
            out.nullValue(); // Escreve null se o valor for null
        } else {
            out.value(value.format(formatter)); // Escreve a data como string
        }
    }

    @Override
    public LocalDate read(JsonReader in) throws IOException {
        // --- ADIÇÃO CRUCIAL AQUI ---
        if (in.peek() == JsonToken.NULL) { // Verifica se o próximo token é NULL
            in.nextNull(); // Consome o token NULL
            return null;   // Retorna null para o LocalDate
        }
        // --- FIM DA ADIÇÃO ---

        String dateString = in.nextString(); // Continua a ler como String se não for NULL
        return LocalDate.parse(dateString, formatter);
    }
}