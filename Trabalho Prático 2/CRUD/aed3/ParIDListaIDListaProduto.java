package aed3;

import java.io.*;

public class ParIDListaIDListaProduto implements RegistroArvoreBMais<ParIDListaIDListaProduto> {
    private int idLista;
    private int idListaProduto;

    public ParIDListaIDListaProduto() { this(-1, -1); }
    public ParIDListaIDListaProduto(int idLista, int idListaProduto) {
        this.idLista = idLista;
        this.idListaProduto = idListaProduto;
    }

    public int getIdLista() { return idLista; }
    public int getIdListaProduto() { return idListaProduto; }

    @Override
    public int compareTo(ParIDListaIDListaProduto o) { return Integer.compare(this.idLista, o.idLista); }

    @Override
    public short size() { return (short) (4 + 4); }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(idLista);
        dos.writeInt(idListaProduto);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        this.idLista = dis.readInt();
        this.idListaProduto = dis.readInt();
    }

    @Override
    public ParIDListaIDListaProduto clone() { return new ParIDListaIDListaProduto(this.idLista, this.idListaProduto); }

    @Override
    public String toString() { return "(" + idLista + " -> " + idListaProduto + ")"; }
}
