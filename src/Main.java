import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Main {
    static int R, W, N; // [Task 2] fields for amount of Readers, Writers, and Readers allowed access to the file
    static int Wait_Input = 0; // [Task 2] used to stop the threads from continuing until all have been created
    static boolean readerInFile = true; // [Task 2] used to see if the Reader or Writer has access to the file

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String task;

        if (args.length != 2) {
            System.out.println("To start a task. Enter either -A 1 or -A 2");
            return;
        }

        // check args at index 0 to see if they started with -A
        if (!args[0].equals("-A")) {
            System.out.println("Invalid argument to start a task. Try again with either -A 1 or -A 2");
            return;
        }

        task = args[1];

        // check args at index 1 to see if user typed 1 or 2
        if (!task.equals("1") && !task.equals("2")) {
            System.out.println("Invalid argument to start a task. Try again with either -A 1 or -A 2");
            return;
        }


        // switch statement to manually determine what task to run depending on what the user types.
        switch (task) {
            case "1":
                System.out.println("Starting Task 1: Dining Philosophers");
                startDiningPhilosophers();
                break;

            case "2":
                System.out.println("Starting Task 2: Readers-Writers Problem");
                startReaderWriters();
                break;
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
    public static void startReaderWriters()
    {
        {
            // Introduction of this Task
            System.out.println("========================================\n========================================");
            System.out.println("\t\t\t\tTASK 2");
            System.out.println("========================================\n======================================== \n\n\n\n");

            // Creates an object to obtain user input
            Scanner input = new Scanner(System.in);

            // asking user for input
            boolean passing = false;
            while (!passing)
            {
                System.out.print("How many Readers do you want (R):\t");
                try { R = input.nextInt(); passing = true; }
                catch (InputMismatchException e)
                { System.out.println("\tPlease insert an Integer -->"); input.nextLine(); continue; }
                catch (OutOfMemoryError e)
                { System.out.println("\tPlease insert a smaller Integer -->"); input.nextLine(); continue; }
                catch (Exception e)
                { System.out.println("\tPlease be kind to us, also try again -->"); input.nextLine(); continue; }
                if (R <= 0) { System.out.println("\tInput too low (must be greater than 0) -->"); input.nextLine(); passing = false; }
            }
            System.out.println(); // line break

            passing = false;
            while (!passing)
            {
                System.out.print("How many Readers have access (N):\t");
                try { N = input.nextInt(); passing = true; }
                catch (InputMismatchException e)
                { System.out.println("\tPlease insert an Integer -->"); input.nextLine(); continue; }
                catch (OutOfMemoryError e)
                { System.out.println("\tPlease insert a smaller Integer -->"); input.nextLine(); continue; }
                catch (Exception e)
                { System.out.println("\tPlease be kind to us, also try again -->"); input.nextLine(); continue; }
                if (N <= 0) { System.out.println("\tInput too low (must be greater than 0) -->"); input.nextLine(); passing = false; }
            }
            System.out.println(); // line break

            passing = false;
            while (!passing)
            {
                System.out.print("How many Writers do you want (W):\t");
                try { W = input.nextInt(); passing = true; }
                catch (InputMismatchException e)
                { System.out.println("\tPlease insert an Integer -->"); input.nextLine(); continue; }
                catch (OutOfMemoryError e)
                { System.out.println("\tPlease insert a smaller Integer -->"); input.nextLine(); continue; }
                catch (Exception e)
                { System.out.println("\tPlease be kind to us, also try again -->"); input.nextLine(); continue; }
                if (W <= 0) { System.out.println("\tInput too low (must be greater than 0) -->"); input.nextLine(); passing = false; }
            }
            System.out.println("\n\n\n"); // line break

            String nameR, nameW; // empty Strings, used to rename Threads
            // Reader Threads
            for (int i = 0; i < R; i++)
            {
                // Create runnable threads of Readers and Writers
                ReaderWriters task2rThread = new ReaderWriters();
                Thread task2Thread = new Thread(task2rThread);

                nameR = Integer.toString(i); task2Thread.setName("Reader" + nameR);

                task2Thread.start();
            }

            // Writer Threads
            for (int j = 0; j < W; j++)
            {
                // Create runnable threads of Readers and Writers
                ReaderWriters task2rThread = new ReaderWriters();
                Thread task2Thread = new Thread(task2rThread);

                nameW = Integer.toString(j); task2Thread.setName("Writer" + nameW);

                task2Thread.start();
            }

            // Wait until the all threads are made
            System.out.println("Waiting for Threads to be Created -->");

            while (Wait_Input != R + W)
            { Thread.yield(); }

            //System.out.println(Wait_Input);
            System.out.println(" --> Finished Waiting --> Threads Created\n\n");
            System.out.println("\n\n\n"); // spacing / line breaks

            // Resumes the Running Threads
            for (int i = 0; i < (W + R); i++)
            {
                ReaderWriters.Wait_Thread.release();
                System.out.println("======Release Thread From Semaphore======");
                Thread.yield();
            }
            System.out.println("\n\n\n"); // Line Break
        }

    }
}