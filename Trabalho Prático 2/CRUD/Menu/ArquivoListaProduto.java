import aed3.Arquivo;
import aed3.ArvoreBMais;
import java.util.ArrayList;
import aed3.ParIDListaIDListaProduto;     // >>> necessário
import aed3.ParIDProdutoIDListaProduto; 

public class ArquivoListaProduto extends Arquivo<ListaProduto> {

    private final ArvoreBMais<ParIDListaIDListaProduto> indicePorLista;
    private final ArvoreBMais<ParIDProdutoIDListaProduto> indicePorProduto;

    public ArquivoListaProduto() throws Exception {
        super("listas_produtos", ListaProduto.class.getConstructor());
        indicePorLista = new ArvoreBMais<>(
            ParIDListaIDListaProduto.class.getConstructor(),
            5,
            ".\\dados\\listas_produtos\\idx_lista.db"
        );
        indicePorProduto = new ArvoreBMais<>(
            ParIDProdutoIDListaProduto.class.getConstructor(),
            5,
            ".\\dados\\listas_produtos\\idx_produto.db"
        );
    }

    @Override
    public int create(ListaProduto lp) throws Exception {
        int id = super.create(lp);
        // insere nas duas B+
        indicePorLista.create(new ParIDListaIDListaProduto(lp.getIdLista(), id));
        indicePorProduto.create(new ParIDProdutoIDListaProduto(lp.getIdProduto(), id));
        return id;
    }

    @Override
    public boolean update(ListaProduto lp) throws Exception {
        ListaProduto old = super.read(lp.getId());
        if (old == null) return false;

        boolean movedLista = old.getIdLista()!= lp.getIdLista();
        boolean movedProd = old.getIdProduto() != lp.getIdProduto();

        boolean ok = super.update(lp);

        if (ok) {
            if (movedLista) {
                indicePorLista.delete(new ParIDListaIDListaProduto(old.getIdLista(), lp.getId()));
                indicePorLista.create(new ParIDListaIDListaProduto(lp.getIdLista(), lp.getId()));
            }
            if (movedProd) {
                indicePorProduto.delete(new ParIDProdutoIDListaProduto(old.getIdProduto(), lp.getId()));
                indicePorProduto.create(new ParIDProdutoIDListaProduto(lp.getIdProduto(), lp.getId()));
            }
        }
        return ok;
    }

    @Override
    public boolean delete(int id) throws Exception {
        ListaProduto lp = super.read(id);
        if (lp == null) return false;
        boolean ok = super.delete(id);
        if (ok) {
            indicePorLista.delete(new ParIDListaIDListaProduto(lp.getIdLista(), id));
            indicePorProduto.delete(new ParIDProdutoIDListaProduto(lp.getIdProduto(), id));
        }
        return ok;
    }

    /** Retorna IDs de ListaProduto para uma lista específica (via B+). */
    public ArrayList<Integer> listarIdsPorLista(int idLista) throws Exception {
        ArrayList<Integer> out = new ArrayList<>();
        ArrayList<ParIDListaIDListaProduto> pares = indicePorLista.read(new ParIDListaIDListaProduto(idLista, -1));
        for (ParIDListaIDListaProduto p : pares) out.add(p.getIdListaProduto());
        return out;
    }

    /** Retorna IDs de ListaProduto para um produto específico (via B+). */
    public ArrayList<Integer> listarIdsPorProduto(int idProduto) throws Exception {
        ArrayList<Integer> out = new ArrayList<>();
        ArrayList<ParIDProdutoIDListaProduto> pares = indicePorProduto.read(new ParIDProdutoIDListaProduto(idProduto, -1));
        for (ParIDProdutoIDListaProduto p : pares) out.add(p.getIdListaProduto());
        return out;
    }
}
