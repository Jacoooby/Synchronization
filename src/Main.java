import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // switch statement to manually determine what task to run depending on what the user types.
        switch (args[1]) {
            case "1":
                System.out.println("Starting Task 1: Dining Philosophers");
                startDiningPhilosophers();
                break;

            case "2":
                System.out.println("Starting Task 2: Readers-Writers Problem");
                break;

            default:
                System.out.println("Invalid argument to start a task. Try again with either -A 1 or -A 2");
        }
    }

    // if the user types argument -A 1, this method will be called. Then it will start the dining philosophers problem.
    public static void startDiningPhilosophers() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of philosophers P: ");
        int P = scanner.nextInt();

        System.out.print("Enter the number of total meals to be eaten M: ");
        int M = scanner.nextInt();

        // Semaphore to track when all philosophers have arrived
        Semaphore presentCounter = new Semaphore(0);
        // Semaphore to track when philosophers have sat down
        Semaphore seatedCounter = new Semaphore(0);

        // create array of semaphores for chopsticks
        Semaphore[] chopsticks = new Semaphore[P];
        for (int i = 0; i < P; i++) {
            // each index is a chopstick so its semaphore value is initialized to 1
            chopsticks[i] = new Semaphore(1);
        }


        // array to store threads for philosophers
        Thread[] threads = new Thread[P];
        // fork a single thread for each philosopher
        for (int i = 0; i < P; i++) {
            threads[i] = new Thread(new DiningPhilosophers(i, M, presentCounter, seatedCounter, P, chopsticks));
            threads[i].start();
        }
    }



    //
}