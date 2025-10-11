package Menu;

import aed3.*;
import java.util.ArrayList;

public class ArquivoListaProduto extends Arquivo<ListaProduto> {
    private ArvoreBMais<ParIDListaIDListaProduto> indicePorLista;
    private ArvoreBMais<ParIDProdutoIDListaProduto> indicePorProduto;

    public ArquivoListaProduto() throws Exception {
        super(ListaProduto.class.getConstructor(),
             ".\\dados\\listas_produtos\\listas_produtos.db");
        this.indicePorLista = new ArvoreBMais<>(ParIDListaIDListaProduto.class.getConstructor(),
             ".\\dados\\listas_produtos\\idx_lista.d.db",
             ".\\dados\\listas_produtos\\idx_lista.c.db");
        this.indicePorProduto = new ArvoreBMais<>(ParIDProdutoIDListaProduto.class.getConstructor(),
             ".\\dados\\listas_produtos\\idx_produto.d.db",
             ".\\dados\\listas_produtos\\idx_produto.c.db");
    }

    public int create(ListaProduto lp) throws Exception {
        int id = super.create(lp);
        indicePorLista.create(new ParIDListaIDListaProduto(lp.getIdLista(), id));
        indicePorProduto.create(new ParIDProdutoIDListaProduto(lp.getIdProduto(), id));
        return id;
    }

    public boolean update(ListaProduto lp) throws Exception {
        ListaProduto old = super.read(lp.getId());
        if (old == null) return false;
        boolean ok = super.update(lp);
        if (ok && (old.getIdLista() != lp.getIdLista() || old.getIdProduto() != lp.getIdProduto())) {
            indicePorLista.delete(old.getIdLista(), new ParIDListaIDListaProduto(old.getIdLista(), lp.getId()));
            indicePorProduto.delete(old.getIdProduto(), new ParIDProdutoIDListaProduto(old.getIdProduto(), lp.getId()));
            indicePorLista.create(new ParIDListaIDListaProduto(lp.getIdLista(), lp.getId()));
            indicePorProduto.create(new ParIDProdutoIDListaProduto(lp.getIdProduto(), lp.getId()));
        }
        return ok;
    }

    public boolean delete(int id) throws Exception {
        ListaProduto lp = super.read(id);
        if (lp == null) return false;
        boolean ok = super.delete(id);
        if (ok) {
            indicePorLista.delete(lp.getIdLista(), new ParIDListaIDListaProduto(lp.getIdLista(), id));
            indicePorProduto.delete(lp.getIdProduto(), new ParIDProdutoIDListaProduto(lp.getIdProduto(), id));
        }
        return ok;
    }

    public java.util.List<Integer> listarIdsPorLista(int idLista) throws Exception {
        java.util.ArrayList<Integer> ids = new java.util.ArrayList<>();
        for (ParIDListaIDListaProduto par : indicePorLista.readAll(new ParIDListaIDListaProduto(idLista, -1))) {
            ids.add(par.getIdListaProduto());
        }
        return ids;
    }
    public java.util.List<Integer> listarIdsPorProduto(int idProduto) throws Exception {
        java.util.ArrayList<Integer> ids = new java.util.ArrayList<>();
        for (ParIDProdutoIDListaProduto par : indicePorProduto.readAll(new ParIDProdutoIDListaProduto(idProduto, -1))) {
            ids.add(par.getIdListaProduto());
        }
        return ids;
    }
}
