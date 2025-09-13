import aed3.*;

public class ArquivoUsuario extends aed3.Arquivo<Usuario> {

    // arqUsuarios é redundante aqui, pois a própria classe herda de Arquivo<Usuario>
    // A classe pai, aed3.Arquivo, já lida com a parte de arquivos genéricos.
    HashExtensivel<ParEmailID> indiceIndiretoEmail;

    public ArquivoUsuario() throws Exception {
        // O nome do arquivo e o construtor da entidade (Usuario)
        super("usuarios", Usuario.class.getConstructor());
        
        // O índice indireto agora usa a nova classe ParEmailID.
        // Os caminhos dos arquivos de diretório e cestos também foram ajustados
        // para refletir o novo nome "usuarios" e o novo índice de email.
        indiceIndiretoEmail = new HashExtensivel<>(
            ParEmailID.class.getConstructor(), 
            4, 
            ".\\dados\\usuarios\\indiceEmail.d.db",  // diretório
            ".\\dados\\usuarios\\indiceEmail.c.db"   // cestos 
        );
    }

    @Override
    public int create(Usuario u) throws Exception {
        // Cria o registro no arquivo principal e obtém o ID
        int id = super.create(u);
        
        // Adiciona o par (email, id) no índice indireto
        indiceIndiretoEmail.create(new ParEmailID(u.getEmail(), id));
        return id;
    }

    public Usuario read(String email) throws Exception {
        // Busca o par (email, id) no índice indireto usando o hash do email
        ParEmailID pei = indiceIndiretoEmail.read(ParEmailID.hash(email));
        if(pei == null)
            return null;
            
        // Se encontrar, usa o ID para ler o registro completo do usuário
        return super.read(pei.getId());
    }
    
    public boolean delete(String email) throws Exception {
        // Busca o par (email, id) para obter o ID do usuário
        ParEmailID pei = indiceIndiretoEmail.read(ParEmailID.hash(email));
        if(pei != null) {
            // Se o registro for excluído do arquivo principal,
            if(super.delete(pei.getId())) {
                // remove também o par (email, id) do índice indireto
                return indiceIndiretoEmail.delete(ParEmailID.hash(email));
            }
        }
        return false;
    }

    @Override
    public boolean delete(int id) throws Exception {
        // Lê o registro para obter o email antes de excluí-lo
        Usuario u = super.read(id);
        if(u != null) {
            // Se o registro for excluído do arquivo principal,
            if(super.delete(id)) {
                // remove o par (email, id) do índice indireto
                return indiceIndiretoEmail.delete(ParEmailID.hash(u.getEmail()));
            }
        }
        return false;
    }

    @Override
    public boolean update(Usuario novoUsuario) throws Exception {
        // Lê o registro antigo para obter o email original
        Usuario usuarioAntigo = this.read(novoUsuario.getEmail());
        if(super.update(novoUsuario)) {
            // Se o email foi alterado, atualiza o índice
            if(!novoUsuario.getEmail().equals(usuarioAntigo.getEmail())) {
                indiceIndiretoEmail.delete(ParEmailID.hash(usuarioAntigo.getEmail()));
                indiceIndiretoEmail.create(new ParEmailID(novoUsuario.getEmail(), novoUsuario.getId()));
            }
            return true;
        }
        return false;
    }
}
