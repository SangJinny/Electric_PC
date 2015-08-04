package electricity;

import java.sql.*;

public class DataBase {
	Statement stmt = null;
	Statement temp_stmt = null;
	MonthValue monthvalue = new MonthValue();

	public Statement Connection() {

//		System.out.println("Statement Connection����");
		try {
			// 1) �ش� Ŭ������ �޸𸮿� �ε��ؼ� �ش� Ŭ������ �����ϴ��� ���θ� Ȯ��
			Class.forName("com.mysql.jdbc.Driver");

			String dbInfo = "jdbc:mysql://localhost:3000/IPV4";
			String dbID = "root";
			String dbPW = "1234";

			// 2) �ش� ����̹��� Ŭ������ �̿��Ͽ� DB���ӽõ�(java.sql.*)
			Connection conn = DriverManager.getConnection(dbInfo, dbID, dbPW);

			// 3) DB�� ��ɾ ������ �� �ִ� ��ü�� �����Ǿ�����( java.sql.Statement)
			stmt = conn.createStatement();
			temp_stmt = conn.createStatement();
			
		} catch (ClassNotFoundException e) {

			System.out.println("JDBC ����̹��� �������� �ʽ��ϴ�. " + e);
		} catch (Exception e) {
			System.out.println("��Ÿ ���� " + e);
		}

//		System.out.println("Statement Connection����");

		return stmt;
	}

	// �Ѵ޳��� Month_data���� �����ؿ´�.
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
			//System.out.print("SelectPowerResut�� �����ؼ� ������ ��");
			//System.out.println(temp_float);
		} catch (Exception e) {
			System.out.println("SelectPowerResut ���� " + e);

		}
		return temp_float;
	}

	// �Ѵ޳��� Month_data���� ������Ʈ���ش�.
	public void UpdatePowerResult(int ID, float PowerResult) {
		stmt = Connection();
		try {
			stmt.execute("use ipv4");
			stmt.executeUpdate("update equipdata set Month_data  = "
					+ PowerResult + " where D_ID = " + ID);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("UpdatePowerResult ����" + e);
		}
	}

	// Power���� �����´�.
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
			System.out.println("SelectOnOff ����" + e);
		}

		return temp_bool;
	}

	// Power���� ������Ʈ���ش�.
	public void UpdateOnoff(int ID, Boolean check) {
		stmt = Connection();
		try {
			stmt.execute("use ipv4");
			stmt.executeUpdate("update state set power  = " + check
					+ " where ID = " + ID);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("UpdateOnoff ����" + e);
		}

	}
	
	// �̸� ���� ������Ʈ���ش�.
	public void UpdateEquipName(int ID, String name) {
		stmt = Connection();
		temp_stmt = Connection();
		try {
			stmt.execute("use ipv4");
			temp_stmt.execute("use ipv4");
			System.out.println("ID�� "+ ID+ "qq name�� " + name);
			stmt.executeUpdate("update state set Equip_name = \""+name
					+ " \"where ID = " + ID);
			temp_stmt.executeUpdate("update equipdata set D_Equip_name = \""+name
					+ " \"where D_ID = " + ID);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("UpdateEquipName ����" + e);
		}

	}

	// data�� ���� �Ѱ���
	public String Numberofdata() {

		String temp_of_String = null;

		stmt = Connection();
		try {
			stmt.execute("use ipv4");
			ResultSet rs_Numberofdata = stmt
					.executeQuery("select COUNT(*) from equipdata");
			rs_Numberofdata.beforeFirst();

			while (rs_Numberofdata.next()) {
				//System.out.printf("rs_Numberofdata�� �Ѱ��� ");
				//System.out.println(rs_Numberofdata.getString(1));
				temp_of_String = rs_Numberofdata.getString(1);

			}
			//System.out.printf("numofdata�� �Ѱ��� ");
			//System.out.println(temp_of_String);

		} catch (Exception e) {
			System.out.println("Numberofdata ���� " + e);

		}

		return temp_of_String;

	}
	//���� �Ϸ� �� ����
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
	
	//data �޺� ����
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
				//������ data���� �켱 �����´�.
				for (w = 0; w < 4; w++) {
					temp[w] = rs_Validate_Month_data.getString(w+1);
					System.out.printf("temp[%d] = %s",w,rs_Validate_Month_data.getString(w+1));
					System.out.println("ù���� ���� ��");
				}
				//data�ڸ��� �ٲ��ش�.
				for(w=0; w<3;w++)
				{
					temp[w] = temp[w+1];
					System.out.printf("temp[%d] = %s",w,rs_Validate_Month_data.getString(w+1));
					System.out.println("�ι��� ���� ��");
				}
				temp[w] = "0";
				
				System.out.println("i �� "+i);
				//update���ش�.
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

	// �׷����� �׸��� ���� data���� �����´�.
	public MonthValue SelectEquipdata(int ID) {

		stmt = Connection();
		try {
			stmt.execute("use ipv4");
		
			// Ư���� ID������ data�� �����ö�
			ResultSet rs_SelectPowerResut = stmt.executeQuery("select * from equipdata where D_ID = "+ ID);
			rs_SelectPowerResut.beforeFirst();

			while (rs_SelectPowerResut.next()) {

				//System.out.println("SelectPowerResut�� �����ؼ� ������ ��");
				monthvalue.P_ID = rs_SelectPowerResut.getString(1);
				//System.out.printf("monthvalue.P_ID�� ����");
				//System.out.println(monthvalue.P_ID);
				monthvalue.name = rs_SelectPowerResut.getString(2);
//				System.out.printf("monthvalue.name�� ����");
//				System.out.println(monthvalue.name);
				monthvalue.three_Monthvalue = rs_SelectPowerResut.getString(3);
//				System.out.printf("monthvalue.three_Monthvalue�� ����");
//				System.out.println(monthvalue.three_Monthvalue);
				monthvalue.two_Monthvalue = rs_SelectPowerResut.getString(4);
//				System.out.printf("monthvalue.two_Monthvalue�� ����");
//				System.out.println(monthvalue.two_Monthvalue);
				monthvalue.one_Monthvalue = rs_SelectPowerResut.getString(5);
//				System.out.printf("monthvalue.one_Monthvalue�� ����");
//				System.out.println(monthvalue.one_Monthvalue);
				monthvalue.Day_after = rs_SelectPowerResut.getString(6);
//				System.out.printf("monthvalue.Day_after�� ����");
//				System.out.println(monthvalue.Day_after);
				monthvalue.Monthvalue = rs_SelectPowerResut.getString(7);
//				System.out.printf("monthvalue.Monthvalue�� ����");
//				System.out.println(monthvalue.Monthvalue);
			}
		} catch (Exception e) {
			System.out.println("SelectPowerResut ���� " + e);

		}
		return monthvalue;
	}

}
