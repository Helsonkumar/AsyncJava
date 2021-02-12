package completablefuture;


import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

//This is a dem to show that using priority queue the threads with High riority can be scheduled first.
//Idea is to create a listner thread whihc would monitor the queue for task.
//If one is available then take it. But jobs would be orer in the queuer based on their priority passed to the custom comparator
public class PriorityQueueScheduling {

    static PriorityBlockingQueue<Shop> queue = new PriorityBlockingQueue(100, Comparator.comparing(Shop::getId).reversed());

    public static void main(String[] args) {

        List<Shop> shops = DiscountCalculator.getShopList();

        //** The Listener thread is triggered inside the constructur of this class. This thread would keep on listening to the jobs added to the queue
        JobScheduler scheduler = new JobScheduler(queue);

        shops.stream().forEach(shop -> queue.add(shop));
    }

}


class JobScheduler {

    ExecutorService es1 = Executors.newSingleThreadExecutor();
    ExecutorService es = Executors.newFixedThreadPool(1);
    PriorityBlockingQueue queue;

    JobScheduler(PriorityBlockingQueue<Shop> queue) {
        this.queue = queue;
        es1.execute(() -> {
            while (true) {

                try {
                    Shop shop = queue.take();
                    es.execute(() -> {
                        System.out.println(shop.getPrice("Shirt") + ":" + shop.getId());
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }

            }

        });

    }

}
