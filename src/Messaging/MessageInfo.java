/**
 * Jacob Varela
 * CS-351-004
 * 12-08-2020
 * Project #3: Auction House
 */

package Messaging;
import java.io.Serializable;

/**
 * This is a class for a Message that is to be sent. It contains information
 * that will be interpreted by whichever Actor is receiving the message.
 * The Actors are either a Bank, an Agent, or an Auction House.
 * This class implements Serializable because it will be sent through
 * ObjectInputStream and ObjectOutputStream.
 * 
 * @author Jacob Varela
 */
public class MessageInfo implements Serializable{

    // Variables that hold information in a message.
    public String message, source, AHAddress;
    public int AHPort, portNum;

    /**
     * This is the constroctor for the MessageInfo object.
     * @param message String that contains instructions for the recipient. 
     * @param source  String that identifies what Actor is sending the message.
     * @param AHAddress String containing the address of an auction house. Will
     *                  be set to 0 if not needed.
     * @param AHPort integer that contains the port number of an auction house.
     *               Will be set to 0 if not needed.
     * @param portNum integer containing the port number of the sender.
     */
    public MessageInfo(String message, String source, String AHAddress,
                   int AHPort, int portNum){
        this.message   = message;
        this.source    = source;
        this.AHAddress = AHAddress;
        this.AHPort    = AHPort;
        this.portNum   = portNum;
    }
    
}
