package CodeJava;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class TestAccess {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String databaseURL = "jdbc:ucanaccess://C:/Users/ruggl/eclipse-workspace_2400/Lab4/src/Lab4Database.accdb";
		try {
			Connection connection = DriverManager.getConnection(databaseURL);
			System.out.println("Connected to the MS Access database");
			
			try (Statement stmt = connection.createStatement();
				ResultSet rs  = stmt.executeQuery("Select BusID, Model, modelYear from Bus")){
				while(rs.next()) {
					int busID = rs.getInt("BusID");
					String model = rs.getString("Model");
					int modelYear = rs.getInt("modelYear");
					System.out.println(busID + ", "+model+", "+modelYear);
				}
			}
			
			try(PreparedStatement pstmt = connection.prepareStatement("SELECT BusID, Model, modelYear FROM Bus WHERE modelYear>? AND Model LIKE ?")){
				System.out.println("Query2");
				pstmt.setInt(1, 2010);
				pstmt.setString(2,"M%");
				try(ResultSet rs2 = pstmt.executeQuery()){
					while(rs2.next()) {
						int busID = rs2.getInt("BusID");
						String model = rs2.getString("Model");
						int modelYear = rs2.getInt("modelYear");
						
						System.out.println(busID);
						
					}
				}
				
				
			}
			try(PreparedStatement pstmt = connection.prepareStatement("SELECT DestinationName, StartLocationName FROM Trip WHERE StartLocationName = ?")){
				System.out.println("Query3");
				pstmt.setString(1, "Oakland");
				try(ResultSet rs3 = pstmt.executeQuery()){
					while(rs3.next()) {
						String dest = rs3.getString("DestinationName");
						System.out.println(dest);
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
