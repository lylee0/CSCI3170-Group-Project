import java.util.*;
import java.sql.*;

public class Main {
    Connection conn;

    class SystemInterface {
        void print_menu() {
            System.out.println("<This is the system interface>");
            System.out.println("-------------------------------");
            System.out.println("1. Create table");
            System.out.println("2. Delete table");
            System.out.println("3. Insert data");
            System.out.println("4. Set system date");
            System.out.println("5. Back to main menu");
            System.out.print("\nPlease enter your choice??..");
        }

        void loop() {
            while (true) {
                print_menu();
                int choice = get_user_choice(5);

                if (choice == 1) {
                    // ...
                } else if (choice == 2) {
                    // ...
                } else if (choice == 3) {
                    // ...
                } else if (choice == 4) {
                    // ...
                } else {
                    break;
                }
            }
        }
    }

    class CustomerInterface {
        void print_menu() {
            System.out.println("<This is the customer interface>");
            System.out.println("---------------------------------");
            System.out.println("1. Book search");
            System.out.println("2. Order creation");
            System.out.println("3. Order altering");
            System.out.println("4. Order query");
            System.out.println("5. Back to main menu");
            System.out.print("\nPlease enter your choice??..");
        }

        void loop() {
            while (true) {
                print_menu();
                int choice = get_user_choice(5);

                if (choice == 1) {
                    // ...
                } else if (choice == 2) {
                    // ...
                } else if (choice == 3) {
                    // ...
                } else if (choice == 4) {
                    // ...
                } else {
                    break;
                }
            }
        }
    }

    class BookstoreInterface {
        void print_menu() {
            System.out.println("<This is the bookstore interface>");
            System.out.println("----------------------------------");
            System.out.println("1. Order update");
            System.out.println("2. Order query");
            System.out.println("3. N most popular book query");
            System.out.println("4. Back to main menu");
            System.out.print("\nPlease enter your choice??..");
        }

        void loop() {
            while (true) {
                print_menu();
                int choice = get_user_choice(4);

                if (choice == 1) {
                    // ...
                } else if (choice == 2) {
                    // ...
                } else if (choice == 3) {
                    // ...
                } else {
                    break;
                }
            }
        }
    }

    static int get_user_choice(int n_choices) {
        // construct the list of choices
        List<String> choices = new ArrayList<>();
        for (int i = 1; i <= n_choices; i++) {
            choices.add(Integer.toString(i));
        }

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
            System.err.println("Unable to load the driver class: " + e.getMessage());  // e.g., "Unable to load the driver class: com.mysql.jdbc.Driver"
            return false;
        }

        // Establish a connection
        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@//db18.cse.cuhk.edu.hk:1521/oradb.cse.cuhk.edu.hk?autoReconnect=true&useSSL=false", "h030", "Kapaibco");
        } catch (Exception e) {
            System.err.println("Unable to connect to the database: " + e.getMessage());
            return false;
        }

        return true;
    }

    void print_menu() {
        System.out.println("The system date is now: ");
        System.out.println("<This is the Book Ordering System>");
        System.out.println("-----------------------------------");
        System.out.println("1. System interface");
        System.out.println("2. Customer interface");
        System.out.println("3. Bookstore interface");
        System.out.println("4. Show system date");
        System.out.println("5. Quit the system......");
        System.out.print("\nPlease enter your choice??..");
    }

    // Set 'public', since this is theoretically the only entrance to the entire application, apart from main()
    public void loop() {
        if (!connect_database()) {
            return;  // Failed
        }

        while (true) {
            print_menu();
            int choice = get_user_choice(5);

            if (choice == 1) {
                new SystemInterface().loop();
            } else if (choice == 2) {
                new CustomerInterface().loop();
            } else if (choice == 3) {
                new BookstoreInterface().loop();
            } else if (choice == 4) {
                // ...
            } else {
                break;
            }
        }
    }

    public static void main(String[] args) {
        new Main().loop();
    }
}
