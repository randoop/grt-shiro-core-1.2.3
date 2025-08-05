/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.session.mgt;

import org.checkerframework.dataflow.qual.Impure;
import org.checkerframework.dataflow.qual.Pure;
import org.checkerframework.dataflow.qual.SideEffectFree;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * SessionValidationScheduler implementation that uses a
 * {@link ScheduledExecutorService} to call {@link ValidatingSessionManager#validateSessions()} every
 * <em>{@link #getInterval interval}</em> milliseconds.
 *
 * @since 0.9
 */
public class ExecutorServiceSessionValidationScheduler implements SessionValidationScheduler, Runnable {

    //TODO - complete JavaDoc

    /** Private internal log instance. */
    private static final Logger log = LoggerFactory.getLogger(ExecutorServiceSessionValidationScheduler.class);

    ValidatingSessionManager sessionManager;
    private ScheduledExecutorService service;
    private long interval = DefaultSessionManager.DEFAULT_SESSION_VALIDATION_INTERVAL;
    private boolean enabled = false;

    @SideEffectFree
    public ExecutorServiceSessionValidationScheduler() {
        super();
    }

    @SideEffectFree
    public ExecutorServiceSessionValidationScheduler(ValidatingSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Pure
    public ValidatingSessionManager getSessionManager() {
        return sessionManager;
    }

    @Impure
    public void setSessionManager(ValidatingSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Pure
    public long getInterval() {
        return interval;
    }

    @Impure
    public void setInterval(long interval) {
        this.interval = interval;
    }

    @Pure
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Creates a single thread {@link ScheduledExecutorService} to validate sessions at fixed intervals 
     * and enables this scheduler. The executor is created as a daemon thread to allow JVM to shut down
     */
    //TODO Implement an integration test to test for jvm exit as part of the standalone example
    // (so we don't have to change the unit test execution model for the core module)
    @Impure
    public void enableSessionValidation() {
        if (this.interval > 0l) {
            this.service = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {  
	        @Impure
	        public Thread newThread(Runnable r) {  
	            Thread thread = new Thread(r);  
	            thread.setDaemon(true);  
	            return thread;  
                }  
            });                  
            this.service.scheduleAtFixedRate(this, interval, interval, TimeUnit.MILLISECONDS);
            this.enabled = true;
        }
    }

    @Impure
    public void run() {
        if (log.isDebugEnabled()) {
            log.debug("Executing session validation...");
        }
        long startTime = System.currentTimeMillis();
        this.sessionManager.validateSessions();
        long stopTime = System.currentTimeMillis();
        if (log.isDebugEnabled()) {
            log.debug("Session validation completed successfully in " + (stopTime - startTime) + " milliseconds.");
        }
    }

    @Impure
    public void disableSessionValidation() {
        this.service.shutdownNow();
        this.enabled = false;
    }
}
