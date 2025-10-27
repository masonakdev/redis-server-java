package dev.masonak.redis.concurrency;

public class Waiter {

    public synchronized void doWait(long timeoutMillis) {
        try {
            if (timeoutMillis == 0) {
                wait();
            } else {
                wait(timeoutMillis);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void doNotify() {
        notify();
    }

}