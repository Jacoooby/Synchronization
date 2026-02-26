import java.util.concurrent.Semaphore;

public class ReaderWriters implements Runnable
{
    static Semaphore Wait_Thread = new Semaphore(0);

    // the file that can house N Reader-Agents and one Writer-Agent
    static Semaphore File = new Semaphore(Main.N, true);

    // a storage for Writer-Agents
    static Semaphore storage = new Semaphore(0, true);

    // the storage for finished Reader/Writer-Agents
    static Semaphore graveyard = new Semaphore(0, true);

    // used to ensure only one Reader (really first but doesn't matter) releases Writer-Agent
    static Semaphore releaseWriter = new Semaphore(1, true);

    // integers representing how many Reader & Writer Agents have completed their cycles
    static int completeRead = 0, completeWrite = 0;

    static int activeRead = 0;
    static int currentRead = 0;

    @Override
    public void run()
    {
        // used to ensure all Threads begin Reading and Writing at the same time,
        Main.Wait_Input++;
        try { Wait_Thread.acquire(); }
        catch (InterruptedException e) { System.out.println("The thread was interrupted, moving along -->"); }

        //============================== True Beginning of Program ==============================

        // Store the Writers then continue
        if ( Thread.currentThread().getName().contains("Writer") )
        {
            try { storage.acquire(); }
            catch (InterruptedException e) { System.out.println("*The Writer-Agent was interrupted, moving along -->"); }
        }
        // make the Reader-Agents wait & Allow N Reader-Agents to access the File
        else if ( Thread.currentThread().getName().contains("Reader") )
        {
            Thread.yield(); Thread.yield(); Thread.yield();
            try { File.acquire(); }
            catch (InterruptedException e) { System.out.println("*The Reader-Agent was interrupted, moving along -->"); }

            Thread.yield();
            activeRead++; currentRead++; // for some reason this is only done by the first N Reader-Agents
            //if (completeRead >= 1) { activeRead++; currentRead++; } // done by the second+ N Reader-Agents
        }
        else // CHANGE THIS
        { System.out.println("Something went wrong when naming the Agents.  Program will NOT progress."); }

        // giving time for the previousReader-Agents to finish exiting before
        // the current Reader-Agents enter the File
        Thread.yield(); Thread.yield(); Thread.yield(); Thread.yield(); Thread.yield();

        // Announce the Reader/Writer-Agents' hold on the resource
        if ( Thread.currentThread().getName().contains("Reader") )
        { System.out.println("––– " + Thread.currentThread().getName() + " has begun reading in the File"); }
        else if ( Thread.currentThread().getName().contains("Writer") )
        { System.out.println("––––– " + Thread.currentThread().getName() + " has begun writing in the File"); }


        // Three wait cycles
        Thread.yield(); Thread.yield(); Thread.yield();


        // Announce the Reader/Writer-Agents' conclusion on the resource
        if ( Thread.currentThread().getName().contains("Reader") )
        {
            System.out.println("–– " + Thread.currentThread().getName() + " has finished reading in the File");
            completeRead++;
        }
        else if ( Thread.currentThread().getName().contains("Writer") )
        {
            System.out.println("–––– " + Thread.currentThread().getName() + " has finished writing in the File");
            completeWrite++; //activeRead--;
        }


        // release the slept Writer/Reader-Agent(s) and then store the finished Reader/Writer-Agent(s)
        if ( Thread.currentThread().getName().contains("Reader") )
        {
            // releases the Writer-Agent to access the File if this is the last Reader-Agent
            activeRead--;

            //** in the case of only Reader-Agents being left (release more Reader-Agents)
            if (completeWrite == Main.W)
            {
                // if these are the last Reader-Agent(s) end the program
                if (completeRead == Main.R)
                { System.out.println("All Reader-Agents have finished, Program Finished"); System.exit(0); }

                // stores the first Reader-Agent and waits for the last Reader-Agent
                try { releaseWriter.acquire(); }
                catch  (InterruptedException e) { System.out.println("*The Reader-Agent was interrupted, moving along -->"); }

                // the "last" Reader-Agent waits for the rest to be stored
                while (activeRead != 0)
                { Thread.yield(); }

                // stores the other Reader-Agents without releasing any Writer-Agents
                if (currentRead == 0)
                {
                    try { graveyard.acquire(); }
                    catch (InterruptedException e) { System.out.println("*The Reader-Agent was interrupted, moving along -->"); }
                }

                // releases the other Reader-Agents
                int reg = currentRead; currentRead = 0;
                releaseWriter.release(reg);

                // release N amount of the slept Reader-Agents and then stores the finished Reader-Agent(s)
                File.release(Main.N);
                try { graveyard.acquire(); }
                catch (InterruptedException e) { System.out.println("*The Reader-Agent was interrupted, moving along -->"); }
            }

            //** in the case of Reader-Agents and Writer-Agents being left (release one Writer-Agent)

            // stores the first Reader-Agent and waits for the last Reader-Agent
            // *** Only done so that we can release a (singular) Writer-Agent
            try { releaseWriter.acquire(); }
            catch  (InterruptedException e) { System.out.println("*The Reader-Agent was interrupted, moving along -->"); }

            // the "last" Reader-Agent waits for the rest to be stored
            while (activeRead != 0)
            { Thread.yield(); }

            // stores the other Reader-Agents without releasing any Writer-Agents
            if (currentRead == 0)
            {
                try { graveyard.acquire(); }
                catch (InterruptedException e) { System.out.println("*The Reader-Agent was interrupted, moving along -->"); }
            }

            // releases the other Reader-Agents
            int reg = currentRead; currentRead = 0;
            releaseWriter.release(reg);

            // releases the (singular) Writer-Agent
            storage.release();

            // store the "last" Reader-Agent
            try { graveyard.acquire(); }
            catch (InterruptedException e) { System.out.println("*The Reader-Agent was interrupted, moving along -->"); }
        }
        else if ( Thread.currentThread().getName().contains("Writer") )
        {
            // in the case of only Writer-Agents being left
            if (completeRead == Main.R)
            {
                // release the next (singular) Writer-Agent
                storage.release();

                // if this is the last Writer-Agent end the program
                if (completeWrite == Main.W)
                { System.out.println("All Writer-Agents have finished, Program Finished"); System.exit(0); }
            }

            // release N amount of the slept Reader-Agents and then stores the finished Writer-Agent
            File.release(Main.N);
            try { graveyard.acquire(); }
            catch (InterruptedException e) { System.out.println("*The Writer-Agent was interrupted, moving along -->"); }

        }
    }
}
