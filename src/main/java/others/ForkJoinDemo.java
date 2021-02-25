package others;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

//** ForkJoin : Used to split a large task into multiple chunks to be executed or processed in parallel.
//** A task must be of type RecursiveTask to be executed in a ForkJoinPool.
//** The compute() method must tell : Logic to divide the task into small chunk and how to process that small chunk

//* In this demo we show how to calculate sum of integers using ForkJoin method using ForkJoinPool of threads
public class ForkJoinDemo {

    private static final Logger logger = LoggerFactory.getLogger(ForkJoinDemo.class);

    public static void main(String[] args) {

        logger.info("Into the main method");

        List<Long> list = LongStream.rangeClosed(1, 200).boxed().collect(Collectors.toList());

        ForkJoinPool pool = new ForkJoinPool();

        logger.info(String.format("The result is : %s", pool.invoke(new ForkJoinDemoTask(list))));

    }

}


//* We would reuse the same input list since it is read only and we would not split and provide new one for every split
//* So the index what we pass would vary for every split
class ForkJoinDemoTask extends RecursiveTask<Long> {

    List<Long> list;
    int threshold_limit = 100;
    int start;
    int end;
    ForkJoinPool pool;

    ForkJoinDemoTask(List<Long> list) {
        this(list, 0, list.size());
    }

    ForkJoinDemoTask(List<Long> list, int start, int end) {
        this.list = list;
        this.start = start;
        this.end = end;
    }


    //**Gives the logic to split the task if big .And to compute when small
    @Override
    protected Long compute() {

        //** Check if the task is smaller than threshold
        int size = end - start;

        //** If yes, split compute the list
        if (size <= threshold_limit) {
            return computeTask();
        }

        //** if not , then split the list
        ForkJoinDemoTask leftTask = new ForkJoinDemoTask(list, start, size / 2);
        leftTask.fork(); //* This would simply submits the taslk in Aysn thread in the ForKJoinPool

        ForkJoinDemoTask rightTask = new ForkJoinDemoTask(list, size / 2, end);

        Long rightResult = rightTask.compute();
        Long leftResult = leftTask.join();

        return leftResult + rightResult;


    }

    private Long computeTask() {

        Long sum = 0L;
        for (int i = 0; i < end; i++) {
            sum += list.get(i);
        }
        return sum;
    }
}