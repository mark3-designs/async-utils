package cyberdyne.async;

import java.time.Duration;
import java.util.Random;

public class BackgroundRunnable {

    private final Duration period;
    private final Runnable runnable;
    private boolean started = false;
    private boolean stopped = false;
    final private long cycle; // = 1000L;
    private long next;

    private final Thread shutdownHook = new Thread(() -> terminate());

    private Thread thread = new Thread() {

        @Override
        public void run() {
            while (!stopped) {
                try {
                    long now = System.currentTimeMillis();
                    if (now >= next) {
                        runnable.run();
                        long execTimeMs = System.currentTimeMillis() - now;
                        next += cycle - execTimeMs;
                    } else {
                        Thread.sleep(cycle / 5);
                    }
                } catch (InterruptedException irq) {
                } catch (Throwable error) {
                    error.printStackTrace();
                } finally {
                }
            }
        }
    };

    public BackgroundRunnable(Duration period, Runnable runnable) {
        this(period, runnable, false);
    }

    public BackgroundRunnable(Duration period, Runnable runnable, boolean quickStart) {
        this.period = period;
        this.runnable = runnable;
        this.cycle = period.toMillis();
        this.next = System.currentTimeMillis() + (quickStart ? -1 : cycle);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    public BackgroundRunnable stop() {
        terminate();
        return this;
    }

    private void terminate() {
        this.stopped = true;
        Thread.yield();
        if (thread.isAlive()) {
            try {
                thread.interrupt();
            } catch (Throwable error) {
                error.printStackTrace();
            }
        }
    }

    public BackgroundRunnable join() {
        if (thread.isAlive()) {
            try {
                thread.join();
            } catch (InterruptedException irq) {
            }
        }
        return this;
    }

    public synchronized void start() {
        if (!started) {
            started = true;
            thread.start();
            Thread.yield();
        }
    }

    public boolean isRunning() {
        return started && thread.isAlive();
    }

    public boolean isStopping() {
        return stopped && isRunning();
    }

}
