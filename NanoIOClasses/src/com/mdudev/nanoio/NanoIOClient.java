package com.mdudev.nanoio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

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
public class NanoIOClient {

	private String serverIP;
	private int serverPort;
	private NanoIOListener listener;
	private boolean connected = false;
	private Socket conn;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Thread listeningServerThread;
	private Object closeMutex = new Object();
	
	/**
	 * Create an instance of NanoIOClient
	 * @param serverIP
	 * NanoIO server ip address to connect
	 * @param serverPort
	 * NanoIO server listening port
	 * @param listener
	 * NanoIO listener that handle received packets
	 */
	public NanoIOClient(String serverIP, int serverPort, NanoIOListener listener)
	{
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		this.listener = listener;
	}
	
	/**
	 * Call this method to connect to the NanoIO server
	 */
	public boolean connect()
	{
		try {
			conn = new Socket(this.serverIP, this.serverPort);
			conn.setTcpNoDelay(true);
			ois = new ObjectInputStream(conn.getInputStream());
			oos = new ObjectOutputStream(conn.getOutputStream());
			
			connected = true;
			
			listeningServerThread = new Thread(new listeningServer());
			listeningServerThread.start();
			
		} catch (UnknownHostException e) {
			System.out.println("Error connecting server " + e);
		} catch (IOException e) {
			System.out.println("Error connecting server " + e);
		}
		return connected;
	}
	
	/**
	 * Call this method to send a packet to the NanoIO server
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
	 * Call this method to close the connection with the NanoIO server
	 */
	public void closeConnection()
	{
		synchronized (closeMutex) {
			if(connected)
			{
				connected = false;
				try {
					conn.close();
				} catch (IOException e) {
					System.out.println("Error closing connection: " + e);
				}
			}
		}	
	}
	
	/**
	 * Thread that listen for the incoming packets from the NanoIO server and forward it to the NanoIO listener
	 */
	private class listeningServer implements Runnable
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
	         	System.out.println("Error reading from server socket: " + e);
	        } catch (ClassNotFoundException e) {
	        	 System.out.println("Error reading from server socket: " + e);
	        } catch (IOException e) {
	        	 System.out.println("Error reading from server socket: " + e);
	        }
			
			closeConnection();
		}
	}
}
