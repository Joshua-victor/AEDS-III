import aed3.Arquivo;
import aed3.HashExtensivel;
import aed3.ParGtinID;
import java.util.ArrayList;
import java.io.File;

/**
 * Classe de arquivo para produtos, com índice hash por GTIN-13
 * e integração com índice invertido para busca textual.
 *
 * Mantém automaticamente o índice invertido atualizado
 * ao criar, atualizar ou excluir produtos.
 */
public class ArquivoProduto extends Arquivo<Produto> {

    private final HashExtensivel<ParGtinID> indiceGtin;
    private ListaInvertida indiceInvertido; // referência ao índice invertido global

    /** Construtor padrão (sem índice invertido). */
    public ArquivoProduto() throws Exception {
        super("produtos", Produto.class.getConstructor());

        // cria pasta de dados se não existir
        File dir = new File(".\\dados\\produtos");
        if (!dir.exists()) dir.mkdirs();

        this.indiceGtin = new HashExtensivel<>(
            ParGtinID.class.getConstructor(),
            4,
            ".\\dados\\produtos\\gtin.d.db",
            ".\\dados\\produtos\\gtin.c.db"
        );
    }

    /** Construtor com índice invertido. */
    public ArquivoProduto(ListaInvertida indiceInvertido) throws Exception {
        this(); // chama o construtor padrão
        this.indiceInvertido = indiceInvertido;
    }

    // ============================================
    // CREATE
    // ============================================

    @Override
    public int create(Produto p) throws Exception {
        // Valida GTIN único
        ParGtinID probe = null;
        try {
            probe = indiceGtin.read(p.getGtin13().hashCode());
        } catch (NullPointerException ignore) {}
        if (probe != null)
            throw new Exception("GTIN-13 já cadastrado.");

        // Cria o produto no arquivo principal
        int id = super.create(p);

        // Atualiza o índice hash
        try {
            indiceGtin.create(new ParGtinID(p.getGtin13(), id));
        } catch (NullPointerException ignore) {}

        // Atualiza o índice invertido (nomes → produtos)
        atualizarIndiceInvertido(p, id);

        return id;
    }

    // ============================================
    // UPDATE
    // ============================================

    @Override
    public boolean update(Produto p) throws Exception {
        Produto old = super.read(p.getId());
        if (old == null) return false;

        // Se trocou GTIN, atualiza o índice hash
        if (!old.getGtin13().equals(p.getGtin13())) {
            ParGtinID dup = indiceGtin.read(p.getGtin13().hashCode());
            if (dup != null)
                throw new Exception("GTIN-13 já cadastrado para outro produto.");

            indiceGtin.delete(old.getGtin13().hashCode());
            indiceGtin.create(new ParGtinID(p.getGtin13(), p.getId()));
        }

        // Atualiza o registro principal
        boolean ok = super.update(p);

        // Se o nome foi alterado, atualiza o índice invertido
        if (ok && !old.getNome().equalsIgnoreCase(p.getNome())) {
            removerDoIndiceInvertido(old);
            atualizarIndiceInvertido(p, p.getId());
        }

        return ok;
    }

    // ============================================
    // DELETE
    // ============================================

    @Override
    public boolean delete(int id) throws Exception {
        Produto p = super.read(id);
        if (p == null) return false;

        boolean ok = super.delete(id);
        if (ok) {
            indiceGtin.delete(p.getGtin13().hashCode());
            removerDoIndiceInvertido(p);
        }

        return ok;
    }

    // ============================================
    // SUPORTE AO ÍNDICE INVERTIDO
    // ============================================

    /** Adiciona o produto no índice invertido. */
    private void atualizarIndiceInvertido(Produto p, int id) {
        if (indiceInvertido == null) return;

        try {
            // Divide o nome do produto em palavras
            String[] termos = p.getNome().toLowerCase().split("[^a-z0-9áéíóúãõç]+");
            ArrayList<String> termosUnicos = new ArrayList<>();

            // Conta ocorrências (TF)
            for (String termo : termos) {
                if (termo.isBlank()) continue;
                long count = java.util.Arrays.stream(termos).filter(t -> t.equals(termo)).count();

                if (!termosUnicos.contains(termo)) {
                    indiceInvertido.create(termo, new ElementoLista(id, (float) count));
                    termosUnicos.add(termo);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao atualizar índice invertido: " + e.getMessage());
        }
    }

    /** Remove o produto do índice invertido. */
    private void removerDoIndiceInvertido(Produto p) {
        if (indiceInvertido == null) return;

        try {
            String[] termos = p.getNome().toLowerCase().split("[^a-z0-9áéíóúãõç]+");
            for (String termo : termos) {
                if (!termo.isBlank())
                    indiceInvertido.delete(termo, p.getId());
            }
        } catch (Exception e) {
            System.err.println("Erro ao remover do índice invertido: " + e.getMessage());
        }
    }

    // ============================================
    // OUTROS MÉTODOS
    // ============================================

    /** Lista todos os IDs ativos de produtos. */
    public ArrayList<Integer> listAllIds() throws Exception {
        ArrayList<Integer> out = new ArrayList<>();
        java.io.RandomAccessFile raf = this.arquivo;
        raf.seek(0);
        raf.readInt(); // pula último ID
        raf.readLong(); // pula lista de excluídos
        long pos = raf.getFilePointer();

        while (pos < raf.length()) {
            raf.seek(pos);
            byte lapide = raf.readByte();
            short tam = raf.readShort();
            long registroPos = pos + 3;
            if (lapide == ' ') {
                byte[] b = new byte[tam];
                raf.readFully(b);
                Produto px = Produto.class.getConstructor().newInstance();
                px.fromByteArray(b);
                out.add(px.getId());
            }
            pos = registroPos + tam;
        }
        return out;
    }

    /** Busca produto por GTIN-13 via índice hash. */
    public Produto readByGTIN(String gtin13) throws Exception {
        try {
            ParGtinID par = indiceGtin.read(gtin13.hashCode());
            if (par == null) return null;
            return super.read(par.getId());
        } catch (NullPointerException npe) {
            return null; // índice ainda não inicializado
        }
    }

    /** Retorna a quantidade total de produtos. */
    public int count() throws Exception {
        return listAllIds().size();
    }
}
