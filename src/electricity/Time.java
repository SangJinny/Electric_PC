package electricity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {
	interface DayAfterCheck{
		void timeout();
		void firstday();
	}
	
	private DayAfterCheck callback;;
	private final String dest_time = "23:59:00";		//��:��:��
	private final String dest_date = "16:23:55:00"; // ��¥:��:��:��
	
	public void setDayAfterCheck(DayAfterCheck callback){
		this.callback = callback;
	}
	
	public void timecheck(){
		if(dest_time.equals(getCrttime())){ // ������ �Ǹ� �Լ��� ȣ���Ѵ�.
			this.callback.timeout();
		}
	}
	
	public void datecheck(){
		if(dest_date.equals(getCrtdate())){ // �Ŵ� �ʰ� �Ǹ� �Լ��� ȣ���Ѵ�.
			this.callback.firstday();
		}
	}
	
	public String getCrttime(){
		long now = System.currentTimeMillis();
		Date date = new Date(now);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String str = sdf.format(date);
		//System.out.println("���� �ð� : "+ str);
		return str;
	}
	
	public String getCrtdate(){
		long now = System.currentTimeMillis();
		Date date = new Date(now);
		SimpleDateFormat sdf = new SimpleDateFormat("dd:HH:mm:ss");
		String str = sdf.format(date);
		//System.out.println("���� ��¥ �� �ð� : "+ str);
		return str;
	}
}
