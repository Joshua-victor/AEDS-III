package aed3;

import java.io.IOException;

public interface RegistroArvoreBMais<T> {

  public int compareTo(T o); 
  
  public byte[] toByteArray() throws IOException; 

  public void fromByteArray(byte[] ba) throws IOException; 

  public short size(); 
  
  public T clone(); 
  
}
