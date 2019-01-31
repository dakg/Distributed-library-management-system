package Servers.Montreal;

import java.util.HashMap;

/**
 *
 * @author daksh
 */
class BookDetails {
    String name;
    int quantity;

    BookDetails(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

}
 class Data {
    static HashMap<String, String> up= new HashMap();   //User id and Password Data
        static HashMap<String, BookDetails> bi = new HashMap();

} 
 class PopulateData {
     static void populate(){
        populate_up();
        populate_bi();
    }
    
    static void populate_up(){
      
         Data.up.put("MONU1111", "1111");
         Data.up.put("MONM1111", "1111");
         Data.up.put("MONM2222", "1111");
     }
    static void populate_bi() {
        Data.bi.put("MON0001", new BookDetails("ds1", 5));
        Data.bi.put("MON0002", new BookDetails("ds2", 2));
        Data.bi.put("MON0001", new BookDetails("ds3", 4));
    }
}
