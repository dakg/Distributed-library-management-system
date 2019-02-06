/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servers;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author daksh
 */
public class startRegistry {
    public static void main(String arg[])
    {
        try{
        LocateRegistry.createRegistry(1099);    
        Registry registry = LocateRegistry.getRegistry(1099); 
        while(true)
        {
            
        }
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
    }
}
