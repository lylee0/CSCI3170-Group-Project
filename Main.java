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

    void print_menu() {
        System.out.println("The System Date is now: ");
        System.out.println("<This is the Book Ordering System.>");
        System.out.println("-----------------------------------");
        System.out.println("1. System interface");
        System.out.println("2. Customer interface");
        System.out.println("3. Bookstore interface");
        System.out.println("4. Show System Date");
        System.out.println("5. Quit the system......");
        System.out.print("\nPlease enter enter your choice??..");
    }

    public void loop() {
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
