package cyberdyne.async;

import java.time.Duration;
import java.util.Random;

public abstract class BackgroundTask {

    private final Duration period;
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
                    if (System.currentTimeMillis() >= next) {
                        execute();
                        next += cycle;
                    }
                    Thread.sleep(cycle/5);
                } catch (InterruptedException irq) {
                } catch (Throwable error) {
                    error.printStackTrace();
                }
            }
        }
    };

    public BackgroundTask(Duration period) {
        this(period, false);
    }

    public BackgroundTask(Duration period, boolean quickStart) {
        this.period = period;
        this.cycle = period.toMillis();
        this.next = System.currentTimeMillis() + (quickStart ? (1 + new Random().nextInt(100)) * 10 : cycle);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    public BackgroundTask stop() {
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

    public BackgroundTask join() {
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

    public abstract void execute();

}
