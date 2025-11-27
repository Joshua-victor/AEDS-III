import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

public class Principal {

    private static final Scanner console = new Scanner(System.in);

    private static ArquivoUsuario arqUsuario;
    private static Usuario usuarioAtivo = null;
    private static ArquivoProduto arqProduto;
    private static ArquivoListaProduto arqListaProduto;
    private static ListaInvertida indiceInvertido; // índice invertido para busca

    public static void main(String[] args) {
        try {
            arqUsuario = new ArquivoUsuario();
            indiceInvertido = new ListaInvertida(10, "dicionario.idx", "blocos.idx");
            arqProduto = new ArquivoProduto(indiceInvertido); // ✅ integração automática
            arqListaProduto = new ArquivoListaProduto();

            int opcao;
            while (true) {
                if (usuarioAtivo == null) {
                    opcao = menuDeslogado();
                    switch (opcao) {
                        case 1 -> entrar();
                        case 2 -> cadastrar();
                        case 0 -> {
                            System.out.println("Saindo...");
                            return;
                        }
                        default -> System.out.println("Opção inválida.");
                    }
                } else {
                    opcao = menuLogado();
                    switch (opcao) {
                        case 1 -> abrirMeuUsuario();
                        case 2 -> abrirMinhasListas();
                        case 3 -> buscarListaOutroUsuario();
                        case 4 -> abrirProdutos();
                        case 5 -> buscarProduto(); // agora unificada
                        case 9 -> usuarioAtivo = null;
                        case 0 -> {
                            System.out.println("Saindo...");
                            return;
                        }
                        default -> System.out.println("Opção inválida.");
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Erro fatal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ======== MENUS ========

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
        System.out.println("5) Buscar produto (por GTIN ou palavra)");
        System.out.println("9) Sair da sessão");
        System.out.println("0) Fechar programa");
        System.out.print("> ");
        String s = console.nextLine();
        return s.isEmpty() ? -1 : Integer.parseInt(s);
    }

    // ======== LOGIN / CADASTRO ========

    private static void entrar() throws Exception {
        System.out.println("\n=== Entrar ===");
        System.out.print("E-mail: ");
        String email = console.nextLine();
        System.out.print("Senha: ");
        String senha = console.nextLine();

        Usuario u = arqUsuario.read(email);
        if (u == null || !u.hashSenha.equals(senha)) {
            System.out.println("Email ou senha incorretos.");
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
        if (id > 0)
            System.out.println("Conta criada com sucesso.");
        else
            System.out.println("Falha ao criar conta.");
    }

    // ======== MENU DE USUÁRIO / LISTAS ========

    private static void abrirMeuUsuario() throws Exception {
        MenuUsuario mu = new MenuUsuario(usuarioAtivo);
        Usuario retorno = mu.menu();
        if (retorno == null) {
            usuarioAtivo = null;
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
                arqUsuario);
        mp.abrir();
    }

    // ============================================================
    // NOVA FUNÇÃO: BUSCA UNIFICADA (GTIN + PALAVRAS)
    // ============================================================

    private static void buscarProduto() throws Exception {
        System.out.println("\n=== Buscar Produto ===");
        System.out.println("1) Buscar por GTIN-13");
        System.out.println("2) Buscar por palavra");
        System.out.print("> ");
        int opcao = Integer.parseInt(console.nextLine());

        switch (opcao) {
            case 1 -> buscarProdutoPorGTIN();
            case 2 -> buscarProdutoPorTermo();
            default -> System.out.println("Opção inválida.");
        }
    }

    // ======== BUSCA POR GTIN ========

    private static void buscarProdutoPorGTIN() throws Exception {
        System.out.print("\nGTIN-13: ");
        String gtin = console.nextLine();

        Produto p = arqProduto.readByGTIN(gtin);
        if (p == null) {
            System.out.println("Nenhum produto encontrado com esse GTIN-13.");
        } else {
            System.out.println("\nProduto encontrado:");
            System.out.println("Nome: " + p.getNome());
            System.out.println("Descrição: " + p.getDescricao());
            System.out.println("ID: " + p.getId());
        }
    }

    // ======== BUSCA POR TERMO (TF×IDF) ========

    private static void buscarProdutoPorTermo() throws Exception {
    System.out.println("\n=== Buscar produto por termo ===");
    System.out.print("Termo: ");
    String termo = console.nextLine().toLowerCase();

    ElementoLista[] resultados = indiceInvertido.read(termo);
    if (resultados.length == 0) {
        System.out.println("Nenhum produto encontrado para o termo \"" + termo + "\".");
        return;
    }

    int N = arqProduto.count();          // total de produtos
    int df = resultados.length;          // em quantos produtos o termo aparece
    float idf = (float) Math.log10((float) N / (float) df);   // IDF clássico
    // Se quiser suavizar para nunca dar zero:  float idf = (float) Math.log10(1f + (float) N / (float) df);

    // Ordena por TF×IDF (tf = e.getFrequencia())
    java.util.Arrays.sort(resultados, (a, b) -> {
        float tfidfA = a.getFrequencia() * idf;
        float tfidfB = b.getFrequencia() * idf;
        return Float.compare(tfidfB, tfidfA);
    });

    System.out.println("\nResultados (ordenados por TF×IDF):");
    for (ElementoLista e : resultados) {
        Produto p = arqProduto.read(e.getId());
        if (p != null) {
            float tf = e.getFrequencia();
            float tfidf = tf * idf;
            System.out.printf("- %s (TF=%.3f, IDF=%.3f, TF×IDF=%.3f)%n",
                    p.getNome(), tf, idf, tfidf);
        }
    }
}

}
