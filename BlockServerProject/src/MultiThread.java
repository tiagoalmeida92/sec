import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Utils.Constants;

/*
 * Ideas from multithread part of the website:
 * http://tutorials.jenkov.com/java-multithreaded-servers/multithreaded-server.html
 */

public class MultiThread implements Runnable{

	private int serverPort = Constants.PORT;
	private ServerSocket serverSocket = null;
	private boolean isStopped = false;
	private Thread runningThread = null;
	private Worker worker = null;
	
	public MultiThread()
	{	
	}
	
	public MultiThread(int port)
	{
		serverPort = port;
	}
	
	public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();           
                
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
            worker = new Worker(clientSocket);
            new Thread(
                    worker
                ).start();
        }
        System.out.println("Server Stopped.");
    }
	
	private synchronized boolean isStopped() {
        return this.isStopped;
    }
	
	public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }
	
	private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + serverPort, e);
        }
    }
	
	
}
