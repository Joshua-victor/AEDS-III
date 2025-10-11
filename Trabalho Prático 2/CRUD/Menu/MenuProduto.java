package Menu;

import java.util.*;
import java.util.stream.Collectors;

public class MenuProduto {

    private final ArquivoProduto arqProduto;
    private final ArquivoListaProduto arqListaProduto;
    private final ArquivoLista arqLista;
    private final ArquivoUsuario arqUsuario;
    private final Scanner sc = new Scanner(System.in);
    private final Usuario usuarioLogado;

    public MenuProduto(Usuario usuarioLogado, ArquivoProduto ap, ArquivoListaProduto alp, ArquivoLista al, ArquivoUsuario au) {
        this.usuarioLogado = usuarioLogado;
        this.arqProduto = ap;
        this.arqListaProduto = alp;
        this.arqLista = al;
        this.arqUsuario = au;
    }

    public void abrir() {
        while (true) {
            System.out.println("PresenteFácil 1.0");
            System.out.println("-----------------");
            System.out.println("> Início > Produtos\n");
            System.out.println("(1) Buscar produtos por GTIN");
            System.out.println("(2) Listar todos os produtos");
            System.out.println("(3) Cadastrar um novo produto");
            System.out.println("\n(R) Retornar ao menu anterior");
            System.out.print("\nOpção: ");
            String op = sc.nextLine().trim().toUpperCase();
            switch (op) {
                case "1": buscarPorGTIN(); break;
                case "2": listarTodos(); break;
                case "3": cadastrar(); break;
                case "R": return;
                default: System.out.println("Opção inválida.");
            }
        }
    }

    private void buscarPorGTIN() {
        try {
            System.out.print("GTIN-13: ");
            String g = sc.nextLine().trim();
            Produto p = arqProduto.readByGTIN(g);
            if (p == null) { System.out.println("Produto não encontrado."); return; }
            abrirFicha(p);
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void listarTodos() {
        try {
            List<Produto> all = new ArrayList<>();
            for (int id : arqProduto.listAllIds()) {
                Produto p = arqProduto.read(id);
                if (p != null) all.add(p);
            }
            all.sort(Comparator.comparing(Produto::getNome, String.CASE_INSENSITIVE_ORDER));
            int page = 0;
            int pageSize = 10;
            while (true) {
                int from = page * pageSize;
                int to = Math.min(from + pageSize, all.size());
                if (from >= all.size()) { System.out.println("Sem itens."); break; }
                System.out.println("\n> Início > Produtos > Listagem");
                System.out.println("Página " + (page+1) + " de " + ((all.size()+9)/10));
                for (int i=from;i<to;i++) {
                    System.out.printf("(%d) %s%s%n", (i-from+1), all.get(i).getNome(), all.get(i).isAtivo() ? "" : " (INATIVADO)");
                }
                System.out.println("(A) Página anterior");
                System.out.println("(P) Próxima página");
                System.out.println("(R) Retornar");
                System.out.print("Opção: ");
                String op = sc.nextLine().trim().toUpperCase();
                if ("R".equals(op)) break;
                if ("A".equals(op)) { if (page>0) page--; continue; }
                if ("P".equals(op)) { if (to < all.size()) page++; continue; }
                try {
                    int sel = Integer.parseInt(op);
                    if (sel>=1 && sel<= (to-from)) {
                        Produto escolhido = all.get(from + sel -1);
                        abrirFicha(escolhido);
                    } else System.out.println("Índice inválido.");
                } catch(NumberFormatException nfe){
                    System.out.println("Opção inválida.");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void cadastrar() {
        try {
            System.out.print("GTIN-13 (13 dígitos): ");
            String g = sc.nextLine().trim();
            if (!g.matches("\\d{13}")) { System.out.println("GTIN inválido."); return; }
            if (arqProduto.readByGTIN(g) != null) { System.out.println("GTIN já cadastrado."); return; }
            System.out.print("Nome: ");
            String n = sc.nextLine();
            System.out.print("Descrição: ");
            String d = sc.nextLine();
            Produto p = new Produto(-1, g, n, d, true);
            int id = arqProduto.create(p);
            System.out.println("Criado id=" + id);
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void abrirFicha(Produto p) {
        while (true) {
            try {
                System.out.println("\n> Início > Produtos > " + p.getNome());
                System.out.println("NOME.......: " + p.getNome());
                System.out.println("GTIN-13....: " + p.getGtin13());
                System.out.println("DESCRIÇÃO..: " + p.getDescricao());
                System.out.println("ATIVO......: " + p.isAtivo());

                List<Integer> idsLP = arqListaProduto.listarIdsPorProduto(p.getId());
                List<Lista> minhas = new ArrayList<>();
                int outras = 0;
                for (int idLP : idsLP) {
                    ListaProduto lp = arqListaProduto.read(idLP);
                    if (lp == null) continue;
                    Lista l = arqLista.read(lp.getIdLista());
                    if (l == null) continue;
                    if (l.getIdUsuario() == usuarioLogado.getId()) minhas.add(l);
                    else outras++;
                }
                minhas.sort(Comparator.comparing(Lista::getNome, String.CASE_INSENSITIVE_ORDER));
                System.out.println("\nAparece nas minhas listas:");
                for (Lista l : minhas) System.out.println("- " + l.getNome());
                System.out.println("Também aparece em " + outras + " lista(s) de outras pessoas.");

                System.out.println("\n(1) Alterar");
                System.out.println("(2) Inativar/Reativar");
                System.out.println("(R) Retornar");
                System.out.print("Opção: ");
                String op = sc.nextLine().trim().toUpperCase();
                if ("R".equals(op)) return;
                if ("1".equals(op)) {
                    System.out.print("Novo nome (enter mantém): ");
                    String n = sc.nextLine();
                    if (!n.isEmpty()) p.setNome(n);
                    System.out.print("Nova descrição (enter mantém): ");
                    String d = sc.nextLine();
                    if (!d.isEmpty()) p.setDescricao(d);
                    arqProduto.update(p);
                } else if ("2".equals(op)) {
                    p.setAtivo(!p.isAtivo());
                    arqProduto.update(p);
                } else {
                    System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
                return;
            }
        }
    }
}
