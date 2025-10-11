
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import aed3.Registro;

public class Usuario implements Registro {

    public int id;
    public String nome;
    public String email;
    public String perguntaSecreta;
    public String respostaSecreta;
    public String hashSenha;

    public Usuario() {
        this(-1, "", "", "", "", "");
    }
    
    public Usuario(String nome, String email, String perguntaSecreta, String respostaSecreta, String hashSenha) {
        this(-1, nome, email, perguntaSecreta, respostaSecreta, hashSenha);
    }

    public Usuario(int id, String nome, String email, String perguntaSecreta, String respostaSecreta, String hashSenha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.perguntaSecreta = perguntaSecreta;
        this.respostaSecreta = respostaSecreta;
        this.hashSenha = hashSenha;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }
    
    public String getEmail() {
        return this.email;
    }

    @Override
    public String toString() {
        return "\nID..............: " + this.id +
               "\nNome............: " + this.nome +
               "\nE-mail..........: " + this.email +
               "\nPergunta Secreta: " + this.perguntaSecreta +
               "\nResposta Secreta: " + this.respostaSecreta +
               "\nHash da Senha...: " + this.hashSenha;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeUTF(this.nome);
        dos.writeUTF(this.email);
        dos.writeUTF(this.perguntaSecreta);
        dos.writeUTF(this.respostaSecreta);
        dos.writeUTF(this.hashSenha);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.email = dis.readUTF();
        this.perguntaSecreta = dis.readUTF();
        this.respostaSecreta = dis.readUTF();
        this.hashSenha = dis.readUTF();
    }
}
