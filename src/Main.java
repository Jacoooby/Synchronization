import java.util.Scanner;

class DiningPhilosophers implements Runnable {
    private final int philosophers;
    private final int meals;

    DiningPhilosophers(int philosophers, int meals) {
        this.philosophers = philosophers;
        this.meals = meals;
    }

    @Override
    public void run() {

    }
}
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of philosophers P: ");
        int P = scanner.nextInt();

        System.out.print("Enter the number of total meals to be eaten M: ");
        int M = scanner.nextInt();

        // array to store forks for number of philosophers
        // do later
        // ....


        // array to store threads for philosophers
        Thread[] threads = new Thread[P];

        // fork a single thread for each philosopher
        for (int i = 0; i < P; i++) {
            threads[i] = new Thread(new DiningPhilosophers(i, M));
            threads[i].start();
        }

    }
}