package service;

import model.Configuracao;
import model.Pessoa;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class CalendarioService {
    private Configuracao configuracao;

    public CalendarioService(List<Pessoa> pessoas, LocalDate dataInicial, Map<LocalDate, Pessoa> substituicoes) {
        this.configuracao = new Configuracao(pessoas, dataInicial);
        this.configuracao.setSubstituicoes(substituicoes);
    }

    public Pessoa getResponsavel(LocalDate data) {
        Map<LocalDate, Pessoa> substituicoes = configuracao.getSubstituicoes();
        if (substituicoes != null && substituicoes.containsKey(data)) {
            return substituicoes.get(data);
        }

        List<Pessoa> pessoas = configuracao.getPessoas();
        long diasPassados = java.time.temporal.ChronoUnit.DAYS.between(configuracao.getDataInicial(), data);
        int index = (int) (diasPassados % pessoas.size());
        if (index < 0) index += pessoas.size();
        return pessoas.get(index);
    }

    public int getIndexDaPessoa(Pessoa p) {
        return configuracao.getPessoas().indexOf(p);
    }

    public Configuracao getConfiguracao() {
        return configuracao;
    }

    public void substituirResponsavel(LocalDate data, Pessoa novaPessoa) {
        configuracao.getSubstituicoes().put(data, novaPessoa);
    }
}
