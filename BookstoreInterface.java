import java.sql.*;
import java.util.*;

class BookstoreInterface extends Main {
    // @formatter:off
    static String[] main_menu = {
            "<This is the bookstore interface>",
            "----------------------------------",
            "1. Order update",
            "2. Order query",
            "3. N most popular book query",
            "4. Back to main menu",
    };
    // @formatter:on

    BookstoreInterface(Main parent_instance) {
        // inherit instance objects from the parent instance
        conn = parent_instance.conn;
        system_date = parent_instance.system_date;
    }

    void order_update() {
        Scanner scanner = new Scanner(System.in);
        long order_id;
        System.out.println("Please input the order ID: ");
        while (true) {
            String input = scanner.nextLine();
            //cancel update
            if (input.equals("E")) {
                System.out.println("Return to Bookstore Interface.");
                return;
            }

            if (input.length() != 8) {
                System.out.println("OrderID should have 8 digits.");
                System.out.println("Please input the order ID again or press E to cancel updates: ");
                continue;
            }
            boolean test = false;
            for (int i = 0; i < input.length(); i++) {
                if (!Character.isDigit(input.charAt(i))) {
                    test = true;
                    System.out.println("Invalid OrderID.");
                    System.out.println("Please input the order ID again or press E to cancel updates: ");
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
                    System.out.println("The books in the order are already shipped, no update is allowed.");
                    System.out.println("Please enter another OrderID or press E to cancel updates: ");
                } else if (status.equals("N")) {
                    test = true;

                    int quantity = 0;
                    try {
                        String sql_statement = "SELECT o.order_id, o.quantity FROM ordering o WHERE o.order_id = ?";
                        PreparedStatement statement = conn.prepareStatement(sql_statement);
                        statement.setLong(1, order_id);
                        ResultSet resultSet = statement.executeQuery();
                        while (resultSet.next()) {
                            order_id = resultSet.getInt(1);
                            quantity = resultSet.getInt(2);
                            if (quantity >= 1) {
                                test = false;
                                break;
                            }
                        }
                        statement.close();
                    } catch (Exception e) {
                        System.err.println("Failed to query: " + e.getMessage());
                    }
                    
                    if (test) {
                        System.out.println("The order contains no book with quantity greater than or equal to 1.");
                        System.out.println("Please enter another OrderID or press E to cancel updates: ");
                        continue;
                    }
                    break;
                }
            } else {
                System.out.println("Wrong order ID.");
                System.out.println("Please enter the OrderID that you want to update again or press E to cancel updates: ");
            }

        }

        System.out.println("Are you sure to update the shipping status? (Yes=Y)");
        while (true) {
            String input = scanner.nextLine();
            //cancel update
            if (input.equals("E")) {
                System.out.println("Return to Bookstore Interface.");
                return;
            }
            //confirm update
            if (input.equals("Y")) {
                try {
                    String sql_statement = "UPDATE orders SET shipping_status = ? WHERE order_id = ?";
                    PreparedStatement statement = conn.prepareStatement(sql_statement);
                    statement.setString(1, "Y");
                    statement.setLong(2, order_id);
                    statement.executeUpdate();
                    statement.close();
                } catch (Exception e) {
                    System.err.println("Failed to update: " + e.getMessage());
                }
                System.out.println("Updated shipping status");
                return;
            } 
            System.out.println("Please enter Y to update the shipping status or E to cancel update: ");
        }
    }

