package model;

import java.awt.Color; // Importar Color
import java.util.Objects;

public class Pessoa {
    private String nome;
    private Color cor; // Novo campo para armazenar a cor da pessoa

    public Pessoa(String nome) {
        this.nome = nome;
        // Definir uma cor padrão ou deixar para ser definida depois
        this.cor = Color.LIGHT_GRAY; // Cor padrão, pode ser alterada
    }

    public Pessoa(String nome, Color cor) {
        this.nome = nome;
        this.cor = cor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Color getCor() {
        return cor;
    }

    public void setCor(Color cor) {
        this.cor = cor;
    }

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pessoa pessoa = (Pessoa) o;
        return Objects.equals(nome.toLowerCase(), pessoa.nome.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome.toLowerCase());
    }
}
