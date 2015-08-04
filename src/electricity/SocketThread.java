package electricity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class SocketThread extends Thread{
	private Socket sock;
	private SerialServer serial;
	private DataInputStream dis;
	private ObjectInputStream ois;
	private DataOutputStream dos;
	private ObjectOutputStream oos;
	private Packet packet;
	private Packet send_packet;
	DataBase db_ipv4 = new DataBase();
	MonthValue recv_monthvalue = new MonthValue();
	/*
	 * ������ Ŭ���̾�Ʈ�� ������ ���� ��û�� ó���Ѵ�.
	 * ������ Ŭ���̾�Ʈ ���� �����尡 �����Ǵ� Blocking Server ���
	 * Ŭ���̾�Ʈ�� ������ ��û�� ����(��ü�� �Ǿ ����)
	 * �� �Ϳ� �´� �����͸� �������ְ� ������ �����Ѵ�.(��ü�� �Ǿ ����)
	 */
	public SocketThread(Socket sock, SerialServer serial) {
		// TODO Auto-generated method stub
		this.sock = sock;
		this.serial = serial;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		super.run();
		try {
			dos = new DataOutputStream(sock.getOutputStream());
			dis = new DataInputStream(sock.getInputStream());
			oos = new ObjectOutputStream(dos);			
			ois = new ObjectInputStream(dis);			
			System.out.println("Stream set Complete!");
			
			packet = (Packet) ois.readObject();
			
			switch(packet.getType()){
			/** ������ ��û **/
			case 1:

				String temp_numberofdata = db_ipv4.Numberofdata();
				String temp[] = {temp_numberofdata,"","","","","","",""};
				send_packet = new Packet();
				send_packet.setType(1);
				send_packet.setData(temp);
				oos.writeObject(send_packet);
				String datatemp[] = {temp_numberofdata,"","","","","","",""};
				//for(int i=0;i<Integer.parseInt(temp_numberofdata);i++)
				//for(int i=0;i<2;i++)
				for(int i=1;i<1+Integer.parseInt(temp_numberofdata);i++)
				{
					Packet temp_packet = new Packet();
					recv_monthvalue = db_ipv4.SelectEquipdata(i);
					datatemp[1] = recv_monthvalue.P_ID;
					datatemp[2] = recv_monthvalue.name;
					datatemp[3] = recv_monthvalue.three_Monthvalue;
					datatemp[4] = recv_monthvalue.two_Monthvalue;
					datatemp[5] = recv_monthvalue.one_Monthvalue;
					datatemp[6] = recv_monthvalue.Day_after;
					datatemp[7] = recv_monthvalue.Monthvalue;					
					Boolean on_off = db_ipv4.SelectOnOff(i);
					temp_packet.setType(1);
					temp_packet.setData(datatemp);
					temp_packet.set_bool(on_off);
					oos.writeObject(temp_packet);
					/*
					System.out.println("datatemp�ִ� ��� ");
					System.out.printf("datatemp[1]��  ");
					System.out.println(temp_packet.getData(1));
					System.out.printf("datatemp[2]��  ");
					System.out.println(temp_packet.getData(2));
					System.out.printf("datatemp[3]��  ");
					System.out.println(temp_packet.getData(3));
					System.out.printf("datatemp[4]��  ");
					System.out.println(temp_packet.getData(4));
					System.out.printf("datatemp[5]��  ");
					System.out.println(temp_packet.getData(5));
					System.out.printf("datatemp[6]��  ");
					System.out.println(temp_packet.getData(6));
					System.out.printf("datatemp[7]��  ");
					System.out.println(temp_packet.getData(7));*/
				}
				System.out.println("Send Complete!");
				break;
			/** On,Off ��û **/
			case 2:
				boolean onoff = packet.get_bool();
				String tmpmsg;
				//boolean temp_bool = false;
				//On�ΰ��
				if(onoff){
					tmpmsg = packet.getData(1)+"1";
					serial.writeData(tmpmsg);
					//temp_bool = db_ipv4.SelectOnOff(Integer.parseInt(packet.getData(1)));
					//temp_bool = packet.get_bool();
					db_ipv4.UpdateOnoff(Integer.parseInt(packet.getData(1)),packet.get_bool() );
				}
				//off�ΰ��
				else {
					tmpmsg = packet.getData(1)+"0";
					serial.writeData(tmpmsg);
					//temp_bool = db_ipv4.SelectOnOff(Integer.parseInt(packet.getData(1)));
					//temp_bool = packet.get_bool();
					db_ipv4.UpdateOnoff(Integer.parseInt(packet.getData(1)),packet.get_bool() );
				}
				oos.writeObject(packet);
				break;
			/** ID���� ��û **/
			case 3:
				System.out.println("packet.getData(1) = "+ packet.getData(1) + "qweqwe packet.getData(3) = "+ packet.getData(3));
				db_ipv4.UpdateEquipName(Integer.parseInt(packet.getData(1)), packet.getData(3));
				break;
			default:
				break;
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOException");
		} catch( ClassNotFoundException e){
			System.out.println("sjjsjsjsjsjskjskjsClassNotFoundException");
			e.printStackTrace();
		}		
	}
}
