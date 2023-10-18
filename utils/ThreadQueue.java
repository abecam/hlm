/*
 * WorkQueue.java
 *
 * Created on 26 de Junho de 2005, 14:46
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

/*
 * This software is distributed under the MIT License
 *
 * Copyright (c) 2005 Alain Becam, Paulo Lopes, Joakim Olsson, and Johan Simonsson - 2005
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this 
 * software and associated documentation files (the "Software"), to deal in the Software 
 * without restriction, including without limitation the rights to use, copy, modify, 
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
 * permit persons to whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies
 *  or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE 
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */

package utils;

import java.util.*;

/**
 *
 * @author Paulo
 */
public final class ThreadQueue {
    
    private final PoolWorker[] threads;
    private final LinkedList queue;
    private int nbThreads=0;
    
    public ThreadQueue(int nThreads) {
        nbThreads=nThreads;
        queue = new LinkedList();
        threads = new PoolWorker[nThreads];
        
        for (int i=0; i<nThreads; i++) {
            threads[i] = new PoolWorker();
            threads[i].start();
        }
    }
    
    public void endWanted()
    {
        for (int i=0; i<nbThreads; i++) {
            threads[i].endWanted();
            threads[i].interrupt();
            //threads[i].notify();
        }
    }
    
    public void execute(Runnable r) {
        synchronized(queue) {
            queue.addLast(r);
            queue.notifyAll();
        }
    }
    
    private final class PoolWorker extends Thread {
        
        private boolean endAsked=false;
        
        public void endWanted()
        {
            endAsked=true;
        }
        
        public void run() {
            Runnable r;
            
            while (!endAsked) {
                synchronized(queue) {
                    while (queue.isEmpty() && !endAsked) {
                        try {
                            queue.wait();
                        } catch (InterruptedException ignored) {
                        }
                    }
                    if (!queue.isEmpty()) // If the queue is empty, it means that the end was asked, nothing to run or to remove
                        r = (Runnable) queue.removeFirst();
                    else
                        r = null; 
                }
                
                if (!endAsked)
                {
                    // If we don't catch RuntimeException,
                    // the pool could leak threads
                    try
                    {
                        if (r != null)
                            r.run();
                    } catch (RuntimeException e)
                    {
                        // You might want to log something here
                    }
                }
            }
        }
    }
}
