package CountDownLatch;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Demo to show simulate the concurrency issue with the help of CountDownLatch
public class MultipleThreads {

    static CountDownLatch startLatch = new CountDownLatch(10);
    static CountDownLatch allStartedLatch = new CountDownLatch(1);
    static CountDownLatch allDoneLatch = new CountDownLatch(10);
    static List<String> list = new ArrayList<>();


    public static void main(String[] args) {

        list.add("Started the Main Thread");

        List<Thread> threadList = Stream.generate(() -> new Thread(new Runner(list, startLatch, allStartedLatch, allDoneLatch))).limit(10).collect(Collectors.toList());

        try {
            threadList.stream().forEach(Thread::start);
            startLatch.await();
            allStartedLatch.countDown();
            allDoneLatch.await();
            list.add("End of Main Thread");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        list.stream().forEach(System.out::println);

    }
}


class Runner implements Runnable {


    List<String> list;
    CountDownLatch allStartedLatch;
    CountDownLatch allDoneLatch;
    CountDownLatch startLatch;

    Runner(List<String> list, CountDownLatch startLatch, CountDownLatch allStartedLatch, CountDownLatch allDoneLatch) {
        this.list = list;
        this.startLatch = startLatch;
        this.allStartedLatch = allStartedLatch;
        this.allDoneLatch = allDoneLatch;

        startLatch.countDown();

    }


    @Override
    public void run() {
        try {
            allStartedLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        list.add("Started Thread child" + Thread.currentThread().getId());
        allDoneLatch.countDown();

    }
}



