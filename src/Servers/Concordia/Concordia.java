package Servers.Concordia;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author daksh
 */
public class Concordia extends CONOperationsImplementation{
    public Concordia(){}
    public static void main(String arg[])
    {
         if (System.getSecurityManager() == null) {
         //   System.setProperty("java.rmi.server.codebase","file:\\C:\\Users\\daksh\\Documents\\NetBeansProjects\\DS Assignment1\\src\\Servers\\Concordia\\java.policy" );
            System.setProperty("java.security.policy","file:\\C:\\Users\\daksh\\Documents\\NetBeansProjects\\DS Assignment1\\src\\Servers\\Concordia\\java.policy");
    //        System.setProperty("java.rmi.server.hostname","127.0.0.1");
            System.setSecurityManager(new SecurityManager());
        }
        try{
            PopulateData.populate();
            CONOperationsImplementation obj = new CONOperationsImplementation();
            int port =1111;
            CONOperations stub  = (CONOperations)UnicastRemoteObject.exportObject(obj, port);
            LocateRegistry.createRegistry(port);
           // Naming.rebind("//localhost:1111/CONImp", stub);
            Registry registry = LocateRegistry.getRegistry(port); 
            registry.rebind("//localhost:" + port + "/CONImp", stub);  

       //     System.err.println(java.rmi.server.);
            System.err.println("Server ready at : " + port); 
        }
       catch (Exception e) { 
         System.err.println("Server exception: " + e.toString()); 
         e.printStackTrace(); 
      }
    }
    
    private static void receive()
    {
        DatagramSocket aSocket = null;
        try{
            aSocket=new DatagramSocket(6000);
            byte buffer[]=new byte[1000];
            System.out.println("Server ready to receive at 6000");
            while(true)
            {
                DatagramPacket request = new DatagramPacket(buffer,buffer.length);
                aSocket.receive(request);
                String replyStr ="Got your message";
                byte replyByte[] = replyStr.getBytes();
                DatagramPacket reply = new DatagramPacket(replyByte,replyByte.length,request.getAddress(),request.getPort());
                aSocket.send(reply);
            }
        }catch(Exception e)
        {
            
        }
        
    }
    
    
    
}
