public class ClassicThreads {

    public static void main(String[] args) {

        System.out.println("hi helson");
        new Thread(() -> inner()).start();
        System.out.println("This is the end of main thread");

    }

    public static void inner() {

        System.out.println("This is inner method");

        //* Thread-1 is created
        new Thread(() -> {
            int x = 3;
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("This is Thread-1 result " + x * 2);
        }
        ).start();

        //** Thread-2 is created
        new Thread(() -> {
            int y = 5;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("This is Thread-2 result " + y * 2);
        }
        ).start();

        System.out.println("This is the end of inner method");

    }
}
