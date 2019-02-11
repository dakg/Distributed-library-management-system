package Servers.Montreal;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author daksh
 */
public class Montreal {

    static String myIPAddress = "localhost";
    static int MYPORT = 8000;
    static int CONPORT = 6000;
    static int MCGPORT = 7000;

    public static void main(String arg[]) {
        if (System.getSecurityManager() == null) {
            //   System.setProperty("java.rmi.server.codebase","file:\\C:\\Users\\daksh\\Documents\\NetBeansProjects\\DS Assignment1\\src\\Servers\\Concordia\\java.policy" );
            System.setProperty("java.security.policy", "file:\\C:\\Users\\daksh\\Documents\\NetBeansProjects\\DS Assignment1\\src\\Servers\\Montreal\\java.policy");
            System.setSecurityManager(new SecurityManager());
        }
        try {
            PopulateData.populate();
            MONOperationsImplementation obj = new MONOperationsImplementation();
            int port = 1099;
            MONOperations stub = (MONOperations) UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.getRegistry(port);
            registry.rebind("//localhost:" + port + "/MONImp", stub);
            System.err.println("Server ready at : " + port);
            receive(obj);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private static void receive(MONOperationsImplementation obj) {
        DatagramSocket aSocket = null;
        try {
            int receivePort = MYPORT;
            aSocket = new DatagramSocket(receivePort);
            byte buffer[];
            System.out.println("Server ready to receive at : " + receivePort);
            while (true) {
                buffer = new byte[2048];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String s = new String(request.getData()).trim();
                Runnable r = () -> {
                    try {
                        System.out.println("Received Packet : " + s);
                        processData(s,obj);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Montreal.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Montreal.class.getName()).log(Level.SEVERE, null, ex);
                    }
                };
                new Thread(r).start();
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    static void sendMessage(String msg, int destPort) throws UnknownHostException, SocketException, IOException {

        try {
            String address = myIPAddress + "," + MYPORT;
            DatagramSocket sSocket = new DatagramSocket();
            byte buffer[] = new byte[2048];
            msg = address + "," + msg;
            buffer = msg.getBytes();
            InetAddress aHost = InetAddress.getByName("localhost");
            DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, aHost, destPort);
            System.out.println("Send Packet : " + msg);
            sSocket.send(sendPacket);

        } catch (SocketException ex) {
        } catch (IOException ex) {
        }
    }

    static void processData(String receivedData,MONOperationsImplementation obj1) throws InterruptedException, SocketException, IOException {

        String refinedReceivedData = receivedData.trim();
        String s[] = refinedReceivedData.split(",");
        System.out.println(refinedReceivedData);
        String ipAddress = s[0];
        int port = Integer.parseInt(s[1]);
        String messageID = s[2];
        int type = Integer.parseInt(s[3]);

        if (type == Communication.REQUEST) {
            String pData = "";
            int requestType = Integer.parseInt(s[4]);
            switch (requestType) {
                case 10: {
                    String userID = s[5];
                    String itemName = s[6];
                    String result = obj1.findItemLocal(userID, itemName);
                    pData = pData + result;
                    break;
                }
                case 11: {
                    String userID = s[5];
                    String itemID = s[6];
                    String result = obj1.checkAvailAndValidate(userID, itemID);
                    pData = pData + result;
                    break;
                }
                case 12: {
                    String userID = s[5];
                    String itemID = s[6];
                    int noOfDays = Integer.parseInt(s[7]);
                    System.out.println("Before Borrow...");
                    obj1.printDatabase1();
                    String result = obj1.checkAvailAndValidateAndDoBorrow(userID, itemID, noOfDays);
                    
                    System.out.println("After Borrow...");
                    obj1.printDatabase1();
                    pData = pData + result;
                    break;
                }
                case 13: {
                    String userID = s[5];
                    String itemID = s[6];
                    System.out.println("Before addWait...");
                    obj1.printDatabase1();
                    Boolean b = obj1.addWait(userID, itemID);
                    System.out.println("After addWait...");
                    obj1.printDatabase1();

                    String result = Boolean.toString(b);
                    pData = pData + result;
                    break;
                }
                case 14: {
                    String userID = s[5];
                    String itemID = s[6];
                    Boolean b = obj1.returnItemLocal(userID, itemID);
                    String result = Boolean.toString(b);
                    pData = pData + result;
                    break;
                }
            }

            String responseMsg = "";
            responseMsg = responseMsg + messageID + "," + Communication.RESPONSE + "," + pData;
            sendMessage(responseMsg, port);
        } else if (type == Communication.RESPONSE) {
            String r = s[4];
            Communication.responseMessages.put(messageID, r);
        }

        System.out.println("Done Processing!");
    }

    public static int[] convertToIntArray(byte[] input) {
        IntBuffer ib = ByteBuffer.wrap(input).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
        int[] ret = new int[ib.capacity()];
        ib.get(ret);
        return ret;
    }

}
