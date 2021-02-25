package others;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class WordCounter {

    public static void main(String[] args) {

        String text = "  My name is HelsonKumar Manoharan. I am doing good. Lets connect after some time. Shall we ? Let me know.  ";

        IteratorCounter it = new IteratorCounter(text);
        System.out.println("This is the iterator counter  : " + it.counter());

        Stream<Character> char_stream = IntStream.range(0, text.length()).mapToObj(text::charAt);

        FunctionalCounter result = char_stream.reduce(new FunctionalCounter(0, true), FunctionalCounter::accumalate, FunctionalCounter::combine);
        System.out.println("This is the Functional Counter : " + result.getCounter());


        Spliterator<Character> splitter = new WordCountSplitter(text);
        Stream<Character> stream_char = StreamSupport.stream(splitter, true); //** We inject the custom SplitIterator
        FunctionalCounter split_counter = stream_char.reduce(new FunctionalCounter(0, true), FunctionalCounter::accumalate, FunctionalCounter::combine);
        System.out.println("This is the SplitIterator  result  : " + split_counter.getCounter());


    }
}


//** This does the word count using iteration.
//** States  : Previous character and the current one
//** Tip  : Word  : if Current is a char and prev is white space.
class IteratorCounter {

    String text;
    long count = 0;
    boolean lastSpace = true;

    IteratorCounter(String text) {
        this.text = text;

    }

    public long counter() {

        for (char c : text.toCharArray()) {

            if (Character.isWhitespace(c)) {
                lastSpace = true;
            } else {
                if (lastSpace) {
                    count++;
                    lastSpace = false;
                }
            }
        }

        return count;
    }

}


//** This does the counting in more fucntionl style using immutable class so it can be parallelized easily.
//** Each state change of the object would result in new object tobe produced insted of altering the state of current one
//** Key is to make all the state variable immutable
class FunctionalCounter {

    private final long count;
    private final boolean lastSpace;

    FunctionalCounter(long count, boolean lastSpace) {
        this.count = count;
        this.lastSpace = lastSpace;
    }

    //** This is used to accumalate the chracter at each split level
    //** We return new object for every state change
    public FunctionalCounter accumalate(Character c) {

        if (Character.isWhitespace(c))
            return lastSpace ? this : new FunctionalCounter(count, true);
        else
            return lastSpace ? new FunctionalCounter(count + 1, false) : this;
    }


    //** This is used to combine the result of two splits
    public FunctionalCounter combine(FunctionalCounter functionalCounter) {
        return new FunctionalCounter(count + functionalCounter.count, lastSpace);
    }

    public long getCounter() {
        return count;
    }

}

class WordCountSplitter implements Spliterator<Character> {

    String text;
    int curr_pos = 0;

    WordCountSplitter(String text) {
        this.text = text;
    }

    //** Defines if there are further characters to be splitted
    @Override
    public boolean tryAdvance(Consumer<? super Character> action) {

        action.accept(text.charAt(curr_pos++));
        return curr_pos < text.length();

    }


    //** This gives the actual splitting logic
    @Override
    public Spliterator<Character> trySplit() {

        int curr_size = text.length() - curr_pos;

        if (curr_size < 10)
            return null;

        for (int split_pos = curr_size / 2 + curr_pos; split_pos < text.length(); split_pos++) {

            if (Character.isWhitespace(text.charAt(split_pos))) {

                Spliterator<Character> splitter = new WordCountSplitter(text.substring(curr_pos, split_pos));
                curr_pos = split_pos;
                return splitter;

            }
        }

        return null;
    }

    @Override
    public long estimateSize() {
        return text.length() - curr_pos;
    }

    @Override
    public int characteristics() {
        return ORDERED + SIZED + SUBSIZED + IMMUTABLE;
    }
}