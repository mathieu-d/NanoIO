package com.mdudev.nanoio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 *	This file is part of NanoIO.
 *
 *	NanoIO is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	NanoIO is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with NanoIO.  If not, see <http://www.gnu.org/licenses/>.
 *
 *	Copyright 2016 Mathieu Dupriez
 */

/**
 * @author Mathieu Dupriez
 */
public class NanoIOServer {

	private int serverPort;
	private boolean connected = false;
	private boolean waitingClient = false;
	private Thread waitingForConnectionThread;
	private Thread listeningClientThread;
	private ServerSocket ss;
	private Socket conn;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private NanoIOListener listener;
	private Object closeMutex = new Object();
	
	/**
	 * Create an instance of NanoIOServer
	 * @param serverPort
	 * NanoIO server listening port
	 * @param listener
	 * NanoIO listener that handle received packets
	 */
	public NanoIOServer(int serverPort, NanoIOListener listener)
	{
		this.serverPort = serverPort;
		this.listener = listener;
	}
	
	/**
	 * Call this method to start the NanoIO server
	 * The server listen for a client connection
	 */
	public void startServer()
	{
		waitingForConnectionThread = new Thread(new waitingForConnection());
		waitingForConnectionThread.start();
	}
	
	/**
	 * Call this method to close the connection with the NanoIO client
	 */
	public void stopServer()
	{
		synchronized (closeMutex) {
			if(connected)
			{
				connected = false;
				try {
					conn.close();
					ss.close();
				} catch (IOException e) {
					System.out.println("Error closing connection: " + e);
				}
			}
			if(waitingClient)
			{
				try {
					ss.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Error closing connection: " + e);
				}
			}
		}	
	}
	
	/**
	 * Call this method to send a packet to the NanoIO client
	 * @param packet
	 * NanoIO packet to send
	 * @return
	 * True if the packet has been successfully sent
	 */
	public boolean sendPacket(NanoIOPacket<?> packet)
	{	
		boolean sendDone = false;
		
		if(connected)
		{
			try {
				oos.reset();
				oos.writeObject(packet);
				sendDone = true;
			} catch (IOException e) {
				System.out.println("Error sending packet " + e);
			}
		}
		
		return sendDone;
	}
	
	/**
	 * Thread that wait for a NanoIO client connection
	 */
	private class waitingForConnection implements Runnable
	{

		public void run() {
			try {
				ss = new ServerSocket(serverPort);
				waitingClient = true;
				conn = ss.accept();
				conn.setTcpNoDelay(true);
				oos = new ObjectOutputStream(conn.getOutputStream());
				ois = new ObjectInputStream(conn.getInputStream());
				
				connected = true;
				waitingClient = false;
				
				listeningClientThread = new Thread(new listeningClient());
				listeningClientThread.start();
			
			}catch (IOException e) {
				System.out.println("Error accepting connection " + e);
			} 
		}
	}
	
	/**
	 * Thread that listen for the incoming packets from the NanoIO client and forward it to the NanoIO listener
	 */
	private class listeningClient implements Runnable
	{
		public void run() {
			try {
				while(connected)
				{
			
						Object obj = ois.readObject();
						
						if(obj instanceof NanoIOPacket<?>)
						{
							listener.receivePacket((NanoIOPacket<?>)obj);
						}
						else
						{
							// TODO Generate error
						}
				}
			} catch(SocketException e) {
	         	System.out.println("Error reading from client socket: " + e);
	        } catch (ClassNotFoundException e) {
	        	 System.out.println("Error reading from client socket: " + e);
	        } catch (IOException e) {
	        	 System.out.println("Error reading from client socket: " + e);
	        }
			stopServer();
		}
	}
}
