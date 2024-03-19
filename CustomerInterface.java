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
    static String[] order_creation_menu = {
        ">> Input ISBN and then the quantity.",
        ">> You can press 'L' to see ordered list, or 'F' to finish ordering."
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

    void order_creation() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter your customer ID: ");
        String customer_id = scanner.nextLine();
        // handle wrong customer id
        while (customer_id){
            String sql_statement = String.format("SELECT c.customer_id FROM customer c WHERE c.customer_id = %s", customer_id);
            ExecuteQuery query = new ExecuteQuery(sql_statement);
            while (query.rs.next()){
                String id_check = query.rs.getString(1);
            }
            if (id_check == customer_id){
                break;
            }
            else{
                System.out.print("Wrong customer ID.");
                System.out.print("Please enter your customer ID again: ");
                customer_id = scanner.nextLine();
            }
        }
 
        Map<String, Integer> isbn_quantity = new LinkedHashMap<>();
        //Dictionary<String, Integer> isbn_quantity= new Hashtable<>();

        print_menu(order_creation_menu);
        System.out.println("Please enter the book's ISBN:");
        String choice = scanner.nextLine();

        // create list of isbn and quantity
        while (choice){
            if (choice == "L"){
                // print isbn and quantity
                System.out.println("ISBN              Number:");
                //print dict line by line
                for (Map.Entry<String, Integer> entry : isbn_quantity.entrySet()) {
                    String key = entry.getKey();
                    Integer value = entry.getValue();
                    System.out.println(key + "   " + value);
                }
            }
            else if (choice == "F"){ 
                //if no order, break 
                if (isbn_quantity.isEmpty()){
                    System.out.println("You did not order any book.");
                    System.out.println("Ordering fails");
                    break;
                }
                Integer check = 0;
                for (Map.Entry<String, Integer> entry : isbn_quantity.entrySet()) {
                    Integer value = entry.getValue();
                    if (value != 0){
                        check = 1;
                    }
                }
                if (check == 0){
                    System.out.println("You did not order any book.");
                    System.out.println("Ordering fails");
                    break;
                }
                //o_date == system date
                Integer o_date = system_date.value;
                //find order id, greatest id + 1
                sql_statement = "SELECT MAX(order_id) FROM orders";
                query = new ExecuteQuery(sql_statement);
                while (query.rs.next()){
                    long order_id = query.rs.getLong(1);
                }
                if (order_id == null){
                    order_id = 0; // need to change to string??? or formatting
                }
                else{
                    order_id += 1;
                }
                //shipping status == "N"
                String shipping_status = "N";
                //find charge, get book price * quantity, sum up
                //shipping charge = total quantity * 10 + 10
                long charge = 0;
                long total_quantity = 0;
                for (Map.Entry<String, Integer> entry : isbn_quantity.entrySet()) {
                    String key = entry.getKey();
                    Integer value = entry.getValue();
                    sql_statement = String.format("SELECT b.unit_price FROM book b WHERE b.isbn = %s", key);
                    query = new ExecuteQuery(sql_statement);
                    while (query.rs.next()){
                        long unit_price = query.rs.getLong(1);
                    }
                    charge += (unit_price * value);
                    total_quantity += value;

                    //get no_of_copies
                    sql_statement = String.format("SELECT b.no_of_copies FROM book b WHERE b.isbn = %s", isbn);
                    query = new ExecuteQuery(sql_statement);
                    while (query.rs.next()){
                        long no_of_copies = query.rs.getLong(1);
                    }
                    //modify no_of_copies
                    no_of_copies += value;   
                    sql_statement = String.format("UPDATE book SET no_of_copies = %d WHERE isbn = %s", no_of_copies, key);
                    query = new ExecuteQuery(sql_statement);

                    //insert ordering
                    sql_statement = String.format("INSERT INTO ordering (order_id, isbn, quantity) VALUES(%d, %s, %d)", order_id, isbn, quantity);
                    query = new ExecuteQuery(sql_statement);
                }
                charge += 10;
                charge += total_quantity * 10;
                //insert
                sql_statement = String.format("INSERT INTO orders (order_id, o_date, shipping_status, charge, customer_id) VALUES(%d, %d, %s, %d, %s)", order_id, o_date, shipping_status, charge, customer_id);
                query = new ExecuteQuery(sql_statement);

                System.out.println("Ordering Finished!");
                break;
            }
            else{
                choice = choice.replace("-", "");
                String isbn = choice;
                //check if book exist
                String sql_statement = String.format("SELECT b.isbn FROM book b WHERE b.isbn = '%s'", isbn);
                ExecuteQuery query = new ExecuteQuery(sql_statement);
                while (query.rs.next()){
                    long isbn_check = query.rs.getLong(1);
                }
                //if book does not exit, get input, break
                if (isbn_check == null){
                    System.out.println("We do not have this book. Please choose another book.");
                    System.out.println("Please enter the book's ISBN:");
                    choice = scanner.nextLine();
                    break;
                }
                //check if book is avaible
                sql_statement = String.format("SELECT b.no_of_copies FROM book b WHERE b.isbn = '%s'", isbn);
                // @formatter:on
                query = new ExecuteQuery(sql_statement);
                while (query.rs.next()){
                    long no_of_copies = query.rs.getLong(1);
                }
                //if the book is out of stock, get another input
                if (no_of_copies == 0) {
                    System.out.println("The book is out of stock. Please choose another book.");
                    System.out.println("Please enter the book's ISBN:");
                    choice = scanner.nextLine();
                    break;
                }
                else{
                    // get quantity
                    System.out.println("Please enter the quantity of the order: ");
                    String quantity = scanner.nextLine();

                    //gather same book
                    if (isbn_quantity.containsKey(isbn)){
                        long num_order = isbn_quantity.get(isbn);
                    }
                    else{
                        long num_order = 0;
                    }
                    while(no_of_copies < (quantity + num_order)){ //check if copies are enough
                        System.out.println(String.format("You have already ordered %d copies.", num_order));
                        System.out.println(String.format("There are only in total %d copies. ", no_of_copies));
                        System.out.println("Please enter the quantity of the order again: ");
                        quantity = scanner.nextLine();
                    }
                    quantity += num_order;
                    //add to dict
                    isbn_quantity.put(isbn, quantity);
                }

            }
            System.out.println("Please enter the book's ISBN:");
            choice = scanner.nextLine();
        }
        scanner.close();
        loop();
    }

    void order_altering() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the OrderID that you want to change: ");
        String input = scanner.nextLine();
        //long order_id = Long.parseLong(input);
        while (input){
            //cancel change
            if (input == "E"){
                loop();
            }
            long order_id = Long.parseLong(input);
            String sql_statement = String.format("SELECT o.order_id o.shipping_status FROM orders o WHERE o.order_id = %d", order_id); // not sure
            ExecuteQuery query = new ExecuteQuery(sql_statement);
            while (query.rs.next()){
                String id_check = query.rs.getString(1);
                String status = query.rs.getString(2);
            }
            if (id_check == order_id){
                if (status == "Y"){
                    System.out.println("The books in the order are shipped”");
                    System.out.println("Please enter another OrderID or press E to cancel changes: ");
                    input = scanner.nextLine();
                }
                else if (status == "N"){
                    break;
                }
            }
            else{
                System.out.print("Wrong order ID.");
                System.out.print("Please enter the OrderID that you want to change again or press E to cancel changes: ");
                input = scanner.nextLine();
            }
        }

        String sql_statement = String.format("SELECT o.order_id, o.shipping_status, o.charge, o.customer_id FROM orders o WHERE o.order_id = %d", order_id); // not sure
        ExecuteQuery query = new ExecuteQuery(sql_statement);
        //find order
        while (query.rs.next()) {
            long order_id = query.rs.getLong(1);
            String shipping_status = query.rs.getString(2);
            int charge = query.rs.getInt(3);
            String customer_id = query.rs.getString(4);
        }
        //find ordering
        //get list of books ordered
        // ...
        Map<Integer, List<Long>> book_dict = new LinkedHashMap<>();
        List<Long> book_ordered = new ArrayList<>();
        //List<List<Long>> book_ordered = new ArrayList<>();
        sql_statement = String.format("SELECT o.isbn, o.quantity FROM ordering o WHERE o.order_id = %d", order_id);
        query = new ExecuteQuery(sql_statement);
        int index = 0;
        while (query.rs.next()){
            index += 1;
            long isbn = query.rs.getLong(1);
            long quantity = query.rs.getLong(2);
            book_ordered.clear();
            book_ordered.add(isbn);
            book_ordered.add(quantity);
            book_dict.put(index, book_ordered);
        }

        System.out.print(String.format("order_id: %s shipping: %s charge: %d customer_id: %s", order_id, shipping_status, charge, customer_id));
        for (Map.Entry<Integer, List> entry : book_dict.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            isbn = value.get(1);
            String isbn_str = isbn_long_to_str(isbn);
            quantity = value.get(2);
            System.out.print(String.format("book no: %d ISBN = %s quantity = %d", key, isbn_str, quantity));
        }
        System.out.println("Which book you want to alter (input book no.):\n");
        int book_alter = get_user_choice(book_dict.size());
        //int book_alter = scanner.nextLine();
        
        //while(book_alter){
            //check if in dict, enter again or cancel
        //}

        System.out.println("input add or remove"); //number to be incremented or decremented
        input = scanner.nextLine();
        while(input){
            if ((input == "add") || (input == "remove")){
                break;
            }
            else{
                System.out.println("Wrong input. Please input again.");
                System.out.println("input add or remove");
                input = scanner.nextLine();
            }
        }

        System.out.println("Input the number: ");
        long quan_alter = scanner.nextLine(); 
        while (quan_alter){
            while (quan_alter){
                //check if integer
                try {
                    long number = Long.parselong(quan_alter);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Input is not an integer.");
                    System.out.println("Please input the number again: ");
                }
                quan_alter = scanner.nextLine();
            }

            sql_statement = String.format("SELECT b.no_of_copies, b.unit_price FROM book b WHERE b.isbn = %d", isbn);
            query = new ExecuteQuery(sql_statement);
            while(query.rs.next()){
                long copies = query.rs.getLong(1);
                long unit_price = query.rs.getLong(2);
            }

            if (input == "add"){
                // check if enough copy
                List book = book_dict.get(book_alter);
                long isbn = book.get(1);
                long quantity = book.get(2);
                //isbn_str = isbn_long_to_str(isbn);

                if (copies < quan_alter){
                    //not enough copies
                    System.out.println(String.format("There are only %d copies available", copies));
                    System.out.println("Please enter the number of copies to be added again: ");
                    quan_alter = scanner.nextLine();
                }
                else{
                    //change stock, minus copies - quan_alter
                    Long new_copies = copies - quan_alter;
                    Long new_quantity = quantity + quan_alter; 
                    break;
                }
            }
            else if (input == "remove"){
                //check if greater than or equals to the quantity ordered
                List book = book_dict.get(book_alter);
                long isbn = book.get(1);
                long quantity = book.get(2);
                if (quantity < quan_alter){
                    System.out.println(String.format("You have only order %d of copies.", quantity));
                    System.out.println("Please enter the number of copies to be removed again: ");
                    quan_alter = scanner.nextLine();
                }
                else{
                    //change stock, copies + quan_alter
                    Long new_copies = copies + quan_alter;
                    Long new_quantity = quantity - quan_alter; 
                    break;
                }

            }
        }

        book_ordered.clear();
        book_ordered.add(isbn);
        book_ordered.add(new_quantity);
        book_dict.put(book, book_ordered);

        //change stock, order, ordering, charge, date
        Long new_charge = charge - (unit_price + 10) * quantity - 10;
        new_charge = new_charge + (unit_price + 10) * new_quantity + 10;
        Integer o_date = system_date.value;
        //order: order_id, o_date, shipping_status, charge, customer_id
        sql_statement = String.format("UPDATE order SET o_date = %d, charge = %d WHERE order_id = %d", o_date, new_charge, order_id);
        query = new ExecuteQuery(sql_statement);
        //ordering: order_id, isbn, quantity
        sql_statement = String.format("UPDATE ordering SET quantity = %d WHERE order_id = %d", new_quantity, order_id);
        query = new ExecuteQuery(sql_statement);
        //book: isbn, title, unit_price, no_of_copies
        sql_statement = String.format("UPDATE book SET no_of_copies = %d WHERE isbn = %d", new_copies, isbn);
        query = new ExecuteQuery(sql_statement);

        System.out.println("Update is ok!");
        System.out.println("Update done!!");
        System.out.println("Updated charge");
        System.out.print(String.format("order_id: %s shipping: %s charge: %d customer_id: %s", order_id, shipping_status, new_charge, customer_id));
        for (Map.Entry<Integer, List> entry : book_dict.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            isbn = value.get(1);
            String isbn_str = isbn_long_to_str(isbn);
            quantity = value.get(2);
            System.out.print(String.format("book no: %d ISBN = %s quantity = %d", key, isbn_str, quantity));
        }

        scanner.close();
        loop();
    }

    void order_query() {
        Integer system_date = system_date.value;
        String system_date_str = date_int_to_str(o_date);
        int system_year = Integer.parseInt(date.substring(0, 4));

        Scanner scanner = new Scanner(System.in);
        System.out.print("Please Input Customer ID: ");
        String customer_id = scanner.nextLine();
        //may define a function of checking customer id
        // handle wrong customer id
        while (customer_id){
            String sql_statement = String.format("SELECT c.customer_id FROM customer c WHERE c.customer_id = %s", customer_id);
            ExecuteQuery query = new ExecuteQuery(sql_statement);
            while (query.rs.next()){
                String id_check = query.rs.getString(1);
            }
            if (id_check == customer_id){
                break;
            }
            else{
                System.out.print("Wrong customer ID.");
                System.out.print("Please enter your customer ID again: ");
                customer_id = scanner.nextLine();
            }
        }
        System.out.print("Please Input the Year: ");
        int year = scanner.nextLine();
        while(year){
            //check if it is year
            try {
                int number = Integer.parseInt(year);
                if ((year <= system_year) && (year >= 1000)){
                    break;
                }
                else{
                    System.out.println("Invalid year.");
                    System.out.println("Please Input the Year again: ");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid year.");
                System.out.println("Please Input the Year again: ");
            }
            year = scanner.nextLine();
        }
        //order: order_id, o_date, shipping_status, charge, customer_id

        //sort order_id
        String sql_statement = String.format("SELECT order_id, o_date, shipping status, charge FROM order WHERE customer_id = %s ORDER BY order_id ASC", customer_id);
        ExecuteQuery query = new ExecuteQuery(sql_statement);
        int index = 0;
        while (query.rs.next()){
            index++;
            long order_id = query.rs.getLong(1);
            int o_date = query.rs.getInt(2);
            String shipping_status = query.rs.getString(3);
            long charge = query.rs.getLong(4);

            String date = date_int_to_str(o_date);
            int order_year = Integer.parseInt(date.substring(0, 4));
            if (order_year == year){
                System.out.println(String.format("Record : %d", index));
                System.out.println(String.format("OrderID : %08d", order_id)); //set 8 digits
                System.out.println(String.format("OrderDate : %s", date));
                System.out.println(String.format("charge : %d", charge));
                System.out.println(String.format("shipping status : %s\n", shipping_status));
            }
        }
        System.out.println("There are no more records");
        scanner.close();
        loop();
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
