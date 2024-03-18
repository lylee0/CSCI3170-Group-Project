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
        // ...
    }

    void order_query() {
        // ...
    }

    void popular_book_query() {
        // ...
    }

    /* Must set 'public' since this method is 'public' in the superclass */
    public void loop() {
        while (true) {
            print_menu(main_menu);
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

/*
public class BookstoreInterface {
    private void orderUpdate() {
        System.out.print("Please input the order ID: ");
        //get input
        //print
        System.out.println("Are you sure to update the shipping status? (Yes=Y)");
        //get input
        System.out.println("Updated shipping status");
        // to be done
        start();
    }

    private void orderQuery() {
        System.out.print("Please input the month for order query (e.g. 2005-09): ");
        //get input
        //print
        System.out.print("Total charge of the month is: "); //print charge
        // to be done
        start();
    }

    private void popular_book_query() {
        System.out.print("Please input the N popular books number: ");
        //get input
        //print
        // to be done
        start();
    }
}
 */
