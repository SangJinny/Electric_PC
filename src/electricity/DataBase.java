package electricity;

import java.sql.*;

public class DataBase {
	Statement stmt = null;
	Statement temp_stmt = null;
	MonthValue monthvalue = new MonthValue();

	public Statement Connection() {

//		System.out.println("Statement Connection실행");
		try {
			// 1) 해당 클래스를 메모리에 로드해서 해당 클래스가 존재하는지 여부를 확인
			Class.forName("com.mysql.jdbc.Driver");

			String dbInfo = "jdbc:mysql://localhost:3000/IPV4";
			String dbID = "root";
			String dbPW = "1234";

			// 2) 해당 드라이버의 클래스를 이용하여 DB접속시도(java.sql.*)
			Connection conn = DriverManager.getConnection(dbInfo, dbID, dbPW);

			// 3) DB에 명령어를 전달할 수 있는 객체가 생성되어진다( java.sql.Statement)
			stmt = conn.createStatement();
			temp_stmt = conn.createStatement();
			
		} catch (ClassNotFoundException e) {

			System.out.println("JDBC 드라이버가 존재하지 않습니다. " + e);
		} catch (Exception e) {
			System.out.println("기타 오류 " + e);
		}

//		System.out.println("Statement Connection종료");

		return stmt;
	}

	// 한달내의 Month_data값을 선택해온다.
	public float SelectPowerResult(int ID) {
		stmt = Connection();
		float temp_float = 0;
		try {
			stmt.execute("use ipv4");
			ResultSet rs_SelectPowerResut = stmt
					.executeQuery("select Month_data from equipdata where D_ID = "
							+ ID);
			rs_SelectPowerResut.beforeFirst();
			while (rs_SelectPowerResut.next()) {
				temp_float = rs_SelectPowerResut.getFloat(1);
			}
			//System.out.print("SelectPowerResut를 실행해서 가져온 값");
			//System.out.println(temp_float);
		} catch (Exception e) {
			System.out.println("SelectPowerResut 오류 " + e);

		}
		return temp_float;
	}

	// 한달내의 Month_data값을 업데이트해준다.
	public void UpdatePowerResult(int ID, float PowerResult) {
		stmt = Connection();
		try {
			stmt.execute("use ipv4");
			stmt.executeUpdate("update equipdata set Month_data  = "
					+ PowerResult + " where D_ID = " + ID);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("UpdatePowerResult 오류" + e);
		}
	}

