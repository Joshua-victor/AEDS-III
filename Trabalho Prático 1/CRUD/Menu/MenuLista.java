import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
//import aed3.ArvoreBMais;

public class MenuLista {
    
    private static Scanner console = new Scanner(System.in);
    private ArquivoLista arqLista;
    private Usuario usuarioAtivo; 

    public MenuLista(Usuario usuarioAtivo) throws Exception {
        this.arqLista = new ArquivoLista();
        this.usuarioAtivo = usuarioAtivo;
    }

    public void menu() {
        if (usuarioAtivo == null) {
            System.out.println("Nenhum usuário logado. Retornando ao menu principal.");
            return;
        }

        int opcao;
        do {
            System.out.println("\n\nAEDsIII");
            System.out.println("-------");
            System.out.println("> Início > Minhas listas");
            System.out.println("\n1 - Listar minhas listas");
            System.out.println("2 - Incluir nova lista");
            System.out.println("3 - Alterar dados da lista");
            System.out.println("4 - Excluir lista");
            System.out.println("0 - Voltar");

            System.out.print("\nOpção: ");
            try {
                opcao = Integer.valueOf(console.nextLine());
            } catch(NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    listarListas();
                    break;
                case 2:
                    incluirLista();
                    break;
                case 3:
                    //alterarLista();
                    System.out.println("Funcionalidade em desenvolvimento");
                    break;
                case 4:
                    //excluirLista();
                    System.out.println("Funcionalidade em desenvolvimento");
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }
        } while (opcao != 0);
    }
    
    private void listarListas() {
        System.out.println("\nListas de " + usuarioAtivo.nome + ":");
        System.out.println("--------------------------------------");
        try {
            Lista[] listas = arqLista.readByUserId(usuarioAtivo.getId());
            if (listas.length == 0) {
                System.out.println("Nenhuma lista encontrada.");
                return;
            }
            for (Lista l : listas) {
                System.out.println(l);
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar as listas.");
            e.printStackTrace();
        }
    }

    private void incluirLista() {
    System.out.println("\nInclusão de nova lista");
    System.out.print("\nNome da lista: ");
    String nome = console.nextLine();

    System.out.print("Descrição detalhada: ");
    String descricao = console.nextLine();
    
    // --- VALOR ARBITRADO AQUI ---
    // Define a data limite fixa para 31 de Dezembro de 2025.
    // O programa não irá mais pedir a data para o usuário.
    LocalDate dataLimite = LocalDate.of(2025, 12, 31);
    System.out.println("Usando data limite padrão: " + dataLimite.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

    
    String codigoCompartilhavel = gerarNanoId();

    System.out.print("\nConfirma a inclusão da lista? (S/N) ");
    char resp = console.nextLine().charAt(0);
    if(resp == 'S' || resp == 's') {
        try {
            Lista l = new Lista(-1, nome, descricao, LocalDate.now(), dataLimite, codigoCompartilhavel, usuarioAtivo.getId());
            arqLista.create(l);
            System.out.println("Lista '" + nome + "' incluída com sucesso.");
            System.out.println("Dados" + l);
        } catch(Exception e) {
            System.out.println("Erro do sistema. Não foi possível incluir a lista!");
            e.printStackTrace();
        }
    }
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
}