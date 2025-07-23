package model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuracao {
    private List<Pessoa> pessoas;
    private LocalDate dataInicial;

    public Configuracao(List<Pessoa> pessoas, LocalDate dataInicial) {
        this.pessoas = pessoas;
        this.dataInicial = dataInicial;
    }

    // Getters e setters
    public List<Pessoa> getPessoas() {
        return pessoas;
    }

    public void setPessoas(List<Pessoa> pessoas) {
        this.pessoas = pessoas;
    }

    public LocalDate getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(LocalDate dataInicial) {
        this.dataInicial = dataInicial;
    }


    private Map<LocalDate, Pessoa> substituicoes = new HashMap<>();

    public Map<LocalDate, Pessoa> getSubstituicoes() {
    return substituicoes;
    }

    public void setSubstituicoes(Map<LocalDate, Pessoa> substituicoes) {
    this.substituicoes = substituicoes;
    }

}
