package org.openhab.binding.hc_gateway.internal;

import rx.functions.Action0;

public class ThreadPoolRunnable implements Runnable {

    // region Fields
    private Thread thread;
    private final Action0 action0;

    // endregion
    // region Constructors
    public ThreadPoolRunnable(final Action0 action0) {
        this.action0 = action0;
    }

    // endregion
    // region InterruptThreadRunnable Implementation
    @Override
    public void interruptThread() {
        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    public void run() {
        thread = Thread.currentThread();
        // Moves the current Thread into the background
//        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        if (!thread.isInterrupted()) {
            action0.call();
        }
    }
}
