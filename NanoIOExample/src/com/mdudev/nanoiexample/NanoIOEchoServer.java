package com.mdudev.nanoiexample;

import com.mdudev.nanoio.NanoIOListener;
import com.mdudev.nanoio.NanoIOPacket;
import com.mdudev.nanoio.NanoIOServer;

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
public class NanoIOEchoServer implements NanoIOListener{

	private NanoIOServer s;
	
	public NanoIOEchoServer() 
	{
		// Create a server listening on port 9000
		s = new NanoIOServer(9000, this);
		// Start the server
		s.startServer();
	}
	
	public void close()
	{
		// Close the server
		s.stopServer();
	}

	 // Implements receivePacket method from NanoIOListener interface
	public void receivePacket(NanoIOPacket<?> packet) {
		System.out.println(((NanoIOTestPacket)packet).getData().toString());
		s.sendPacket(packet);
	}
}
