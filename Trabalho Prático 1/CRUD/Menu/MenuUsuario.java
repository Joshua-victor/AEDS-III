

import java.util.Scanner;

public class MenuUsuario {
    
    ArquivoUsuario arqUsuario;
    private static Scanner console = new Scanner(System.in);

    public MenuUsuario() throws Exception {
        arqUsuario= new ArquivoUsuario();
    }

    public void menu() {
        int opcao;
        do {
            System.out.println("\n\nAEDsIII");
            System.out.println("-------");
            System.out.println("> Início > Usuários");
            System.out.println("\n1 - Buscar");
            System.out.println("2 - Incluir");
            System.out.println("3 - Alterar");
            System.out.println("4 - Excluir");
            System.out.println("0 - Voltar");

            System.out.print("\nOpção: ");
            try {
                opcao = Integer.valueOf(console.nextLine());
            } catch(NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    buscarUsuario();
                    break;
                case 2:
                    incluirUsuario();
                    break;
                case 3:
                    alterarUsuario();
                    break;
                case 4:
                    excluirUsuario();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }
        } while (opcao != 0);
    }

    public void buscarUsuario() {
        System.out.println("\nBusca de usuário");
        String email;
        System.out.print("\nEmail: ");
        email = console.nextLine();
        
        if (email.isEmpty())
            return;

        try {
            Usuario usuario = arqUsuario.read(email);
            if (usuario != null) {
                mostraUsuario(usuario);
            } else {
                System.out.println("Usuário não encontrado.");
            }
        } catch(Exception e) {
            System.out.println("Erro do sistema. Não foi possível buscar o usuário!");
            e.printStackTrace();
        }
    }   

    public void incluirUsuario() {
        System.out.println("\nInclusão de usuário");
        String nome = "";
        String email = "";
        String perguntaSecreta = "";
        String respostaSecreta = "";
        String senha = "";

        do {
            System.out.print("\nNome (min. de 4 letras ou vazio para cancelar): ");
            nome = console.nextLine();
            if(nome.length() == 0)
                return;
            if(nome.length() < 4)
                System.err.println("O nome do cliente deve ter no mínimo 4 caracteres.");
        } while(nome.length() < 4);

        System.out.print("Email: ");
        email = console.nextLine();
        
        System.out.print("Pergunta Secreta: ");
        perguntaSecreta = console.nextLine();

        System.out.print("Resposta Secreta: ");
        respostaSecreta = console.nextLine();
        
        System.out.print("Senha: ");
        senha = console.nextLine();

        System.out.print("\nConfirma a inclusão do usuário? (S/N) ");
        char resp = console.nextLine().charAt(0);
        if(resp == 'S' || resp == 's') {
            try {
                
                String hashSenha = String.valueOf(senha.hashCode()); 
                Usuario u = new Usuario(-1, nome, email, perguntaSecreta, respostaSecreta, hashSenha);
                arqUsuario.create(u);
                System.out.println("Usuário incluído com sucesso.");
            } catch(Exception e) {
                System.out.println("Erro do sistema. Não foi possível incluir o usuário!");
                e.printStackTrace();
            }
        }
    }

    public void alterarUsuario() {
        System.out.println("\nAlteração de usuário");
        String email;
        System.out.print("\nEmail do usuário a ser alterado: ");
        email = console.nextLine();
        
        if(email.isEmpty())
            return;

        try {
            Usuario usuario = arqUsuario.read(email);
            if (usuario != null) {
                System.out.println("Usuário encontrado:");
                mostraUsuario(usuario);

                System.out.print("\nNovo nome (deixe em branco para manter o anterior): ");
                String novoNome = console.nextLine();
                if (!novoNome.isEmpty()) {
                    usuario.nome = novoNome;
                }

                System.out.print("Nova pergunta secreta (deixe em branco para manter a anterior): ");
                String novaPergunta = console.nextLine();
                if (!novaPergunta.isEmpty()) {
                    usuario.perguntaSecreta = novaPergunta;
                }

                System.out.print("Nova resposta secreta (deixe em branco para manter a anterior): ");
                String novaResposta = console.nextLine();
                if (!novaResposta.isEmpty()) {
                    usuario.respostaSecreta = novaResposta;
                }

                System.out.print("Nova senha (deixe em branco para manter a anterior): ");
                String novaSenha = console.nextLine();
                if (!novaSenha.isEmpty()) {
                    usuario.hashSenha = String.valueOf(novaSenha.hashCode());
                }

                System.out.print("\nConfirma as alterações? (S/N) ");
                char resp = console.nextLine().charAt(0);
                if (resp == 'S' || resp == 's') {
                    boolean alterado = arqUsuario.update(usuario);
                    if (alterado) {
                        System.out.println("Usuário alterado com sucesso.");
                    } else {
                        System.out.println("Erro ao alterar o usuário.");
                    }
                } else {
                    System.out.println("Alterações canceladas.");
                }
            } else {
                System.out.println("Usuário não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro do sistema. Não foi possível alterar o usuário!");
            e.printStackTrace();
        }
    }

    public void excluirUsuario() {
        System.out.println("\nExclusão de usuário");
        String email;
        System.out.print("\nEmail do usuário a ser excluído: ");
        email = console.nextLine();
        
        if(email.isEmpty())
            return;

        try {
            Usuario usuario = arqUsuario.read(email);
            if (usuario != null) {
                System.out.println("Usuário encontrado:");
                mostraUsuario(usuario);

                System.out.print("\nConfirma a exclusão do usuário? (S/N) ");
                char resp = console.nextLine().charAt(0);
                if (resp == 'S' || resp == 's') {
                    boolean excluido = arqUsuario.delete(email);
                    if (excluido) {
                        System.out.println("Usuário excluído com sucesso.");
                    } else {
                        System.out.println("Erro ao excluir o usuário.");
                    }
                } else {
                    System.out.println("Exclusão cancelada.");
                }
            } else {
                System.out.println("Usuário não encontrado.");
            }
        } catch (Exception e) {
            System.out.println("Erro do sistema. Não foi possível excluir o usuário!");
            e.printStackTrace();
        }
    }

    public void mostraUsuario(Usuario usuario) {
        if (usuario != null) {
            System.out.println("\nDetalhes do Usuário:");
            System.out.println("----------------------");
            System.out.printf("ID..............: %d%n", usuario.getId());
            System.out.printf("Nome............: %s%n", usuario.nome);
            System.out.printf("Email...........: %s%n", usuario.email);
            System.out.printf("Pergunta Secreta: %s%n", usuario.perguntaSecreta);
            System.out.printf("Resposta Secreta: %s%n", usuario.respostaSecreta);
            System.out.printf("Hash da Senha...: %s%n", usuario.hashSenha);
            System.out.println("----------------------");
        }
    }
}
