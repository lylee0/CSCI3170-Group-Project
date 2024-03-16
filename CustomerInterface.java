import java.util.*;

class CustomerInterface extends Main {
    // @formatter:off
    static String[] main_menu = {
            "<This is the customer interface>",
            "---------------------------------",
            "1. Book search",
            "2. Order creation",
            "3. Order altering",
            "4. Order query",
            "5. Back to main menu",
    };
    static String[] book_search_menu = {
            "What do you want to search?",
            "1. ISBN",
            "2. Book title",
            "3. Author name",
            "4. Exit"
    };
    // @formatter:on

    CustomerInterface(Main parent_instance) {
        // inherit instance objects from the parent instance
        conn = parent_instance.conn;
        system_date = parent_instance.system_date;
    }

    void book_search() {
        print_menu(book_search_menu);
        int choice = get_user_choice(4);

        String where_stmt_general;
        String where_stmt_exact_match = null;  // For displaying exact matches of 'title' and 'author_name' first

        if (choice == 1) {
            // Query by ISBN
            System.out.print("Input the ISBN: ");

            Scanner scanner = new Scanner(System.in);
            String isbn = scanner.nextLine().replace("-", "");

            where_stmt_general = String.format("WHERE (b.isbn = ba.isbn) and (b.isbn = %s)", isbn);
        } else if (choice == 2) {
            // Query by book title
            System.out.print("Input the book title (wild cards '%' and '_' are supported): ");

            Scanner scanner = new Scanner(System.in);
            String title = scanner.nextLine();

            where_stmt_general = String.format("WHERE (b.isbn = ba.isbn) and (title LIKE '%s')", title);

            // Construct query statement for exact match
            if (title.substring(1, title.length() - 1).indexOf('%') == -1) {
                // '%' does not exist in the middle of the input
                title = title.replace("%", "");
                where_stmt_exact_match = String.format("WHERE (b.isbn = ba.isbn) and (title LIKE '%s')", title);
            }
        } else if (choice == 3) {
            // Query by author name
            System.out.print("Input the author name (wild cards '%' and '_' are supported): ");

            Scanner scanner = new Scanner(System.in);
            String author_name = scanner.nextLine();

            where_stmt_general = String.format("WHERE (b.isbn = ba.isbn) and (author_name LIKE '%s')", author_name);

            // Construct query statement for exact match
            if (author_name.substring(1, author_name.length() - 1).indexOf('%') == -1) {
                // '%' does not exist in the middle of the input
                author_name = author_name.replace("%", "");
                where_stmt_exact_match = String.format("WHERE (b.isbn = ba.isbn) and (author_name LIKE '%s')", author_name);
            }
        } else return;

        // Print query result
        try {
            List<Long> printed_isbn_list = new ArrayList<>();
            long current_isbn = -1;
            int author_count = -1;

            // Exact matches go first
            for (String where_stmt : new String[]{where_stmt_exact_match, where_stmt_general}) {
                if (where_stmt == null) continue;

                // Construct the complete SQL statement
                // @formatter:off
                String sql_statement = String.join(" ",
                        "SELECT b.isbn, title, unit_price, no_of_copies, author_name",
                        "FROM book b, book_author ba",
                        where_stmt,
                        "ORDER BY title, b.isbn, author_name");
                // @formatter:on

                ExecuteQuery query = new ExecuteQuery(sql_statement);
                while (query.rs.next()) {
                    long isbn = query.rs.getLong(1);
                    String title = query.rs.getString(2);
                    int unit_price = query.rs.getInt(3);
                    int no_of_copies = query.rs.getInt(4);
                    String author_name = query.rs.getString(5);

                    // Skip if the book is printed already
                    if (printed_isbn_list.contains(isbn)) continue;

                    if (isbn == current_isbn) {
                        // Print the remaining authors of the book that is currently printing
                        author_count++;
                    } else {
                        printed_isbn_list.add(current_isbn);  // Record the ISBN of the book that has just been printed
                        author_count = 1;

                        System.out.printf("\nRecord %d\n", printed_isbn_list.size());
                        System.out.println("ISBN: " + isbn_long_to_str(isbn));
                        System.out.println("Book title: " + title);
                        System.out.printf("Unit price: %d\n", unit_price);
                        System.out.printf("No. of available: %d\n", no_of_copies);
                        System.out.println("Authors:");
                    }
                    System.out.printf("%d: %s\n", author_count, author_name);

                    current_isbn = isbn;
                }
                query.close();
            }

            if (printed_isbn_list.isEmpty()) System.out.println("No results found.");
        } catch (Exception e) {
            System.err.println("Failed to query: " + e.getMessage());
        }
    }

    /* Must set 'public' since this method is 'public' in the superclass */
    public void loop() {
        while (true) {
            print_menu(main_menu);
            int choice = get_user_choice(5);

            if (choice == 1) book_search();
            else if (choice == 2) {
                // ...
            } else if (choice == 3) {
                // ...
            } else if (choice == 4) {
                // ...
            } else break;
        }
    }
}

/*
public class CustomerInterface{

    private void orderCreation(){
        System.out.println("Please enter your customerID??");
        //get input
        System.out.println(">> What books do you want to order??");
        System.out.println(">> Input ISBN and then the quantity.");
        System.out.println(">> You can press \"L\" to see ordered list, or \"F\" to finish ordering.");
        System.out.println("Please enter the book's ISBN: ");
        //get input
        start();
        // to be done
    }

    private void orderAltering(){
        System.out.println("Please enter the OrderID that you want to change: ");
        //get input
        //print
        System.out.println("Which book you want to alter (input book no.):\n");
        //get input
        System.out.println("input add or remove");
        //get input
        System.out.println("Input the number: ");
        //get input
        System.out.println("Update is ok!");
        System.out.println("Update done!!");
        System.out.println("Update charge");
        //print
        start();
        // to be done
    }

    private void orderQuery(){
        System.out.println("Please Input Customer ID: ");
        //get input
        System.out.println("Please Input the Year: ");
        //get input
        //print
        start();
        // to be done
    }
}
 */
