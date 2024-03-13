import java.util.*;

public class Main {
    class SystemInterface {
        void print_menu() {

        }

        void loop() {
            // ...
        }
    }

    class CustomerInterface {
        void print_menu() {

        }

        void loop() {
            // ...
        }
    }

    class BookstoreInterface {
        void print_menu() {

        }

        void loop() {
            // ...
        }
    }

    static int ask_for_options(int n_options) {
        // construct the list of options
        List<String> options = new ArrayList<>();
        for (int i = 1; i <= n_options; i++) {
            options.add(Integer.toString(i));
        }

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while (!options.contains(input)) {
            System.out.print("Invalid input. Please try again: ");
            input = scanner.nextLine();
        }

        return Integer.parseInt(input);
    }

    void print_menu() {
        System.out.println("The System Date is now: ");
        System.out.println("<This is the Book Ordering System.>");
        System.out.println("--------------------------------------------");
        System.out.println("1. System interface.");
        System.out.println("2. Customer interface.");
        System.out.println("3. Bookstore interface.");
        System.out.println("4. Show System Date.");
        System.out.println("5. Quit the system......");
        System.out.print("\nPlease enter enter your choice??..");
    }

    public void loop() {
        while (true) {
            print_menu();
            int option = ask_for_options(5);

            if (option == 1) {
                new SystemInterface().loop();
            } else if (option == 2) {
                new CustomerInterface().loop();
            } else if (option == 3) {
                new BookstoreInterface().loop();
            } else if (option == 4) {
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
