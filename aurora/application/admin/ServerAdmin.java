/*
 * Created on 2008-9-16
 */
package aurora.application.admin;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import uncertain.core.EngineInitiator;

public class ServerAdmin extends Thread {
    
        int                 mPort;
        ServerSocket        mSocket;
        String              mHome;
        boolean             mIsRunning = true;
        EngineInitiator     mEngineInitiator;
        List                mClientThreadList;
        
        /**
         * @param port
         * @param home
         */
        public ServerAdmin(int port, String home) {
            super();
            mPort = port;
            mHome = home;
        }

        public void doShutdown(){
            mIsRunning = false;
            if( mClientThreadList!=null){
                Iterator it = mClientThreadList.iterator();
                while(it.hasNext()){
                    CommandHandleThread thread = (CommandHandleThread)it.next();
                    thread.clearUp();
                }
                mClientThreadList.clear();
                mClientThreadList = null;
            }
            if( mSocket != null ){
                try{
                    mSocket.close();
                }catch(IOException ex){
                    ex.printStackTrace();
                }
                mSocket = null;
            }
            if( mEngineInitiator != null ){
//                mEngineInitiator.shutdown();
            	if(mEngineInitiator.getUncertainEngine() != null&&mEngineInitiator.getUncertainEngine().getIsRunning()){
            		mEngineInitiator.getUncertainEngine().shutdown();
            	}
                mEngineInitiator = null;
            }
        }
        
        public void doStartup(){
            doShutdown();
            try{
                mSocket = new ServerSocket(mPort);
                mClientThreadList = new LinkedList();
                File home_path = new File(mHome);
                File config_path = new File(home_path, "WEB-INF");
                mEngineInitiator = new EngineInitiator(home_path,config_path);
                System.out.println("init EngineInitiator");
                mEngineInitiator.init();
                mIsRunning = true;
                start();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        
        void addClient( CommandHandleThread thread ){
            mClientThreadList.add(thread);
        }
        
        void removeClient( CommandHandleThread thread ){
            thread.clearUp();
            mClientThreadList.remove(thread);
        }
        
        public void run(){
            while(mIsRunning){
                try{
                    Socket socket = mSocket.accept();
                    String address = socket.getInetAddress().getHostAddress();
                    if(!"127.0.0.1".equals(address)){
                        socket.close();
                        continue;
                    }
                    //System.out.println("Accepting "+address.toString());
                    CommandHandleThread thread = new CommandHandleThread(this,socket);
                    addClient(thread);
                    thread.start();
                }catch( IOException ex){
                    ex.printStackTrace();
                }
            }
        }
        
        static void printUsage(){
            System.out.println("Usage:");
            System.out.println("ServerAdmin <port> <lwap home directory>");
        }
        
        public static void main( String[] args ) throws Exception {
//        	args = new String[]{"18080", "E:/tomcats/HAP/tomcat/webapps/HAP/web"};
            if(args.length<2){
                printUsage();
                return;
            }
            try{
                int port = Integer.parseInt(args[0]);
                File path = new File(args[1]);
                if(!path.exists()) throw new IllegalArgumentException(path.getPath()+" doesn't exist");
                ServerAdmin admin = new ServerAdmin(port, path.getPath());
                admin.doStartup();
            }catch(Exception ex){
                System.out.println(ex.getMessage());
                printUsage();
                return;
            }
        }
        
}
