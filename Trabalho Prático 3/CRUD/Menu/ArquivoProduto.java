import aed3.Arquivo;
import aed3.HashExtensivel;
import aed3.ParGtinID;
import aed3.TermoUtils;
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
    // ... (código GTIN e criação super.create(p)) ...
    int id = super.create(p);
    
    // Atualiza o índice hash
    // ...
    
    // Atualiza o índice invertido (nomes → produtos)
    atualizarIndiceInvertido(p, id);
    
    // 🚨 CORREÇÃO TP3: INCREMENTAR O CONTADOR DE ENTIDADES (N) 🚨
    indiceInvertido.incrementaEntidades();

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
        
        // 🚨 CORREÇÃO TP3: DECREMENTAR O CONTADOR DE ENTIDADES (N) 🚨
        indiceInvertido.decrementaEntidades();
    }
    return ok;
}



/**
 * Busca produtos por termos, calcula o score TFxIDF e retorna os IDs ordenados por relevância.
 * Retorna IDs de produtos ATIVOS, filtrando-os após a ordenação.
 * (Acessa diretamente o índice invertido e calcula o IDF)
 */


public java.util.ArrayList<Integer> searchByTerms(String query) throws Exception {

    // 1. Processamento da Consulta (Chama TermoUtils)
    java.util.List<String> termosConsulta = TermoUtils.processarTexto(query); // <-- Usa a função que você enviou
    if (termosConsulta.isEmpty()) {
        return new java.util.ArrayList<>();
    }

    // Map: Acumulador de Score (ID Produto -> Score TFxIDF Total)
    java.util.Map<Integer, Float> scores = new java.util.HashMap<>();
    int totalEntidades = indiceInvertido.numeroEntidades(); 

    if (totalEntidades == 0) return new java.util.ArrayList<>();

   
    for (String termo : termosConsulta) { 
        
        // Recupera a Lista Invertida para o termo
        ElementoLista[] listaTermo = indiceInvertido.read(termo);
        
        if (listaTermo.length == 0) continue; 
        
        // Calcula o IDF (Inverse Document Frequency)
        int produtosComOTermo = listaTermo.length; 
        float idf = (float) (Math.log((double) totalEntidades / produtosComOTermo) + 1.0);
        
        // 3. Multiplica TF por IDF e acumula o score
        for (ElementoLista el : listaTermo) {
            int idProduto = el.getId();
            float tf = el.getFrequencia(); // TF é o valor de frequência armazenado
            
            float scoreTermo = tf * idf;
            
            // Acumula (soma) o score: essencial para frases com múltiplos termos
            scores.put(idProduto, scores.getOrDefault(idProduto, 0.0f) + scoreTermo);
        }
    }
    
    // 4. Ordenar os resultados (por score, decrescente)
    java.util.List<java.util.Map.Entry<Integer, Float>> listaScores = 
        new java.util.ArrayList<>(scores.entrySet());

    listaScores.sort(java.util.Map.Entry.comparingByValue(java.util.Comparator.reverseOrder()));

    // 5. Retornar os IDs ordenados
    java.util.ArrayList<Integer> idsOrdenados = new java.util.ArrayList<>();
    for (java.util.Map.Entry<Integer, Float> entry : listaScores) {
        idsOrdenados.add(entry.getKey());
    }
    
    return idsOrdenados;
}

    // ============================================
    // SUPORTE AO ÍNDICE INVERTIDO
    // ============================================

    // Em ArquivoProduto.java (Substitua a função dele)

/** Adiciona o produto no índice invertido (corrigido para TP3). */
private void atualizarIndiceInvertido(Produto p, int id) {
    if (indiceInvertido == null) return;

    try {
       
        java.util.List<String> termosLimpos = TermoUtils.processarTexto(p.getNome());
        
        System.out.println("DEBUG: Indexando " + p.getNome() + " -> Termos: " + termosLimpos);

        if (termosLimpos.isEmpty()) {
            
             // indiceInvertido.decrementaEntidades(); // Se o produto foi criado e não indexado, pode precisar de ajuste de contagem
             return; 
        }

        // Conta ocorrências e calcula o TF
        java.util.Map<String, Integer> contagem = new java.util.HashMap<>();
        for (String termo : termosLimpos) {
            contagem.put(termo, contagem.getOrDefault(termo, 0) + 1);
        }
        
        int totalTermosValidos = termosLimpos.size();

        
        for (java.util.Map.Entry<String, Integer> entry : contagem.entrySet()) {
             String termo = entry.getKey();
             int freq = entry.getValue(); // Frequência bruta do termo

    
            float tf = (float) freq / totalTermosValidos; 
    
            // Tenta ler/atualizar (para o caso de updates onde só o TF muda)
            ElementoLista el = indiceInvertido.read(termo, p.getId());
            if (el != null) {
                el.setFrequencia(tf); // Agora salva a TAXA
                indiceInvertido.update(termo, el);
            } else {
            // Cria novo elemento
             indiceInvertido.create(termo, new ElementoLista(id, tf)); // Agora salva a TAXA
        }
}
        
        // Se a entidade for nova (chamada no create), incrementa a contagem de entidades (N)
        // Note: Se o create do seu amigo não chama incrementaEntidades(),
        // essa lógica precisa ser adicionada ao seu create(Produto p) também.
        
    } catch (Exception e) {
        System.err.println("Erro ao atualizar índice invertido: " + e.getMessage());
    }
}

    

/** Remove o produto do índice invertido (corrigido para usar TermoUtils). */
private void removerDoIndiceInvertido(Produto p) {
    if (indiceInvertido == null) return;

    try {
       
        java.util.List<String> termosLimpos = TermoUtils.processarTexto(p.getNome());
        
        for (String termo : termosLimpos) {
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
