import java.sql.*;
import java.time.*;
import java.util.*;

public class Main {
    Connection conn;
    int system_date = 0;

    class SystemInterface {
        /* Create all tables regardless of errors. */
        void create_table() {
            Map<String, String> table_definitions = new LinkedHashMap<>();
            table_definitions.put("book", """
                    CREATE TABLE book
                    (
                        isbn         INTEGER,
                        title        VARCHAR(100),
                        unit_price   INTEGER,
                        no_of_copies INTEGER,
                        PRIMARY KEY (isbn)
                    )
                    """);
            table_definitions.put("customer", """
                    CREATE TABLE customer
                    (
                        customer_id      VARCHAR(10),
                        name             VARCHAR(50),
                        shipping_address VARCHAR(200),
                        credit_card_no   INTEGER,
                        PRIMARY KEY (customer_id)
                    )
                    """);
            table_definitions.put("orders", """
                    CREATE TABLE orders
                    (
                        order_id        INTEGER,
                        o_date          INTEGER,
                        shipping_status CHAR,
                        charge          INTEGER,
                        customer_id     VARCHAR(10),
                        PRIMARY KEY (order_id),
                        FOREIGN KEY (customer_id) REFERENCES customer
                    )
                    """);
            table_definitions.put("ordering", """
                    CREATE TABLE ordering
                    (
                        order_id INTEGER,
                        isbn     INTEGER,
                        quantity INTEGER,
                        PRIMARY KEY (order_id, isbn),
                        FOREIGN KEY (order_id) REFERENCES orders,
                        FOREIGN KEY (isbn) REFERENCES book
                    )
                    """);
            table_definitions.put("book_author", """
                    CREATE TABLE book_author
                    (
                        isbn        INTEGER,
                        author_name VARCHAR(50),
                        PRIMARY KEY (isbn, author_name),
                        FOREIGN KEY (isbn) REFERENCES book
                    )
                    """);

            table_definitions.forEach((table_name, table_definition) -> {
                if (execute_update(table_definition)) System.out.printf("Table '%s' has been created.\n", table_name);
                else System.out.printf("Failed to create table '%s'.\n", table_name);
            });
        }

        /* Delete all tables regardless of errors. */
        void delete_table() {
            List<String> table_names = List.of("book", "customer", "orders", "ordering", "book_author");

            for (String table_name : table_names) {
                String sql_statement = String.format("DROP TABLE %s CASCADE CONSTRAINTS", table_name);
                if (execute_update(sql_statement)) System.out.printf("Table '%s' has been deleted.\n", table_name);
                else System.out.printf("Failed to delete table '%s'.\n", table_name);
            }
        }

        void insert_data() {
            // ...
        }

        void set_system_date() {
            // ...
        }

        void print_menu() {
            System.out.println("\n<This is the system interface>");
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

                if (choice == 1) create_table();
                else if (choice == 2) delete_table();
                else if (choice == 3) insert_data();
                else if (choice == 4) set_system_date();
                else break;
            }
        }
    }

    class CustomerInterface {
        void print_menu() {
            System.out.println("\n<This is the customer interface>");
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
                } else break;
            }
        }
    }

    class BookstoreInterface {
        void print_menu() {
            System.out.println("\n<This is the bookstore interface>");
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
                } else break;
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
            String[] parts = date_str.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            LocalDate.of(year, month, day);
        } catch (Exception e) {
            return false;
        }

        return true;
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
            System.err.println("Unable to load the driver class: " + e.getMessage());  // e.g., "Unable to load the driver class: com.mysql.jdbc.Driver"
            return false;
        }

        // Establish a connection
        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@//db18.cse.cuhk.edu.hk:1521/oradb.cse.cuhk.edu.hk", "h030", "Kapaibco");  // 'user', 'password'
        } catch (Exception e) {
            System.err.println("Unable to connect to the database: " + e.getMessage());
            return false;
        }

        return true;
    }

    Statement create_statement() {
        Statement stmt;

        try {
            stmt = conn.createStatement();
        } catch (Exception e) {
            System.err.println("Unable to create a statement: " + e.getMessage());
            return null;
        }

        return stmt;
    }

    boolean execute_update(String sql_statement) {
        Statement stmt = create_statement();
        if (stmt == null) return false;  // Failed

        try {
            stmt.executeUpdate(sql_statement);
        } catch (Exception e) {
            // System.err.println("SQL update statement execution failed: " + e.getMessage());
            return false;
        }

        return true;
    }

    ResultSet execute_query(String sql_statement) {
        ResultSet rs;

        Statement stmt = create_statement();
        if (stmt == null) return null;  // Failed

        try {
            rs = stmt.executeQuery(sql_statement);
        } catch (Exception e) {
            System.err.println("SQL query statement execution failed: " + e.getMessage());
            return null;
        }

        return rs;
    }

    void show_system_date() {
        System.out.println("\nThe system date is now: " + date_int_to_str(system_date));
    }

    void print_menu() {
        System.out.println("\n<This is the Book Ordering System>");
        System.out.println("-----------------------------------");
        System.out.println("1. System interface");
        System.out.println("2. Customer interface");
        System.out.println("3. Bookstore interface");
        System.out.println("4. Show system date");
        System.out.println("5. Quit the system......");
        System.out.print("\nPlease enter your choice??..");
    }

    /* Set 'public', since this is theoretically the only entrance to the entire application, apart from main(). */
    public void loop() {
        if (!connect_database()) return;  // Failed

        show_system_date();

        while (true) {
            print_menu();
            int choice = get_user_choice(5);

            if (choice == 1) new SystemInterface().loop();
            else if (choice == 2) new CustomerInterface().loop();
            else if (choice == 3) new BookstoreInterface().loop();
            else if (choice == 4) show_system_date();
            else break;
        }

        try {
            conn.close();
        } catch (Exception e) {
            System.err.println("Unable to close the database connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Main().loop();
    }
}
