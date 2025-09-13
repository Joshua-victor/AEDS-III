import aed3.*;
//import java.io.IOException;
import java.util.ArrayList;

public class ArquivoLista extends aed3.Arquivo<Lista> {
    
    // Usaremos a Árvore B+ como índice para o relacionamento 1:N
    // O índice irá armazenar pares (idUsuario, idLista)
    private ArvoreBMais<ParIDListaIDUsuario> indiceListas;

    public ArquivoLista() throws Exception {
        // O nome do arquivo e o construtor da entidade (Lista)
        super("listas", Lista.class.getConstructor());
        
        // Inicializa a Árvore B+
        indiceListas = new ArvoreBMais<>(
            ParIDListaIDUsuario.class.getConstructor(), 
            10, // Ordem da árvore, ajustável se necessário
            ".\\dados\\listas\\listas-rel.db"
        );
    }

    @Override
    public int create(Lista obj) throws Exception {
        // Cria a lista no arquivo principal e obtém o ID
        int id = super.create(obj);
        
        // Adiciona o par (idUsuario, idLista) no índice da Árvore B+
        indiceListas.create(new ParIDListaIDUsuario(obj.getIdUsuario(), id));
        
        return id;
    }

    // Método para buscar todas as listas de um usuário
    public Lista[] readByUserId(int idUsuario) throws Exception {
        // Cria um objeto de busca com o idUsuario para encontrar as listas
        ParIDListaIDUsuario p = new ParIDListaIDUsuario(idUsuario, -1);
        
        // Usa o método read() da ArvoreBMais para buscar todos os pares que
        // têm o mesmo idUsuario.
        ArrayList<ParIDListaIDUsuario> pares = indiceListas.read(p);
        
        // Se a lista de pares estiver vazia, retorna um array vazio
        if (pares.size() == 0) {
            return new Lista[0];
        }
        
        // Cria um array de Listas com o mesmo tamanho da lista de pares encontrada
        Lista[] listas = new Lista[pares.size()];
        
        // Percorre a lista de pares e busca cada lista no arquivo principal
        for (int i = 0; i < pares.size(); i++) {
            listas[i] = super.read(pares.get(i).idLista);
        }
        
        return listas;
    }

    @Override
    public boolean delete(int id) throws Exception {
        // Lê o registro para obter o idUsuario antes de excluí-lo
        Lista l = super.read(id);
        if (l != null) {
            // Se o registro for excluído do arquivo principal,
            if (super.delete(id)) {
                // Remove o par (idUsuario, idLista) do índice da Árvore B+
                return indiceListas.delete(new ParIDListaIDUsuario(l.getIdUsuario(), id));
            }
        }
        return false;
    }
    
    public Lista readByCode(String code) throws Exception {
        // Aqui, a forma mais simples de implementar a busca por código é com uma busca sequencial.
        // O trabalho prático sugere o uso de um índice, mas a implementação de um novo índice
        // (como uma Tabela Hash para o código) seria um passo adicional.
        // A busca sequencial é a mais simples para começar.
        
        long pos = 12; // Início do primeiro registro (após cabeçalho)
        while(pos < this.arquivo.length()) {
            this.arquivo.seek(pos);
            byte lapide = this.arquivo.readByte();
            short tamanho = this.arquivo.readShort();
            
            if(lapide == ' ') {
                byte[] dados = new byte[tamanho];
                this.arquivo.read(dados);
                Lista lista = this.construtor.newInstance();
                lista.fromByteArray(dados);
                
                if(lista.getCodigoCompartilhavel().equals(code)) {
                    return lista;
                }
            } else {
                this.arquivo.skipBytes(tamanho);
            }
            pos = this.arquivo.getFilePointer();
        }
        
        return null;
    }
}