import java.sql.*;
import java.time.*;
import java.util.*;

public class Main {
    // @formatter:off
    static String[] main_menu = {
            "<This is the Book Ordering System>",
            "-----------------------------------",
            "1. System interface",
            "2. Customer interface",
            "3. Bookstore interface",
            "4. Show system date",
            "5. Quit the system......",
    };
    // @formatter:on

    // Instance objects that should be inherited by every subclass
    Connection conn;
    IntWrapper system_date = new IntWrapper(0);

    class ExecuteQuery {
        Statement stmt;
        ResultSet rs;

        ExecuteQuery(String sql_statement) {
            try {
                // Create a statement object
                stmt = conn.createStatement();

                // Execute the update statement
                rs = stmt.executeQuery(sql_statement);
            } catch (Exception e) {
                // System.err.println("SQL query statement execution failed: " + e.getMessage());
                rs = null;
            }
        }

        void close() {
            try {
                rs.close();
                stmt.close();
            } catch (Exception e) {
                System.err.println("Failed to close resources: " + e.getMessage());
            }
        }
    }

    static String date_int_to_str(int date_int) {
        String year = String.format("%04d", date_int / 10000);
        String month = String.format("%02d", date_int % 10000 / 100);
        String day = String.format("%02d", date_int % 100);

        return String.join("-", year, month, day);
    }

    /* Assume the input string is valid. */
    static int date_str_to_int(String date_str) {
        return Integer.parseInt(date_str.replace("-", ""));
    }

    /* Verify whether the date string is valid. */
    static boolean verify_date_str(String date_str) {
        try {
            date_str = date_str.replace("-", "");
            int year = Integer.parseInt(date_str.substring(0, 4));
            int month = Integer.parseInt(date_str.substring(4, 6));
            int day = Integer.parseInt(date_str.substring(6));

            // noinspection ResultOfMethodCallIgnored
            LocalDate.of(year, month, day);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    static String isbn_long_to_str(long isbn_int) {
        String isbn_str = String.format("%010d", isbn_int);

        String part_1 = isbn_str.substring(0, 1);
        String part_2 = isbn_str.substring(1, 5);
        String part_3 = isbn_str.substring(5, 9);
        String part_4 = isbn_str.substring(9);

        return String.join("-", part_1, part_2, part_3, part_4);
    }

    static void print_menu(String[] lines) {
        // Print a blank line first
        System.out.println();

        // Print the menu content
        for (String line : lines) {
            System.out.println(line);
        }

        // Print user prompt
        System.out.print("\nPlease enter your choice??..");
    }

    static int get_user_choice(int n_choices) {
        // construct the list of choices
        List<String> choices = new ArrayList<>();
        for (int i = 1; i <= n_choices; i++)
            choices.add(Integer.toString(i));

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while (!choices.contains(input)) {
            System.out.print("Invalid input. Please try again: ");
            input = scanner.nextLine();
        }

        return Integer.parseInt(input);
    }

    boolean connect_database() {
        // Load the JDBC driver for Oracle DBMS
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (Exception e) {
            System.err.println("Failed to load the driver class: " + e.getMessage());  // e.g., "Failed to load the driver class: com.mysql.jdbc.Driver"
            return false;
        }

        // Establish a connection
        try {
            // @formatter:off
            conn = DriverManager.getConnection("jdbc:oracle:thin:@//db18.cse.cuhk.edu.hk:1521/oradb.cse.cuhk.edu.hk",
                    "h030", "Kapaibco");  // 'user', 'password'
            // @formatter:on
        } catch (Exception e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            return false;
        }

        return true;
    }

    /* Return 'true' if successful, else 'false'. */
    boolean execute_update(String sql_statement) {
        try {
            // Create a statement object
            Statement stmt = conn.createStatement();

            // Execute the update statement
            stmt.executeUpdate(sql_statement);
        } catch (Exception e) {
            // System.err.println("SQL update statement execution failed: " + e.getMessage());
            return false;
        }

        return true;
    }

    void show_system_date() {
        System.out.println("\nThe system date is now: " + date_int_to_str(system_date.value));
    }

    /* Set 'public', since this is theoretically the only entrance to the entire application, apart from main(). */
    public void loop() {
        if (!connect_database()) return;  // Failed

        show_system_date();

        while (true) {
            print_menu(main_menu);
            int choice = get_user_choice(5);

            if (choice == 1) new SystemInterface(this).loop();
            else if (choice == 2) new CustomerInterface(this).loop();
            else if (choice == 3) new BookstoreInterface(this).loop();
            else if (choice == 4) show_system_date();
            else break;
        }

        try {
            conn.close();
        } catch (Exception e) {
            System.err.println("Failed to close the database connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Main().loop();
    }
}
