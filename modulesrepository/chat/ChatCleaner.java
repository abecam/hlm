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

package modulesrepository.chat;

/**
 * Cleaner is a thread that will run in small time periods to remove zombie players from the system. Zombie players are player who did not end their sessions with the appropriate messages. It can happend if the network connection link is down or by other issues.
 *
 * @author Paulo <pmlopes@gmail.com>
 * @version 1.0
 */
public class ChatCleaner extends Thread {

    // control for the main loop
    private boolean run = true;

    /**
     * Creates a new Cleaner thread with the daemon flag set to true.
     */
    public ChatCleaner() {
        super.setDaemon(true);
        run = true;
    }

    /**
     * When invoked the thread will exit on the next iteraction
     */
    public void exitCleaner() {
        this.run = false;
    }

    /**
     * Perform the cleaning methods for the rest of the time.
     */
    public void run() {
        while(run) {
            Player.cleanUp();
            Chatroom.cleanUp();
            try {
                Thread.sleep(Player.SESSION_TIMEOUT / 2);
            } catch(InterruptedException ie) {
            }
        }
    }
}
