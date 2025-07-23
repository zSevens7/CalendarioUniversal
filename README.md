# 📅 Calendário Universal – Java Swing

Este é um pequeno projeto pessoal desenvolvido com Java e Swing, com o objetivo de:

- Aprender Java na prática, especialmente a parte gráfica (Swing)
- Criar uma aplicação útil e reutilizável para gerenciar **revezamentos diários**
- Construir um sistema baseado em **datas fixas** que funcione em qualquer contexto de revezamento, como turnos, plantões ou cuidados com familiares

> A lógica é pensada de forma universal, **sem dados privados ou sensíveis**, podendo ser adaptada para diferentes famílias ou ambientes.

---

## 🎯 Objetivo

O projeto foi inspirado em um caso real: o revezamento entre duas pessoas (ex: pais) para dormir na casa de um parente. A aplicação calcula automaticamente quem está responsável a cada dia, de forma justa, alternando dias a partir de uma **data inicial fixa configurável**.

---

## 🚀 Etapas do Projeto em Java Swing

Vamos dividir em partes, como um mini-projeto modular e evolutivo:

### 🔹 Parte 1 – Estrutura base ✅ (concluída)
- [x] Criar uma janela Swing
- [x] Mostrar mês atual
- [x] Botões de avançar e voltar mês
- [x] Mostrar os dias em formato de grade (7 colunas)

### 🔹 Parte 2 – Lógica de revezamento (em andamento)
- [x] Definir uma **data inicial configurável** para iniciar o ciclo
- [x] Alternar dia sim, dia não (revezamento entre duas pessoas)
- [x] Calcular, para qualquer data, quem está escalado
- [x] Salvar os Dados
- [ ] Apagar os Dados Salvos
- [ ] Fazer modificações manualmente no calendario no futuro
- [ ] Criar um botao para Salvar novas mudanças


### 🔹 Parte 3 – Visual
- [ ] Mostrar o nome da pessoa escalada no dia
- [ ] Aplicar cores distintas para cada pessoa
- [ ] (Opcional) Exibir imagem personalizada no dia

### 🔹 Parte 4 – Personalização
- [ ] Editar facilmente nomes, datas, ordem e lógica
- [ ] Preparar para múltiplos idiomas (internacionalização)

---

## 🛠 Tecnologias Utilizadas

- Java 8+
- Swing (interface gráfica)
- API moderna de datas (`java.time`)
- Organização modular com pacotes (`model`, `service`, `ui`)

---


## 🗂️ Estrutura do Projeto

Este projeto segue uma estrutura modular padrão para aplicações Java Swing, organizada da seguinte forma:

| Caminho/Arquivo | Descrição |
| :------------------------ | :--------------------------------------------------------------------- |
| `CalendarioUniversal/`    | Diretório raiz do projeto.                                             |
| ├── `App.java`            | Ponto de entrada principal da aplicação.                               |
| ├── `.gitignore`          | Define arquivos e pastas a serem ignorados pelo Git.                   |
| ├── `data/`               | Armazena dados de configuração da aplicação.                           |
| │   └── `config.json`     | Arquivo de configuração em formato JSON.                               |
| ├── `lib/`                | Contém bibliotecas externas (JARs) necessárias para o projeto.         |
| │   └── `gson-2.10.1.jar` | Biblioteca Gson para manipulação de JSON.                              |
| ├── `model/`              | Define os modelos de dados e entidades do projeto.                     |
| │   ├── `Configuracao.java`| Classe para gerenciar configurações da aplicação.                     |
| │   └── `Pessoa.java`     | Representa uma pessoa no sistema de revezamento.                       |
| ├── `service/`            | Contém a lógica de negócio e os serviços da aplicação.                 |
| │   ├── `CalendarioService.java`| Gerencia a lógica de cálculo dos revezamentos.                 |
| │   ├── `ConfigHelper.java`| Classe auxiliar para manipulação de configurações.                   |
| │   └── `LocalDateAdapter.java`| Adaptador para serialização/desserialização de `LocalDate`.       |
| └── `ui/`                 | Contém as classes da interface gráfica (Java Swing).                   |
|     └── `CalendarioFrame.java`| A janela principal do calendário.                                  |

---

## 🧪 Como Executar
Compile os arquivos:
```
javac App.java ui/CalendarioFrame.java
```
Execute o app:

No Windows :
```
java -cp "lib/gson-2.10.1.jar;." App
```

No Linux/Mac :
```
java -cp "lib/gson-2.10.1.jar:." App
```

---

## 🔧 Futuras Melhorias
1. Adicionar persistência (salvar configuração em arquivo) -> _esta sendo produzindo no momento_

2. Criar empacotamento em .jar executável

3. Exportar calendário como imagem ou PDF

4. Adicionar suporte para mais de duas pessoas no revezamento -> _Foi Feito_

5. Suporte para feriados, finais de semana ou revezamentos semanais

## 👤 Autor
zSevens7
📎 [github.com/zSevens7](https://github.com/zSevens7)
