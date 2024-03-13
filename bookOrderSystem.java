//package Project.codes;
import java.util.Scanner;

public class Main{
    public static void main(String[] args){
        System.out.println("The System Date is now: ");
        System.out.println("<This is the Book Ordering System.>");
        System.out.println("-----------------------------------");
        System.out.println("1. System interface.");
        System.out.println("2. Customer interface.");
        System.out.println("3. Bookstore interface.");
        System.out.println("4. Show System Date.");
        System.out.println("5. Quit the system......");
        scanner = new Scanner(System.in);
        Integer input = getInput(scanner);
        run(input, scanner);
    }

    public getInput(Scanner scanner){
        System.out.println("\nPlease enter your choice??..");
        Integer input = Integer.parseInt(scanner.next());
        return input;
    }

    public void run(Integer input, Scanner scanner){
        if (input == 1) {
            SystemInterface sys = new SystemInterface();
        } else if (input == 2) {
            CustomerInterface cus = new CustomerInterface();
        } else if (input == 3) {
            BookstoreInterface bks = new BookstoreInterface();
        } else if (input == 4) {
            systemDate();
        } else if (input == 5){
            scanner.close();
            System.exit(1);
        }
        else {
            System.out.println("Invalid input, please try again.");
            input = getInput(scanner);
            run(input, scanner);
        }
    }

}
