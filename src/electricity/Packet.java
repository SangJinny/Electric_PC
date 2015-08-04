package electricity;

import java.io.Serializable;

public class Packet implements Serializable{
	/**
	 * ������ſ��� ��Ŷ�� ������ �� ��ü
	 * ����ȭ������� ������.
	 * ���ϴ� ������ �߰��Ͽ� �����Ӱ� ���� ����.
	 */
	private static final long serialVersionUID = 1L;
	private int type; // Request Data/ACK, On,Off Request/ACK, ID modify/ACK
	private String[] data; //Packet num, ID, Name, 3mago,2mago, 1mago, dayafter,now
	private boolean on_off;
	
	public Packet() {
		data = new String[8];
		boolean on_off = false;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getData(int index) {
		return data[index];
	}
	public void setData(String[] data) {
		System.arraycopy(data, 0, this.data, 0, 8);
	}
	public boolean get_bool() {
		return on_off;
	}
	public void set_bool(boolean on_off) {
		this.on_off = on_off;
	}
}
