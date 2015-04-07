package com.mobile.utils;

import com.mobile.framework.utils.LogTagHelper;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.akquinet.android.androlog.Log;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/03/30
 */
public class WorkerThread extends Thread{

    private static final String TAG = LogTagHelper.create(WorkerThread.class);
    //Must be volatile because all I/O operations must be done on main memory instead of CPU's cache.
    private volatile boolean mToRun;
    private ConcurrentLinkedQueue<Runnable> mRunnableQueue;

    public WorkerThread(ConcurrentLinkedQueue<Runnable> runnableQueue){
        mToRun = true;
        mRunnableQueue = (runnableQueue != null) ? runnableQueue : new ConcurrentLinkedQueue<Runnable>();
        setName("WorkerThread_"+getId());
    }

    public WorkerThread(){
        this(null);
    }

    @Override
    public void run() {
        while(mToRun){
            if(mRunnableQueue.isEmpty()) {
                try {
                    synchronized (this){
                        wait();
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "InterruptedException on worker thread: wait()");
                } catch(IllegalMonitorStateException ex){
                    Log.e(TAG, "IllegalMonitorStateException on worker thread: wait()");
                }

            } else {
                print();
                Runnable runnable = mRunnableQueue.poll();
                if(runnable != null){
                    runnable.run();
                }
            }
        }
    }

    private void print() {
        Iterator<Runnable> itr= mRunnableQueue.iterator();
        String string = "";
        while(itr.hasNext()){
            string += " " + itr.next().toString();
        }
        Log.e(TAG, "Queue: " + string);
    }

    public void requestStop(){
        mToRun = false;
    }

    public ConcurrentLinkedQueue<Runnable> getRunnableQueue(){
        return mRunnableQueue;
    }

    public static void executeRunnable(WorkerThread auxThread, Runnable runnable) {

        if(auxThread == null){
            throw new IllegalStateException("Thread must be not null");
        }

        ConcurrentLinkedQueue<Runnable> runnables = auxThread.getRunnableQueue();

        runnables.add(runnable);
        try {
            synchronized (auxThread) {
                auxThread.notify();
            }
        }catch(IllegalMonitorStateException ex){
            Log.e(auxThread.TAG, "IllegalMonitorStateException: notify()");
        }

    }
}