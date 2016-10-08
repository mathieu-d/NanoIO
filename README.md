# NanoIO

NanoIO is a basic set of classes to exchange data over tcp between two devices running Java.

This is a first draft, but it's doing the job.

I intend to make it lightweight and pure Java.

It should run on small embedded devices like a Lego Mindstorm EV3 brick, a raspberry pi or an Android smartphone.

All remarks and improvement proposals are welcome, but don't forget the watchwords: **lightweight and pure Java**

# Usage
### The packet
Extends the NanoIOPacket class to hold your data

**Take care to the package definition and the UID of the NanoIOPacket class and the extending class. It must be the same on the server and client side. Otherwise an incoming packet will be rejected by the receiver**

```java
package com.mdudev.nanoiexample;
public class NanoIOTestPacket extends NanoIOPacket<Integer>{
	private static final long serialVersionUID = -8734916862756660009L;
}
```

### The server

The class that instantiate the NanoIOServer must implement the NanoIOListener interface to be notified when a new packet is available
```java
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
	public void receivePacket(NanoIOPacket<?> packet) 
	{
	    // Print incoming value
		System.out.println(((NanoIOTestPacket)packet).getData().toString());
		// Send data back to the client
		s.sendPacket(packet);
	}
}
```

### The client
```java
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
```

# License

[GNU General Public License V3](https://www.gnu.org/licenses/gpl-3.0.html)