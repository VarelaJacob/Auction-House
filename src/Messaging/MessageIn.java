package Messaging;

import AuctionHouse.AuctionHouse;
import Bank.Bank;
import Messaging.MessageInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MessageIn implements Runnable{

    private Socket socket;
    private Bank bank;
    private AuctionHouse auction;
    private String source;
    private String returnMessage;
    private ObjectOutputStream outStream;
    private ObjectInputStream  inputStream;
 
    /*********** */
    public MessageIn(Socket socket, Object reference) throws IOException {
        
        this.socket = socket;
        setVar(reference);

        outStream   = new ObjectOutputStream(socket.getOutputStream());
        inputStream = new ObjectInputStream(socket.getInputStream()) ;
    }

    /******** */
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

    /******* */
    public void sendMessage(String newMessage) throws IOException {
        outStream.writeObject(new MessageInfo(newMessage, source, null, 0, 0));
    }

    /****** */
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
