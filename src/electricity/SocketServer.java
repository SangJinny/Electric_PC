package electricity;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
	private SerialServer serial;
	
	SocketServer(SerialServer serial){
		this.serial = serial;
	}
	public void startServer() {		
		ServerSocket serv_sock = null;
		try {
			serv_sock = new ServerSocket(10040);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		while (true) {
			try {
				System.out.println("Socket Wait..");				
				Socket clnt_sock = serv_sock.accept();
				System.out.println("Socket Connected!!");
				/*Socket Start*/
				SocketThread thread = new SocketThread(clnt_sock, serial);
				thread.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Main Thread STOPPED!!");
				try {
					serv_sock.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

	}

}
