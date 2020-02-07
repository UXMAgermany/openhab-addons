/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.hc_gateway.internal;

import rx.functions.Action0;
/**
 * The {@link ThreadPoolRunnable} is a runnable for thread pools.
 *
 * @author UXMA - Initial contribution
 */
public class ThreadPoolRunnable implements Runnable {

    private Thread thread;
    private final Action0 action0;

    public ThreadPoolRunnable(final Action0 action0) {
        this.action0 = action0;
    }

    public void interruptThread() {
        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    public void run() {
        thread = Thread.currentThread();
        if (!thread.isInterrupted()) {
            action0.call();
        }
    }
}
