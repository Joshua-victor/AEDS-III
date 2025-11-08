import aed3.*;
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
            8,
            ".\\dados\\listas\\indiceListas.db"
        );
    }

    @Override
    public int create(Lista obj) throws Exception {
        // Cria a lista no arquivo principal e obtém o ID
        int id = super.create(obj);

        // Adiciona o par (idUsuario, idLista) no índice da Árvore B+
        indiceListas.create(new ParIDListaIDUsuario(obj.idUsuario, id));

        // (removidos prints de debug para não exibir IDs ao usuário)
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
            int idLista = pares.get(i).idLista;
            listas[i] = super.read(idLista);
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
                return indiceListas.delete(new ParIDListaIDUsuario(l.idUsuario, id));
            }
        }
        return false;
    }

    /**
     * Busca sequencial por código compartilhável.
     * Mantém o padrão do seu Arquivo base:
     *  - cabeçalho 12 bytes
     *  - cada registro: lápide(1) + tamanho(2) + dados(tamanho)
     */
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

            if (lapide == ' ') { // registro válido
                byte[] dados = new byte[tamanho];
                this.arquivo.read(dados);

                Lista lista = this.construtor.newInstance();
                lista.fromByteArray(dados);

                if (code.equals(lista.codigoCompartilhavel)) {
                    return lista;
                }
            } else {
                // registro excluído (lápide != ' ')
                this.arquivo.skipBytes(tamanho);
            }
            pos = this.arquivo.getFilePointer();
        }
        
        return null;
    }

    /**
     * Atualiza a lista no arquivo principal e sincroniza o índice B+
     * caso o dono (idUsuario) seja alterado.
     */
    public boolean update(Lista nova) throws Exception {
        // Lê a versão antiga para verificar se houve troca de dono
        Lista antiga = super.read(nova.id);
        if (antiga == null) return false;

        // Atualiza o registro no arquivo principal (preserva layout binário)
        boolean ok = super.update(nova);

        if (ok) {
            // Se o idUsuario mudou, reflete no índice B+
            if (antiga.idUsuario != nova.idUsuario) {
                // Remove par antigo e cria o novo
                indiceListas.delete(new ParIDListaIDUsuario(antiga.idUsuario, nova.id));
                indiceListas.create(new ParIDListaIDUsuario(nova.idUsuario, nova.id));
            }
        }
        return ok;
    }
}
