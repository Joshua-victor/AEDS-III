# Trabalho Prático 4 – AEDs III — Relatório

## Integrantes
- [Joshua Victor Costa e Pereira](https://github.com/Joshua-victor)
- [Gabriel Filipe Lanza Candido](https://github.com/biellanzac)
- [Pedro Henrique Pereira de Alexandria](https://github.com/AlexandriaPedro)

---

## Descrição (TP4 — Projeto web do CRUD de Produtos)

O Trabalho Prático 4 (TP4) da disciplina Algoritmos e Estruturas de Dados III consistiu no desenvolvimento de uma página web com o objetivo extensionista de facilitar a compreensão das estruturas de dados (emulando um arquivo) para futuros alunos do curso de Ciência da Computação.

A aplicação oferece uma visualização interativa das operações básicas de manipulação de dados, que são: Inclusão (Create), Busca (Read), Alteração (Update) e Exclusão (Delete) de "produtos".

**Operações Especiais Implementadas:**
- Visualização Dinâmica: [Vizualização de tabelas com produtos inseridos, permitindo que os mesmos possam sofrer alterações/exclusões].
-Inclusão (Create): [Produto com GTIN pode ser inserido permitindo adicionar o nome do protudo juntamente com sua descrição].
- Busca (Read): [Permitindo fazer busca por nomes ou GTIN a tabela é atualizada através do desejo de busca do usuario].
- Alteração (Update): [Selecionando o item o programa permite você alterar dados do item].
- Exclusão (Delete): [selecionando o item o programa permite você excluir os dados do item].

---<img width="697" height="687" alt="image" src="https://github.com/user-attachments/assets/b2f073fc-98b5-49ac-b7c4-e5354cb33e8a" />


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

- [x] **Há um CRUD de produtos** (que **estende a classe ArquivoIndexado**, acrescentando **Tabelas Hash Extensíveis** e **Árvores B+** como índices diretos e indiretos conforme necessidade) **que funciona corretamente?**  
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
