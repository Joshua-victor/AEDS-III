import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParEmailID implements aed3.RegistroHashExtensivel<ParEmailID> {
    
    
    private String email; 
    private int id;     

    public ParEmailID() {
        this.email = ""; // Inicializa a string vazia.
        this.id = -1;
    }

    public ParEmailID(String email, int id) throws Exception {
        this.email = email;
        this.id = id;
    }

    
    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        //  método de hash usa o email.
        return hash(this.email);
    }
    
    // Este método a retorna o tamanho dinâmico do registro.
    @Override
    public short size() {
        
        return (short) (2 + this.email.length() + 4); 
    }

    @Override
    public String toString() {
        return "(" + this.email + ";" + this.id + ")";
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(this.email); 
        dos.writeInt(this.id);
        return baos.toByteArray();
    }

    @Override
public void fromByteArray(byte[] ba) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(ba);
    DataInputStream dis = new DataInputStream(bais);
    try {
        this.email = dis.readUTF();
        this.id = dis.readInt();
    } catch (IOException e) {
        // Se chegou no fim inesperadamente, limpa os campos
        this.email = "";
        this.id = -1;
        throw e; // ou apenas retorna, se quiser engolir o erro
    }
}


    public static int hash(String email) {
        return email.hashCode();
    }
}