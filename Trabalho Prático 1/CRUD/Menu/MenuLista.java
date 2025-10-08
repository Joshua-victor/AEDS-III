import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MenuLista {

    private final Scanner console = new Scanner(System.in);
    private final ArquivoLista arqLista;
    private final ArquivoUsuario arqUsuario;
    private final Usuario usuarioAtivo;

    public MenuLista(Usuario usuarioAtivo) throws Exception {
        this.arqLista = new ArquivoLista();
        this.arqUsuario = new ArquivoUsuario();
        this.usuarioAtivo = usuarioAtivo;
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
        boolean ok = arqLista.delete(l.id);
        System.out.println(ok ? "Lista desativada." : "Falha ao desativar.");
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
}
