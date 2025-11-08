package aed3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParIDListaIDUsuario implements aed3.RegistroArvoreBMais<ParIDListaIDUsuario>, Cloneable {

    
    public int idLista;
    public int idUsuario;
    private final short TAMANHO = 8; // 4 bytes para o idLista + 4 bytes para o idUsuario

    public ParIDListaIDUsuario() {
        this.idLista = -1;
        this.idUsuario = -1;
    }
    
    public ParIDListaIDUsuario(int idUsuario, int idLista) {
        this.idLista = idLista;
        this.idUsuario = idUsuario;
    }

    public int getId() {
        return this.idLista;
    }
    
    public void setId(int id) {
        this.idLista = id;
    }

    @Override
    public int getPrimeiraChave() {
        return this.idUsuario; // Chave de busca para esse índice
    }
    
    @Override
    public int compareTo(ParIDListaIDUsuario o) {
        // A comparação é feita primeiro pelo idUsuario e depois pelo idLista
        // Isso é crucial para que a Árvore B+ armazene os pares corretamente.
        if (this.idUsuario != o.idUsuario) {
            return this.idUsuario - o.idUsuario;
        }
        return this.idLista - o.idLista;
    }
    
    @Override
    public short size() {
        return this.TAMANHO;
    }
    
    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.idUsuario);
        dos.writeInt(this.idLista);
        return baos.toByteArray();
    }
    
    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.idUsuario = dis.readInt();
        this.idLista = dis.readInt();
    }
    

   

@Override
public ParIDListaIDUsuario clone() {
  
    try {
        return (ParIDListaIDUsuario) super.clone();
    } catch (CloneNotSupportedException e) {
        // Se a clonagem não for suportada, lance uma exceção em vez de retornar nulo.
        throw new InternalError(e);
    }
}
}
