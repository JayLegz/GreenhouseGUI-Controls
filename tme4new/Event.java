//: innerclasses/controller/Event.java
// The common methods for any control event.
// From 'Thinking in Java, 4th ed.' (c) Bruce Eckel 2005
// www.BruceEckel.com. See copyright notice in CopyRight.txt.

/***********************************************************************
 * Adapated for COMP308 Java for Programmer, 
 *		SCIS, Athabasca University
 *
 * Assignment: TME3
 * @author: Steve Leung
 * @date  : Oct. 21, 2006
 *
 * Description: Event abstract class
 *
 * Edited By: Jason A Leger
 * Student ID: 3169026
 * Date : November through Dec 6th 2023
 *
 * Special thanks to Steve for helping me understand the event time logic better
 *
 * opening Comments:  This Assignment was Long and Frusterating, It was a good humbling experience
 * regarding organization, planning, and ensuring all pieces of the program worked during all interations
 * of the coding development. Overall I am very pleased with my final product. I really do wish we would have
 * had an online lab (like I would have had access to at a normal university) where the tutor could explain
 * teach, or question my logic as I developed this).  However, Steve L was excellent at answering my chats.
 *
 *
 * Please see TME4 Test plan to see various ways that I have confirmed that the program and GUI run as
 * intended.  At this time I am confident that this code meets all of the TME Requirements, with some additional
 * feature I decided to keep during my personal troubleshooting (copy button, Emerg Shutdown Button)
 *
 * Compile Instrs:  Please ensure tme4.GreenhouseControls.java, tme.GreenhouseGUI.java and all relevant
 * event classes are contained within the same directory. I had numerous issues during the development when
 * I had a parent and child directory system, so I made the decision to combine them into 1.
 *
 */

import java.io.Serializable;

public abstract class Event implements Serializable, Runnable {
  private long eventTime;

  protected final long delayTime;
  private boolean suspended = false;
  private long suspendTime; // Time suspend button pushed

  protected GreenhouseGUI gui;
  private long shutdownTime = 0; // Added shutdownTime field

  public Event(long delayTime) {
    this.delayTime = delayTime;
    start(System.currentTimeMillis() + delayTime);
  }

  // Constructor that accepts GUI reference
  public Event(long delayTime, GreenhouseGUI gui) {
    this.delayTime = delayTime;
    this.gui = gui;
    start(System.currentTimeMillis() + delayTime);
  }

  // Set the GUI
  public void setGui(GreenhouseGUI gui) {
    this.gui = gui;
  }

  public void start(long startTime) { // Allows restarting
    this.eventTime = startTime;
  }

  public void setShutdownTime(long shutdownTime) {
    this.shutdownTime = shutdownTime;
  }

  public long getRemainingTime() {
    return eventTime - System.currentTimeMillis();
  }

  public void adjustEventTime(long elapsedTime) {
    eventTime += (elapsedTime);
  }


  public boolean ready() {
    return System.currentTimeMillis() >= eventTime;
  }

  public abstract void action() throws Exception; // Added throws Exception

  public synchronized void suspend() {
    suspended = true;
    suspendTime = System.currentTimeMillis(); // Record suspension time
  }

  public synchronized void resume() {
    if (suspended) {
      long resumeTime = System.currentTimeMillis();
      long timeElapsed = resumeTime - suspendTime;
      eventTime += timeElapsed; // Adjust event time based on elapsed suspension time
      suspended = false;
      notify(); // Notifies thread to resume execution
    }
  }

  @Override
  public void run() {
    try {

      //Important step to ensure Events are Executing as expected, added after Restore was firing properly
      long waitTime = eventTime - System.currentTimeMillis();
      if (waitTime > 0) {
        Thread.sleep(waitTime);
      }

      while (!Thread.currentThread().isInterrupted()) {
        synchronized (this) {
          while (suspended) {
            wait();
          }
        }
        if (ready()) {
          action();
          break; // Exit after action is performed
        }
        Thread.sleep(delayTime, 100); // Checks every 100ms if events ready
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (Exception e) {
    }
  }
}


