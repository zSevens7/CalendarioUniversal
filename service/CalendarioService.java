package service;

import model.Pessoa;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CalendarioService {
    private final List<Pessoa> pessoas;
    private final LocalDate dataInicial;

    public CalendarioService(List<Pessoa> pessoas, LocalDate dataInicial) {
        if (pessoas == null || pessoas.isEmpty()) {
            throw new IllegalArgumentException("A lista de pessoas não pode estar vazia.");
        }
        this.pessoas = pessoas;
        this.dataInicial = dataInicial;
    }

    public Pessoa getResponsavel(LocalDate dataConsulta) {
        long diasPassados = ChronoUnit.DAYS.between(dataInicial, dataConsulta);

        int index = (int) (diasPassados % pessoas.size());
        if (index < 0) index += pessoas.size(); // Para datas anteriores à data inicial

        return pessoas.get(index);
    }

    public int getIndexDaPessoa(Pessoa pessoa) {
        return pessoas.indexOf(pessoa);
    }
}
