package electricity;

public class Server {

	public static void main(String[] args) {
		SerialServer serial = new SerialServer(); // arduino
		SocketServer socket = new SocketServer(serial); // android
		serial.initialize();
		System.out.println("Serial Communicate Start");
//		for(int i = 0 ; i < 10 ; i++){
//			try {				
//				serial.writeData("10");
//				Thread.sleep(10000);
//				serial.writeData("11");
//				Thread.sleep(10000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		/*TimeChec Start*/
		TimeCheck timecheck = new TimeCheck();
		timecheck.start();
		socket.startServer();
		System.out.println("Socket Communicate Start");
		
		
	}
}
