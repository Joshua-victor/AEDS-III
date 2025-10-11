package aed3;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ParGtinID implements RegistroHashExtensivel<ParGtinID> {
    private String gtin13;
    private int id;

    public ParGtinID() { this("", -1); }
    public ParGtinID(String gtin13, int id) {
        this.gtin13 = gtin13 == null ? "" : gtin13;
        this.id = id;
    }

    public String getGtin13() { return gtin13; }
    public void setGtin13(String g) { this.gtin13 = g == null ? "" : g; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @Override
    public int hashCode() { return gtin13 == null ? 0 : gtin13.hashCode(); }

    @Override
    public short size() {
        int len = gtin13.getBytes(StandardCharsets.UTF_8).length;
        return (short)(2 + len + 4);
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        byte[] s = gtin13.getBytes(StandardCharsets.UTF_8);
        dos.writeShort(s.length);
        dos.write(s);
        dos.writeInt(id);
        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);
        int len = dis.readShort();
        byte[] s = new byte[len];
        dis.readFully(s);
        this.gtin13 = new String(s, StandardCharsets.UTF_8);
        this.id = dis.readInt();
    }

    @Override
    public ParGtinID clone() { return new ParGtinID(this.gtin13, this.id); }

    @Override
    public String toString() { return "(" + gtin13 + " -> " + id + ")"; }
}
