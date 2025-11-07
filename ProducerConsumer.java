import java.util.*;
import java.util.concurrent.*;

public class ProducerConsumer {
    private static final int BUFFER_SIZE = 5;
    private final Queue<Integer> buffer = new LinkedList<>();
    private final Semaphore empty = new Semaphore(BUFFER_SIZE);
    private final Semaphore full = new Semaphore(0);
    private final Object mutex = new Object();

    class Producer extends Thread {
        public void run() {
            for (int i = 1; i <= 10; i++) {
                try {
                    empty.acquire(); // wait if buffer full
                    synchronized (mutex) {
                        buffer.add(i);
                        System.out.println("Producer produced: " + i);
                    }
                    full.release(); // signal consumer
                    Thread.sleep((int)(Math.random() * 1000) + 500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Consumer extends Thread {
        public void run() {
            for (int i = 1; i <= 10; i++) {
                try {
                    full.acquire(); // wait if buffer empty
                    synchronized (mutex) {
                        int item = buffer.remove();
                        System.out.println("Consumer consumed: " + item);
                    }
                    empty.release(); // signal producer
                    Thread.sleep((int)(Math.random() * 1000) + 500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void startSimulation() throws InterruptedException {
        Thread producer = new Producer();
        Thread consumer = new Consumer();
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        System.out.println("\nAll items produced and consumed successfully!");
    }

    public static void main(String[] args) throws InterruptedException {
        new ProducerConsumer().startSimulation();
    }
}
