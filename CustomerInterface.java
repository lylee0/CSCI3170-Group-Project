import java.sql.*;
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
        String input;
        if (choice == 1) {
            // Query by ISBN
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Input the ISBN(X-XXXX-XXXX-X): ");
                input = scanner.nextLine();
                boolean test = false;
                if (input.length() != 13) {
                    System.out.println("ISBN should have 13 digits(X-XXXX-XXXX-X).");
                    continue;
                }
                for (int i = 0; i < input.length(); i++) {
                    char check = input.charAt(i);
                    if ((i != 1) && (i != 6) && (i != 11)) {
                        if (!Character.isDigit(check)) {
                            test = true;
                            System.out.println("Invalid ISBN");
                            break;
                        }
                    } else {
                        if (check != '-') {
                            test = true;
                            System.out.println("Invalid ISBN");
                            break;
                        }
                    }
                }
                if (!test) {
                    break;
                }
            }
            String isbn = input.replace("-", "");

            where_stmt_general = String.format("WHERE (b.isbn = ba.isbn) and (b.isbn = %s)", isbn);
        } else if (choice == 2) {
            // Query by book title
            System.out.print("Input the book title (wild cards '%' and '_' are supported): ");

            Scanner scanner = new Scanner(System.in);
            String title = scanner.nextLine();

            where_stmt_general = String.format("WHERE (b.isbn = ba.isbn) and (title LIKE '%s')", title);

            // Construct query statement for exact match
            if (title.length() > 1 && title.substring(1, title.length() - 1).indexOf('%') == -1) {
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
            if (author_name.length() > 1 && author_name.substring(1, author_name.length() - 1).indexOf('%') == -1) {
                // '%' does not exist in the middle of the input
                author_name = author_name.replace("%", "");
                where_stmt_exact_match = String.format("WHERE (b.isbn = ba.isbn) and (author_name LIKE '%s')", author_name);
            }
        } else return;

        // Print query result
        try {
            List<String> where_stmt_list = new ArrayList<>();
            where_stmt_list.add(where_stmt_exact_match);  // Exact matches go first
            where_stmt_list.add(where_stmt_general);
            while (!where_stmt_list.isEmpty()) {
                /* The beginning of the "try block" before fixing the bug in querying by author_name */
                Set<Long> printed_isbn_set = new HashSet<>();
                long previous_isbn = -1;
                int author_count = -1;

                for (String where_stmt : new ArrayList<>(where_stmt_list)) {  // Iterate over a cloned list
                    if (where_stmt == null) continue;

                    // Construct the complete SQL statement
                    // @formatter:off
                    String sql_statement = String.join(" ",
                            "SELECT b.isbn, title, unit_price, no_of_copies, author_name",
                            "FROM book b, book_author ba",
                            where_stmt,
                            "ORDER BY title, b.isbn, author_name");
                    // @formatter:on
                    PreparedStatement statement = conn.prepareStatement(sql_statement);
                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        long isbn = resultSet.getLong(1);
                        String title = resultSet.getString(2);
                        int unit_price = resultSet.getInt(3);
                        int no_of_copies = resultSet.getInt(4);
                        String author_name = resultSet.getString(5);

                        // Deal with the bug in querying by author_name
                        if (where_stmt.contains("author_name")) {
                            where_stmt_list.add(String.format("WHERE (b.isbn = ba.isbn) and (b.isbn = %s)", isbn));
                            continue;
                        }

                        // Skip if the book is printed already
                        if (printed_isbn_set.contains(isbn)) continue;

                        if (isbn == previous_isbn) {
                            // Print the remaining authors of the book that is currently printing
                            author_count++;
                        } else {
                            printed_isbn_set.add(previous_isbn);  // Record the ISBN of the book that has just been printed
                            author_count = 1;

                            System.out.printf("\nRecord %d\n", printed_isbn_set.size());
                            System.out.println("ISBN: " + isbn_long_to_str(isbn));
                            System.out.println("Book title: " + title);
                            System.out.printf("Unit price: %d\n", unit_price);
                            System.out.printf("No. of available: %d\n", no_of_copies);
                            System.out.println("Authors:");
                        }
                        System.out.printf("%d: %s\n", author_count, author_name);

                        previous_isbn = isbn;
                    }
                    printed_isbn_set.add(previous_isbn);  // Fix the "duplicate author" bug when there is only one result
                    statement.close();
                }

                if (printed_isbn_set.size() == 1) System.out.println("No results found.");
                /* The end of the "try block" before fixing the bug in querying by author_name */

                where_stmt_list.subList(0, 2).clear();
            }
        } catch (Exception e) {
            System.err.println("Failed to query: " + e.getMessage());
        }
    }

    void order_creation() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter your customer ID: ");
        String customer_id = scanner.nextLine();
        // handle wrong customer id
        while (!customer_id.isEmpty()) {
            String id_check = "";
            try {
                String sql_statement = "SELECT c.customer_id FROM customer c WHERE c.customer_id = ?";
                PreparedStatement statement = conn.prepareStatement(sql_statement);
                statement.setString(1, customer_id);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    id_check = resultSet.getString(1);
                }
                statement.close();
            } catch (Exception e) {
                System.err.println("Failed to query: " + e.getMessage());
            }

            if (id_check.equals(customer_id)) {
                break;
            } else {
                System.out.print("Wrong customer ID.");
                System.out.print("Please enter your customer ID again: ");
                customer_id = scanner.nextLine();
            }
        }

        Map<String, Long> isbn_quantity = new LinkedHashMap<>();
        System.out.println(">> Input ISBN and then the quantity.");
        System.out.println(">> You can press 'L' to see ordered list, or 'F' to finish ordering.");
        // create list of isbn and quantity
        while (true) {
            System.out.println("Please enter the book's ISBN(X-XXXX-XXXX-X):");
            String choice = scanner.next();
            if (choice.equals("L")) {
                // print isbn and quantity
                System.out.println("ISBN            Number:");
                //print dict line by line
                for (Map.Entry<String, Long> entry : isbn_quantity.entrySet()) {
                    String key = entry.getKey();
                    long key_long = Long.parseLong(key);
                    Long value = entry.getValue();
                    String key_str = isbn_long_to_str(key_long);
                    System.out.println(key_str + "   " + value);
                }
            } else if (choice.equals("F")) {
                //if no order, break 
                if (isbn_quantity.isEmpty()) {
                    System.out.println("You did not order any book.");
                    System.out.println("Ordering fails");
                    break;
                }
                int check = 1;
                for (Map.Entry<String, Long> entry : isbn_quantity.entrySet()) {
                    Long value = entry.getValue();
                    if (value != 0) {
                        check = 0;
                        break;
                    }
                }
                if (check == 1) {
                    System.out.println("You did not order any book.");
                    System.out.println("Ordering fails");
                    break;
                }
                int o_date = system_date.value;
                //find order id, greatest id + 1
                long order_id = -1;
                try {
                    String sql_statement = "SELECT MAX(order_id) FROM orders";
                    PreparedStatement statement = conn.prepareStatement(sql_statement);
                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        order_id = resultSet.getLong(1);
                    }
                    statement.close();
                } catch (Exception e) {
                    System.err.println("Failed to query: " + e.getMessage());
                }

                if (order_id == -1) { // there is no order
                    order_id = 0;
                } else {
                    order_id += 1;
                }
                String shipping_status = "N";
                //find charge, get book price * quantity, sum up
                //shipping charge = total quantity * 10 + 10
                long charge = 0;
                long total_quantity = 0;
                for (Map.Entry<String, Long> entry : isbn_quantity.entrySet()) {
                    String key = entry.getKey();
                    Long value = entry.getValue();
                    long unit_price = 0;
                    try {
                        String sql_statement = "SELECT b.unit_price FROM book b WHERE b.isbn = ?";
                        PreparedStatement statement = conn.prepareStatement(sql_statement);
                        statement.setString(1, key);
                        ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            unit_price = resultSet.getLong(1);
                        }
                        statement.close();
                    } catch (Exception e) {
                        System.err.println("Failed to query: " + e.getMessage());
                    }
                    charge += (unit_price * value);
                    total_quantity += value;

                    //get no_of_copies
                    long no_of_copies = 0;
                    try {
                        String sql_statement = "SELECT b.no_of_copies FROM book b WHERE b.isbn = ?";
                        PreparedStatement statement = conn.prepareStatement(sql_statement);
                        statement.setString(1, key);
                        ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            no_of_copies = resultSet.getLong(1);
                        }
                        statement.close();
                    } catch (Exception e) {
                        System.err.println("Failed to query: " + e.getMessage());
                    }
                    //modify no_of_copies
                    no_of_copies -= value;
                    try {
                        String sql_statement = "UPDATE book SET no_of_copies = ? WHERE isbn = ?";
                        PreparedStatement statement = conn.prepareStatement(sql_statement);
                        statement.setLong(1, no_of_copies);
                        statement.setString(2, key);
                        statement.executeUpdate();
                        statement.close();
                    } catch (Exception e) {
                        System.err.println("Failed to query: " + e.getMessage());
                    }

                }
                //insert orders
                charge += 10;
                charge += total_quantity * 10;
                try {
                    String sql_statement = "INSERT INTO orders (order_id, o_date, shipping_status, charge, customer_id) VALUES(?, ?, ?, ?, ?)";
                    PreparedStatement statement = conn.prepareStatement(sql_statement);
                    statement.setLong(1, order_id);
                    statement.setInt(2, o_date);
                    statement.setString(3, shipping_status);
                    statement.setLong(4, charge);
                    statement.setString(5, customer_id);
                    statement.executeUpdate();
                    statement.close();
                } catch (Exception e) {
                    System.err.println("Failed to query: " + e.getMessage());
                }

                for (Map.Entry<String, Long> entry : isbn_quantity.entrySet()) {
                    String key = entry.getKey();
                    Long value = entry.getValue();
                    //insert ordering
                    try {
                        String sql_statement = "INSERT INTO ordering (order_id, isbn, quantity) VALUES(?, ?, ?)";
                        PreparedStatement statement = conn.prepareStatement(sql_statement);
                        statement.setLong(1, order_id);
                        statement.setString(2, key);
                        statement.setLong(3, value);
                        statement.executeUpdate();
                        statement.close();
                    } catch (Exception e) {
                        System.err.println("Failed to query: " + e.getMessage());
                    }
                }
                isbn_quantity.clear();
                System.out.printf("Your Order ID is %08d%n", order_id);
                System.out.println("Ordering Finished!");
                break;
            } else {
                if (choice.length() != 13) {
                    System.out.println("ISBN should have 13 digits(X-XXXX-XXXX-X).");
                    continue;
                }
                boolean test = false;
                for (int i = 0; i < choice.length(); i++) {
                    char check = choice.charAt(i);
                    if ((i != 1) && (i != 6) && (i != 11)) {
                        if (!Character.isDigit(check)) {
                            test = true;
                            System.out.println("Invalid ISBN");
                            break;
                        }
                    } else {
                        if (check != '-') {
                            test = true;
                            System.out.println("Invalid ISBN");
                            break;
                        }
                    }
                }
                if (test) {
                    continue;
                }
                choice = choice.replace("-", "");
                String isbn = choice;
                //check if book exist
                long isbn_check = 0;
                try {
                    String sql_statement = "SELECT b.isbn FROM book b WHERE b.isbn = ?";
                    PreparedStatement statement = conn.prepareStatement(sql_statement);
                    statement.setString(1, isbn);
                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        isbn_check = resultSet.getLong(1);
                    }
                    statement.close();
                } catch (Exception e) {
                    System.err.println("Failed to query: " + e.getMessage());
                }
                //if book does not exit, get input, break
                if (isbn_check == 0) {
                    System.out.println("We do not have this book. Please choose another book.");
                    continue;
                }
                //check if book is avaible
                long no_of_copies = 0;
                try {
                    String sql_statement = "SELECT b.no_of_copies FROM book b WHERE b.isbn = ?";
                    PreparedStatement statement = conn.prepareStatement(sql_statement);
                    statement.setString(1, isbn);
                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        no_of_copies = resultSet.getLong(1);
                    }
                    statement.close();
                } catch (Exception e) {
                    System.err.println("Failed to query: " + e.getMessage());
                }
                //if the book is out of stock, get another input
                if (no_of_copies == 0) {
                    System.out.println("The book is out of stock. Please choose another book.");
                } else {
                    // get quantity
                    long quantity;
                    while (true) {
                        try {
                            System.out.println("Please enter the quantity of the order: ");
                            quantity = scanner.nextLong();
                            if (no_of_copies < quantity) { //check if copies are enough
                                System.out.printf("You have already ordered %d copies.%n", quantity);
                                System.out.printf("There are only in total %d copies. %n", no_of_copies);
                                continue;
                            }
                            break;
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid Input.");
                            scanner.nextLine();
                        }
                    }
                    //gather same book
                    if (isbn_quantity.containsKey(isbn)) {
                        System.out.printf("Quantity updated to %d copies.%n", quantity);
                    }
                    //add to dict
                    isbn_quantity.put(isbn, quantity);
                }

            }
        }
    }

    void order_altering() {
        Scanner scanner = new Scanner(System.in);
        long order_id;
        System.out.println("Please enter the OrderID that you want to change: ");
        while (true) {
            String input = scanner.nextLine();
            //cancel change
            if (input.equals("E")) {
                System.out.println("Return to Customer Interface.");
                return;
            }

            if (input.length() != 8) {
                System.out.println("OrderID should have 8 digits.");
                System.out.println("Please enter the OrderID that you want to change again or press E to cancel changes: ");
                continue;
            }
            boolean test = false;
            for (int i = 0; i < input.length(); i++) {
                if (!Character.isDigit(input.charAt(i))) {
                    test = true;
                    System.out.println("Invalid OrderID.");
                    System.out.println("Please enter the OrderID that you want to change again or press E to cancel changes: ");
                    break;
                }
            }
            if (test) {
                continue;
            }

            order_id = Long.parseLong(input);
            long id_check = -1;
            String status = "";
            try {
                String sql_statement = "SELECT o.order_id, o.shipping_status FROM orders o WHERE o.order_id = ?";
                PreparedStatement statement = conn.prepareStatement(sql_statement);
                statement.setLong(1, order_id);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    id_check = resultSet.getLong(1);
                    status = resultSet.getString(2);
                }
                statement.close();
            } catch (Exception e) {
                System.err.println("Failed to query: " + e.getMessage());
            }
            if (id_check == order_id) {
                if (status.equals("Y")) {
                    System.out.println("The books in the order are shipped.");
                    System.out.println("Please enter another OrderID or press E to cancel changes: ");
                } else if (status.equals("N")) {
                    break;
                }
            } else {
                System.out.println("Wrong order ID.");
                System.out.println("Please enter the OrderID that you want to change again or press E to cancel changes: ");
            }
        }

        //find order
        String shipping_status = "";
        int charge = 0;
        String customer_id = "";
        try {
            String sql_statement = "SELECT o.order_id, o.shipping_status, o.charge, o.customer_id FROM orders o WHERE o.order_id = ?"; // not sure
            PreparedStatement statement = conn.prepareStatement(sql_statement);
            statement.setLong(1, order_id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                order_id = resultSet.getLong(1);
                shipping_status = resultSet.getString(2);
                charge = resultSet.getInt(3);
                customer_id = resultSet.getString(4);
            }
            statement.close();
        } catch (Exception e) {
            System.err.println("Failed to query: " + e.getMessage());
        }
        //find ordering
        //get list of books ordered
        Map<Integer, List<Long>> book_dict = new LinkedHashMap<>();
        List<Long> book_ordered = new ArrayList<>();
        int index = 0;
        long isbn = 0;
        long quantity;
        try {
            String sql_statement = "SELECT o.isbn, o.quantity FROM ordering o WHERE o.order_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql_statement);
            statement.setLong(1, order_id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                index += 1;
                isbn = resultSet.getLong(1);
                quantity = resultSet.getLong(2);
                book_ordered.clear();
                book_ordered.add(isbn);
                book_ordered.add(quantity);
                book_dict.put(index, book_ordered);
            }
            statement.close();
        } catch (Exception e) {
            System.err.println("Failed to query: " + e.getMessage());
        }

        System.out.printf("order_id: %08d shipping: %s charge: %d customer_id: %s%n", order_id, shipping_status, charge, customer_id);
        for (Map.Entry<Integer, List<Long>> entry : book_dict.entrySet()) {
            Integer key = entry.getKey();
            List<Long> value = entry.getValue();
            isbn = value.get(0);
            String isbn_str = isbn_long_to_str(isbn);
            quantity = value.get(1);
            System.out.printf("book no: %d ISBN = %s quantity = %d%n", key, isbn_str, quantity);
        }
        System.out.println("Which book you want to alter (input book no.):");
        int book_alter = get_user_choice(book_dict.size());
        System.out.println("input add or remove"); //number to be incremented or decremented
        String input;
        while (true) {
            input = scanner.nextLine();
            if ((input.equals("add")) || (input.equals("remove"))) {
                break;
            } else {
                System.out.println("Wrong input. Please input again.");
                System.out.println("input add or remove");
            }
        }

        System.out.println("Input the number: ");
        long quan_alter;
        long copies = 0;
        long unit_price = 0;
        long new_copies;
        long new_quantity;
        List<Long> book;
        while (true) {
            while (true) {
                try {
                    quan_alter = scanner.nextLong();
                    if (quan_alter >= 0) {
                        break;
                    } else {
                        System.out.println("Ihe number cannot be a negative number.");
                        System.out.println("Please input the number again: ");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Input is not an integer.");
                    System.out.println("Please input the number again: ");
                    scanner.next();
                }
            }
            try {
                String sql_statement = "SELECT b.no_of_copies, b.unit_price FROM book b WHERE b.isbn = ?";
                PreparedStatement statement = conn.prepareStatement(sql_statement);
                statement.setLong(1, isbn);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    copies = resultSet.getLong(1);
                    unit_price = resultSet.getLong(2);
                }
                statement.close();
            } catch (Exception e) {
                System.err.println("Failed to query: " + e.getMessage());
            }
            if (input.equals("add")) {
                // check if enough copy
                book = book_dict.get(book_alter);
                isbn = book.get(0);
                quantity = book.get(1);

                if (copies < quan_alter) {
                    //not enough copies
                    System.out.printf("There are only %d copies available%n", copies);
                    if (copies == 0) {
                        System.out.print("Order altering fails.");
                        return;
                    }
                    System.out.println("Please enter the number of copies to be added again: ");
                } else {
                    //change stock, minus copies - quan_alter
                    new_copies = copies - quan_alter;
                    new_quantity = quantity + quan_alter;
                    break;
                }
            } else {
                //check if greater than or equals to the quantity ordered
                book = book_dict.get(book_alter);
                isbn = book.get(0);
                quantity = book.get(1);
                if (quantity == 0) {
                    System.out.println("You did not order this book.");
                    System.out.println("Order altering fails.");
                    return;
                } else if (quantity < quan_alter) {
                    System.out.printf("You have only order %d copies.%n", quantity);
                    System.out.println("Please enter the number of copies to be removed again: ");
                } else {
                    //change stock, copies + quan_alter
                    new_copies = copies + quan_alter;
                    new_quantity = quantity - quan_alter;
                    break;
                }

            }
        }

        book_ordered.clear();
        book_ordered.add(isbn);
        book_ordered.add(new_quantity);
        book_dict.put(book_alter, book_ordered);

        //change stock, order, ordering, charge, date
        long new_charge = 0;
        if (quantity != 0) {
            new_charge = charge - (unit_price + 10) * quantity - 10;
        }
        new_charge = new_charge + (unit_price + 10) * new_quantity + 10;
        int o_date = system_date.value;
        if (new_quantity == 0) {
            new_charge = 0;
        }

        try {
            String sql_statement = "UPDATE orders SET o_date = ?, charge = ? WHERE order_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql_statement);
            statement.setInt(1, o_date);
            statement.setLong(2, new_charge);
            statement.setLong(3, order_id);
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            System.err.println("Failed to query: " + e.getMessage());
        }
        try {
            String sql_statement = "UPDATE ordering SET quantity = ? WHERE order_id = ?";
            PreparedStatement statement = conn.prepareStatement(sql_statement);
            statement.setLong(1, new_quantity);
            statement.setLong(2, order_id);
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            System.err.println("Failed to query: " + e.getMessage());
        }
        try {
            String sql_statement = "UPDATE book SET no_of_copies = ? WHERE isbn = ?";
            PreparedStatement statement = conn.prepareStatement(sql_statement);
            statement.setLong(1, new_copies);
            statement.setLong(2, isbn);
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
            System.err.println("Failed to query: " + e.getMessage());
        }

        System.out.println("Update is ok!");
        System.out.println("Update done!!");
        System.out.println("Updated charge");
        System.out.printf("order_id: %08d shipping: %s charge: %d customer_id: %s%n", order_id, shipping_status, new_charge, customer_id);
        for (Map.Entry<Integer, List<Long>> entry : book_dict.entrySet()) {
            Integer key = entry.getKey();
            List<Long> value = entry.getValue();
            isbn = value.get(0);
            String isbn_str = isbn_long_to_str(isbn);
            quantity = value.get(1);
            System.out.printf("book no: %d ISBN = %s quantity = %d", key, isbn_str, quantity);
        }

    }

    void order_query() {
        int sys_date = system_date.value;
        String system_date_str = date_int_to_str(sys_date);
        int system_year = Integer.parseInt(system_date_str.substring(0, 4));

        Scanner scanner = new Scanner(System.in);
        System.out.print("Please Input Customer ID: ");
        String customer_id = scanner.nextLine();
        // handle wrong customer id
        while (true) {
            String id_check = "";
            try {
                String sql_statement = "SELECT c.customer_id FROM customer c WHERE c.customer_id = ?";
                PreparedStatement statement = conn.prepareStatement(sql_statement);
                statement.setString(1, customer_id);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    id_check = resultSet.getString(1);
                }
                statement.close();
            } catch (Exception e) {
                System.err.println("Failed to query: " + e.getMessage());
            }

            if (id_check.equals(customer_id)) {
                break;
            } else {
                System.out.print("Wrong customer ID.");
                System.out.print("Please enter your customer ID again: ");
                customer_id = scanner.nextLine();
            }
        }
        System.out.print("Please Input the Year: ");
        String year_str;
        int year;
        while (true) {
            //check if it is year
            year_str = scanner.nextLine();
            try {
                if (year_str.length() != 4) {
                    System.out.println("Year should have 4 digits.");
                    System.out.println("Please Input the Year again: ");
                    continue;
                }
                year = Integer.parseInt(year_str);
                if ((year <= system_year) && (year >= 0)) {
                    break;
                } else {
                    System.out.println("Invalid year.");
                    System.out.println("Please Input the Year again: ");
                }
            } catch (Exception e) {
                System.out.println("Invalid year.");
                System.out.println("Please Input the Year again: ");
            }
        }

        //sort order_id
        int index = 0;
        try {
            String sql_statement = "SELECT ORDER_ID, O_DATE, SHIPPING_STATUS, CHARGE FROM ORDERS WHERE CUSTOMER_ID = ? ORDER BY ORDER_ID";
            PreparedStatement statement = conn.prepareStatement(sql_statement);
            statement.setString(1, customer_id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                index++;
                long order_id = resultSet.getLong(1);
                int o_date = resultSet.getInt(2);
                String shipping_status = resultSet.getString(3);
                long charge = resultSet.getLong(4);

                String date = date_int_to_str(o_date);
                int order_year = Integer.parseInt(date.substring(0, 4));
                if (order_year == year) {
                    System.out.printf("Record : %d%n", index);
                    System.out.printf("OrderID : %08d%n", order_id); //set 8 digits
                    System.out.printf("OrderDate : %s%n", date);
                    System.out.printf("charge : %d%n", charge);
                    System.out.printf("shipping status : %s\n%n", shipping_status);
                }
            }
            statement.close();
        } catch (Exception e) {
            System.err.println("Failed to query: " + e.getMessage());
        }
        System.out.println("There are no more records");
    }

    /* Must set 'public' since this method is 'public' in the superclass */
    public void loop() {
        while (true) {
            print_menu(main_menu);
            int choice = get_user_choice(5);

            if (choice == 1) book_search();
            else if (choice == 2) order_creation();
            else if (choice == 3) order_altering();
            else if (choice == 4) order_query();
            else break;
        }
    }
}
