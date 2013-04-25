import java.util.Hashtable;

/**
 * Simple key Class
 *
 * @author
 * @version
 */
class Key {
   private int i;
   private String s;

   Key(int i, String s) {
      this.i = i;
      this.s = s;
   }

   public int getI() {
      return i;
   }

   public String getS() {
      return s;
   }

   public boolean equals(Object o) {
      // TODO: Add null check
      Key k = (Key) o;
      if(i == k.i && s.equals(k.s))
         return true;
      return false;
   }

   public int hashCode() {
      return i+s.hashCode(); // Better hashCode
   }
}

public class TestHash1 {
   public static void main(String[] args) {
      Hashtable ht = new Hashtable();

      ht.put(new Key(10, "I am 10"), "Value of 10");
      System.out.println("Get with Same key different object: " + ht.get(new Key(10, "I am 10")));
      System.out.println("Get with another key: " + ht.get(new Key(11, "I am 10")));
   }
}
