package com.mdudev.nanoiexample;

import java.io.IOException;

import com.mdudev.nanoio.NanoIOClient;
import com.mdudev.nanoio.NanoIOListener;
import com.mdudev.nanoio.NanoIOPacket;

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
public class NanoIOTestClient implements NanoIOListener {

	NanoIOClient c;
	NanoIOTestPacket p;
	Thread t;
	
	public NanoIOTestClient()
	{
		// Create a packet with default data value 0
		p = new NanoIOTestPacket();
		p.setData(0);
		
		// Create a client that will connect at localhost on port 9000
		c = new NanoIOClient("localhost", 9000, this);
		// connect to the server
		boolean connected = c.connect();
		
		// Check if connection succeed
		if(connected)
		{
			// Start the thread that will periodicaly send a packet to the server
			t = new Thread(new NanoIOClientTestThread());
			t.start();
		}
	}
	
    // Implements receivePacket method from NanoIOListener interface
	public void receivePacket(NanoIOPacket<?> packet) 
	{
		// Print incoming value
		System.out.println(((NanoIOTestPacket)packet).getData().toString());
	}

	private class NanoIOClientTestThread implements Runnable {
		
		public void run() {

			try {
				do
				{
					// Send a packet to the server
					c.sendPacket(p);
					// Increase packet data value
					p.setData(p.getData()+1);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}while(System.in.available() == 0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Close connection to the server
			c.closeConnection();
		}
	}
}
