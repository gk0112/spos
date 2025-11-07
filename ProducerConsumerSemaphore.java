import java.util.*;
import java.util.concurrent.Semaphore;

public class ProducerConsumerSemaphore {
    static final int BUFFER_SIZE = 5;
    static Queue<Integer> buffer = new LinkedList<>();

    static Semaphore mutex = new Semaphore(1);
    static Semaphore empty = new Semaphore(BUFFER_SIZE);
    static Semaphore full = new Semaphore(0);

    static class Producer extends Thread {
        public void run() {
            try {
                for (int item = 1; item <= 10; item++) {
                    empty.acquire();
                    mutex.acquire();
                    buffer.add(item);
                    System.out.println("Producer produced:" + item);
                    mutex.release();
                    full.release();
                    Thread.sleep((int) (Math.random() * 1000 + 500));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class Consumer extends Thread {
        public void run() {
            try {
                for (int count = 1; count <= 10; count++) {
                    full.acquire();
                    mutex.acquire();
                    int item = buffer.remove();
                    System.out.println("Consumer consumed: Item-" + item);
                    mutex.release();
                    empty.release();
                    Thread.sleep((int) (Math.random() * 1000 + 500));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Producer producer = new Producer();
        Consumer consumer = new Consumer();

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nAll items produced and consumed successfully using Semaphores!");
    }
}
