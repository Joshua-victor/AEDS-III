import java.util.Scanner;

public class Principal {

    private static final Scanner console = new Scanner(System.in);
    private static ArquivoUsuario arqUsuario;
    private static Usuario usuarioAtivo = null;
    private static ArquivoProduto arqProduto;
    private static ArquivoListaProduto arqListaProduto;

    public static void main(String[] args) {
        try {
            arqUsuario = new ArquivoUsuario();
            arqProduto = new ArquivoProduto();
            arqListaProduto = new ArquivoListaProduto();
            int opcao;
            while (true) {
                if (usuarioAtivo == null) {
                    opcao = menuDeslogado();
                    switch (opcao) {
                        case 1 -> entrar();
                        case 2 -> cadastrar();
                        case 0 -> { System.out.println("Saindo..."); return; }
                        default -> System.out.println("Opção inválida.");
                    }
                } else {
                    opcao = menuLogado();
                    switch (opcao) {
                        case 1 -> abrirMeuUsuario();
                        case 2 -> abrirMinhasListas();
                        case 3 -> buscarListaOutroUsuario();
                        case 4 -> abrirProdutos();
                        case 9 -> usuarioAtivo = null; // sair da sessão
                        case 0 -> { System.out.println("Saindo..."); return; }
                        default -> System.out.println("Opção inválida.");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erro fatal: " + e.getMessage());
        }
    }

    private static int menuDeslogado() {
        System.out.println("\n=== Sistema ===");
        System.out.println("1) Entrar");
        System.out.println("2) Criar conta");
        System.out.println("0) Sair");
        System.out.print("> ");
        String s = console.nextLine();
        return s.isEmpty() ? -1 : Integer.parseInt(s);
    }

    private static int menuLogado() {
        System.out.println("\n=== Bem-vindo, " + usuarioAtivo.nome + " ===");
        System.out.println("1) Meu usuário");
        System.out.println("2) Minhas listas");
        System.out.println("3) Buscar lista por código (de outro usuário)");
        System.out.println("4) Produtos");      
        System.out.println("9) Sair da sessão");
        System.out.println("0) Fechar programa");
        System.out.print("> ");
        String s = console.nextLine();
        return s.isEmpty() ? -1 : Integer.parseInt(s);
    }

 private static void entrar() throws Exception {
        System.out.println("\n=== Entrar ===");
        System.out.print("E-mail: ");
        String email = console.nextLine();
        System.out.print("Senha: ");
        String senha = console.nextLine();
        Usuario u = arqUsuario.read(email);
        Usuario s = arqUsuario.read(senha);
        if (u == null || s == null) {
            System.out.println("Email ou senha incorretos");
            return;
        }
        usuarioAtivo = u; 
        System.out.println("Sessão iniciada.");
    }

    private static void cadastrar() throws Exception {
        System.out.println("\n=== Criar conta ===");
        Usuario u = new Usuario();
        System.out.print("Nome: ");
        u.nome = console.nextLine();
        System.out.print("E-mail: ");
        u.email = console.nextLine();
        System.out.print("Pergunta secreta: ");
        u.perguntaSecreta = console.nextLine();
        System.out.print("Resposta secreta: ");
        u.respostaSecreta = console.nextLine();
        System.out.print("Senha: ");
        u.hashSenha = console.nextLine();

        int id = arqUsuario.create(u);
        if (id > 0) {
            System.out.println("Conta criada com sucesso.");
        } else {
            System.out.println("Falha ao criar conta.");
        }
    }

    private static void abrirMeuUsuario() throws Exception {
        MenuUsuario mu = new MenuUsuario(usuarioAtivo);
        Usuario retorno = mu.menu();
        if (retorno == null) {
            usuarioAtivo = null; // conta desativada → encerrar sessão
        } else {
            usuarioAtivo = retorno;
        }
    }

    private static void abrirMinhasListas() throws Exception {
        MenuLista ml = new MenuLista(usuarioAtivo);
        ml.menu();
    }

    private static void buscarListaOutroUsuario() throws Exception {
    
    System.out.println("\n=== Buscar lista de outro usuário ===");
    System.out.print("Código compartilhável: ");
    String codigo = console.nextLine();

    ArquivoLista arqLista = new ArquivoLista();
    Lista listaEncontrada = arqLista.readByCode(codigo);

    if (listaEncontrada != null) {
        ArquivoUsuario arqU = new ArquivoUsuario();
        Usuario dono = arqU.read(listaEncontrada.idUsuario);
        String nomeDono = (dono == null ? "(desconhecido)" : dono.nome);
        
        System.out.println("\nLista encontrada:");
        System.out.println("Nome da lista....: " + listaEncontrada.nome);
        System.out.println("Descrição........: " + listaEncontrada.descricao);
        System.out.println("Código...........: " + listaEncontrada.codigoCompartilhavel);
        System.out.println("Dono.............: " + nomeDono);
    
        ArquivoListaProduto arqListaProduto = new ArquivoListaProduto(); 
        ArquivoProduto arqProduto = new ArquivoProduto();
        
        // 1. Carrega os IDs dos itens da lista (ListaProduto)
        var idsLP = arqListaProduto.listarIdsPorLista(listaEncontrada.getId()); // Use o ID da lista
        java.util.ArrayList<ListaProduto> itens = new java.util.ArrayList<>();
        for (int idLP : idsLP) {
            ListaProduto lp = arqListaProduto.read(idLP);
            if (lp != null) itens.add(lp);
        }

        System.out.println("\n--- ITENS DA LISTA ---");
        if (itens.isEmpty()) {
            System.out.println("(Lista vazia)");
        } else {
            // 2. Exibe cada item
            for (int i = 0; i < itens.size(); i++) {
                ListaProduto lp = itens.get(i);
                Produto p = arqProduto.read(lp.getIdProduto()); 
                
                String nomeProduto = (p == null ? ("Produto #" + lp.getIdProduto()) : p.getNome());
                String obs = lp.getObservacoes().isEmpty() ? "" : " (Obs: " + lp.getObservacoes() + ")";
                
                System.out.printf(" - %s (x%d)%s%n", nomeProduto, lp.getQuantidade(), obs);
            }
        }
        // ----------------------------------------------------
        
    } else {
        System.out.println("Nenhuma lista encontrada com este código.");
    }
}

    private static void abrirProdutos() throws Exception {
        MenuProduto mp = new MenuProduto(
            usuarioAtivo,
            arqProduto,
            arqListaProduto,
            new ArquivoLista(),
            arqUsuario
        );
        mp.abrir();
    }
}
