package Menu;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Produto implements Registro {
    private int id;
    private String gtin13;
    private String nome;
    private String descricao;
    private boolean ativo;

    public Produto() { this(-1, "", "", "", true); }
    public Produto(int id, String gtin13, String nome, String descricao, boolean ativo) {
        this.id = id;
        this.gtin13 = gtin13 == null ? "" : gtin13;
        this.nome = nome == null ? "" : nome;
        this.descricao = descricao == null ? "" : descricao;
        this.ativo = ativo;
    }

    @Override public int getId() { return id; }
    @Override public void setId(int id) { this.id = id; }

    public String getGtin13() { return gtin13; }
    public void setGtin13(String g) { this.gtin13 = g == null ? "" : g; }
    public String getNome() { return nome; }
    public void setNome(String n) { this.nome = n == null ? "" : n; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String d) { this.descricao = d == null ? "" : d; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean a) { this.ativo = a; }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        writeString(dos, gtin13);
        writeString(dos, nome);
        writeString(dos, descricao);
        dos.writeBoolean(ativo);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.gtin13 = readString(dis);
        this.nome = readString(dis);
        this.descricao = readString(dis);
        this.ativo = dis.readBoolean();
    }

    private static void writeString(DataOutputStream dos, String s) throws IOException {
        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        dos.writeShort(b.length);
        dos.write(b);
    }
    private static String readString(DataInputStream dis) throws IOException {
        int len = dis.readShort();
        byte[] b = new byte[len];
        dis.readFully(b);
        return new String(b, StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        return "Produto{id=" + id + ", gtin=" + gtin13 + ", nome='" + nome + "', ativo=" + ativo + "}";
    }
}
