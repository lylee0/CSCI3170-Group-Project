//package Project.codes;
import java.util.Scanner;

public class SystemInterface{
    private Scanner scanner;

    public void start(){
        printOptions();
        scanner = new Scanner(System.in);
        Integer input = getInput(scanner);
        run(input, scanner);
    }

    public void printOptions(){
        System.out.println("<This is the system interface.>");
        System.out.println("-------------------------------");
        System.out.println("1. Create Table.");
        System.out.println("2. Delete Table.");
        System.out.println("3. Insert Data.");
        System.out.println("4. Set System Date.");
        System.out.println("5. Back to main menu.");
    }

    public getInput(Scanner scanner){
        System.out.println("\nWhat is your choice??..");
        Integer input = Integer.parseInt(scanner.next());
        return input;
    }

    public void run(Integer input, Scanner scanner){
        if (input == 1) {
            createTable();
        } else if (input == 2) {
            deleteTable();
        } else if (input == 3) {
            insertData();
        } else if (input == 4) {
            setDate();
        } else if (input == 5){
            Main.main(new String[0]);  // change to the name of main function
        }
        else {
            System.out.println("Invalid input, please try again.");
            input = getInput(scanner);
            run(input, scanner);
        }
    }

    private void createTable(){
        // to be done
        start();
    }

    private void deleteTable(){
        // to be done
        start();
    }

    private void insertData(){
        // to be done
        start();
    }

    private void setDate(){
        // to be done
        start();
    }
}
