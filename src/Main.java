import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean validInput = false;
        String task;

        if (args.length < 2) {
            System.out.println("To start a task. Enter either -A 1 or -A 2");
            String letter = scanner.next();
            task = scanner.next();

        } else {
            task = args[1];
        }

        // switch statement to manually determine what task to run depending on what the user types.
        while (!validInput) {
            switch (task) {
                case "1":
                    System.out.println("Starting Task 1: Dining Philosophers");
                    startDiningPhilosophers();
                    validInput = true;
                    break;

                case "2":
                    System.out.println("Starting Task 2: Readers-Writers Problem");
                    startReaderWriters();
                    validInput = true;
                    break;

                default:
                    System.out.println("Invalid argument to start a task. Try again with either -A 1 or -A 2");
                    String letter = scanner.next();
                    task = scanner.next();
            }
        }
    }



    // if the user types argument -A 1, this method will be called to start the dining philosophers problem.
    public static void startDiningPhilosophers() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of philosophers P: ");
        int P = scanner.nextInt();
        while (P < 2) {
            System.out.print("The minimum number of philosophers is 2. Please enter a valid number, P: ");
            P = scanner.nextInt();
        }

        System.out.print("Enter the number of total meals to be eaten M: ");
        int M = scanner.nextInt();
        while (M < 1) {
            System.out.print("The minimum number of meals is 1. Please enter a valid number, M: ");
            M = scanner.nextInt();
        }


        // mutex semaphore to help track when all philosophers have arrived
        Semaphore arrive = new Semaphore(1);
        // semaphore to track when the last philosopher arrives so everyone can sit
        Semaphore seatedCounter = new Semaphore(0);
        // arrival counter
        int[] arrived = {0};


        // mutex semaphore to protect meals left counter
        Semaphore mealMutex = new Semaphore(1);
        // meals left counter
        int[] mealsLeft = {M};


        // mutex semaphore to track exitCount
        Semaphore exitMutex = new Semaphore(1);
        // semaphore that opens once last philosopher is ready to leave
        Semaphore exitBarrier = new Semaphore(0);
        // count of how many philosophers are ready to leave
        int[] exitCount = {0};

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
            threads[i] = new Thread(new DiningPhilosophers(i, arrive, seatedCounter, arrived, mealMutex, mealsLeft, exitMutex, exitBarrier, exitCount, chopsticks, P));
            threads[i].start();
        }

        // wait for all philosopher threads
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }



    // if the user types argument -A 2, this method will be called to start the readers-writers problem.
    //....
    public static void startReaderWriters() {
        // stuff

    }
}