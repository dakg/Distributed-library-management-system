package Servers.Concordia;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * @author daksh
 */
public interface CONOperations extends Remote {

    //Manager Role: addItem,removeItem,listItemAvailability

    int authuser(String id) throws RemoteException;

    int addItem(String managerID, String itemID, String itemName, int quantity) throws RemoteException;
//                                                                                addItem (managerID, itemID, itemName, quantity):
//                                                                                When a manager invokes this method on the associated server (determined by the
//                                                                                unique managerID prefix), it attempts to add an item with the information passed,
//                                                                                and inserts the record at the appropriate location in the hash map. The server
//                                                                                returns information to the manager whether the operation was successful or not
//                                                                                and both the server and the client store this information in their logs. If an item
//                                                                                already exists, the new quantity entered should be added to the current quantity of
//                                                                                the item. If an item does not exist in the database, then simply add it. Log the
//                                                                                information into the manager log file. 

    int removeItem(String managerID, String itemID, int quantity) throws RemoteException;
//                                                                                removeItem (managerID, itemID, quantity)
//                                                                                When invoked by a manager, the server associated with this manager (determined
//                                                                                by the unique managerID) searches in the hashmap to find and delete the item.
//                                                                                There can be two cases of deletion, first, if the manager wants to decrease the
//                                                                                quantity of that item, second, if the manager wants to completely remove the item
//                                                                                from the library. Upon success or failure it returns a message to the manager and
//                                                                                the logs are updated with this information. If an item does not exist, then obviously
//                                                                                there is no deletion performed. Just in case that, if an item exists and a user has
//                                                                                borrowed it, then, delete the item and take the necessary actions. Log the
//                                                                                information into the log file. 

    List listItemAvailability(String managerID) throws RemoteException;
//                                                                                listItemAvailability (managerID):
//                                                                                When a manager invokes this method from his/her library through the associated
//                                                                                server, that library server finds out the names and quantities of each item available
//                                                                                in the library. Eg: CON6231 Distributed Systems 5, CON6441 Advanced
//                                                                                Programming 4, CON6491 Systems Software 0

    //Library User Role: borrowItem,findItem,returnItem 
    int borrowItem(String userID, String itemID, String numberOfDays,int waitWIsh) throws RemoteException;
//                                                                                borrowItem (userID, itemID, numberOfDays) :
//                                                                                When a user invokes this method from his/her library through the server associated
//                                                                                with this user (determined by the unique userId prefix), it attempts to borrow the
//                                                                                specified item. If the item is from a different library, then the user’s library sends a
//                                                                                UDP/IP request to the item’s library to borrow. If the operation was successful,
//                                                                                borrow the item and decrement the quantity for that item. Also, display an
//                                                                                appropriate message to the user and both the server, the client stores this
//                                                                                information in their logs. If the borrow operation is unsuccessful, ask the user if
//                                                                                he/she wants to be added in the waiting queue. If prompted no, method ends,
//                                                                                otherwise add the userID to the queue corresponding to the requested item and
//                                                                                whenever the item is available again, automatically lend the item to the first user in
//                                                                                that queue.

    boolean findItem(String userID, String itemName) throws RemoteException;
//                                                                                findItem (userID, itemName):
//                                                                                When a user invokes this method from his/her library through the server associated
//                                                                                with this user, that library server gets all the itemIDs with the specified itemName
//                                                                                and the number of such items available in each of the libraries and display them on
//                                                                                the console. This requires inter server communication that will be done using
//                                                                                UDP/IP sockets and the result will be returned to the user. Eg: MON6231 5,
//                                                                                CON6441 4, CON6497 1, MCG6132 5.

    boolean returnItem(String userID, String itemID) throws RemoteException;
//                                                                                returnItem (userID, itemID):
//                                                                                When a user invokes this method from his/her library through the server associated
//                                                                                with this user (determined by the unique userID prefix) searches the hash map to
//                                                                                find the itemID and returns the item to its library. Upon success or failure it
//                                                                                returns a message to the user and the logs are updated with this information. It is
//                                                                                required to check that an item can only be returned if it was borrowed by the same
//                                                                                user who sends the return request. 

}
