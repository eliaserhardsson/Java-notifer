package javaNotifer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPSocket {
	
	private String host;
	private int port;
	
	private Socket socket;
	private BufferedReader reader;
	
	public TCPSocket(String host, int port) throws Exception {
		this.host = host;
		this.port = port;
		
		this.socket = new Socket(host, port);
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf8"));
	}
	
	public BufferedReader getReader() {
		return this.reader;
	}
	
	public Socket getSocket() {
		return this.socket;
	}
	
}
