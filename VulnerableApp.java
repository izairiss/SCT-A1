import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.logging.Level;



public class VulnerableApp {

    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final Scanner scanner = new Scanner(System.in);

    public static String getUserInput() {
        System.out.print("Enter your name: ");
        return scanner.nextLine();
    }

    public static void sendEmail(String to, String subject, String body) {
        try {
            ProcessBuilder pb = new ProcessBuilder("mail", "-s", subject, to);
            Process process = pb.start();
           try (OutputStream os = process.getOutputStream()) {
                os.write(body.getBytes());
                os.flush();
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Email sent successfully.");
            } else {
                System.out.println("Failed to send email. Exit code: " + exitCode);
            }
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
    }

    public static String getData() {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL("https://insecure-api.com/get-data");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            InputStream inputStream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            reader.close();
        } catch (Exception e) {
            Logger logger = Logger.getLogger("AppLogger");
            logger.log(Level.SEVERE, "Failed to fetch data from API", e);
        }

        return result.toString();
    }

    public static void saveToDb(String data) {
        String query = "INSERT INTO mytable (column1, column2) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, data);
            pstmt.setString(2, "Another Value");

            pstmt.executeUpdate(query);
            System.out.println("Data saved to database.");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String userInput = getUserInput();
        String data = getData();
        saveToDb(data);
        sendEmail("admin@example.com", "User Input", userInput);
    }
}