# Trabalho Prático 2 – AEDs III — Relatório

## Integrantes
- [Joshua Victor Costa e Pereira](https://github.com/Joshua-victor)
- [Gabriel Filipe Lanza Candido](https://github.com/biellanzac)
- [Pedro Henrique Pereira de Alexandria](https://github.com/AlexandriaPedro)

---

## Descrição (TP2 — Presente Fácil 2.0)

Este trabalho amplia o TP2 implementando um Índice Invertido para suportar buscas textuais (por palavras-chave) em nomes de produtos. Os resultados são obrigatoriamente ordenados por relevância utilizando o algoritmo TFxIDF (Term Frequency - Inverse Document Frequency).

**Funcionalidades Adicionadas/AlteradasÍndice Invertido:**
- Criado e mantido pela classe ListaInvertida (baseada em arquivos de dicionário e blocos) para mapear Termo → Lista de IDs de Produtos.
- Processamento de Termos: Introdução da classe TermoUtils para normalizar (minúsculas, sem acentos) e filtrar stop words da consulta e dos nomes dos produtos.
- Métricas TF/IDF:O valor TF (Term Frequency) é calculado e armazenado no índice para cada termo/produto.O valor IDF (Inverse Document Frequency) é calculado dinamicamente durante a busca, usando o contador total de produtos ($N$) da ListaInvertida.
- Busca por Relevância: A nova opção de busca processa a query, acumula o score TF × IDF para cada produto e retorna os IDs ordenados de forma decrescente por esse score.
- Manutenção Automática: Os métodos create, update (se o nome mudar) e delete em ArquivoProduto foram alterados para manter o Índice Invertido consistente e o contador de entidades atualizado.

---

## Como compilar e executar

### Windows (CMD/PowerShell)
```bat
chcp 65001
javac -encoding UTF-8 -d out aed3\*.java
javac -encoding UTF-8 -cp out -d out Menu\*.java
java -cp out Principal
```
> Se `Principal.java` estiver com `package Menu;`, use: `java -cp out Menu.Principal`.

### Linux/macOS
```bash
find aed3 -name "*.java" > s1.txt
find Menu -name "*.java" > s2.txt
javac -encoding UTF-8 -d out @s1.txt
javac -encoding UTF-8 -cp out -d out @s2.txt
java -cp out Principal
```

---

## Fluxo de Teste (roteiro rápido)

1. **Produtos → Cadastrar**
   - Cadastrar produtos com nomes que se sobrepõem (ex: "Copo de Vidro", "Vasilha de Vidro verde").
   
2. **Menu Principal**
   - Verificar se a busca ignora o 'de' e soma os scores de 'copo' e 'vidro'.    - Os resultados devem ser exibidos ordenados pelo maior score TFxIDF
3. **Produtos → Buscar por GTIN-13**
   - Informar um GTIN cadastrado → deve trazer o produto.
4. **Produtos → Ficha**
   - Ver **Minhas listas** (ordenadas) onde o produto aparece.
   - Ver **Contagem** de aparições em listas de **outros usuários**.
5. **Minhas listas → Incluir nova lista** → **Acrescentar produto**
   -Realizar busca por palavras e verificar se a lista de seleção também aparece ordenada por relevância.

---


## Validações e Regras

- TFxIDF: Implementado no método ArquivoProduto.searchByTerms(query), que calcula o score e ordena a lista de IDs antes de retornar.
- Normalização: Utiliza TermoUtils para garantir que o índice e a busca sejam case-insensitive e accent-insensitive.
- Stop Words: Palavras como "de", "o", "a", etc., são removidas da indexação e das consultas.

---


## Checklist

- [x] **O índice invertido com os termos dos nomes dos produts foi criado usando a classe ListaInvertida**  
  **SIM.**

- [x] **É possível buscar produtos por palavras no menu de manutenção de produtos?**  
  **SIM.**

- [x] **É possível buscar produtos por palavras na hora de acrescentá-los às listas dos usuários?**  
  **SIM.**

- [x] **O trabalho compila corretamente?**  
  **SIM.** Comandos incluídos acima.

- [x] **O trabalho está completo e funcionando sem erros de execução?**  
  **SIM.**

- [x] **O trabalho é original e não a cópia de um trabalho de outro grupo?**  
  **SIM.**

---

## Link do Vídeo (até 3 minutos)
- `https://linkDoVideo`

---

## FIM
