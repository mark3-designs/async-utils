package cyberdyne.async;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TestBackgroundRunnable {

    @Test
    public void test() {

        Duration duration = Duration.ofSeconds(1);
        AtomicBoolean executed = new AtomicBoolean(false);
        AtomicInteger execTimes = new AtomicInteger(0);

        BackgroundRunnable task = new BackgroundRunnable(duration, () -> {
                executed.set(true);
                execTimes.incrementAndGet();
            }, false
        );

        task.start();

        Assertions.assertTrue(task.isRunning());

        try {
            Thread.sleep(5500);
        } catch (InterruptedException ignore) {

        }

        long stopRequested = System.currentTimeMillis();

        task.stop();

        Assertions.assertTrue(task.isStopping(), "isStopping != True");

        task.join();

        long threadStopped = System.currentTimeMillis();
        System.out.println("took "+ (threadStopped - stopRequested) +"ms to stop.");

        Assertions.assertTrue(threadStopped - stopRequested < 5, "expected stop within 5ms, but took "+ (threadStopped - stopRequested));
        Assertions.assertFalse(task.isRunning(), "isRunning != False");

        Assertions.assertTrue(executed.get(), "Runnable not executed.");
        Assertions.assertEquals(5, execTimes.get());

    }

}
