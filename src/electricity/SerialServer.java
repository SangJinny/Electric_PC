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
	public static float powerResult[] = new float[2]; // 전력량 평균값
	public static boolean READYTOSEND = false;
	public int count = 0;
	public boolean buffer = false;
	private int index = 0; // 센서 아이디를 구분하기 위해.
	SerialPort serialPort;
	DataBase db = new DataBase();

	/** 당신의 아두이노와 연결된 시리얼 포트로 변경해야 한다. */
	private static final String PORT_NAMES[] = { "/dev/tty.usbserial-A9007UX1", // Mac
																				// OS
																				// X
			"/dev/ttyUSB0", // Linux
			"COM10", // Windows
	};
	/** 포트에서 데이터를 읽기 위한 버퍼를 가진 input stream */
	private InputStream input;

	/** 포트를 통해 아두이노에 데이터를 전송하기 위한 output stream */
	private OutputStream output;

	/** 포트가 오픈되기 까지 기다리기 위한 대략적인 시간(2초) */
	private static final int TIME_OUT = 2000;

	/** 포트에 대한 기본 통신 속도, 아두이노의 Serial.begin의 속도와 일치 */
	private static final int DATA_RATE = 9600;

	public void initialize() {
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		// 시리얼 포트들 중 아두이노와 연결된 포트에 대한 식별자를 찾는다.

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

		// 식별자를 찾지 못했을 경우 종료
		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// 시리얼 포트 오픈, 클래스 이름을 애플리케이션을 위한 포트 식별 이름으로 사용
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// 속도등 포트의 파라메터 설정
			serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			// 포트를 통해 읽고 쓰기 위한 스트림 오픈
			input = serialPort.getInputStream();
			output = serialPort.getOutputStream();

			// 아두이노로 부터 전송된 데이터를 수신하는 리스너를 등록
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}

	}

	/*
	 * 이 메서드는 포트 사용을 중지할 때 반드시 호출해야 한다. 리눅스와 같은 플랫폼에서는 포트 잠금을 방지한다.
	 */

	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * 시리얼 통신에 대한 이벤트를 처리. 데이터를 읽고 출력한다..
	 */

	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (READYTOSEND) { // 10개의 데이터가 모두 수집이 끝나면
			float powerFloat[] = new float[10];
			for (int i = 0; i < 10; i++) {
				powerFloat[i] = Float.parseFloat(powerString[0][i]);
				powerResult[0] += powerFloat[i];
			}
			for (int i = 0; i < 10; i++) {
				powerFloat[i] = Float.parseFloat(powerString[1][i]);
				powerResult[1] += powerFloat[i];
			}
			powerResult[0] /= 10; // 평균내고
			powerResult[0] /= 1000; // kwh로 변환
			System.out.println("결과1 : " + powerResult[0] + "Kwh");
			float temp = db.SelectPowerResult(0 + 1); // DB에서 누적 전력량 가져옴
			temp += powerResult[0];
			System.out.println("넣기");
			db.UpdatePowerResult(0 + 1, temp); // 더해서 올림
			
			powerResult[1] /= 10; // 평균내고
			powerResult[1] /= 1000; // kwh로 변환
			System.out.println("결과2 : " + powerResult[1] + "Kwh");
			float temp2 = db.SelectPowerResult(1 + 1); // DB에서 누적 전력량 가져옴
			temp2 += powerResult[1];
			System.out.println("넣기");
			db.UpdatePowerResult(1 + 1, temp2); // 더해서 올림			

			/*
			 * temp = db_ipv4.SelectPowerResut(1);
			 * System.out.print("UpdatePowerResult를 한 이후의 값");
			 * System.out.println(temp);
			 */
			READYTOSEND = false;
			index = 0;
			count = 0;
			// //////////////////////////////////////
		} else { // 10개의 데이터를 모으는 과정이면
			if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
				try {
					int available = input.available();
					byte chunk[] = new byte[available];
					input.read(chunk, 0, available);
					// 바로 출력
					if (buffer) { // 값을 읽는 도중이면
						powerString[index][count] += new String(chunk);
						// System.out.println("READ "+powerString[index][count]+"/"+powerString[index][count].length());
					} else { // 값을 처음 읽는 것이면
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
		// 다른 이벤트 유형은 무시한다. 만약 당신이 필요한 이벤트가 있으면 추가하면된다.
		// 다른 이벤트에 대한 것은 SerialPortEvent 소스를 참조
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