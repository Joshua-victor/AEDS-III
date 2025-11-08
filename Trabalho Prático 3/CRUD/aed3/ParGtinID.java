package aed3;

import java.io.*;

public class ParGtinID implements RegistroHashExtensivel<ParGtinID> {
    private String gtin13; // exatamente 13 dígitos
    private int id;

    public ParGtinID() { this("", -1); }
    public ParGtinID(String gtin13, int id) {
        setGtin13(gtin13);
        this.id = id;
    }

    public String getGtin13() { return gtin13; }
    public void setGtin13(String g) {
        if (g == null) g = "";
        g = g.replaceAll("\\D", "");        // só dígitos
        if (g.length() > 13) g = g.substring(0, 13);
        // pad à esquerda com '0' até 13, para manter exatamente 13 bytes
        this.gtin13 = String.format("%13s", g).replace(' ', '0');
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @Override
    public int hashCode() { return gtin13 == null ? 0 : gtin13.hashCode(); }

   
    @Override
    public short size() { return (short) (13 + 4); }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(17);
        DataOutputStream dos = new DataOutputStream(baos);
        // escreve exatamente 13 bytes (ASCII) do GTIN
        for (int i = 0; i < 13; i++) dos.writeByte(gtin13.charAt(i));
        dos.writeInt(id);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(ba));
        StringBuilder sb = new StringBuilder(13);
        for (int i = 0; i < 13; i++) sb.append((char) dis.readByte());
        this.gtin13 = sb.toString();
        this.id = dis.readInt();
    }

    @Override
    public ParGtinID clone() { return new ParGtinID(this.gtin13, this.id); }

    @Override
    public String toString() { return "(" + gtin13 + " -> " + id + ")"; }
}
