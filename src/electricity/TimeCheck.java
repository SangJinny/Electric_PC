package electricity;

public class TimeCheck extends Thread {
	private Time time;
	private Time.DayAfterCheck callback;
	public DataBase db_ipv4 = new DataBase();
	public TimeCheck() {
		// TODO Auto-generated constructor stub
		time = new Time();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		Time.DayAfterCheck callback = new Time.DayAfterCheck() {

			@Override
			public void timeout() {
				// TODO Auto-generated method stub
				System.out.println("12�ð� �Ǹ��� ���̴�����");
				db_ipv4.add_day();
			}

			@Override
			public void firstday() {
				// TODO Auto-generated method stub
				System.out.println("1���� �Ǿ����ϴ礻");
				db_ipv4.Validate_Month_data();
			}
		};
		while(true){
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			time.setDayAfterCheck(callback);
			time.timecheck();
			time.datecheck();
		}
	}
}
