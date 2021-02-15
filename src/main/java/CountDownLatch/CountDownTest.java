package CountDownLatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;


// Demo to show the usage of thr CountDownLatch
// * Used to block a calling thread untill all child tread completes an action of expected task.
public class CountDownTest {

    public static void main(String[] args) {

        List<String> list = new ArrayList<>();

        ExecutorService es = Executors.newFixedThreadPool(5);

        //Set the latch for five child threads
        CountDownLatch latch1 = new CountDownLatch(5);

        list.add("Start of main thread");

        for (int i = 0; i < 5; i++) {
            es.execute(new latchTask(latch1, list));
        }


        //Awaiting for the child threads with the latch to finish or until the latch reaches 0
        try {
            latch1.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        list.add("End of the main Thread");

        list.stream().forEach(System.out::println);

        es.shutdown();

    }


}


class latchTask implements Runnable {

    CountDownLatch latch;
    List<String> list;

    latchTask(CountDownLatch latch, List<String> list) {
        this.latch = latch;
        this.list = list;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(new Random().nextInt(10) * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        list.add("This is  Child thread");

        //CountDown the latch for every child thread
        latch.countDown();
    }
}
