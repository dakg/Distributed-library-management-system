/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servers.Concordia;

import java.util.List;

/**
 *
 * @author daksh
 */
 public class printHashmaps {
     
    public static void print(){
        System.out.println(Data.bookInformation.size());
        for(String key : Data.borrowDetails.keySet())
        {
            List l  = Data.borrowDetails.get(key);
            System.out.println(key + " " + l);
        }
    }
}
