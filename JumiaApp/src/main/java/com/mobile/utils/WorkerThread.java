package com.mobile.utils;

import com.mobile.framework.utils.LogTagHelper;

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

    private final String TAG = LogTagHelper.create(WorkerThread.class);
    //Must be volatile because all I/O operations must be done on main memory instead of CPU's cache.
    private volatile boolean mToRun;
    private ConcurrentLinkedQueue<Runnable> mRunnableQueue;

    public WorkerThread(ConcurrentLinkedQueue<Runnable> runnableQueue){
        mToRun = true;
        mRunnableQueue = (runnableQueue != null) ? runnableQueue : new ConcurrentLinkedQueue<Runnable>();
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
                Runnable runnable = mRunnableQueue.poll();
                if(runnable != null){
                    runnable.run();
                }
            }
        }
    }

    public void requestStop(){
        mToRun = false;
    }

    public ConcurrentLinkedQueue<Runnable> getRunnableQueue(){
        return mRunnableQueue;
    }

    public static void executeRunnable(WorkerThread auxThread, Runnable runnable) {

        if(auxThread == null){
            auxThread = new WorkerThread();
            auxThread.start();
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
