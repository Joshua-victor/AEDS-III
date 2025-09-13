
import java.util.Scanner;

public class Principal {
    private static Scanner console = new Scanner(System.in);
    private static ArquivoUsuario arqUsuario;
    private static Usuario usuarioAtivo = null; // Usuário logado no sistema

    public static void main(String[] args) {
        try {
            arqUsuario = new ArquivoUsuario();
            int opcao;
            do {
                System.out.println("\n\nPresente Fácil 1.0");
                System.out.println("-----------------");

                if (usuarioAtivo == null) {
                    System.out.println("\n> Início");
                    System.out.println("\n1 - Fazer Login");
                    System.out.println("2 - Cadastrar Novo Usuário");
                    System.out.println("0 - Sair");
                } else {
                    System.out.println("\n> Início (Logado como: " + usuarioAtivo.nome + ")");
                    System.out.println("\n1 - Meus dados");
                    System.out.println("2 - Minhas listas");
                    System.out.println("3 - Buscar lista de outro usuário");
                    System.out.println("0 - Sair");
                }

                System.out.print("\nOpção: ");
                try {
                    opcao = Integer.valueOf(console.nextLine());
                } catch (NumberFormatException e) {
                    opcao = -1;
                }

                if (usuarioAtivo == null) {
                    switch (opcao) {
                        case 1:
                            fazerLogin();
                            break;
                        case 2:
                            cadastrarNovoUsuario();
                            break;
                        case 0:
                            break;
                        default:
                            System.out.println("Opção inválida!");
                            break;
                    }
                } else {
                    switch (opcao) {
                        case 1:
                            // Acessa o menu do usuário logado
                            (new MenuUsuario()).menu();
                            break;
                        case 2:
                            // Acessa o menu de listas do usuário logado
                            (new MenuLista(usuarioAtivo)).menu();
                            break;
                        case 3:
                            // Lógica para buscar lista de outro usuário por código
                            buscarListaCompartilhavel();
                            break;
                        case 0:
                            usuarioAtivo = null; // Desloga o usuário
                            System.out.println("Sessão encerrada.");
                            break;
                        default:
                            System.out.println("Opção inválida!");
                            break;
                    }
                }

            } while (opcao != 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void fazerLogin() throws Exception {
        System.out.println("\nLOGIN");
        System.out.print("\nE-mail: ");
        String email = console.nextLine();
        System.out.print("Senha: ");
        String senha = console.nextLine();
        
        Usuario u = arqUsuario.read(email);
        if (u != null && u.hashSenha.equals(String.valueOf(senha.hashCode()))) {
            usuarioAtivo = u;
            System.out.println("Login bem-sucedido. Bem-vindo, " + usuarioAtivo.nome + "!");
        } else {
            System.out.println("E-mail ou senha inválidos.");
        }
    }

    private static void cadastrarNovoUsuario() throws Exception {
        (new MenuUsuario()).incluirUsuario();
    }
    
    private static void buscarListaCompartilhavel() throws Exception {
        System.out.println("\nBUSCAR LISTA DE OUTRO USUÁRIO");
        System.out.print("\nCódigo compartilhável: ");
        String codigo = console.nextLine();
        
        ArquivoLista arqLista = new ArquivoLista();
        Lista listaEncontrada = arqLista.readByCode(codigo);
        
        if (listaEncontrada != null) {
            System.out.println("\nLista encontrada:");
            System.out.println(listaEncontrada);
        } else {
            System.out.println("Nenhuma lista encontrada com este código.");
        }
    }
}