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
import java.sql.Statement;	
import java.time.DayOfWeek;	
import java.time.LocalDate;	
import java.time.temporal.TemporalAdjusters;
/*
 * Best way to run:
 * javac -cp "src/libs/*;src" src/CodeJava/Access.java
 * java -cp "src/libs/*;src" CodeJava.Access
 */

/*
 * Trip, TripOffering, Bus, Driver, Stop, ActualTripStopInfo, TripStopInfo
 *
 */

public class Access {
	// private static String databaseURL =
	// "jdbc:ucanaccess://C:/Users/valde/Desktop/cs_classes/cs4350_pc/AccessJDBC/Lab4/src/Lab4Database.accdb";
	private static String databaseURL = "jdbc:ucanaccess://C:/Users/valde/Desktop/CS classes/CS4350/AccessJDBC/Lab4/src/Lab4Database.accdb";

	public static void main(String[] args) {
		// Task 1
		// System.out.println("Checking displaySchedule:");
		// displaySchedule("Oakland", "Alameda", "4/18/2025");

		// System.out.println("Checking displayStops:");
		// displayStops(1);

		// Task 2
		// Part 3: Add a set of trip offerings assuming the values of all attributes
		// (software asks if you have more trips to enter)
		displayTripOfferings();
		addMultipleTripOfferings();
		displayTripOfferings();

		// displayTripOfferings();
		addTripOffering(1, "4/10/2025", "2:00PM", "3:00PM", "Driver1", "1");

		// displayTripOfferings();
		// Part 2: Delete a trip offering and its associated trip // works
		deleteTripOffering(1, "4/10/2025", "2:00PM");
		
		
		// Part 4: Change the driver for a given trip offering
		displayTripOfferings();
		changeDriver(1, "4/18/2025", "10:00AM", "John Doe");
		displayTripOfferings();
		changeDriver(1, "4/18/2025", "10:00AM", "Arnold Palmer");
		displayTripOfferings();

		// Change bus for a given trip offering
		displayTripOfferings();
		changeBus(1, "4/18/2025", "10:00AM", 14);
		displayTripOfferings();
		//Task 4: display the weekly schedule of a driver given a date
		displayWeeklySchedule("Arnold Palmer", "4/18/2025");

		
		// Task 5 Add a Drive
		displayDrivers();
		addDriver("Jaden Smith", "909-555-1234");
		displayDrivers();
		//Task 6: Add a bus (note if you run the same cmd twice it will throw an error since you will have already added a bus of that primary key, and you can't have two buses of the same primary key)
		addBus("32","TOY",1989);
		// Task 7 Delete a bus
		displayBuses();
		deleteBus("20", "Taurus", 2009);
		displayBuses();

		// Task 8 Insert actual trip data // works
		insertActualTripData(1, "4/10/2025", "2:00PM", 4, "3:00PM", "2:30PM",
				"3:30PM", 10, 5, 1, "1Hr", "John Doe", "1");
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

	public static void addMultipleTripOfferings() {
		Scanner scanner = new Scanner(System.in);
		String choice;
		do {
			System.out.println("Enter Trip Number:");
			int tripNumber = scanner.nextInt();
			scanner.nextLine(); // Consume newline

			System.out.println("Enter Trip Date (M/d/yyyy):");
			String tripDate = scanner.nextLine();

			System.out.println("Enter Scheduled Start Time:");
			String scheduledStartTime = scanner.nextLine();

			System.out.println("Enter Scheduled Arrival Time:");
			String scheduledArrivalTime = scanner.nextLine();

			System.out.println("Enter Driver Name:");
			String driverName = scanner.nextLine();

			System.out.println("Enter Bus ID:");
			String busID = scanner.nextLine();

			addTripOffering(tripNumber, tripDate, scheduledStartTime, scheduledArrivalTime, driverName, busID);

			System.out.println("Do you want to add another trip offering? (yes/no)");
			choice = scanner.nextLine();
		} while (choice.equalsIgnoreCase("yes"));

		scanner.close();
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

	public static void insertActualTripData(int tripNumber, String tripDate, String schStartTime, int stopNumber,
			String schArrivalTime, String actStartTime, String actArrivalTime, int numberOfPassengerIn,
			int numberOfPassengerOut, int seqNumber, String drivingTime, String driverName, String busID) {
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			Connection connection = DriverManager.getConnection(databaseURL);
			System.out.println("Connected to the MS Access database");

			SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
			java.util.Date parsedUtilDate = sdf.parse(tripDate);
			Date sqlDate = new Date(parsedUtilDate.getTime());

			// System.out.println(sqlDate);

			String sqlTripOffering = "INSERT INTO TripOffering (TripNumber, TripDate, ScheduledStartTime, ScheduledArrivalTime, DriverName, BusID) VALUES (?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(sqlTripOffering)) {
				pstmt.setInt(1, tripNumber);
				pstmt.setDate(2, sqlDate);
				pstmt.setString(3, schStartTime);
				pstmt.setString(4, schArrivalTime);
				pstmt.setString(5, driverName);
				pstmt.setString(6, busID);
				int rowsAffected = pstmt.executeUpdate();
				System.out.println("Inserted " + rowsAffected + " row(s) into the TripOffering table.");
			}

			// works and inserts
			String sqlTripStopInfo = "INSERT INTO TripStopInfo (TripNumber, StopNumber, SequenceNumber, DrivingTime) VALUES (?, ?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(sqlTripStopInfo)) {
				pstmt.setInt(1, tripNumber);
				pstmt.setInt(2, stopNumber);
				pstmt.setInt(3, seqNumber);
				pstmt.setString(4, drivingTime);
				int rowsAffected = pstmt.executeUpdate();
				System.out.println("Inserted " + rowsAffected + " row(s) into the TripStopInfo table.");
			}

			String sqlActualTripStop = "INSERT INTO ActualTripStopInfo (TripNumber, TripDate, ScheduledStartTime, StopNumber, ScheduledArrivalTime, ActualStartTime, ActualArrivalTime, NumberOfPassengerIn, NumberOfPassengerOut) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			try (PreparedStatement pstmt = connection.prepareStatement(sqlActualTripStop)) {
				pstmt.setInt(1, tripNumber);
				pstmt.setDate(2, sqlDate);
				pstmt.setString(3, schStartTime);
				pstmt.setInt(4, stopNumber);
				pstmt.setString(5, schArrivalTime);
				pstmt.setString(6, actStartTime);
				pstmt.setString(7, actArrivalTime);
				pstmt.setInt(8, numberOfPassengerIn);
				pstmt.setInt(9, numberOfPassengerOut);
				int rowsAffected = pstmt.executeUpdate();
				System.out.println("Inserted " + rowsAffected + " row(s) into the ActualTripStopInfo table.");
			}
		} catch (ClassNotFoundException e) {
			System.out.println("UCanAccess driver not found: " + e.getMessage());
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


	public static void addBus(String myBusID, String myModel, int myModelYear) {
	try {
		Connection connection = DriverManager.getConnection(databaseURL);
		try(PreparedStatement pstmt = connection.prepareStatement("INSERT INTO Bus (BusID, Model, modelYear) VALUES (?, ?, ?)")){
			pstmt.setString(1, myBusID);
			pstmt.setString(2, myModel);
			pstmt.setInt(3, myModelYear);
			int rowsAffected = pstmt.executeUpdate();  

	    System.out.println("Inserted " + rowsAffected + " row(s) into Bus.");
		}


	}
	catch (SQLException e) {
	e.printStackTrace();
    }
}

	public static void displayWeeklySchedule(String myDriverName, String myStringDate ) {
	try {
		Date myDate = convertToDate(myStringDate);
		
		Connection connection = DriverManager.getConnection(databaseURL);
		LocalDate localDate = myDate.toLocalDate();
		LocalDate weekStart = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate weekEnd = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
		//converting back to Date objects for the sql comparison
		Date startDate = Date.valueOf(weekStart);
		Date endDate = Date.valueOf(weekEnd);
		
		try(PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM TripOffering "
																+ "WHERE DriverName = ? AND TripDate BETWEEN ? AND ?")){
			
			System.out.println("Displaying the schedule of "+myDriverName +" during the week of "+myDate);
			pstmt.setString(1, myDriverName);
			pstmt.setDate(2, startDate);
			pstmt.setDate(3, endDate);
			
			try(ResultSet rs = pstmt.executeQuery()){	
				while(rs.next()) {
					Date tripDate1 = rs.getDate("TripDate");
					String schStartTime = rs.getString("ScheduledStartTime");
					String schArrivalTime = rs.getString("ScheduledArrivalTime");
					System.out.println("Tripdate:"+tripDate1+", Scheduled Start Time: "+schStartTime+", Scheduled Arrival Time:"+schArrivalTime);
				}
			}
			}
	}
	 catch (ParseException e) {
	    
		return;
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
}

	

}
