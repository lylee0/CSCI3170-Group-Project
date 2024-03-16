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
//package Project.codes;
import java.util.Scanner;

public class BookstoreInterface {
    private Scanner scanner;

    public void start() {
        printOptions();
        scanner = new Scanner(System.in);
        Integer input = getInput(scanner);
        run(input, scanner);
    }

    public void printOptions() {
        System.out.println("<This is the bookstore interface.>");
        System.out.println("----------------------------------");
        System.out.println("1. Order Update.");
        System.out.println("2. Order Query.");
        System.out.println("3. N most Popular Book Query.");
        System.out.println("4. Back to main menu.");
    }

    public getInput(Scanner scanner) {
        System.out.println("\nWhat is your choice??..");
        Integer input = Integer.parseInt(scanner.next());
        return input;
    }

    public void run(Integer input, Scanner scanner) {
        if (input == 1) {
            orderUpdate();
        } else if (input == 2) {
            orderQuery();
        } else if (input == 3) {
            popularBook();
        } else if (input == 4) {
            Main.main(new String[0]);  // change to the name of main function
        } else {
            System.out.println("Invalid input, please try again.");
            input = getInput(scanner);
            run(input, scanner);
        }
    }

    private void orderUpdate() {
        System.out.println("Please input the order ID: ");
        //get input
        //print
        System.out.println("Are you sure to update the shipping status? (Yes=Y) ");
        //get input
        System.out.println("Updated shipping status");
        // to be done
        start();
    }

    private void orderQuery() {
        System.out.println("Please input the Month for Order Query (e.g.2005-09): ");
        //get input
        //print
        System.out.println("Total charge of the month is : "); //print charge
        // to be done
        start();
    }

    private void popularBook() {
        System.out.println("Please input the N popular books number: ");
        //get input
        //print
        // to be done
        start();
    }
}
 */