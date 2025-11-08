 import java.time.LocalDate;

import java.time.format.DateTimeFormatter;

import java.util.Scanner;



public class MenuLista {



    private final Scanner console = new Scanner(System.in);

    private final ArquivoLista arqLista;

    private final ArquivoUsuario arqUsuario;

    private final Usuario usuarioAtivo;

    private final ArquivoListaProduto arqListaProduto;

    private final ArquivoProduto arqProduto;



    public MenuLista(Usuario usuarioAtivo) throws Exception {

        this.arqLista = new ArquivoLista();

        this.arqUsuario = new ArquivoUsuario();

        this.usuarioAtivo = usuarioAtivo;

        this.arqListaProduto = new ArquivoListaProduto();

        this.arqProduto = new ArquivoProduto();

    }



    public void menu() {

        int opcao;

        do {

            System.out.println("\n=== Minhas Listas ===");

            System.out.println("1) Listar minhas listas");

            System.out.println("2) Incluir nova lista");

            System.out.println("3) Alterar minha lista");

            System.out.println("4) Desativar/Excluir minha lista");

            System.out.println("5) Buscar lista de outro usuário por código");

            System.out.println("6) Gerenciar produtos da lista");   // TP2 (nova)

            System.out.println("7) Acrescentar produto à lista");

            System.out.println("0) Voltar");

            System.out.print("> ");

            String s = console.nextLine();

            opcao = s.isEmpty() ? -1 : Integer.parseInt(s);



            try {

                switch (opcao) {

                    case 1 -> listarListas();

                    case 2 -> incluirLista();

                    case 3 -> alterarLista();

                    case 4 -> desativarLista();

                    case 5 -> buscarListaOutroUsuario();

                    case 6 -> gerenciarProdutosDaLista();    // TP2 (novo)

                    case 7 -> acrescentarProdutoNaLista();  

                    case 0 -> { /* voltar */ }

                    default -> System.out.println("Opção inválida.");

                }

            } catch (Exception e) {

                System.out.println("Erro: " + e.getMessage());

            }

        } while (opcao != 0);

    }



    private void listarListas() throws Exception {

        Lista[] listas = arqLista.readByUserId(usuarioAtivo.id);

        System.out.println("\n=== Suas Listas ===");

        if (listas.length == 0) {

            System.out.println("(vazio)");

            return;

        }

        int i = 1;

        for (Lista l : listas) {

            System.out.printf("%d) Nome: %s | Descrição: %s | Código: %s | Criada: %s%n",

                i++,

                l.nome,

                l.descricao,

                l.codigoCompartilhavel,

                formatarData(l.dataCriacao));

        }

    }



    private void incluirLista() throws Exception {

        System.out.println("\n=== Nova Lista ===");

        System.out.print("Nome: ");

        String nome = console.nextLine();

        System.out.print("Descrição: ");

        String descricao = console.nextLine();

        System.out.print("Data limite (yyyy-MM-dd) (enter p/ sem): ");

        String dl = console.nextLine();



        Long dataLimiteEpochDay = -1L;

        if (!dl.isEmpty()) dataLimiteEpochDay = LocalDate.parse(dl).toEpochDay();



        Lista l = new Lista();

        l.nome = nome;

        l.descricao = descricao;

        l.dataCriacao = LocalDate.now().toEpochDay(); // epoch day

        l.dataLimite = (dataLimiteEpochDay == null ? -1L : dataLimiteEpochDay);

        l.codigoCompartilhavel = gerarNanoId();

        l.idUsuario = usuarioAtivo.id;



        arqLista.create(l);

        System.out.println("Lista criada com sucesso. Código: " + l.codigoCompartilhavel);

    }



    private void alterarLista() throws Exception {

        System.out.println("\n=== Alterar minha lista ===");

        System.out.print("Informe o CÓDIGO da lista: ");

        String code = console.nextLine();

        Lista l = arqLista.readByCode(code);

        if (l == null || l.idUsuario != usuarioAtivo.id) {

            System.out.println("Lista não encontrado ou não pertence a você.");

            return;

        }



        System.out.print("Nome atual: " + l.nome + " | Novo nome (enter p/ manter): ");

        String nome = console.nextLine();

        if (!nome.isEmpty()) l.nome = nome;



        System.out.print("Descrição atual: " + l.descricao + " | Nova descrição (enter p/ manter): ");

        String desc = console.nextLine();

        if (!desc.isEmpty()) l.descricao = desc;



        System.out.print("Data limite atual: " + (l.dataLimite == -1 ? "-" : LocalDate.ofEpochDay(l.dataLimite)) + " | Nova (yyyy-MM-dd, enter p/ manter/limpar): ");

        String dl = console.nextLine();

        if (!dl.isEmpty()) {

            if (dl.equalsIgnoreCase("limpar")) {

                l.dataLimite = -1L;

            } else {

                l.dataLimite = LocalDate.parse(dl).toEpochDay();

            }

        }



        boolean ok = arqLista.update(l);

        System.out.println(ok ? "Lista atualizada." : "Nada foi alterado.");

    }

    




