package Menu;

import aed3.*;

public class ArquivoProduto extends Arquivo<Produto> {

    private HashExtensivel<ParGtinID> indiceGtin;

    public ArquivoProduto() throws Exception {
        super(Produto.class.getConstructor(),
              ".\\dados\\produtos\\produtos.db");
        this.indiceGtin = new HashExtensivel<>(ParGtinID.class.getConstructor(),
                ".\\dados\\produtos\\indiceGtin.d.db",
                ".\\dados\\produtos\\indiceGtin.c.db");
    }

    public int create(Produto p) throws Exception {
        ParGtinID probe = new ParGtinID(p.getGtin13(), -1);
        ParGtinID found = indiceGtin.read(probe.hashCode(), probe);
        if (found != null && found.getGtin13().equals(p.getGtin13()))
            throw new IllegalArgumentException("GTIN-13 já cadastrado: " + p.getGtin13());

        int id = super.create(p);
        indiceGtin.create(new ParGtinID(p.getGtin13(), id));
        return id;
    }

    public boolean update(Produto p) throws Exception {
        Produto old = super.read(p.getId());
        if (old == null) return false;
        if (!old.getGtin13().equals(p.getGtin13())) {
            ParGtinID probe = new ParGtinID(p.getGtin13(), -1);
            ParGtinID dup = indiceGtin.read(probe.hashCode(), probe);
            if (dup != null && dup.getGtin13().equals(p.getGtin13()))
                throw new IllegalArgumentException("GTIN-13 já cadastrado: " + p.getGtin13());
            indiceGtin.delete(old.getGtin13().hashCode(), new ParGtinID(old.getGtin13(), old.getId()));
            indiceGtin.create(new ParGtinID(p.getGtin13(), p.getId()));
        }
        return super.update(p);
    }

    public boolean delete(int id) throws Exception {
        Produto p = super.read(id);
        if (p == null) return false;
        boolean ok = super.delete(id);
        if (ok) {
            indiceGtin.delete(p.getGtin13().hashCode(), new ParGtinID(p.getGtin13(), id));
        }
        return ok;
    }

    public Produto readByGTIN(String gtin13) throws Exception {
        ParGtinID out = indiceGtin.read(gtin13.hashCode(), new ParGtinID(gtin13, -1));
        if (out == null) return null;
        return super.read(out.getId());
    }

    // utilitário para listagem
    public int[] listAllIds() throws Exception {
        return super.listAllIds();
    }
}
