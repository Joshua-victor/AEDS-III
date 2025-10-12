import java.util.Scanner;

public class MenuUsuario {

    private final ArquivoUsuario arqUsuario;
    private final Scanner console = new Scanner(System.in);
    private Usuario usuarioAtivo;

    public MenuUsuario(Usuario usuarioAtivo) throws Exception {
        this.arqUsuario = new ArquivoUsuario();
        this.usuarioAtivo = usuarioAtivo;
    }

    public Usuario menu() {
        int opcao;
        do {
            System.out.println("\n=== Meu Usuário ===");
            System.out.println("1) Alterar meus dados");
            System.out.println("2) Desativar minha conta");
            System.out.println("0) Voltar");
            System.out.print("> ");
            String s = console.nextLine();
            opcao = s.isEmpty() ? -1 : Integer.parseInt(s);

            try {
                switch (opcao) {
                    case 1 -> alterarMeusDados();
                    case 2 -> { if (desativarMinhaConta()) return null; }
                    case 0 -> { /* voltar */ }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (opcao != 0 && usuarioAtivo != null);
        return usuarioAtivo;
    }

    private void alterarMeusDados() throws Exception {
        System.out.println("\n=== Alterar meus dados ===");
        System.out.print("Nome atual: " + usuarioAtivo.nome + " | Novo nome (enter p/ manter): ");
        String nome = console.nextLine();
        if (!nome.isEmpty()) usuarioAtivo.nome = nome;

        System.out.print("E-mail atual: " + usuarioAtivo.email + " | Novo e-mail (enter p/ manter): ");
        String email = console.nextLine();
        if (!email.isEmpty()) usuarioAtivo.email = email;

        System.out.print("Pergunta secreta (enter p/ manter): ");
        String ps = console.nextLine();
        if (!ps.isEmpty()) usuarioAtivo.perguntaSecreta = ps;

        System.out.print("Resposta secreta (enter p/ manter): ");
        String rs = console.nextLine();
        if (!rs.isEmpty()) usuarioAtivo.respostaSecreta = rs;

        System.out.print("Hash da senha (enter p/ manter): ");
        String hs = console.nextLine();
        if (!hs.isEmpty()) usuarioAtivo.hashSenha = hs;

        if (arqUsuario.update(usuarioAtivo)) {
            System.out.println("Dados atualizados.");
        } else {
            System.out.println("Nada foi alterado.");
        }
    }

    private boolean desativarMinhaConta() throws Exception {
        System.out.println("\nTem certeza que deseja desativar sua conta? (s/N)");
        String c = console.nextLine().trim().toLowerCase();
        if (!c.equals("s")) return false;

        // Exclusão lógica do próprio usuário por id
        boolean ok = arqUsuario.delete(usuarioAtivo.id);
        if (ok) {
            System.out.println("Conta desativada.");
            usuarioAtivo = null;
            return true;
        } else {
            System.out.println("Falha ao desativar.");
            return false;
        }
    }

    public static void mostraUsuarioPublico(Usuario usuario) {
        if (usuario != null) {
            System.out.println("\nUsuário:");
            System.out.println("Nome............: " + usuario.nome);
            System.out.println("Email...........: " + usuario.email);
        }
    }
}
