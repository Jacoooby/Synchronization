import java.util.concurrent.Semaphore;

class DiningPhilosophers implements Runnable {
    private final int philosopherID;
    private final int meals;

    // semaphore used to know when all philosophers have arrived
    private static Semaphore presentCounter;
    // semaphore used to know when all philosophers have sat down
    private static Semaphore seatedCounter;

    private static Semaphore[] chopsticks;

    private static int totalPhilosophers;

    DiningPhilosophers(int philosopherID, int meals, Semaphore present, Semaphore seated, int total, Semaphore[] sticks) {
        this.philosopherID = philosopherID;
        this.meals = meals;
        DiningPhilosophers.presentCounter = present;
        DiningPhilosophers.seatedCounter = seated;
        DiningPhilosophers.totalPhilosophers = total;
        DiningPhilosophers.chopsticks = sticks;
    }

    @Override
    public void run() {
        // start of dining philosopher logic
        System.out.println("Philosopher " + philosopherID + " has entered." );
        // release the semaphore to indicate a philosopher has arrived
        presentCounter.release();

        // loop to check if semaphore value is equal to total philosophers, once it is equal the philosophers are allowed
        // to sit down and eat
        if (presentCounter.availablePermits() == totalPhilosophers) {
            System.out.println("All philosophers have arrived! They sit down and get ready to eat.");
            // release the semaphore indicating all philosophers have sat down
            seatedCounter.release();
        }

        // Main logic where each philosopher waits
        try {
            // if unable to acquire the semaphore, it means not all philosophers have sat down
            seatedCounter.acquire();
            seatedCounter.release();

            // Shared logic for eating starts here
            for (int mealCount = 0; mealCount < meals; mealCount++) {
                int left_chopstick = philosopherID;
                int right_chopstick = (philosopherID + 1) % totalPhilosophers;

                // acquire the chopsticks in order to prevent deadlock from happening
                if (philosopherID % 2 == 0) {
                    chopsticks[left_chopstick].acquire();
                    chopsticks[right_chopstick].acquire();
                    System.out.println("Philosopher " + philosopherID + " has grabbed left and right sticks");
                } else {
                    chopsticks[right_chopstick].acquire();
                    chopsticks[left_chopstick].acquire();
                    System.out.println("Philosopher " + philosopherID + " has grabbed right and left sticks");
                }
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}