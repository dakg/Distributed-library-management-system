package Servers.McGill;

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
   
         Data.up.put("MCGU1111", "1111");
         Data.up.put("MCGM1111", "1111");
     }
    static void populate_bi() {
        Data.bi.put("MCG0001", new BookDetails("ds1", 5));
        Data.bi.put("MCG0002", new BookDetails("ds2", 2));
        Data.bi.put("MCG0003", new BookDetails("ds3", 4));
    }
}
