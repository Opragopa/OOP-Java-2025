import java.util.concurrent.*;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) {
        int numThreads = 3;
        final int SLEEP_TIME_IN_SECONDS = 1;

        // Если аргумент передан, используем его
        if (args.length > 0) {
            try {
                numThreads = Integer.parseInt(args[0]);
                if (numThreads <= 0) {
                    System.out.println("Number of threads must be positive. Using default value 3.");
                    numThreads = 3;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Using default value 3.");
                numThreads = 3;
            }
        } else {
            System.out.println("No thread count provided. Using default value 3.");
        }

        System.out.println("Starting with " + numThreads + " writer and " + numThreads + " reader threads");

        BlockingQueue<String> messageQueue = new ArrayBlockingQueue<>(100);

        ExecutorService writerPool = Executors.newFixedThreadPool(numThreads);
        ExecutorService readerPool = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            writerPool.execute(() -> {
                try {
                    String message = "Message from writer " + threadId;
                    messageQueue.put(message);
                    System.out.println("Writer " + threadId + " wrote: " + message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Writer " + threadId + " was interrupted");
                }
            });
        }

        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            readerPool.execute(() -> {
                try {
                    String message = messageQueue.take();
                    System.out.println("Reader " + threadId + " read: " + message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Reader " + threadId + " was interrupted");
                }
            });
        }

        writerPool.shutdown();
        readerPool.shutdown();

        try {
            long timeoutMillis = 60000;
            long startTime = System.currentTimeMillis();

            while (!writerPool.isTerminated()) {
                if (System.currentTimeMillis() - startTime > timeoutMillis) {
                    System.out.println("Timeout waiting for writer threads");
                    writerPool.shutdownNow();
                    break;
                }
                sleep(100);
            }

            startTime = System.currentTimeMillis();
            while (!readerPool.isTerminated()) {
                if (System.currentTimeMillis() - startTime > timeoutMillis) {
                    System.out.println("Timeout waiting for reader threads");
                    readerPool.shutdownNow();
                    break;
                }
                sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted");
            writerPool.shutdownNow();
            readerPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}