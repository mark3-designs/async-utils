package cyberdyne.async;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TestBackgroundTask {

    @Test
    public void test() {

        var duration = Duration.ofSeconds(1);
        var executed = new AtomicBoolean(false);
        var execTimes = new AtomicInteger(0);

        BackgroundTask task = new BackgroundTask(duration) {
            @Override
            public void execute() {
                executed.set(true);
                execTimes.incrementAndGet();
            }
        };

        task.start();

        Assertions.assertTrue(task.isRunning());

        try {
            Thread.sleep(5500);
        } catch (InterruptedException ignore) {

        }

        long stopRequested = System.currentTimeMillis();

        task.stop();

        Assertions.assertTrue(task.isStopping(), "isStopping != True");
        while (task.isStopping()) {
            Thread.yield();
        }

        long threadStopped = System.currentTimeMillis();
        System.out.println("took "+ (threadStopped - stopRequested) +"ms to stop.");

        Assertions.assertTrue(threadStopped - stopRequested < 5, "stop within 5ms");
        Assertions.assertFalse(task.isRunning(), "isRunning != False");

        Assertions.assertTrue(executed.get());
        Assertions.assertEquals(5, execTimes.get());

    }

}
