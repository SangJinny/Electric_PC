package electricity;

import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

public class SerialServer implements SerialPortEventListener {
	public static String powerString[][] = new String[2][10];
	public static float powerResult[] = new float[2]; // ���·� ��հ�
	public static boolean READYTOSEND = false;
	public int count = 0;
	public boolean buffer = false;
	private int index = 0; // ���� ���̵� �����ϱ� ����.
	SerialPort serialPort;
	DataBase db = new DataBase();

	/** ����� �Ƶ��̳�� ����� �ø��� ��Ʈ�� �����ؾ� �Ѵ�. */
	private static final String PORT_NAMES[] = { "/dev/tty.usbserial-A9007UX1", // Mac
																				// OS
																				// X
			"/dev/ttyUSB0", // Linux
			"COM10", // Windows
	};
	/** ��Ʈ���� �����͸� �б� ���� ���۸� ���� input stream */
	private InputStream input;

	/** ��Ʈ�� ���� �Ƶ��̳뿡 �����͸� �����ϱ� ���� output stream */
	private OutputStream output;

	/** ��Ʈ�� ���µǱ� ���� ��ٸ��� ���� �뷫���� �ð�(2��) */
	private static final int TIME_OUT = 2000;

	/** ��Ʈ�� ���� �⺻ ��� �ӵ�, �Ƶ��̳��� Serial.begin�� �ӵ��� ��ġ */
	private static final int DATA_RATE = 9600;

	public void initialize() {
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		// �ø��� ��Ʈ�� �� �Ƶ��̳�� ����� ��Ʈ�� ���� �ĺ��ڸ� ã�´�.

		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum
					.nextElement();

			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}

		// �ĺ��ڸ� ã�� ������ ��� ����
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// �ø��� ��Ʈ ����, Ŭ���� �̸��� ���ø����̼��� ���� ��Ʈ �ĺ� �̸����� ���
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// �ӵ��� ��Ʈ�� �Ķ���� ����
			serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			// ��Ʈ�� ���� �а� ���� ���� ��Ʈ�� ����
			input = serialPort.getInputStream();
			output = serialPort.getOutputStream();

			// �Ƶ��̳�� ���� ���۵� �����͸� �����ϴ� �����ʸ� ���
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}

	}

	/*
	 * �� �޼���� ��Ʈ ����� ������ �� �ݵ�� ȣ���ؾ� �Ѵ�. �������� ���� �÷��������� ��Ʈ ����� �����Ѵ�.
	 */

	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * �ø��� ��ſ� ���� �̺�Ʈ�� ó��. �����͸� �а� ����Ѵ�..
	 */

	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (READYTOSEND) { // 10���� �����Ͱ� ��� ������ ������
			float powerFloat[] = new float[10];
			for (int i = 0; i < 10; i++) {
				powerFloat[i] = Float.parseFloat(powerString[0][i]);
				powerResult[0] += powerFloat[i];
			}
			for (int i = 0; i < 10; i++) {
				powerFloat[i] = Float.parseFloat(powerString[1][i]);
				powerResult[1] += powerFloat[i];
			}
			powerResult[0] /= 10; // ��ճ���
			powerResult[0] /= 1000; // kwh�� ��ȯ
			System.out.println("���1 : " + powerResult[0] + "Kwh");
			float temp = db.SelectPowerResult(0 + 1); // DB���� ���� ���·� ������
			temp += powerResult[0];
			System.out.println("�ֱ�");
			db.UpdatePowerResult(0 + 1, temp); // ���ؼ� �ø�
			
			powerResult[1] /= 10; // ��ճ���
			powerResult[1] /= 1000; // kwh�� ��ȯ
			System.out.println("���2 : " + powerResult[1] + "Kwh");
			float temp2 = db.SelectPowerResult(1 + 1); // DB���� ���� ���·� ������
			temp2 += powerResult[1];
			System.out.println("�ֱ�");
			db.UpdatePowerResult(1 + 1, temp2); // ���ؼ� �ø�			

			/*
			 * temp = db_ipv4.SelectPowerResut(1);
			 * System.out.print("UpdatePowerResult�� �� ������ ��");
			 * System.out.println(temp);
			 */
			READYTOSEND = false;
			index = 0;
			count = 0;
			// //////////////////////////////////////
		} else { // 10���� �����͸� ������ �����̸�
			if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
				try {
					int available = input.available();
					byte chunk[] = new byte[available];
					input.read(chunk, 0, available);
					// �ٷ� ���
					if (buffer) { // ���� �д� �����̸�
						powerString[index][count] += new String(chunk);
						// System.out.println("READ "+powerString[index][count]+"/"+powerString[index][count].length());
					} else { // ���� ó�� �д� ���̸�
						powerString[index][count] = new String(chunk);
						// System.out.println("READ "+powerString[index][count]+"/"+powerString[index][count].length());
						buffer = true;
					}
					if (powerString[index][count].length() >= 8) {
						buffer = false;
						if (powerString[index][count].charAt(0) == '0') {
							powerString[index][count] = powerString[index][count]
									.substring(1);
							System.out.println("power1: "
									+ powerString[index][count]);
							index = 1;
						} else if (powerString[index][count].charAt(0) == '1') {
							powerString[index][count] = powerString[index][count]
									.substring(1);
							System.out.println("power2: "
									+ powerString[index][count]);
							index = 0;
							count++;
						}
					}
					if (count >= 10) {
						count = 0;
						READYTOSEND = true;
					}
				} catch (Exception e) {
					System.err.println(e.toString());
				}
			}
		}
		// �ٸ� �̺�Ʈ ������ �����Ѵ�. ���� ����� �ʿ��� �̺�Ʈ�� ������ �߰��ϸ�ȴ�.
		// �ٸ� �̺�Ʈ�� ���� ���� SerialPortEvent �ҽ��� ����
	}

	public synchronized void writeData(String data) {
		System.out.println("Sented Data: " + data);
		try {
			output.write(data.getBytes());
		} catch (Exception e) {
			System.out.println("could not write to port");
		}
	}

}