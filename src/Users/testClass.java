/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Users;

import java.io.IOException;
import Servers.Concordia.CONOperations;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daksh
 */
public class testClass {

    static String tokenGenerator(String ID) {

        String id = ID.toUpperCase();
        String token = "";
        if (id.length() == 8) {
            if (id.substring(0, 4).equals("CONU")) {
                token = token + "CU";
            } else if (id.substring(0, 4).equals("MCGU")) {
                token = token + "GU";
            } else if (id.substring(0, 4).equals("MONU")) {
                token = token + "MU";
            } else if (id.substring(0, 4).equals("CONM")) {
                token = token + "CM";
            } else if (id.substring(0, 4).equals("MCGM")) {
                token = token + "GM";
            } else if (id.substring(0, 4).equals("MONM")) {
                token = token + "MM";
            }
            return token;
        }
        return "Invalid ID";

    }

    public static void main(String arg[]) throws IOException {
        try {
            final String userid[] = {"CONU1111", "CONU4444", "CONU2222", "CONM1111", "CONM2222", "CONU3333"};
            final String bookid[] = {"CON1000", "MON0001", "MON1001", "MCG1000"};
//            for (int i = 0; i < userid.length; i++) {
//                for (int j = 0; j < bookid.length; j++) {
//                    Registry registry = LocateRegistry.getRegistry(1099);
//                    CONOperations stub = (CONOperations) registry.lookup("//localhost:1099/CONImp");
//                    int r = stub.returnItem(userid[i], bookid[j]);
//                    System.out.println(userid[i] + " " + bookid[j] + " " + r );
//                }
//            }

            Registry registry = LocateRegistry.getRegistry(1099);
            CONOperations stub = (CONOperations) registry.lookup("//localhost:1099/CONImp");
            //      Thread t = new Thread(() -> stub.addItem("CONM1111", "CON6666", "ds6", 5);).start();
            Runnable r = () -> {
                try {
                    stub.addItem("CONM1111", "CON6666", "ds6", 5);
                } catch (RemoteException ex) {
                    Logger.getLogger(testClass.class.getName()).log(Level.SEVERE, null, ex);
                }
            };
            Runnable r1 = () -> {
                try {
                    stub.addItem("CONM1111", "CON6668", "ds8", 5);
                } catch (RemoteException ex) {
                    Logger.getLogger(testClass.class.getName()).log(Level.SEVERE, null, ex);
                }
            };
            Runnable r2 = () -> {
                try {
                    stub.addItem("CONM1111", "CON6667", "ds7", 5);
                } catch (RemoteException ex) {
                    Logger.getLogger(testClass.class.getName()).log(Level.SEVERE, null, ex);
                }
            };
            Thread t1 = new Thread(r);
            Thread t2 = new Thread(r1);
            Thread t3 = new Thread(r2);
            t1.start();
            t2.start();
            t3.start();
            //   testThread();
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

}