	// Power값을 가져온다.
	public Boolean SelectOnOff(int ID) {
		stmt = Connection();
		Boolean temp_bool = null;
		try {
			stmt.execute("use ipv4");
			ResultSet rs_ValidateOnOff = stmt.executeQuery("select power from state where ID = " + ID);
			rs_ValidateOnOff.beforeFirst();
			while (rs_ValidateOnOff.next()) {
				temp_bool = rs_ValidateOnOff.getBoolean(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("SelectOnOff 오류" + e);
		}

		return temp_bool;
	}

	// Power값을 업데이트해준다.
	public void UpdateOnoff(int ID, Boolean check) {
		stmt = Connection();
		try {
			stmt.execute("use ipv4");
			stmt.executeUpdate("update state set power  = " + check
					+ " where ID = " + ID);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("UpdateOnoff 오류" + e);
		}

	}
	
	// 이름 값을 업데이트해준다.
	public void UpdateEquipName(int ID, String name) {
		stmt = Connection();
		temp_stmt = Connection();
		try {
			stmt.execute("use ipv4");
			temp_stmt.execute("use ipv4");
			System.out.println("ID는 "+ ID+ "qq name은 " + name);
			stmt.executeUpdate("update state set Equip_name = \""+name
					+ " \"where ID = " + ID);
			temp_stmt.executeUpdate("update equipdata set D_Equip_name = \""+name
					+ " \"where D_ID = " + ID);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("UpdateEquipName 오류" + e);
		}

	}

	// data에 쏴줄 총개수
	public String Numberofdata() {

		String temp_of_String = null;

		stmt = Connection();
		try {
			stmt.execute("use ipv4");
			ResultSet rs_Numberofdata = stmt
					.executeQuery("select COUNT(*) from equipdata");
			rs_Numberofdata.beforeFirst();

			while (rs_Numberofdata.next()) {
				//System.out.printf("rs_Numberofdata의 총개수 ");
				//System.out.println(rs_Numberofdata.getString(1));
				temp_of_String = rs_Numberofdata.getString(1);

			}
			//System.out.printf("numofdata의 총개수 ");
			//System.out.println(temp_of_String);

		} catch (Exception e) {
			System.out.println("Numberofdata 오류 " + e);

		}

		return temp_of_String;

	}
	//날자 하루 씩 증가
	public void add_day()
	{
		int temp=0;
		int i=1;
		stmt = Connection();
		temp_stmt = Connection();
		
		try {
			stmt.execute("use ipv4");
			temp_stmt.execute("use ipv4");
			int k = Integer.parseInt(Numberofdata());
			
			ResultSet rs_add_day = stmt.executeQuery("select Day_after from equipdata");
			rs_add_day.beforeFirst();
			
			while(rs_add_day.next() && i <= k)
			{
				temp = rs_add_day.getInt(1);
				temp +=1;
				temp_stmt.executeUpdate("update equipdata set Day_after="+temp +" where D_ID = " +i);
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//data 달별 수정
	public void Validate_Month_data()
	{
		int i=1;
		int w=0;
		String temp[] = new String[4];
		stmt = Connection();
		temp_stmt = Connection();
		
		try {
			stmt.execute("use ipv4");
			temp_stmt.execute("use ipv4");
			int k = Integer.parseInt(Numberofdata());
			
			ResultSet rs_Validate_Month_data = stmt.executeQuery("select 3Month_data,2Month_data,1Month_data,Month_data from equipdata");
			rs_Validate_Month_data.beforeFirst();
			
			while(rs_Validate_Month_data.next() && i <= k)
			{
				//각각의 data값을 우선 가져온다.
				for (w = 0; w < 4; w++) {
					temp[w] = rs_Validate_Month_data.getString(w+1);
					System.out.printf("temp[%d] = %s",w,rs_Validate_Month_data.getString(w+1));
					System.out.println("첫번쨰 포문 끝");
				}
				//data자리를 바꿔준다.
				for(w=0; w<3;w++)
				{
					temp[w] = temp[w+1];
					System.out.printf("temp[%d] = %s",w,rs_Validate_Month_data.getString(w+1));
					System.out.println("두번쨰 포문 끝");
				}
				temp[w] = "0";
				
				System.out.println("i 값 "+i);
				//update해준다.
				temp_stmt.executeUpdate("update equipdata set "
						+ "3Month_data="+temp[0]
						+ ", 2Month_data="+temp[1]
						+ ", 1Month_data="+temp[2]
						+ ", Month_data="+temp[3]
						+ ", Day_after=0"
						+" where D_ID="+i);
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	// 그래프를 그리기 위한 data값을 가져온다.
	public MonthValue SelectEquipdata(int ID) {

		stmt = Connection();
		try {
			stmt.execute("use ipv4");
		
			// 특정한 ID값만의 data를 가져올때
			ResultSet rs_SelectPowerResut = stmt.executeQuery("select * from equipdata where D_ID = "+ ID);
			rs_SelectPowerResut.beforeFirst();

			while (rs_SelectPowerResut.next()) {

				//System.out.println("SelectPowerResut를 실행해서 가져온 값");
				monthvalue.P_ID = rs_SelectPowerResut.getString(1);
				//System.out.printf("monthvalue.P_ID의 값은");
				//System.out.println(monthvalue.P_ID);
				monthvalue.name = rs_SelectPowerResut.getString(2);
//				System.out.printf("monthvalue.name의 값은");
//				System.out.println(monthvalue.name);
				monthvalue.three_Monthvalue = rs_SelectPowerResut.getString(3);
//				System.out.printf("monthvalue.three_Monthvalue의 값은");
//				System.out.println(monthvalue.three_Monthvalue);
				monthvalue.two_Monthvalue = rs_SelectPowerResut.getString(4);
//				System.out.printf("monthvalue.two_Monthvalue의 값은");
//				System.out.println(monthvalue.two_Monthvalue);
				monthvalue.one_Monthvalue = rs_SelectPowerResut.getString(5);
//				System.out.printf("monthvalue.one_Monthvalue의 값은");
//				System.out.println(monthvalue.one_Monthvalue);
				monthvalue.Day_after = rs_SelectPowerResut.getString(6);
//				System.out.printf("monthvalue.Day_after의 값은");
//				System.out.println(monthvalue.Day_after);
				monthvalue.Monthvalue = rs_SelectPowerResut.getString(7);
//				System.out.printf("monthvalue.Monthvalue의 값은");
//				System.out.println(monthvalue.Monthvalue);
			}
		} catch (Exception e) {
			System.out.println("SelectPowerResut 오류 " + e);

		}
		return monthvalue;
	}

}
