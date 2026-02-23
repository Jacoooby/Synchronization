import java.util.concurrent.Semaphore;

class DiningPhilosophers implements Runnable {
    private final int philosopherID;

    private static int[] arrived;

    // mutex semaphore to help track when all philosophers have arrived
    private static Semaphore arrive;
    // semaphore to track when the last philosopher arrives so everyone can sit
    private static Semaphore seatedCounter;

    private static Semaphore[] chopsticks;

    private static int totalPhilosophers;

    // mutex semaphore to protect meals left counter
    private static Semaphore mealMutex;
    // semaphore that opens once last philosopher is ready to leave
    private static Semaphore exitBarrier;

    private static int[] exitCount;
    private static int[] mealsLeft;

    // mutex semaphore to track exitCount
    private static Semaphore exitMutex;

    DiningPhilosophers(int philosopherID, Semaphore arrive, Semaphore seated, int[] arrived, Semaphore mealMutex, int[] mealsLeft, Semaphore exitMutex, Semaphore exitBarrier, int[] exitCount, Semaphore[] chopsticks, int total) {
        this.philosopherID = philosopherID;
        DiningPhilosophers.arrive = arrive;
        DiningPhilosophers.seatedCounter = seated;
        DiningPhilosophers.arrived = arrived;
        DiningPhilosophers.mealMutex = mealMutex;
        DiningPhilosophers.mealsLeft = mealsLeft;
        DiningPhilosophers.exitMutex = exitMutex;
        DiningPhilosophers.exitBarrier = exitBarrier;
        DiningPhilosophers.exitCount = exitCount;
        DiningPhilosophers.chopsticks = chopsticks;
        DiningPhilosophers.totalPhilosophers = total;
    }

    @Override
    public void run() {
        // Main logic where each philosopher waits
        try {
            // start of dining philosopher logic
            System.out.println("-Philosopher " + philosopherID + " has entered.");

            // each philosopher acquires arrive and will increment arrived
            arrive.acquire();
            arrived[0]++;

            // checks if arrived value is equal to total philosophers, once it is equal the philosophers are allowed
            // to sit down and eat
            if (arrived[0] == totalPhilosophers) {
                System.out.println("--All philosophers have arrived! They get ready to sit down!");
                // release the semaphore indicating all philosophers have arrived
                seatedCounter.release(totalPhilosophers);
            }
            arrive.release();


            // Step One: wait for seatedCounter to be acquired before allowing all philosophers to sit down
            seatedCounter.acquire();
            System.out.println("---Philosopher " + philosopherID + " sits down.");

            // Step 10: this loop handles chopstick, eating, and thinking logic. All these steps within this loop will keep repeating until all meals have been eaten.
            while (true) {
                // check if all meals have been eaten before trying to grab chopsticks
                mealMutex.acquire();
                if (mealsLeft[0] <= 0) {
                    mealMutex.release();
                    break;
                }
                mealMutex.release();

                // prevent deadlock from happening by having an even ID philosophers picking up left chopstick first
                // and an odd ID philosophers picking up right chopstick first.
                int leftChopstick, rightChopstick;
                if (philosopherID % 2 == 0) {
                    leftChopstick = philosopherID;
                    rightChopstick = (philosopherID + 1) % totalPhilosophers;
                } else {
                    leftChopstick = (philosopherID + 1) % totalPhilosophers;
                    rightChopstick = philosopherID;
                }

                // Step Two/Three: try to pick up left and right chopsticks
                chopsticks[leftChopstick].acquire();

                // check meals again before grabbing right chopstick
                mealMutex.acquire();
                if (mealsLeft[0] <= 0) {
                    mealMutex.release();
                    chopsticks[leftChopstick].release();
                    break;
                }
                mealMutex.release();

                System.out.println("---Philosopher " + philosopherID + "'s left chopstick is available");
                // now check if the right is available
                chopsticks[rightChopstick].acquire();
                System.out.println("---Philosopher " + philosopherID + "'s right chopstick is available");


                // Step Four: begin eating
                // start eating only if there are meals remaining
                boolean ate = false;
                mealMutex.acquire();
                if (mealsLeft[0] > 0) {
                    mealsLeft[0]--;
                    ate = true;
                    System.out.println("----Philosopher " + philosopherID + " grabs a pair of chopsticks!");
                    System.out.println("-----Philosopher " + philosopherID + " is eating.");
                    System.out.println("------Meals left to share: " + mealsLeft[0]);
                }
                mealMutex.release();

                // Step Five: the philosopher will continue eating for 3-6 cycles
                if (ate) {
                    int randomCyles = 3 + (int) (Math.random() * 4);
                    for (int i = 0; i < randomCyles; i++) {
                        Thread.sleep(100);
                    }
                }

                // Steps Six/Seven: put down left and right chopsticks after eating
                System.out.println("-------Philosopher " + philosopherID + " puts their left chopstick down");
                System.out.println("-------Philosopher " + philosopherID + " puts their right chopstick down");
                chopsticks[leftChopstick].release();
                chopsticks[rightChopstick].release();


                // Step Eight: begin thinking after putting down chopsticks
                System.out.println("--------Philosopher " + philosopherID + " starts thinking.");

                // Step Nine: the philosopher will continue thinking for 3-6 cycles
                int randomCyles2 = 3 + (int) (Math.random() * 4);
                for (int i = 0; i < randomCyles2; i++) {
                    if (i == randomCyles2 - 1) {
                        System.out.println("--------Philosopher " + philosopherID + " is done thinking.");
                    }
                    Thread.sleep(100);
                }
            }

            // Step 11: a philosopher finally breaks out of the loop because no meals are left
            System.out.println("---------No meals left for Philosopher " + philosopherID + " they are ready to leave.");

            // Wait for all philosophers to finish eating before they can all exit together
            exitMutex.acquire();
            exitCount[0]++;

            // once the last philosopher is ready to leave he releases this barrier
            if (exitCount[0] == totalPhilosophers) {
                exitBarrier.release(totalPhilosophers);
            }
            exitMutex.release();

            // wait until all philosophers are ready to leave together
            exitBarrier.acquire();
            System.out.println("Philosopher " + philosopherID + " leaves the table");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}