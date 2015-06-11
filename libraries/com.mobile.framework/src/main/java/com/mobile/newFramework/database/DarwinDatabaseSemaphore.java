/**
 * 
 */
package com.mobile.newFramework.database;

import java.util.concurrent.Semaphore;

/**
 * This singleton object has the reponsabillity of modulating the access to the applications  
 * 
 * @author nunocastro
 *
 */
public class DarwinDatabaseSemaphore {

    private static class Singleton {
        public static final DarwinDatabaseSemaphore INSTANCE = new DarwinDatabaseSemaphore(); 
    }
    
    private Semaphore geralLock = new Semaphore(1);  
    
    /**
     * 
     */
   private DarwinDatabaseSemaphore() {

   }

   public static DarwinDatabaseSemaphore getInstance() {
       return Singleton.INSTANCE;
   }
 
   
   public void getLock() throws InterruptedException {
       geralLock.acquire();
   }
 
   public void releaseLock() {
       geralLock.release();
   }
   
}
