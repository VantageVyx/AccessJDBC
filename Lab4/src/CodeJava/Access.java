package CodeJava;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
/*
 * Stuff Code must do:
 * TASK 1
 * Display the schedule of all trips for a given StartLocationName and Destination Name and Date. 
 * MUST INCLUDE the ScheduledStartTime, ScheduledArrivalTime, DriverID, and BusID.
 * 
 * TASK 2
 * Edit the schedule, namely edit the table of Trip Offering:
 * 		1. Delete a trip offering specified by trip # and date and scheduled start time.
 * 
 *      2. Add a set of trip offerings assuming the values of all attributed are given (software asks if you have more trips to enter)
 * 
 * 
 *      3. Change the driver for a given trip offering (given TripNumber, Date, ScheduledStartTime)
 * 
 *      4. Change the bus for a given trip offering 
 * 
 * 
 * TASK 3
 * Display the stops of a given trip (attributes of the table TripStopInfo)
 * 
 * TASK 4
 * Display the weekly schedule of a given driver and date
 * 
 * TASK 5
 * Add a drive
 * 
 * TASK 6
 * Add a bus
 * 
 * TASK 7
 * Delete a bus
 * 
 * TASK 8
 * Record (insert) the actual data of a given trip offering specified by its key. The actual data include the attributes of the table ActualTripStopInfo.
 * 
 * 
 * Test Program using several test data for the above transactions.
 */

/*
 * Personal Notes:
 * Run from Lab4
 * javac src/CodeJava/Access.java
 * java -cp src CodeJava.Access
 * 
 * javac -cp "src/libs/ucanaccess-5.0.1.jar;src/libs/jackcess-3.3.0.jar;src/libs/commons-lang3-3.12.0.jar;src/libs/commons-logging-1.2.jar;src/libs/hsqldb.jar" src/CodeJava/Access.java
 * 
 * java -cp "src/libs/ucanaccess-5.0.1.jar;src/libs/jackcess-3.3.0.jar;src/libs/commons-lang3-3.12.0.jar;src/libs/commons-logging-1.2.jar;src/libs/hsqldb.jar;src" CodeJava.Access
 * 
 * Best way to run:
 * javac -cp "src/libs/*;src" src/CodeJava/Access.java
 * java -cp "src/libs/*;src" CodeJava.Access
 */

public class Access {
	private static String databaseURL = "jdbc:ucanaccess://C:/Users/valde/Desktop/cs_classes/cs4350_pc/AccessJDBC/Lab4/src/Lab4Database.accdb";

	public static void main(String[] args) {
		System.out.println("Checking displaySchedule:");
		displaySchedule("Oakland", "Alameda", "4/18/2025");
		System.out.println("");
		System.out.println("Checking displayStops:");
		displayStops(1);
		// deleteTrip(3, "4/10/2025", "2:00PM");

		// TODO Auto-generated method stub
	}

	// inner join TripOffering (for ScheduledStartTime, ScheduledArrivalTime,
	// DriverID, BusID) to Trip (for StartLocationName, DestinationName).
	static void displaySchedule(String myStartLocationName, String myDestinationName, String dateStr) {
		try {
			// Load the UCanAccess driver
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

			// Establish connection
			Connection connection = DriverManager.getConnection(databaseURL);
			System.out.println("Connected to the MS Access database");
			Date mydate = convertToDate(dateStr);

			try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM TripOffering " +
					"INNER JOIN Trip AS t ON t.TripNumber=TripOffering.TripNumber " +
					"WHERE t.StartLocationName = ? " +
					"AND t.DestinationName = ?"
					+ "AND TripOffering.TripDate = ?")) {
				pstmt.setString(1, myStartLocationName);
				pstmt.setString(2, myDestinationName);
				pstmt.setDate(3, mydate);
				try (ResultSet rs = pstmt.executeQuery()) {

					while (rs.next()) {
						String schArrivalT = rs.getString("ScheduledArrivalTime");
						String schStartT = rs.getString("ScheduledStartTime");
						String driverName = rs.getString("DriverName");
						String busID = rs.getString("BusID");
						System.out.println("Scheduled Arrival Time:" + schArrivalT + ", ScheduledStartTime:" + schStartT
								+ ", driverName:" + driverName + ",BusID:" + busID);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.println("UCanAccess driver not found: " + e.getMessage());
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}

	}

	static void displayStops(int myTripNumber) {

		try {
			Connection connection = DriverManager.getConnection(databaseURL);
			System.out.println("Connected to the MS Access database");
			System.out.println("Stops for TripNumber:" + myTripNumber);
			try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM Stop " +
					"INNER JOIN TripStopInfo ON TripStopInfo.StopNumber=Stop.StopNumber " +
					"WHERE TripStopInfo.TripNumber = ?")) {
				pstmt.setInt(1, myTripNumber);
				try (ResultSet rs = pstmt.executeQuery()) {

					while (rs.next()) {
						String stop = rs.getString("StopAddress");
						System.out.println("Stop: " + stop);
					}
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	static void deleteTrip(int myTripNumber, String mydate, String myschStartTime) {
		try (Connection connection = DriverManager.getConnection(databaseURL)) {
			SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
			java.util.Date parsedUtilDate = sdf.parse(mydate);
			Date sqlDate = new Date(parsedUtilDate.getTime());

			// deleting from TripOffering first
			String sqlTripOffering = "DELETE FROM TripOffering WHERE TripNumber = ? AND TripDate = ? AND ScheduledStartTime = ?";
			try (PreparedStatement pstmt1 = connection.prepareStatement(sqlTripOffering)) {
				pstmt1.setInt(1, myTripNumber);
				pstmt1.setDate(2, sqlDate);
				pstmt1.setString(3, myschStartTime);
				int rowsAffected1 = pstmt1.executeUpdate();
				System.out.println("Deleted " + rowsAffected1 + " row(s) from the TripOffering table.");
			}

			// next deleting from trip
			String sqlTrip = "DELETE FROM Trip WHERE TripNumber = ?";
			try (PreparedStatement pstmt2 = connection.prepareStatement(sqlTrip)) {
				pstmt2.setInt(1, myTripNumber);
				int rowsAffected2 = pstmt2.executeUpdate();
				System.out.println("Deleted " + rowsAffected2 + " row(s) from the Trip table.");
			}
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
	}

	// its easiest to just have users input a string date that the code converts to
	// a usable Date format
	public static Date convertToDate(String dateStr) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
		java.util.Date parsedUtilDate = sdf.parse(dateStr);
		return new Date(parsedUtilDate.getTime());

	}

}
