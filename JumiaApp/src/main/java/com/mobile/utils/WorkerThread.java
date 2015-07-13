package com.mobile.utils;

import com.mobile.newFramework.utils.output.Print;

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
        mRunnableQueue = (runnableQueue != null) ? runnableQueue : new ConcurrentLinkedQueue<Runnable>();
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
                } catch (InterruptedException e) {
                    Print.e(TAG, "InterruptedException on worker thread: wait()");
                } catch(IllegalMonitorStateException ex){
                    Print.e(TAG, "IllegalMonitorStateException on worker thread: wait()");
                }

            } else {
//                print();
                //If list is not empty, task will be removed from it and executed
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
        String string = "";
        while(itr.hasNext()){
            string += " " + itr.next().toString();
        }
        Print.e(TAG, "Queue: " + string);
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
        }catch(IllegalMonitorStateException ex){
            Print.e(TAG, "IllegalMonitorStateException: notify()");
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
     * @throws java.lang.IllegalStateException If auxThread is null.
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
        }catch(IllegalMonitorStateException ex){
            Print.e(TAG, "IllegalMonitorStateException: notify()");
        }

    }
}