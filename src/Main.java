import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        System.out.println("=== MyPhotoStudio ===");
        System.out.println("1. Free Version");
        System.out.println("2. Premium Demo");

        System.out.print("Choose your version: ");

        int choice = input.nextInt();

        if (choice == 1) {
            System.out.println("Free editing is available!");
        }
        else if (choice == 2) {
            System.out.println("Welcome to Premium Demo!");
            System.out.println("Premium filters are unlocked for this demo.");
        }
        else {
            System.out.println("Invalid selection.");
        }

        input.close();
    }
}
