import aed3.Arquivo;
import aed3.HashExtensivel;
import aed3.ParGtinID; 

public class ArquivoProduto extends Arquivo<Produto> {

    private final HashExtensivel<ParGtinID> indiceGtin;

    public ArquivoProduto() throws Exception {
        super("produtos", Produto.class.getConstructor());

        // cria a pasta do índice de produtos, se não existir
        java.io.File dir = new java.io.File(".\\dados\\produtos");
        if (!dir.exists()) dir.mkdirs();

        this.indiceGtin = new HashExtensivel<>(
            ParGtinID.class.getConstructor(),
            4,
            ".\\dados\\produtos\\gtin.d.db",
            ".\\dados\\produtos\\gtin.c.db"
        );
    }


    @Override
    public int create(Produto p) throws Exception {
        // GTIN único (se índice ainda não está ok, consideramos "não encontrado")
        ParGtinID probe = null;
        try {
            probe = indiceGtin.read(p.getGtin13().hashCode());
        } catch (NullPointerException ignore) {
            // deixa probe=null
        }
        if (probe != null) {
            throw new Exception("GTIN-13 já cadastrado.");
        }

        int id = super.create(p);

        // Tenta gravar no índice; se falhar por NPE, ainda assim o produto foi criado
        try {
            indiceGtin.create(new ParGtinID(p.getGtin13(), id));
        } catch (NullPointerException ignore) {
            // índice será reconstruído/atualizado em próxima execução, se necessário
        }
        return id;
    }


    @Override
    public boolean update(Produto p) throws Exception {
        Produto old = super.read(p.getId());
        if (old == null) return false;

        // se trocou GTIN, atualiza índice
        if (!old.getGtin13().equals(p.getGtin13())) {
            // não permitir duplicar GTIN
            ParGtinID dup = indiceGtin.read(p.getGtin13().hashCode());
            if (dup != null) throw new Exception("GTIN-13 já cadastrado para outro produto.");

            // remove o antigo e insere o novo
            indiceGtin.delete(old.getGtin13().hashCode());
            indiceGtin.create(new ParGtinID(p.getGtin13(), p.getId()));
        }

        return super.update(p);
    }

    @Override
    public boolean delete(int id) throws Exception {
        Produto p = super.read(id);
        if (p == null) return false;

        // regra do TP2: não vamos excluir produto por padrão (usar inativar).
        // Se ainda assim precisar, mantemos índice consistente:
        boolean ok = super.delete(id);
        if (ok) {
            indiceGtin.delete(p.getGtin13().hashCode());
        }
        return ok;
    }

    /** Lista todos os IDs percorrendo o arquivo base. Útil para paginação/menus. */
    public java.util.ArrayList<Integer> listAllIds() throws Exception {
        java.util.ArrayList<Integer> out = new java.util.ArrayList<>();
        // percorre usando o índice direto (id -> endereço) não está exposto publicamente,
        // então vamos varrer o arquivo físico como em Arquivo.create/update.
        java.io.RandomAccessFile raf = this.arquivo; // protegido em Arquivo<T>
        raf.seek(0);
        raf.readInt(); // pula último ID
        long listaExcluidos = raf.readLong(); // não usado aqui
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

    /** Leitura por GTIN-13 via índice hash. */
    public Produto readByGTIN(String gtin13) throws Exception {
        try {
            ParGtinID par = indiceGtin.read(gtin13.hashCode());
            if (par == null) return null;
            return super.read(par.getId());
        } catch (NullPointerException npe) {
            // Índice ainda não inicializado/arquivos ausentes → trata como "não achou"
            return null;
        }
    }

}
