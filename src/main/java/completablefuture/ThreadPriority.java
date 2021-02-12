package completablefuture;


import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.stream.Collectors.toList;

//* This is a demo to hsow threads with user given priority behaves whilst executing
//** Expectation is to prefer High priority thread over lowest one in resource allocation
//* But ideally setting prioriyt for  athread is not advisable since thread scheduling is platform dependant.
public class ThreadPriority {

    public static void main(String[] args) {

        List<Shop> shops = DiscountCalculator.getShopList();
        ExecutorService es = Executors.newFixedThreadPool(8);

        Random random = new Random();

        List<Integer> p = Arrays.asList(1,5,10);

        List<CompletableFuture<Integer>> future_shops = shops.stream().map(shop -> CompletableFuture.supplyAsync(() -> {

            //int n = random.nextInt(10);
            int inx = random.nextInt(3);

            //By default the JVM tends to assign Normal priority for the threads
            Thread.currentThread().setPriority(p.get(inx));
            System.out.println("Start of Thread: " + Thread.currentThread().getName() + ":" + Thread.currentThread().getPriority());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("End of Thread: " + Thread.currentThread().getName()+ ":" + Thread.currentThread().getPriority());
            return Thread.currentThread().getPriority();

        }, es)).collect(toList());

        future_shops.stream().forEach(future -> System.out.println(future.join()));

        es.shutdown();


    }


}


