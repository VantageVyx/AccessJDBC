package CodeJava;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.util.Scanner;

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

/*
 * Trip, TripOffering, Bus, Driver, Stop, ActualTripStopInfo, TripStopInfo
 *
 */

public class Access {
	private static String databaseURL = "jdbc:ucanaccess://C:/Users/valde/Desktop/cs_classes/cs4350_pc/AccessJDBC/Lab4/src/Lab4Database.accdb";

	public static void main(String[] args) {
		// Task 1
		// System.out.println("Checking displaySchedule:");
		// displaySchedule("Oakland", "Alameda", "4/18/2025");

		// System.out.println("Checking displayStops:");
		// displayStops(1);

		// Task 2
		// displayTripOffering();
		// Part 3: Add a set of trip offerings assuming the values of all attributes
		// (software asks if you have more trips to enter)

		// displayTripOfferings();
		// addTripOffering(1, "4/10/2025", "2:00PM", "3:00PM", "Driver1", "1");

		// displayTripOfferings();
		// Part 2: Delete a trip offering and its associated trip // works
		// deleteTripOffering(1, "4/10/2025", "2:00PM");

		// Convert the date
		// displayTripOfferings();

		// displayDrivers();

		// displayBuses();

		// Part 4: Change the driver for a given trip offering
		// changeDriver(1, "2025-04-18", "10:00AM", "John Doe"); // works
		// changeDriver(1, "2025-04-18", "10:00AM", "Arnold Palmer"); // works

		// Change bus for a given trip offering
		// changeBus(1, "2025-04-18", "10:00AM", 7); //works

		// Task 5 Add a Drive
		// addDriver("James Madison", "909-844-9551"); // works

		// Task 7 Delete a bus
		// deleteBus("1", "Volvo 9700", 2019); works

		// Add a set of trip offerings assuming the values of all attributes are given
		// (software asks if you have more trips to enter)
	}

