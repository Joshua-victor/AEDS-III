import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import aed3.Registro;

public class Lista implements Registro {

    public int id;
    public String nome;
    public String descricao;
    public long dataCriacao;
    public long dataLimite;
    public String codigoCompartilhavel;
    public int idUsuario;

    public Lista() {
        this(-1, "", "", LocalDate.now(), null, "", -1);
    }
    
    public Lista(int id, String nome, String descricao, LocalDate dataCriacao, LocalDate dataLimite, String codigoCompartilhavel, int idUsuario) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.dataCriacao = dataCriacao.toEpochDay();
        this.dataLimite = (dataLimite != null) ? dataLimite.toEpochDay() : -1;
        this.codigoCompartilhavel = codigoCompartilhavel;
        this.idUsuario = idUsuario;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getCodigoCompartilhavel() {
        return this.codigoCompartilhavel;
    }

    @Override
    public String toString() {
        LocalDate dataCriacaoLocal = LocalDate.ofEpochDay(this.dataCriacao);
        LocalDate dataLimiteLocal = (this.dataLimite != -1) ? LocalDate.ofEpochDay(this.dataLimite) : null;
        
        String dados = "\nID........: " + this.id +
                       "\nNome......: " + this.nome +
                       "\nDescrição.: " + this.descricao +
                       "\nCriação...: " + dataCriacaoLocal.toString() +
                       "\nCompartilhar: " + this.codigoCompartilhavel +
                       "\nID Usuário: " + this.idUsuario;

        if (dataLimiteLocal != null) {
            dados += "\nLimite....: " + dataLimiteLocal.toString();
        }
        
        return dados;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeUTF(this.nome);
        dos.writeUTF(this.descricao);
        dos.writeLong(this.dataCriacao);
        dos.writeLong(this.dataLimite);
        dos.writeUTF(this.codigoCompartilhavel);
        dos.writeInt(this.idUsuario);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.nome = dis.readUTF();
        this.descricao = dis.readUTF();
        this.dataCriacao = dis.readLong();
        this.dataLimite = dis.readLong();
        this.codigoCompartilhavel = dis.readUTF();
        this.idUsuario = dis.readInt();
    }
}