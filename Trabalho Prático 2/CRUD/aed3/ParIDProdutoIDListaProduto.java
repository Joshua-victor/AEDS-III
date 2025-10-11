package aed3;

import java.io.*;

public class ParIDProdutoIDListaProduto implements RegistroArvoreBMais<ParIDProdutoIDListaProduto> {
    private int idProduto;
    private int idListaProduto;

    public ParIDProdutoIDListaProduto() { this(-1, -1); }
    public ParIDProdutoIDListaProduto(int idProduto, int idListaProduto) {
        this.idProduto = idProduto;
        this.idListaProduto = idListaProduto;
    }

    public int getIdProduto() { return idProduto; }
    public int getIdListaProduto() { return idListaProduto; }

    @Override
    public int compareTo(ParIDProdutoIDListaProduto o) { return Integer.compare(this.idProduto, o.idProduto); }

    @Override
    public short size() { return (short) (4 + 4); }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(idProduto);
        dos.writeInt(idListaProduto);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.idProduto = dis.readInt();
        this.idListaProduto = dis.readInt();
    }

    @Override
    public ParIDProdutoIDListaProduto clone() { return new ParIDProdutoIDListaProduto(this.idProduto, this.idListaProduto); }

    @Override
    public String toString() { return "(" + idProduto + " -> " + idListaProduto + ")"; }
}
