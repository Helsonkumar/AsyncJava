package completablefuture;

public class Tester {


    public static void main(String[] args){

        String name = "helson";

        HelperClass cls  = new HelperClass("helson", 25);

        System.out.println("End of main method");


    }




}


class HelperClass {

    HelperClass(String name , int age) {

        while (name.equals("helson")){
            System.out.println("hi Helson!!1");
            try {
                Thread.sleep(5000);
                System.out.println("Sorry i was sleeping");

                if (age > 20) {
                    continue;
                }
                System.out.println("hey i am awake");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

        System.out.println("This is helper method");

    }

}
