# Trabalho Pratico AEDs 3 - Parte 1

## Integrantes

- [Joshua Victor Costa e Pereira](https://github.com/Joshua-victor)
- [Gabriel Filipe Lanza Candido](https://github.com/biellanzac)

## Descrição

O trabalho prático 1 é sobre a implementação de um sistema para gerenciar listas de presentes, chamado "Presente Fácil 1.0". O objetivo é que os usuários possam criar listas com produtos que gostariam de receber.

Cada tarefa possui os seguintes atributos:

Entidade Usuário: Deve ter atributos como nome, e-mail, hash de senha, pergunta e resposta secreta. O acesso é feito por e-mail e senha, e deve haver uma opção para cadastro de novos usuários.

Entidade Lista: Deve ter atributos como nome, descrição, data de criação e um código compartilhável.

Busca de Listas: É possível buscar listas de outras pessoas usando um código compartilhável (NanoID).

Relacionamento e Busca: O sistema deve registrar o relacionamento 1:N entre usuários e listas usando uma Árvore B+.


## Estrutura do Registro

As operações de CRUD são realizadas por uma classe genérica `Principal` que pode manipular qualquer entidade que implemente a interface.

### Índice Direto

O projeto utiliza um índice direto baseado em tabela hash extensível para gerenciar os usuarios. Este índice armazena a chave (email), facilitando as operações de busca, inserção, atualização e exclusão.

## Estrutura do Projeto

### Classes Principais

- Principal: Classe genérica que gerencia as operações de CRUD no arquivo de dados.
- Usuario: Classe que representa a entidade usuario, implementando a interface de login/registro.
- Lista: Classe que representa a entidade listas, implementando a interface de listas.

### Métodos Principais

- create: Insere um novo usuario no arquivo.
- read: Lê uma lista do arquivo com base no ID e retorna o objeto correspondente.
- update: Atualiza um usuario/lista existente no arquivo.
- delete: Marca um registro como excluído no arquivo.

## Experiência de Desenvolvimento

Durante o desenvolvimento do projeto, implementamos todas as funcionalidades básicas necessárias para o CRUD de tarefas. 
A primeira etapa envolveu a implementação conjunta da classe usuario e lista, seguida pela atribuição das funções do CRUD à classe 
Arquivo para cada membro da equipe.

A implementação do CRUD e das classes de usuario e lista a equipe conseguiu resolver sem muitas dificuldades.


## Checklist

- Há um CRUD de usuários (que estende a classe ArquivoIndexado, acrescentando Tabelas Hash Extensíveis e Árvores B+ como índices diretos e indiretos conforme necessidade) que funciona corretamente?
   ````
   SIM
   ````
- Há um CRUD de listas (que estende a classe ArquivoIndexado, acrescentando Tabelas Hash Extensíveis e Árvores B+ como índices diretos e indiretos conforme necessidade) que funciona corretamente?
   ````
   SIM
   ````
- As listas de presentes estão vinculadas aos usuários usando o idUsuario como chave estrangeira?
   ````
   SIM
   ````
- Há uma árvore B+ que registre o relacionamento 1:N entre usuários e listas?
   ````
   SIM
   ````
- Há um CRUD de usuários (que estende a classe ArquivoIndexado, acrescentando Tabelas Hash Extensíveis e Árvores B+ como índices diretos e indiretos conforme necessidade)?
   ````
   SIM
   ````
- Há uma visualização das listas de outras pessoas por meio de um código NanoID?
   ````
   SIM
   ````
- O trabalho compila corretamente?
   ````
   SIM
   ````
- O trabalho está completo e funcionando sem erros de execução?
   ````
   SIM
   ````
- O trabalho é original e não a cópia de um trabalho de outro grupo?
   ````
   SIM
   ````
-Link do video explicativo(https://youtu.be/xnOAi2CQIEc)


## FIM
