# Trabalho Prático 2 – AEDs III — Relatório

## Integrantes
- [Joshua Victor Costa e Pereira](https://github.com/Joshua-victor)
- [Gabriel Filipe Lanza Candido](https://github.com/biellanzac)
- [Pedro Henrique Pereira de Alexandria](https://github.com/AlexandriaPedro)

---

## Descrição (TP2 — Presente Fácil 2.0)

Este TP amplia o TP1 com a entidade **Produto** (identificado por **GTIN-13**) e o relacionamento **N:N** entre **Lista** e **Produto** via entidade **ListaProduto** (campos: `id`, `idLista`, `idProduto`, `quantidade`, `observacoes`).  
Foram implementados **índices** para sustentar as consultas:

- **Hash Extensível**: `GTIN-13 → idProduto` (garante **unicidade** e **busca direta por GTIN-13**).
- **Duas Árvores B+** (índices indiretos):
  - `idLista → idListaProduto` (quais itens uma lista possui),
  - `idProduto → idListaProduto` (em quais listas um produto aparece).

**Principais funcionalidades novas:**
- CRUD de **Produto** (GTIN-13 único, nome, descrição, ativo/inativo).
- **Buscar Produto por GTIN-13**.
- **Listar Produtos** em **ordem alfabética**, com **10 por página**.
- **Ficha do Produto**: exibe as **minhas listas** (ordenadas) onde ele aparece e a **contagem de listas de outros usuários** onde está presente.
- **Gerenciar produtos da lista**: listar itens `Nome (xquantidade)`, **alterar quantidade**, **alterar observações**, **remover item**.
- **Acrescentar produto à lista**: por **GTIN** ou **listando apenas os ATIVOS** (10/página).
- **Cascade** ao excluir/desativar **Lista**: remove as associações `ListaProduto` antes de apagar a lista.

---

## Estrutura do Projeto

```
/CRUD
  /aed3
    Arquivo.java
    HashExtensivel.java
    ArvoreBMais.java
    ParGtinID.java
    ParIDListaIDListaProduto.java
    ParIDProdutoIDListaProduto.java
    ... (outros utilitários de índice/registro)
  /Menu
    Principal.java
    Usuario.java              / ArquivoUsuario.java
    Lista.java                / ArquivoLista.java
    Produto.java              / ArquivoProduto.java
    ListaProduto.java         / ArquivoListaProduto.java
    MenuUsuario.java / MenuLista.java / MenuProduto.java
  /dados
    /usuarios
    /listas
    /produtos
    /listas_produtos
```
> Observação: arquivos do diretório `Menu/` estão no **pacote padrão** (sem `package`), compatível com os comandos de compilação abaixo.

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
   - GTIN-13 exemplos: `7890000000017`, `9780000000019`, `4000000000013`.
   - Tente cadastrar **GTIN repetido** → deve bloquear (unicidade no hash).
2. **Produtos → Listar**
   - Conferir **ordem alfabética** e **paginação (10 por página)**.
3. **Produtos → Buscar por GTIN-13**
   - Informar um GTIN cadastrado → deve trazer o produto.
4. **Produtos → Ficha**
   - Ver **Minhas listas** (ordenadas) onde o produto aparece.
   - Ver **Contagem** de aparições em listas de **outros usuários**.
5. **Minhas listas → Incluir nova lista** → **Acrescentar produto**
   - Por **GTIN** e por **Listar ATIVOS** (10/página).
   - Produto **inativo** não aparece e **não** pode ser adicionado.
6. **Minhas listas → Gerenciar produtos**s
   - **Alterar quantidade**, **Alterar observações**, **Remover** item.
7. **Minhas listas → Desativar/Excluir lista**
   - Verificar **cascade**: associações N:N removidas antes da exclusão.

---

## Validações e Regras

- **GTIN-13 (formato)**: aceitamos exatamente **13 dígitos** (`\d{13}`).
- **GTIN-13 (unicidade)**: garantida via `HashExtensivel` (`GTIN → idProduto`).
- **Produto inativo**:
  - Não aparece na listagem de inclusão,
  - Não pode ser adicionado por GTIN.
- **Quantidade mínima**: `>= 1` ao adicionar/alterar.
- **Cascade em Lista**: exclusão/desativação de uma **Lista** remove previamente todas as `ListaProduto` associadas.

---

## Evidências/prints

1. **Cadastro de Produto** — `docs/01_cadastro_produto.png`  
2. **Listagem de Produtos (10/página)** — `docs/02_listagem_produtos.png`  
3. **Busca por GTIN-13** — `docs/03_busca_gtin.png`  
4. **Ficha do Produto (minhas listas + contagem de outras)** — `docs/04_ficha_produto.png`  
5. **Acrescentar Produto à Lista (listar ATIVOS)** — `docs/05_add_produto_lista.png`  
6. **Gerenciar itens da Lista (alterar/remover)** — `docs/06_gerenciar_itens.png`  
7. **Exclusão de Lista com Cascade** — `docs/07_excluir_lista_cascade.png`

---

## Checklist

- [x] **O índice invertido com os termos dos nomes dos produts foi criado usando a classe ListaInvertida**  
  **SIM.** `ArquivoProduto` estende `Arquivo` e usa `HashExtensivel (GTIN→id)`; listagem ordenada e busca por GTIN implementadas.

- [x] **Há um CRUD da entidade de associação ListaProduto** (que **estende a classe ArquivoIndexado**, acrescentando **Tabelas Hash Extensíveis** e **Árvores B+** como índices diretos e indiretos conforme necessidade) **que funciona corretamente?**  
  **SIM.** `ArquivoListaProduto` estende `Arquivo`; índices indiretos via **duas B+** (`idLista→idListaProduto` e `idProduto→idListaProduto`).

- [x] **A visão de produtos** está corretamente implementada e permite **consultas às listas em que o produto aparece** (**apenas a quantidade no caso de lista de outras pessoas)?**  
  **SIM.** Ficha do produto mostra **minhas listas** (ordenadas) e **contagem** em listas de outros usuários.

- [x] **A visão de listas** funciona corretamente e permite a **gestão dos produtos na lista?**  
  **SIM.** Gerenciamento: listar itens `Nome (xquantidade)`, **alterar quantidade**, **alterar observações**, **remover**.

- [x] **A integridade do relacionamento** entre listas e produtos está mantida em **todas as operações?**  
  **SIM.** Índices B+ atualizados em create/update/delete; ao **excluir/desativar lista**, ocorre **cascade** das associações.

- [x] **O trabalho compila corretamente?**  
  **SIM.** Comandos incluídos acima.

- [x] **O trabalho está completo e funcionando sem erros de execução?**  
  **SIM.**

- [x] **O trabalho é original e não a cópia de um trabalho de outro grupo?**  
  **SIM.**

---

## Link do Vídeo (até 3 minutos)
- `https://youtu.be/hTK_ZYqcJEs`

---

## FIM
