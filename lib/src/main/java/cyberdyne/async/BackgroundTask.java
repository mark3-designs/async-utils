package cyberdyne.async;

import java.time.Duration;

public abstract class BackgroundTask {

    private final Duration period;
    private boolean started = false;
    private boolean stopped = false;
    final private long cycle; // = 1000L;

    private final Thread shutdownHook = new Thread(() -> terminate());

    private Thread thread = new Thread() {
        @Override
        public void run() {
            while (!stopped) {
                try {
                    Thread.sleep(cycle);
                    execute();
                } catch (InterruptedException irq) {
                } catch (Throwable error) {
                    error.printStackTrace();
                }
            }
        }
    };

    public BackgroundTask(Duration period) {
        this.period = period;
        this.cycle = period.toMillis();
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    public void stop() {
        terminate();
        Thread.yield();
    }

    private void terminate() {
        this.stopped = true;
        try {
            thread.interrupt();
        } catch (Throwable error) {
            error.printStackTrace();
        }
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
