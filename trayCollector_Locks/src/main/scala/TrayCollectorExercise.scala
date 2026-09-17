import Concurrency.thread

import java.util.concurrent.Semaphore
import java.util.concurrent.locks.ReentrantLock
import scala.util.Random

object Cafeteria :
  // Synchronization conditions:
  // SC-1: A student cannot put a tray if there is no empty station.
  var empty = true
  // SC-2: The worker cannot take a tray if there is no one available.
  var available= false


  //for locks
  val l = new ReentrantLock()
  val emptyCond = l.newCondition()
  val availableCond = l.newCondition()

  private val NUM_STATIONS = 4
  var ocupied = 0

  def takeTray = {
    try {
      l.lock()
      // Worker flushed the content
      while !available do availableCond.await()
      ocupied -=1
      println(s"Worker has taken from a tray")

      empty = true
      emptyCond.signal()

      if(ocupied==0){
        available=false
      }

      Thread.sleep(Random.nextInt(300))
    } finally {
      l.unlock()
    }
  }



  def putTray(id: Int) = {
    try {
      l.lock()
      while !empty do emptyCond.await()

      ocupied+=1
      println(s"Student $id puts in a tray")
      available= true
      availableCond.signal()

      if (ocupied==NUM_STATIONS){
        empty=false
      }

    } finally {
      l.unlock()
    }
  }


object MainCafeteria :
  private val NUM_STUDENTS = 12
  def main(args: Array[String]) =
    val students = new Array[Thread](NUM_STUDENTS)
    for (i <- students.indices)
      students(i) = thread {
        // Student eats
        Thread.sleep(Random.nextInt(2000))
        Cafeteria.putTray(i)
      }
    val worker = thread {
        for(i <- 1 to NUM_STUDENTS)
          Cafeteria.takeTray
          // Worker rests a bit
          Thread.sleep(Random.nextInt(200))
      }
    for (i <- students.indices)
      students(i).join
    worker.join
    println("End Of Program")