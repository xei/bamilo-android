package com.bamilo.android.appmodule.bamiloapp.utils;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/03/30
 *
 * @see <a href="http://tutorials.jenkov.com/java-concurrency/volatile.html">volatile</a>
 */
public class WorkerThread extends Thread{

    private static final String TAG = WorkerThread.class.getSimpleName();
    //Must be volatile because all R/W operations must be done on main memory instead of CPU's cache.
    private volatile boolean mToStop;
    private volatile boolean mStopRequested;
    private ConcurrentLinkedQueue<Runnable> mRunnableQueue;

    /**
     * Creates a new WorkerThread.
     *
     * @param runnableQueue If queue is null, thread will initialize a new one.
     */
    public WorkerThread(ConcurrentLinkedQueue<Runnable> runnableQueue){
        mToStop = false;
        mRunnableQueue = (runnableQueue != null) ? runnableQueue : new ConcurrentLinkedQueue<>();
        setName(TAG+"_"+getId());
    }

    public WorkerThread(){
        this(null);
    }

    @Override
    public void run() {
        while(isToRun()){
            if(mRunnableQueue.isEmpty()) {
                // If list is empty, then thread will interrupt and wait for a new job to be done
                try {
                    synchronized (this){
                        wait();
                    }
                } catch (InterruptedException ignored) {
                } catch(IllegalMonitorStateException ignored){
                }

            } else {
                Runnable runnable = mRunnableQueue.poll();
                if(runnable != null){
                    runnable.run();
                }
            }
        }
    }

    /**
     *
     * @return True if thread will continue running, false otherwise
     */
    private boolean isToRun(){
        return mStopRequested ? !mRunnableQueue.isEmpty() : !mToStop;
    }

    /**
     * Print the queue.
     */
    private void printQueue() {
        Iterator<Runnable> itr= mRunnableQueue.iterator();
        StringBuilder string = new StringBuilder();
        while(itr.hasNext()){
            string.append(" ").append(itr.next().toString());
        }
    }

    /**
     *  Stop the thread by natural way. The thread may stop with pending jobs.
     */
    public void end(){
        mToStop = true;
        try {
            //Notifies thread to terminate flow
            synchronized (this) {
                notify();
            }
        }catch(IllegalMonitorStateException ignored){
        }
    }

    /**
     *  Request the thread to stop by natural way. The thread will stop only after pending jobs are all done.
     */
    public void requestStop(){
        mStopRequested = true;
    }

    public ConcurrentLinkedQueue<Runnable> getRunnableQueue(){
        return mRunnableQueue;
    }

    /**
     * Execute the runnable on the thread.
     *
     * @param auxThread
     * @param runnable
     *
     * @throws IllegalStateException If auxThread is null.
     */
    public static void executeRunnable(WorkerThread auxThread, Runnable runnable) {

        if(auxThread == null){
            throw new IllegalStateException("Thread must be not null");
        }

        ConcurrentLinkedQueue<Runnable> runnableQueue = auxThread.getRunnableQueue();
        //Adds runnable to queue
        runnableQueue.add(runnable);
        try {
            //Notifies thread that there are jobs to be done
            synchronized (auxThread) {
                auxThread.notify();
            }
        }catch(IllegalMonitorStateException ignored){
        }
    }
}