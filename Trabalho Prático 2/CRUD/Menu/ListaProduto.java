package Menu;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ListaProduto implements Registro {
    private int id;
    private int idLista;
    private int idProduto;
    private int quantidade;
    private String observacoes;

    public ListaProduto() { this(-1, -1, -1, 1, ""); }
    public ListaProduto(int id, int idLista, int idProduto, int quantidade, String observacoes) {
        this.id = id;
        this.idLista = idLista;
        this.idProduto = idProduto;
        this.quantidade = Math.max(1, quantidade);
        this.observacoes = observacoes == null ? "" : observacoes;
    }

    @Override public int getId() { return id; }
    @Override public void setId(int id) { this.id = id; }

    public int getIdLista() { return idLista; }
    public void setIdLista(int idLista) { this.idLista = idLista; }
    public int getIdProduto() { return idProduto; }
    public void setIdProduto(int idProduto) { this.idProduto = idProduto; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = Math.max(1, quantidade); }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String o) { this.observacoes = o == null ? "" : o; }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeInt(idLista);
        dos.writeInt(idProduto);
        dos.writeInt(quantidade);
        writeString(dos, observacoes);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.idLista = dis.readInt();
        this.idProduto = dis.readInt();
        this.quantidade = dis.readInt();
        this.observacoes = readString(dis);
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
        return "ListaProduto{id="+id+", lista="+idLista+", produto="+idProduto+", qtd="+quantidade+"}";
    }
}