    private void desativarLista() throws Exception {

        System.out.println("\n=== Desativar/Excluir minha lista ===");

        System.out.print("Informe o CÓDIGO da lista: ");

        String code = console.nextLine();

        Lista l = arqLista.readByCode(code);

        if (l == null || l.idUsuario != usuarioAtivo.id) {

            System.out.println("Lista não encontrada ou não pertence a você.");

            return;

        }



        var idsLP = arqListaProduto.listarIdsPorLista(l.id);

        for (int idLP : idsLP) {

            arqListaProduto.delete(idLP); // limpa as duas B+ por trás

        }



        boolean ok = arqLista.delete(l.id);

        System.out.println(ok ? "Lista desativada (e associações removidas)." : "Falha ao desativar.");

    }



    private void buscarListaOutroUsuario() throws Exception {

        System.out.println("\n=== Buscar lista de outro usuário ===");

        System.out.print("Código compartilhável: ");

        String codigo = console.nextLine();



        Lista lista = arqLista.readByCode(codigo);

        if (lista == null) {

            System.out.println("Nenhuma lista encontrada com este código.");

            return;

        }

        // Buscar nome do dono (sem exibir ID)

        Usuario dono = arqUsuario.read(lista.idUsuario);

        String nomeDono = (dono == null ? "(desconhecido)" : dono.nome);



        System.out.println("\nLista encontrada:");

        System.out.println("Nome da lista....: " + lista.nome);

        System.out.println("Descrição........: " + lista.descricao);

        System.out.println("Código...........: " + lista.codigoCompartilhavel);

        System.out.println("Dono.............: " + nomeDono);

        System.out.println("Criada em........: " + formatarData(lista.dataCriacao));

        System.out.println("Data limite......: " + (lista.dataLimite == -1 ? "-" : LocalDate.ofEpochDay(lista.dataLimite)));

        
    

    }



    private String formatarData(long epochDay) {

        return LocalDate.ofEpochDay(epochDay).format(DateTimeFormatter.ISO_DATE);

    }



    private String gerarNanoId() {

        String alfabeto = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        StringBuilder sb = new StringBuilder();

        java.util.Random random = new java.util.Random();

        for (int i = 0; i < 10; i++) {

            sb.append(alfabeto.charAt(random.nextInt(alfabeto.length())));

        }

        return sb.toString();

    }



    private void gerenciarProdutosDaLista() throws Exception {

        System.out.println("\n=== Gerenciar produtos da lista ===");

        System.out.print("Informe o CÓDIGO da lista: ");

        String code = console.nextLine();

        Lista lista = arqLista.readByCode(code);

        if (lista == null || lista.idUsuario != usuarioAtivo.id) {

            System.out.println("Lista não encontrada ou não pertence a você.");

            return;

        }



        var idsLP = arqListaProduto.listarIdsPorLista(lista.id);

        java.util.ArrayList<ListaProduto> itens = new java.util.ArrayList<>();

        for (int idLP : idsLP) {

            ListaProduto lp = arqListaProduto.read(idLP);

            if (lp != null) itens.add(lp);

        }

        itens.sort(java.util.Comparator.comparing(lp -> {

            try {

                Produto p = arqProduto.read(lp.getIdProduto());

                return p == null ? "" : p.getNome();

            } catch (Exception e) { return ""; }

        }, String.CASE_INSENSITIVE_ORDER));



        if (itens.isEmpty()) {

            System.out.println("Esta lista não possui produtos.");

            return;

        }



        while (true) {

            System.out.println("\nLista: " + lista.nome + " — Produtos");

            for (int i = 0; i < itens.size(); i++) {

                ListaProduto lp = itens.get(i);

                Produto p = arqProduto.read(lp.getIdProduto());

                String nome = (p == null ? ("#"+lp.getIdProduto()) : p.getNome());

                System.out.printf("(%d) %s (x%d)%n", i+1, nome, lp.getQuantidade());

            }

            System.out.println("(A) Alterar quantidade");

            System.out.println("(O) Alterar observações");

            System.out.println("(R) Remover produto desta lista");

            System.out.println("(V) Voltar");

            System.out.print("> ");

            String op = console.nextLine().trim().toUpperCase();



            if ("V".equals(op)) return;



            if ("A".equals(op) || "O".equals(op) || "R".equals(op)) {

                System.out.print("Selecione o índice do item: ");

                String s = console.nextLine().trim();

                int idx;

                try { idx = Integer.parseInt(s) - 1; } catch (Exception e) { System.out.println("Índice inválido."); continue; }

                if (idx < 0 || idx >= itens.size()) { System.out.println("Índice inválido."); continue; }

                ListaProduto lp = itens.get(idx);



                if ("A".equals(op)) {

                    System.out.print("Nova quantidade (>=1): ");

                    String q = console.nextLine().trim();

                    int nq;

                    try { nq = Math.max(1, Integer.parseInt(q)); } catch (Exception e) { System.out.println("Valor inválido."); continue; }

                    lp.setQuantidade(nq);

                    arqListaProduto.update(lp);

                    System.out.println("Quantidade atualizada.");

                } else if ("O".equals(op)) {

                    System.out.print("Observações (enter para limpar): ");

                    String o = console.nextLine();

                    lp.setObservacoes(o);

                    arqListaProduto.update(lp);

                    System.out.println("Observações atualizadas.");

                } else if ("R".equals(op)) {

                    arqListaProduto.delete(lp.getId());

                    itens.remove(idx);

                    System.out.println("Produto removido desta lista.");

                }

            } else {

                System.out.println("Opção inválida.");

            }

        }

    }



    

