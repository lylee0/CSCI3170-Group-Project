import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

class SystemInterface extends Main {
    static String[] table_names = {"book", "customer", "orders", "ordering", "book_author"};

    SystemInterface(Main parent_instance) {
        // inherit instance objects from the parent instance
        conn = parent_instance.conn;
        system_date = parent_instance.system_date;
    }

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
        for (String table_name : table_names) {
            String sql_statement = String.format("DROP TABLE %s CASCADE CONSTRAINTS", table_name);
            if (execute_update(sql_statement)) System.out.printf("Table '%s' has been deleted.\n", table_name);
            else System.out.printf("Failed to delete table '%s'.\n", table_name);
        }
    }

    void insert_data() {
        List<File> data_files = new ArrayList<>();

        System.out.println("Please enter the folder path:");

        // Check if the directory is appropriate
        Scanner scanner = new Scanner(System.in);
        String folder_path = scanner.nextLine();
        try {
            for (String table_name : table_names) {
                String filename = table_name + ".txt";
                data_files.add(new File(folder_path, filename));

                // Check whether the latest added file exists
                if (!data_files.get(data_files.size() - 1).exists())
                    throw new Exception(String.format("'%s' does not exist in the directory.", filename));
            }
        } catch (Exception e) {
            System.err.println("Failed to get data: " + e.getMessage());
            return;
        }

        // Define the format specifiers for the tuple of tables
        String[] tuple_formats = {"(%s, '%s', %s, %s)",  // book
                "('%s', '%s', '%s', %s)",  // customer
                "(%s, %s, '%s', %s, '%s')",  // orders
                "(%s, %s, %s)",  // ordering
                "(%s, '%s')",  // book_author
        };

        // Start to insert data
        System.out.println("Processing...");
        try {
            for (int i = 0; i < table_names.length; i++) {
                BufferedReader reader = new BufferedReader(new FileReader(data_files.get(i)));

                // Read line by line until EOF
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|");

                    // Format input data according to the specific table
                    if (i == 0) {
                        // book (isbn, title, unit_price, no_of_copies)
                        parts[0] = parts[0].replace("-", "");
                    } else if (i == 1) {
                        // customer (customer_id, name, shipping_address, credit_card_no)
                        parts[3] = parts[3].replace("-", "");
                    } else if (i == 2) {
                        // orders (order_id, o_date, shipping_status, charge, customer_id)
                        parts[1] = Integer.toString(date_str_to_int(parts[1]));
                    } else if (i == 3) {
                        // ordering (order_id, isbn, quantity)
                        parts[1] = parts[1].replace("-", "");
                    } else if (i == 4) {
                        // book_author (isbn, author_name)
                        parts[0] = parts[0].replace("-", "");
                    }

                    // Construct the SQL update statement
                    String tuple_str = String.format(tuple_formats[i], (Object[]) parts);
                    String sql_statement = String.format("INSERT INTO %s VALUES ", table_names[i]) + tuple_str;

                    // Execute the statement
                    String stmt_msg = String.format("%s %s", table_names[i], tuple_str);
                    if (execute_update(sql_statement)) System.out.println("Inserted: " + stmt_msg);
                    else throw new Exception(stmt_msg);
                }
            }
            System.out.println("All data loaded!");
        } catch (Exception e) {
            System.err.println("Failed to insert data: " + e.getMessage());
        }
    }

    void set_system_date() {
        System.out.print("Please input the date (YYYYMMDD): ");

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while (!verify_date_str(input)) {
            System.out.print("Invalid input. Please try again: ");
            input = scanner.nextLine();
        }

        // Update system date
        system_date.value = date_str_to_int(input);

        // Get latest date in orders
        try {
            Main.ExecuteQuery query = new Main.ExecuteQuery("SELECT MAX(o_date) FROM orders");
            query.rs.next();  // Move the cursor to the 1st row of the result set
            int latest_order_date = query.rs.getInt(1);  // 0 if the table is empty
            query.close();

            // Print the date only if query is successful
            System.out.println("Latest date in orders: " + date_int_to_str(latest_order_date));
        } catch (Exception e) {
            System.err.println("Failed to get latest order date: " + e.getMessage());
        }

        // Print current system date
        System.out.println("Today is " + date_int_to_str(system_date.value));
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

    /* Must set 'public' since this method is 'public' in the superclass */
    public void loop() {
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
