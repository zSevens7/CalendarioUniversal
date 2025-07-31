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

### 🔹 Parte 2 – Lógica de revezamento ✅ (concluída)
- [x] Definir uma **data inicial configurável** para iniciar o ciclo
- [x] Alternar dia sim, dia não (revezamento entre duas pessoas)
- [x] Calcular, para qualquer data, quem está escalado
- [x] Salvar os Dados
- [x] Apagar os Dados Salvos
- [x] Fazer modificações manualmente no calendario no futuro
- [x] Criar um botao para Salvar novas mudanças


### 🔹 Parte 3 – Visual ✅ (concluída)
- [x] Mostrar o nome da pessoa escalada no dia
- [x] Aplicar cores distintas para cada pessoa
- [ ] (Opcional) Exibir imagem personalizada no dia

### 🔹 Parte 4 – Personalização ✅ (concluída)
- [x] Editar facilmente nomes, datas, ordem e lógica
- [x] Preparar para múltiplos idiomas (internacionalização)

### 🔹 Parte 5 – Melhoria
- [x] Poder salvar imagem/pdf no computador do calendario
- [ ] Fazer um executivel para rodar o programa.
- [ ] (Opcional) - Tentar melhorar o visual, adicionando skins em certo dias especiais.


---

## ✨ Recursos Adicionados Recentemente
- **Exportação para PDF:** Agora é possível salvar o calendário do mês atual como um arquivo PDF, ideal para impressão ou compartilhamento.

---

## 🛠 Tecnologias Utilizadas

- Java 8+
- Swing (interface gráfica)
- API moderna de datas (`java.time`)
- Google Gson (para JSON)
- OpenPDF (para a geração de arquivos PDF)
- Organização modular com pacotes (`model`, `service`, `ui`)

---


## 🗂️ Estrutura do Projeto

| Caminho / Arquivo                 | Descrição                                                                 |
|----------------------------------|---------------------------------------------------------------------------|
| `App.java`                       | Ponto de entrada da aplicação (classe App).                               |
| `.gitignore`                     | Lista de arquivos/pastas ignorados pelo Git.                              |
| `lib/gson-2.10.1.jar`            | Biblioteca Gson para JSON.                                                |
| `lib/openpdf-2.2.4.jar`          | Biblioteca para gerar PDFs.                                               |
| `model/Configuracao.java`       | Gerencia as configurações de data e revezamento.                          |
| `model/Pessoa.java`             | Representa uma pessoa no sistema de revezamento.                          |
| `resources/mensagens*.properties` | ResourceBundles para cada idioma (pt_BR, en, es, fr, de, it, ja, ko, nl, pl, ru, ar, zh_CN). |
| `service/LocaleManager.java`    | Classe responsável pela troca de Locale e carregamento de bundles.        |
| `service/TestLocaleManager.java`| Teste interativo para verificar carregamento de traduções.                |
| `service/CalendarioService.java`| Lógica de cálculo do revezamento.                                         |
| `service/ConfigHelper.java`     | Auxílio na leitura/escrita de configuração JSON.                          |
| `service/LocalDateAdapter.java` | Adaptador para (de)serialização de LocalDate com Gson.                    |
| `ui/CalendarioFrame.java`       | Janela principal do calendário.                                           |
| `ui/ConfiguracaoDialog.java`    | Diálogo de configuração inicial.                                          |

---


## 🧪 Como Executar
Compile os arquivos (certifique-se de que o JAR do OpenPDF também está no classpath):
```
javac -d bin -cp "lib/gson-2.10.1.jar;lib/openpdf-2.2.4.jar;resources;." App.java model*.java service*.java ui*.java
```
Execute o app:

No Windows :
```
java -cp "bin;lib/gson-2.10.1.jar;lib/openpdf-2.2.4.jar;resources" App
```

No Linux/Mac :
```
java -cp "bin:lib/gson-2.10.1.jar:lib/openpdf-2.2.4.jar:resources" App
```

---

## 🔧 Bugs Conhecidos

1. O único bug no momento é para línguas coreana, japonesa e chinesa, onde o formato de texto está incorreto e os caracteres aparecem como quadrados.

---

## 🤝 Como Contribuir
Sinta-se à vontade para sugerir melhorias ou corrigir bugs. As contribuições são bem-vindas, principalmente para resolver o problema com a renderização de caracteres em idiomas asiáticos.

---

## 👤 Autor
zSevens7
📎 [github.com/zSevens7](https://github.com/zSevens7)