    private void acrescentarProdutoNaLista() throws Exception {

        System.out.println("\n=== Acrescentar produto à lista ===");

        System.out.print("Informe o CÓDIGO da lista: ");

        String code = console.nextLine();

        Lista lista = arqLista.readByCode(code);

        if (lista == null || lista.idUsuario != usuarioAtivo.id) {

            System.out.println("Lista não encontrada ou não pertence a você.");

            return;

        }



        while (true) {

            System.out.println("(1) Buscar por GTIN-13");

            System.out.println("(2) Listar produtos ATIVOS");

            System.out.println("(V) Voltar");

            System.out.print("> ");

            String op = console.nextLine().trim().toUpperCase();

            if ("V".equals(op)) return;



            Produto escolhido = null;



            if ("1".equals(op)) {

                System.out.print("GTIN-13: ");

                String g = console.nextLine().trim();

                escolhido = arqProduto.readByGTIN(g);

                if (escolhido == null) { System.out.println("Não encontrado."); continue; }

                if (!escolhido.isAtivo()) { System.out.println("Produto INATIVO — não pode ser adicionado."); continue; }

            } else if ("2".equals(op)) {

                java.util.ArrayList<Produto> ativos = new java.util.ArrayList<>();

                for (int id : arqProduto.listAllIds()) {

                    Produto p = arqProduto.read(id);

                    if (p != null && p.isAtivo()) ativos.add(p);

                }

                if (ativos.isEmpty()) { System.out.println("Nenhum produto ATIVO cadastrado."); continue; }

                ativos.sort(java.util.Comparator.comparing(Produto::getNome, String.CASE_INSENSITIVE_ORDER));



                int page = 0, pageSize = 10;

                while (true) {

                    int from = page * pageSize;

                    int to = Math.min(from + pageSize, ativos.size());

                    System.out.println("\nProdutos (ATIVOS) — página " + (page+1) + " de " + ((ativos.size()+9)/10));

                    for (int i = from; i < to; i++) {

                        System.out.printf("(%d) %s%n", (i - from + 1), ativos.get(i).getNome());

                    }

                    System.out.println("(A) Anterior  (P) Próxima  (R) Retornar  (número) Escolher");

                    System.out.print("> ");

                    String sel = console.nextLine().trim().toUpperCase();

                    if ("R".equals(sel)) break;

                    if ("A".equals(sel)) { if (page > 0) page--; continue; }

                    if ("P".equals(sel)) { if (to < ativos.size()) page++; continue; }

                    try {

                        int k = Integer.parseInt(sel);

                        if (k >= 1 && k <= (to - from)) {

                            escolhido = ativos.get(from + k - 1);

                            break;

                        } else System.out.println("Índice inválido.");

                    } catch (NumberFormatException nfe) {

                        System.out.println("Opção inválida.");

                    }

                }

                if (escolhido == null) continue;

            } else {

                System.out.println("Opção inválida.");

                continue;

            }



            System.out.print("Quantidade (>=1): ");

            int qtd = 1;

            try { qtd = Math.max(1, Integer.parseInt(console.nextLine().trim())); } catch (Exception e) { qtd = 1; }



            var idsLP = arqListaProduto.listarIdsPorLista(lista.id);

            ListaProduto existente = null;

            for (int idLP : idsLP) {

                ListaProduto lp = arqListaProduto.read(idLP);

                if (lp != null && lp.getIdProduto() == escolhido.getId()) { existente = lp; break; }

            }

            if (existente != null) {

                existente.setQuantidade(existente.getQuantidade() + qtd);

                arqListaProduto.update(existente);

                System.out.println("Quantidade somada ao item existente.");

            } else {

                ListaProduto novo = new ListaProduto();

                novo.setIdLista(lista.id);                // sua classe Lista usa campos públicos

                novo.setIdProduto(escolhido.getId());

                novo.setQuantidade(qtd);

                novo.setObservacoes("");

                arqListaProduto.create(novo);

                System.out.println("Produto adicionado à lista.");

            }

            return;

        }

    }

}