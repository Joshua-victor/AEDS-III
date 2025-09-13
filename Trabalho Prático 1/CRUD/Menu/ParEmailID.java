import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParEmailID implements aed3.RegistroHashExtensivel<ParEmailID> {
    
    private String email; 
    private int id;
    private final short TAMANHO = 54; // 50 bytes para o email + 4 bytes para o id

    public ParEmailID() {
        this("", -1);
    }
    
    public ParEmailID(String email, int id) {
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
        return this.email.hashCode();
    }
    
    @Override
    public short size() {
        return this.TAMANHO;
    }

    @Override
    public String toString() {
        return "(" + this.email + ";" + this.id + ")";
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        byte[] emailBytes = new byte[TAMANHO - 4]; // TAMANHO_MAX_EMAIL
        byte[] emailStringBytes = this.email.getBytes("UTF-8");
        
        System.arraycopy(emailStringBytes, 0, emailBytes, 0, Math.min(emailStringBytes.length, TAMANHO - 4));
        
        dos.write(emailBytes);
        dos.writeInt(this.id);
        
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        
        byte[] emailBytes = new byte[TAMANHO - 4]; // TAMANHO_MAX_EMAIL
        dis.read(emailBytes);
        
        
        int i = emailBytes.length - 1;
        while (i >= 0 && emailBytes[i] == 0) {
            i--;
        }
        byte[] temp = new byte[i + 1];
        System.arraycopy(emailBytes, 0, temp, 0, i + 1);

        this.email = new String(temp, "UTF-8");
        this.id = dis.readInt();
    }
}
