import java.util.concurrent.Semaphore;


public class ReaderWriters implements Runnable
{
    static Semaphore Wait_Thread = new Semaphore(0);
    static Semaphore sFile = new Semaphore( Main.N );
    static Semaphore bFile = new Semaphore(1, true);
    int tester = 10000000;

    @Override
    public void run()
    {
        // used to ensure all Threads begin Reading and Writing at the same time,
        Main.Wait_Input++;
        try { Wait_Thread.acquire(); }
        catch (InterruptedException e) { System.out.println("The thread was interrupted, moving along -->"); }

        System.out.println( "First Iteration of The Reading and Writing for " + Thread.currentThread().getName() ); // indexing

        // makes sure there is / will be a Reader-Agent accessing the File
        if (Main.readerInFile)
        {
            // Checks to see if the File has space
            if ( sFile.availablePermits() > 0 && Thread.currentThread().getName().contains("Reader") )
            {
                // Begin reading for 3-5 cycles
                System.out.println("\n\t||| Waiting to finish reading – " + Thread.currentThread().getName() + " |||");
                System.out.println( Thread.currentThread().getName() );

                try { sFile.acquire(); }
                catch (InterruptedException e) { System.out.println("The thread was interrupted, moving along -->"); }

                // the 3-5 cycles
                System.out.println( Thread.currentThread().getName() + " is yielding");
                Thread.yield(); Thread.yield(); Thread.yield(); Thread.yield(); Thread.yield(); // five yielding cycles
                System.out.println( Thread.currentThread().getName() + " is resuming");

                // Finish reading and return access to the File
                System.out.println("\n\t||| Finished reading – " + Thread.currentThread().getName() + " |||");
                sFile.release();

                // Allow a [single] Writer to access the File
                if (sFile.availablePermits() == Main.N)
                { Thread.yield(); Main.readerInFile = false; Main.readerInFile = false; }
                System.out.println("\n-----\n" + Thread.currentThread().getName() + " is Swapping Accessors: Writers" + "\n-----\n");
            }
        }
        System.out.println( Thread.currentThread().getName() + " is Looping The Reading and Writing" ); // indexing

        while (tester > 0)
        {
            if (!Main.readerInFile) // there is / will be a Writer in the File
            {
                // have a single Writer go into the file and then no other (3-5 cycles)
                if ( bFile.availablePermits() == 1 && Thread.currentThread().getName().contains("Writer") )
                {
                    try { bFile.acquire(); }
                    catch (InterruptedException e) { System.out.println("The thread was interrupted, moving along -->"); continue; }

                    // Begin writing for 3-5 cycles
                    System.out.println("\n\t||| Waiting to finish writing – " + Thread.currentThread().getName() + " |||");

                    // the 3-5 cycles
                    System.out.println( Thread.currentThread().getName() + " is yielding");
                    Thread.yield(); Thread.yield(); Thread.yield(); Thread.yield(); Thread.yield(); // five yielding cycles
                    System.out.println( Thread.currentThread().getName() + " is resuming");

                    // Finish writing and leave the File
                    System.out.println("\n\t||| Finished writing – " + Thread.currentThread().getName() + " |||");

                    Main.readerInFile = true; Main.readerInFile = true;  // allow N amount of Readers in the File
                    Thread.yield(); // give time for Writer-Agents to leave this part of the loop
                    bFile.release();

                    System.out.println("\n-----\n" + Thread.currentThread().getName() + " is Swapping Accessors: Readers" + "\n-----\n");
                }
            }
            else // there is / will be a Reader in the File
            {
                // have N amount of Reader-Agents read for 3-5 cycles and then dip

                // Checks to see if the File has space
                if ( sFile.availablePermits() > 0 && Thread.currentThread().getName().contains("Reader") )
                {
                    try { sFile.acquire(); }
                    catch (InterruptedException e) { System.out.println("The thread was interrupted, moving along -->"); continue; }

                    // Begin reading for 3-5 cycles
                    System.out.println("\n\t||| Waiting to finish reading – " + Thread.currentThread().getName() + " |||");

                    // the 3-5 cycles
                    System.out.println( Thread.currentThread().getName() + " is yielding");
                    Thread.yield(); Thread.yield(); Thread.yield(); Thread.yield(); Thread.yield(); // five yielding cycles
                    System.out.println( Thread.currentThread().getName() + " is resuming");

                    // Finish reading and leave the File
                    sFile.release();
                    System.out.println("\n\t||| Finished reading – " + Thread.currentThread().getName() + " |||");

                    // when there is no Agent accessing the File, allow the Reader-Agents access
                    if (sFile.availablePermits() == Main.N) // allow a [single] Writer in the File
                    { Main.readerInFile = false; Main.readerInFile = false; }
                    System.out.println("\n-----\n" + Thread.currentThread().getName() + " is Swapping Accessors: Writers" + "\n-----\n");
                }
            }
            tester--; // decrements the tester to eventually end the loop
        }
        System.out.println( Thread.currentThread().getName()  + " – ReaderAccess: " + Main.readerInFile);
    }
}
