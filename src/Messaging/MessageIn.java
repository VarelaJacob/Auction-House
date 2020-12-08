package Messaging;

import AuctionHouse.AuctionHouse;
import Bank.Bank;
import Messaging.MessageInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * MessageIn Class.
 * This class is used to help send messages and communicate between
 * clients using ObjectInputStream and ObjectOutputStream.
 *  
 * @author Jacob Varela 
 */
public class MessageIn implements Runnable{

    // The current socket.
    private Socket socket;

    // Initialize client Objects
    private Bank bank;
    private AuctionHouse auction;
    
    // String represents where the message is coming from.
    private String source;
    
    // Declare default message to return.
    private String returnMessage;
    
    // Declare streams for communication.
    private ObjectOutputStream outStream;
    private ObjectInputStream  inputStream;
 
    /**
     * MessageIn constructor.
     * Initializes the socket that the communication is set up on and
     * call setVar to assign variables based on the type of object passed.
     * 
     * @param socket Socket the connection is on.
     * @param reference Object such as an Bank or Auction House.
     * @throws IOException
     */
    public MessageIn(Socket socket, Object reference) throws IOException {
        
        this.socket = socket;
        setVar(reference);

        outStream   = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream()) ;
    }

    /**
     * Assigns variables values based on the object passed in.
     * 
     * @param reference Either a Bank or an Auction House.
     */
    private void setVar(Object reference){
        
        if( reference instanceof Bank){
            bank = (Bank) reference;
            source = "Bank";
            returnMessage = "Successfully established a connection with the Bank";
        }
        else if(reference instanceof AuctionHouse){
            auction = (AuctionHouse) reference;
            source = "auction";
            returnMessage = "Successfully established a connection with the Auction House.";
        }
    }

    /**
     * newMessage method.
     * This method simply sends the newMessage to an Agent or Auction House.
     * 
     * @param newMessage The message to be sent out.
     * @throws IOException
     */
    public void sendMessage(String newMessage) throws IOException {
        outStream.writeObject(new MessageInfo(newMessage, source, null, 0, 0));
    }

    /**
     * run method.
     * This method sets up a connection between the client and the server.
     * It will handle the connection setup process differently based on the 
     * objects that are trying to communicate. 
     */
    @Override
    public void run(){
        
        String newMessage = "" + socket.getPort();

        try {
            outStream.writeObject(
                new MessageInfo(newMessage, source, null, 0, 0));
            outStream.writeObject(
                new MessageInfo(returnMessage, source, null, 0, 0));
            
            MessageInfo inputMessage;
            while( (inputMessage = (MessageInfo) inputStream.readObject()) != null) {
                if( source.equals("Bank") ){
                    bank.handleMessage(inputMessage, socket, this);
                }
                else if( source.equals("auction") ){
                    auction.handleMessage(inputMessage, socket, this);
                }
                
                outStream.writeObject(
                    new MessageInfo(newMessage, source, null, 0, 0)
                );
            }
            socket.close();
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            if( source.equals("Bank")){
                System.out.println("Connection Lost." +
                                   "Try the command 'bank info' for more information.");
                try {
                    bank.handleMessage(
                        new MessageInfo("delete","bank", null, 0,socket.getPort()), socket, this);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } 
    }

}
