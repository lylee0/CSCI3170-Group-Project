import java.util.Scanner;

public class bookOrderSystem {
    private Scanner scanner;

    public void initInfo() {
        System.out.println("The System Date is now: ");
        System.out.println("<This is the Book Ordering System.>");
        System.out.println("-----------------------------------------");
        System.out.println("1. System interface.");
        System.out.println("2. Customer interface.");
        System.out.println("3. Bookstore interface.");
        System.out.println("4. Show System Date.");
        System.out.println("5. Quit the system......");
    }

    public void start() {
        System.out.println("Please enter enter your choice??..");
        scanner = new Scanner(System.in);
        Integer input = Integer.parseInt(scanner.next());
        runMethod(input);
    }

    private void runMethod(Integer input) {
        if (input == 1) {
            systemInterface();
        } else if (input == 2) {
            customerInterface();
        } else if (input == 3) {
            bookstoreInterface();
        } else if (input == 4) {
            systemDate()
        } else if (input == 5){
            goBack();
        }
        else {
            System.out.println("[ERROR] Invalid input");
            start();
        }
    }    
}    