	// inner join TripOffering (for ScheduledStartTime, ScheduledArrivalTime,
	// DriverID, BusID) to Trip (for StartLocationName, DestinationName).
	public static void displaySchedule(String myStartLocationName, String myDestinationName, String dateStr) {
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
				// Set the parameters for the prepared statement
				pstmt.setString(1, myStartLocationName);
				pstmt.setString(2, myDestinationName);
				pstmt.setDate(3, mydate);

				// Execute the query
				try (ResultSet rs = pstmt.executeQuery()) {

					while (rs.next()) {
						String schArrivalT = rs.getString("ScheduledArrivalTime");
						String schStartT = rs.getString("ScheduledStartTime");
						String driverName = rs.getString("DriverName");
						String busID = rs.getString("BusID");
						System.out.println("Scheduled Arrival Time: " + schArrivalT + ", ScheduledStartTime: "
								+ schStartT + ", driverName: " + driverName + ",BusID: " + busID);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.println("UCanAccess driver not found: " + e.getMessage());
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}

	}

	public static void displayStops(int myTripNumber) {

		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
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
		} catch (ClassNotFoundException e) {
			System.out.println("Ucanaccess driver not found: " + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void displayDrivers() {
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

			Connection connection = DriverManager.getConnection(databaseURL);
			System.out.println("Connected to the MS Access database");

			String sqlDriver = "SELECT * FROM Driver";
			try (PreparedStatement pstmt = connection.prepareStatement(sqlDriver)) {
				try (ResultSet rs = pstmt.executeQuery()) {
					while (rs.next()) {
						String driverName = rs.getString("DriverName");
						String date = rs.getString("DrivertelephoneNumber");

						System.out.println("DriverName: " + driverName + ", DrivertelephoneNumber: " + date);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.println("UCanAccess driver not found: " + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Trip Offering modification functions

	public static void displayTripOfferings() {
		try {
			// Load the UCanAccess driver
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

			// Establish connection
			Connection connection = DriverManager.getConnection(databaseURL);
			System.out.println("Connected to the MS Access database");

			String sqlTripOffering = "SELECT * FROM TripOffering";
			try (PreparedStatement pstmt = connection.prepareStatement(sqlTripOffering)) {
				try (ResultSet rs = pstmt.executeQuery()) {
					while (rs.next()) {
						int tripNumber = rs.getInt("TripNumber");
						Date tripDate = rs.getDate("TripDate");
						String schStartTime = rs.getString("ScheduledStartTime");
						String schArrivalTime = rs.getString("ScheduledArrivalTime");
						String driverName = rs.getString("DriverName");
						String busID = rs.getString("BusID");

						System.out.println("TripNumber: " + tripNumber + ", TripDate: " + tripDate
								+ ", ScheduledStartTime: " + schStartTime + ", ScheduledArrivalTime: " + schArrivalTime
								+ ", DriverName: " + driverName + ", BusID: " + busID);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.println("UCanAccess driver not found: " + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void displayBuses() {
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			Connection connection = DriverManager.getConnection(databaseURL);
			// System.out.println("Connected to the MS Access Database");

			String sqlBus = "SELECT * FROM Bus";
			try (PreparedStatement pstmt = connection.prepareStatement(sqlBus)) {
				try (ResultSet rs = pstmt.executeQuery()) {
					while (rs.next()) {
						String busID = rs.getString("BusID");
						String model = rs.getString("Model");
						int modelYear = rs.getInt("modelYear");

						System.out.println("BusID: " + busID + ", Model: " + model + ", Model Year: " + modelYear);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.println("UCanAccess driver not found: " + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void addTripOffering(int myTripNumber, String mydate, String myschStartTime, String myschArrivalTime,
			String myDriverName, String myBusID) {
		try {
			// Load the UCanAccess driver
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

			// Establish connection
			Connection connection = DriverManager.getConnection(databaseURL);
			System.out.println("Connected to the MS Access database");

			// Convert the string date to java.sql.Date
			SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
			java.util.Date parsedUtilDate = sdf.parse(mydate);
			Date sqlDate = new Date(parsedUtilDate.getTime());

			String sqlTripOffering = "INSERT INTO TripOffering (TripNumber, TripDate, ScheduledStartTime, ScheduledArrivalTime, DriverName, BusID) VALUES (?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(sqlTripOffering)) {
				pstmt.setInt(1, myTripNumber);
				pstmt.setDate(2, sqlDate);
				pstmt.setString(3, myschStartTime);
				pstmt.setString(4, myschArrivalTime);
				pstmt.setString(5, myDriverName);
				pstmt.setString(6, myBusID);
				int rowsAffected = pstmt.executeUpdate();
				System.out.println("Inserted " + rowsAffected + " row(s) into the TripOffering table.");
			}
		} catch (ClassNotFoundException e) {
			System.out.println("UCanAccess driver not found: " + e.getMessage());
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
	}

	public static void changeDriver(int myTripNumber, String myDate, String mySchStartTime, String newName) {
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			Connection connection = DriverManager.getConnection(databaseURL);
			System.out.println("Connected to the MS Access database");

			// Convert the string date to java.sql.Date
			SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
			java.util.Date parsedUtilDate = sdf.parse(myDate);
			Date sqlDate = new Date(parsedUtilDate.getTime());

			String sqlTripOffering = "UPDATE TripOffering SET DriverName = ? WHERE TripNumber = ? AND TripDate = ? AND ScheduledStartTime = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(sqlTripOffering)) {
				pstmt.setString(1, newName);
				pstmt.setInt(2, myTripNumber);
				pstmt.setDate(3, sqlDate);
				pstmt.setString(4, mySchStartTime);
				int rowsAffected = pstmt.executeUpdate();
				System.out.println("Updated " + rowsAffected + " row(s) in the TripOffering table.");
			}

		} catch (ClassNotFoundException e) {
			System.out.println("UCanAccess driver not found: " + e.getMessage());
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}

	}

	public static void changeBus(int myTripNumber, String myDate, String mySchStartTime, int busID) {
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			Connection connection = DriverManager.getConnection(databaseURL);
			System.out.println("Connected to the MS Access database");

			// Convert the string date to java.sql.Date
			SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
			java.util.Date parsedUtilDate = sdf.parse(myDate);
			Date sqlDate = new Date(parsedUtilDate.getTime());

			String sqlTripOffering = "UPDATE TripOffering SET BusID = ? WHERE TripNumber = ? AND TripDate = ? AND ScheduledStartTime = ?";

			try (PreparedStatement pstmt = connection.prepareStatement(sqlTripOffering)) {
				pstmt.setInt(1, busID);
				pstmt.setInt(2, myTripNumber);
				pstmt.setDate(3, sqlDate);
				pstmt.setString(4, mySchStartTime);
				int rowsAffected = pstmt.executeUpdate();
				System.out.println("Updated " + rowsAffected + " row(s) in the TripOffering Table.");
			}
		} catch (ClassNotFoundException e) {
			System.out.println("UCanAccess driver not found: " + e.getMessage());
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
	}

	public static void deleteTripOffering(int myTripNumber, String mydate, String myschStartTime) {
		try {
			// Load the UCanAccess driver
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");

			// Establish connection
			Connection connection = DriverManager.getConnection(databaseURL);
			System.out.println("Connected to the MS Access database");

			SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
			java.util.Date parsedUtilDate = sdf.parse(mydate);
			Date sqlDate = new Date(parsedUtilDate.getTime());

			String sqlTripOffering = "DELETE FROM TripOffering WHERE TripNumber = ? AND TripDate = ? AND ScheduledStartTime = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(sqlTripOffering)) {
				pstmt.setInt(1, myTripNumber);
				pstmt.setDate(2, sqlDate);
				pstmt.setString(3, myschStartTime);
				int rowsAffected = pstmt.executeUpdate();
				System.out.println("Deleted " + rowsAffected + " row(s) from the TripOffering table.");
			}

			String sqlTrip = "DELETE FROM Trip WHERE TripNumber = ?";
			try (PreparedStatement pstmt2 = connection.prepareStatement(sqlTrip)) {
				pstmt2.setInt(1, myTripNumber);
				int rowsAffected2 = pstmt2.executeUpdate();
				System.out.println("Deleted " + rowsAffected2 + " row(s) from the Trip table.");
			}
		} catch (ClassNotFoundException e) {
			System.out.println("UCanAccess driver not found: " + e.getMessage());
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}
	}

	public static void deleteBus(String busID, String model, int modelYear) {
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			Connection connection = DriverManager.getConnection(databaseURL);
			System.out.println("Connected to the MS Access database");

			int TripNumber = 0;
			String sqlTripOffering = "SELECT TripNumber FROM TripOffering WHERE BusID = ?";
			try (PreparedStatement pstmt2 = connection.prepareStatement(sqlTripOffering)) {
				pstmt2.setString(1, busID);
				try (ResultSet rs = pstmt2.executeQuery()) {

					while (rs.next()) {
						TripNumber = rs.getInt("TripNumber");
					}
				}
			}

			String sqlActualTripStopInfo = "DELETE FROM ActualTripStopInfo WHERE TripNumber = ?";
			try (PreparedStatement pstmt4 = connection.prepareStatement(sqlActualTripStopInfo)) {
				pstmt4.setInt(1, TripNumber);
				int rowsAffected3 = pstmt4.executeUpdate();
				System.out.println("Deleted " + rowsAffected3 + " row(s) from the ActualTripStopInfo table");
			}

			sqlTripOffering = "DELETE FROM TripOffering WHERE BusID = ?";
			try (PreparedStatement pstmt3 = connection.prepareStatement(sqlTripOffering)) {
				pstmt3.setString(1, busID);
				int rowsAffected2 = pstmt3.executeUpdate();
				System.out.println("Deleted " + rowsAffected2 + " row(s) from the TripOffering table.");
			}

			String sqlBus = "DELETE FROM Bus WHERE BusID = ? AND Model = ? AND modelYear = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(sqlBus)) {
				pstmt.setString(1, busID);
				pstmt.setString(2, model);
				pstmt.setInt(3, modelYear);
				int rowsAffected = pstmt.executeUpdate();
				System.out.println("Deleted " + rowsAffected + " row(s) from the Bus table.");
			}

		} catch (ClassNotFoundException e) {
			System.out.println("UCanAccess driver not found: " + e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void addDriver(String driverName, String driverPhone) {
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			Connection connection = DriverManager.getConnection(databaseURL);
			System.out.println("Connected to the MS Access database");

			String sqlTripOffering = "INSERT INTO Driver (DriverName, DriverTelephoneNumber) VALUES (?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(sqlTripOffering)) {
				pstmt.setString(1, driverName);
				pstmt.setString(2, driverPhone);
				int rowsAffected = pstmt.executeUpdate();
				System.out.println("Inserted " + rowsAffected + " row(s) into the TripOffering table.");
			}
		} catch (ClassNotFoundException e) {
			System.out.println("UCanAccess driver not found: " + e.getMessage());
		} catch (SQLException e) {
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
