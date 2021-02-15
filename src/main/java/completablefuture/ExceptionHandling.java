package completablefuture;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

//Shows the diff ways of handling exceptions in CompletableFutures
public class ExceptionHandling {

    public static void main(String[] args) {

        //** Using handle method enables us to provide default value when there is an exception
        //** This expects a return value under success and failure condition
        CompletableFuture.supplyAsync(() -> "abc")
                .thenApply(Integer::parseInt)
                .handle((result, e) -> {
                    if (e != null) {
                        System.out.println("This is the incorrect input : " + e);
                        return 0;
                    } else
                        return result;
                })
                .thenAccept(s -> System.out.println("This is the future1  : " + s));


        //** Using exeptionally : Similar to handle but less verbose
        CompletableFuture future2 = CompletableFuture.supplyAsync(() -> "123s")
                .thenApply(Integer::parseInt)
                .exceptionally(ex -> {
                    System.out.println("This is the incorrect input : " + ex);
                    return 0;
                });

        future2.join();


        //** whenComplete : This would not execuste the consecutive stpes on exception
        CompletableFuture.supplyAsync(() -> "895")
                .thenApply(Integer::parseInt)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.out.println(ex);
                    }
                })
                .thenAccept(result -> System.out.println("This is the future3 : " + result));


        //** CompletedExceptionally  : Propagating the exception
        CompletableFuture<Integer> future4 = new CompletableFuture<>();
        try {
            future4.complete(Integer.parseInt("asss"));
        } catch (CompletionException ex) {
            future2.completeExceptionally(ex);
        }
        System.out.println(future4.join());
    }

}
