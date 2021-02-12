
package completablefuture;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

//This is a demo to show how to leverage the Java 8 Concurreny Package features.
public class DiscountCalculator {

    //** Create a service which calcuates the discount for a product from each shop
    public static void main(String[] args) {

        //** Use the number of threads equal to number of tasks if task(Workloads) are IO bound.
        ExecutorService es = Executors.newFixedThreadPool(9);

        long start = System.nanoTime();

        List<Shop> shops = getShopList();

        //Using paralle Streams defaults the number of threads to the number of cores available
        //* shops.parallelStream().map(shop -> shop.getPrice("leggins")).forEach(price -> System.out.println(price));


        //* Using CompletableFuture enables you to pass customized executors servcie with number of thread as per the requirement
        //* Lazy evaluation  : The operation with narrow dependencies are consolicated and executed together (trigger Future thread  + join()). This delays execution.
      /*  List<String> list_price_future = shops.stream().map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice("leggins")))
                                                               .map(CompletableFuture :: join).collect(toList());
        list_price_future.forEach(future -> System.out.println(future));
*/
        //** Here the Future creation and joins raee performed in seperate pipelines. This is very effective.
        //** We use the ExecutorService with the number of threads equal to the number of tasks.
        //** Due to time slicing and context switching the threads are effectively executed.
        List<CompletableFuture<String>> list_price_future = shops.stream().map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice("leggins"), es)).collect(toList());

        list_price_future.stream().forEach(future -> System.out.println(future.join()));

        long end = System.nanoTime();

        System.out.println(Runtime.getRuntime().availableProcessors());
        System.out.println("Took about : " + (end - start) / 1000000000);

        es.shutdown();

    }

    public static List<Shop> getShopList() {

        Shop shop1 = new Shop("H&M", 1,  "Shirt:89#Trouser:56#Shoe:45#leggins:76");
        Shop shop2 = new Shop("Pantloons", 5, "Shirt:67#Trouser:256#Shoe:425#leggins:716");
        Shop shop3 = new Shop("Gucci", 1,"Shirt:819#Trouser:256#Shoe:345#leggins:76");
        Shop shop4 = new Shop("M&S", 1,"Shirt:289#Trouser:506#Shoe:245#leggins:76");
        Shop shop5 = new Shop("Tommy",10,  "Shirt:489#Trouser:5446#Shoe:435#leggins:7236");
        Shop shop6 = new Shop("Kappa", 5,"Shirt:879#Trouser:156#Shoe:445#leggins:746");
        Shop shop7 = new Shop("Nike", 1,"Shirt:899#Trouser:356#Shoe:453#leggins:756");
        Shop shop8 = new Shop("Jordan", 5,"Shirt:8929#Trouser:3256#Shoe:4533#leggins:7356");
        Shop shop9 = new Shop("Allan", 10, "Shirt:4929#Trouser:3456#Shoe:45443#leggins:72556");
        Shop shop10 = new Shop("Allan1", 1,"Shirt:43929#Trouser:33456#Shoe:45543#leggins:72556");
        Shop shop11 = new Shop("Allan2", 10, "Shirt:49229#Trouser:34536#Shoe:45453#leggins:72565");
        Shop shop12 = new Shop("Allan3", 1,"Shirt:4929#Trouser:34536#Shoe:4543#leggins:72556");
        Shop shop13 = new Shop("Allan4", 10, "Shirt:4929#Trouser:3456#Shoe:45453#leggins:75256");
        Shop shop14 = new Shop("Allan5",1, "Shirt:4929#Trouser:3456#Shoe:45453#leggins:75256");
        Shop shop15 = new Shop("Allan6",10,  "Shirt:4929#Trouser:3456#Shoe:45453#leggins:75256");
        Shop shop16 = new Shop("Allan7",5, "Shirt:4929#Trouser:3456#Shoe:45453#leggins:75256");
        Shop shop17 = new Shop("Allan8",5, "Shirt:4929#Trouser:3456#Shoe:45453#leggins:75256");

        List<Shop> shops = Arrays.asList(shop1, shop2, shop3, shop4, shop5, shop6, shop7, shop8, shop9, shop10, shop11, shop12, shop13, shop14, shop15, shop16, shop17);

        return shops;

    }
}


// A Shop gives the price for the given Product
class Shop {

    String shopName;
    int shopId;
    Map<String, Integer> product_price = new HashMap<>();

    public Shop(String name, int shopId, String items_prices) {
        this.shopName = name;
        this.shopId = shopId;
        String[] item_price = items_prices.split("#");
        Arrays.stream(item_price).map(IP -> IP.split(":")).forEach(x -> product_price.put(x[0], Integer.parseInt(x[1])));
    }

    public String getPrice(String product) {

        String result = shopName + ":" + product + ":" + product_price.get(product) + "=========================================";
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int getId() {
        return shopId;
    }

    public String getShopName(){
        return this.shopName;
    }
}
