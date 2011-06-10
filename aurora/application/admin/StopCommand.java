/*
 * Created on 2008-9-16
 */
package aurora.application.admin;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class StopCommand {
    
    public static void printUsage(){
        System.out.println("Usage:");
        System.out.println("java " + StopCommand.class.getName()+" <port>");        
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        if(args.length!=1){
            printUsage();
            return;            
        }
        try{
            int port = Integer.parseInt(args[0]);
            Socket socket = new Socket("localhost", port);
            OutputStream os = socket.getOutputStream();
            os.write("stop\r\n".getBytes());
        }catch(NumberFormatException ex){
            printUsage();
            return;
        }catch(IOException ioex){
            System.out.println(ioex.getMessage());
        }

    }

}
