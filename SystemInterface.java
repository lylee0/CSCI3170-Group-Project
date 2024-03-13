//package Project.codes;
import java.util.Scanner;

public class SystemInterface{
    private Scanner scanner;

    public void printOptions(){
        System.out.println("<This is the system interface.>");
        System.out.println("-------------------------------");
        System.out.println("1. Create Table.");
        System.out.println("2. Delete Table.");
        System.out.println("3. Insert Data.");
        System.out.println("4. Set System Date.");
        System.out.println("5. Back to main menu.");
        System.out.println("Please enter your choice??..");
    }
}