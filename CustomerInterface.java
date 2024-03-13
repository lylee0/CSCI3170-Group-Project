//package Project.codes;
import java.util.Scanner;

public class CustomerInterface{
    private Scanner scanner;

    public void start(){
        printOptions();
        scanner = new Scanner(System.in);
        Integer input = getInput(scanner);
        run(input, scanner);
    }

    public void printOptions(){
        System.out.println("<This is the customer interface.>");
        System.out.println("---------------------------------");
        System.out.println("1. Book Search.");
        System.out.println("2. Order Creation.");
        System.out.println("3. Order Altering.");
        System.out.println("4. Order Query.");
        System.out.println("5. Back to main menu.");
    }

    public getInput(Scanner scanner){
        System.out.println("\nWhat is your choice??..");
        Integer input = Integer.parseInt(scanner.next());
        return input;
    }

    public void run(Integer input, Scanner scanner){
        if (input == 1) {
            bookSearch();
        } else if (input == 2) {
            orderCreation();
        } else if (input == 3) {
            orderAltering();
        } else if (input == 4) {
            orderQuery();
        } else if (input == 5){
            Main.main(new String[0]);  // change to the name of main function
        }
        else {
            System.out.println("Invalid input, please try again.");
            input = getInput(scanner);
            run(input, scanner);
        }
    }

    private void bookSearch(){
        System.out.println("What do u want to search??");
        System.out.println("1 ISBN");
        System.out.println("2 Book Title");
        System.out.println("3 Author Name");
        System.out.println("4 Exit");
        //loop
        start();
        // to be done
    }

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