    void order_query() {
        int sys_date = system_date.value;
        String system_date_str = date_int_to_str(sys_date);
        int system_year = Integer.parseInt(system_date_str.substring(0, 4));
        int system_month = Integer.parseInt(system_date_str.substring(5, 7));

        String year_str;
        int year;
        String month_str;
        int month;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Please input the Month for Order Query (e.g.2005-09): ");
        while (true) {
            String input = scanner.nextLine();

            //cancel query
            if (input.equals("E")) {
                System.out.println("Return to Bookstore Interface.");
                return;
            }

            if (input.length() != 7) {
                System.out.println("The Month for Order Query should be in the format YYYY-MM (e.g. 2005-09).");
                System.out.println("Please input the Month again or press E to cancel query: ");
                continue;
            }
            //check input format
            boolean test = false;
            boolean month_flag = false;
            for (int i = 0; i < input.length(); i++) {
                if (i < 4) {
                    if (!Character.isDigit(input.charAt(i))) {
                        test = true;
                        break;
                    }
                } else if (i == 4) {
                    if (input.charAt(i) != '-') {
                        test = true;
                        break;
                    }
                } else if (i == 5) {
                    if (input.charAt(i) == '0') {
                        month_flag = false;
                    } else if (input.charAt(i) == '1') {
                        month_flag = true;
                    } else {
                        test = true;
                        break;
                    }
                } else {
                    if (month_flag == false) {
                        if (!(input.charAt(i) >= '1' && input.charAt(i) <= '9')) {
                            test = true;
                            break;
                        }
                    } else {
                        if (!(input.charAt(i) >= '0' && input.charAt(i) <= '2')) {
                            test = true;
                            break;
                        }
                    }
                }
            }
            if (test) {
                System.out.println("The Month for Order Query should be in the format YYYY-MM (e.g. 2005-09).");
                System.out.println("Please input the Month again or press E to cancel query: ");
                continue;
            }

            //check if valid month
            year_str = input.substring(0, 4);
            month_str = input.substring(5, 7);
            year = Integer.parseInt(year_str);
            month = Integer.parseInt(month_str);
            if ((year >= system_year) && (month > system_month)) {
                System.out.println("Invalid month after system date.");
                System.out.println("Please input the Month in the format YYYY-MM (e.g. 2005-09) again or press E to cancel query: ");
                continue;
            }
            break;
        }
        
        //sort order_id
        int index = 0;
        int total_charge = 0;
        try {
            String sql_statement = "SELECT ORDER_ID, CUSTOMER_ID, O_DATE, CHARGE, SHIPPING_STATUS FROM ORDERS WHERE SHIPPING_STATUS = 'Y' ORDER BY ORDER_ID";
            PreparedStatement statement = conn.prepareStatement(sql_statement);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                index++;
                long order_id = resultSet.getLong(1);
                String customer_id = resultSet.getString(2);
                int o_date = resultSet.getInt(3);
                long charge = resultSet.getLong(4);

                String date = date_int_to_str(o_date);
                int order_year = Integer.parseInt(date.substring(0, 4));
                int order_month = Integer.parseInt(date.substring(5, 7));

                if ((order_year == year) && (order_month == month)) {
                    total_charge += charge;
                    System.out.printf("Record : %d%n", index);
                    System.out.printf("order_id : %08d%n", order_id); //set 8 digits
                    System.out.printf("customer_id : %s%n", customer_id);
                    System.out.printf("date : %s%n", date);
                    System.out.printf("charge : %d\n%n", charge);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to query: " + e.getMessage());
        }
        System.out.printf("Total charge of the month is: %d%n", total_charge);
    }

    void popular_book_query() {
        long n;
        String input;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please input the N popular books number: ");
        while (true) {
            input = scanner.nextLine();
            //cancel query
            if (input.equals("E")) {
                System.out.println("Return to Bookstore Interface.");
                return;
            }
            //check format
            boolean test = false;
            for (int i = 0; i < input.length(); i++) {
                if (!Character.isDigit(input.charAt(i))) {
                    test = true;
                    System.out.println("Invalid integer value.");
                    System.out.println("Please input the N popular books number or press E to cancel changes: ");
                    break;
                }
            }
            if (test) {
                continue;
            }
            break;
        }

        n = Long.parseLong(input);
        int index = 0;
        long min_quantity = Long.MAX_VALUE;
        try {
            String sql_statement = "SELECT bo.ISBN, b.TITLE, bo.SUM_QUANTITY FROM (SELECT ISBN, SUM(QUANTITY) as SUM_QUANTITY FROM ordering GROUP BY ISBN) bo LEFT JOIN book b on bo.ISBN = b.ISBN ORDER BY bo.SUM_QUANTITY DESC, b.TITLE ASC, bo.ISBN ASC";
            PreparedStatement statement = conn.prepareStatement(sql_statement);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (index == 0) {
                    System.out.println("ISBN            Title             copies");
                }
                index++;
                long isbn = resultSet.getLong(1);
                String title = resultSet.getString(2);
                long sum_quantity = resultSet.getLong(3);
                
                String isbn_str = isbn_long_to_str(isbn);

                if ((index <= n) || (sum_quantity >= min_quantity)){
                    System.out.printf("%s   %s   %d%n", isbn_str, title, sum_quantity);
                    min_quantity = sum_quantity;
                }
            }
            statement.close();
        } catch (Exception e) {
            System.err.println("Failed to query: " + e.getMessage());
        }
    }

    /* Must set 'public' since this method is 'public' in the superclass */
    public void loop() {
        while (true) {
            print_menu(main_menu);
            int choice = get_user_choice(4);

            if (choice == 1) {
                order_update();
            } else if (choice == 2) {
                order_query();
            } else if (choice == 3) {
                popular_book_query();
            } else break;
        }
    }
}
