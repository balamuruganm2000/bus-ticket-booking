package buss;
import java.sql.*;
import java.util.Scanner;

public class BusTicketSystem {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/bus_ticket_systems";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "bala9789";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            System.out.println("Connected to the database");

            try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
				    System.out.println("\n1. View Bus Information");
				    System.out.println("2. Book Ticket");
				    System.out.println("3. Cancel Ticket");
				    System.out.println("4. Check Ticket Availability by Date");
				    System.out.println("5. Exit");
				    System.out.print("Enter your choice: ");
				    int choice = scanner.nextInt();

				    switch (choice) {
				        case 1:
				            viewBusInformation(connection);
				            break;
				        case 2:
				            bookTicket(connection);
				            break;
				        case 3:
				            cancelTicket(connection);
				            break;
				        case 4:
				            checkTicketAvailability(connection);
				            break;
				        case 5:
				            System.out.println("Exiting...");
				            return;
				        default:
				            System.out.println("Invalid choice. Please try again.");
				    }
				}
			}

        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
    }

    public static void viewBusInformation(Connection connection) throws SQLException {
        String query = "SELECT * FROM buses";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            System.out.println("Bus Information:");
            System.out.println("Bus Number | Departure Time");
            while (resultSet.next()) {
                System.out.printf("%-11s | %s%n", resultSet.getString("bus_number"), resultSet.getString("departure_time"));
            }
        }
    }

    public static void bookTicket(Connection connection) throws SQLException {
        try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Enter passenger name:");
			String passengerName = scanner.nextLine();

			System.out.println("Enter bus number:");
			String busNumber = scanner.nextLine();

			System.out.println("Enter departure time (YYYY-MM-DD HH:MM:SS):");
			String departureTime = scanner.nextLine();

			System.out.println("Enter seat number:");
			String seatNumber = scanner.nextLine();

			String insertQuery = "INSERT INTO bus_tickets (passenger_name, bus_number, departure_time, seat_number) VALUES (?, ?, ?, ?)";
			try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
			    preparedStatement.setString(1, passengerName);
			    preparedStatement.setString(2, busNumber);
			    preparedStatement.setString(3, departureTime);
			    preparedStatement.setString(4, seatNumber);
			    preparedStatement.executeUpdate();
			    System.out.println("Ticket booked successfully!");
			}
		}
    }

    public static void cancelTicket(Connection connection) throws SQLException {
        try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Enter ticket ID to cancel:");
			int ticketId = scanner.nextInt();

			String deleteQuery = "DELETE FROM bus_tickets WHERE ticket_id = ?";
			try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
			    preparedStatement.setInt(1, ticketId);
			    int rowsAffected = preparedStatement.executeUpdate();
			    if (rowsAffected > 0) {
			        System.out.println("Ticket canceled successfully!");
			    } else {
			        System.out.println("Ticket not found.");
			    }
			}
		}
    }

    public static void checkTicketAvailability(Connection connection) throws SQLException {
        try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Enter date to check availability (YYYY-MM-DD):");
			String date = scanner.nextLine();

			String query = "SELECT COUNT(*) AS total_tickets FROM bus_tickets WHERE departure_time LIKE ?";
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			    preparedStatement.setString(1, date + "%");
			    ResultSet resultSet = preparedStatement.executeQuery();
			    if (resultSet.next()) {
			        int totalTickets = resultSet.getInt("total_tickets");
			        System.out.println("Total tickets booked for " + date + ": " + totalTickets);
			    }
			}
		}
    }
}