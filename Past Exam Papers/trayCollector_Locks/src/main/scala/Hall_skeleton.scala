import Concurrency.{log, thread}

import java.util.concurrent.*
import java.util.concurrent.locks.ReentrantLock
import scala.util.Random


class Hall(capacity: Int):
  private var numStudents = 0

  // CS-Dean1: The Dean does not enter the hall until notified that capacity is exceeded
  // CS-Dean2: The Dean waits until the hall is empty to go back to sleep
  // CS-Student1: A student cannot enter if the Dean is inside

  var emptyHall = true
  var callDean = false
  var deanInside = false
  var exitHall = false

  //using locks
  val l = ReentrantLock()
  val canEnter = l.newCondition()
  val haveToExit = l.newCondition()
  val deanIsHere = l.newCondition()
  val deanIsCalled = l.newCondition()


  def arriveParty(id: Int): Unit = {
    try{
      l.lock()
      while !emptyHall do canEnter.await()
      numStudents+=1
      log(s"Student $id arrives at the party. Present: $numStudents")
        if(numStudents==capacity) {
         log(s"                 Student $id notifies the Dean. Present: $numStudents")
          emptyHall = false
          callDean = true
          deanIsCalled.signal()
        }
      } finally {
        l.unlock()
      }
  }



  def leaveParty(id: Int): Unit = {
    try {
      l.lock()
      while !exitHall do haveToExit.await()
      numStudents-=1
      log(s"Student $id leaves the party. Remaining: $numStudents")
      if(numStudents==0) {
        exitHall = false
      }

    }finally {
      l.unlock()
    }
  }


  def goToSleep(): Unit = {
    try {
      l.lock()
      while !callDean do deanIsCalled.await()
      log("Dean: I'm waking up")
      callDean = false
      log("Dean: I enter the hall")
      deanInside = true
      deanIsHere.signal()
    } finally {
      l.unlock()
    }
  }

  def waitAllOut(): Unit = {
    try {
      l.lock()
      while !deanInside do deanIsHere.await()
      log("Dean: I wait for everyone to leave")
      exitHall=true
      haveToExit.signalAll()

      while(numStudents>0) do canEnter.await()
      log("Dean: I go back to sleep")
      deanInside = false
      emptyHall=true
      canEnter.signal()
    } finally {
      l.unlock()
    }
  }



object ResidenceApp:

  def main(args: Array[String]): Unit =
    val totalStudents = 20
    val maxCapacity = 5
    val partiesPerStudent = 1
    val hall = Hall(maxCapacity)
    val students = Array.ofDim[Thread](totalStudents)

    for i <- students.indices do
      students(i) = thread {
        for _ <- 0 until partiesPerStudent do
          Thread.sleep(Random.nextInt(700))
          hall.arriveParty(i)
          Thread.sleep(100)
          hall.leaveParty(i)
          Thread.sleep(700)
      }

    val dean = thread {
      var finished = false
      while !finished && !Thread.interrupted() do
        try
          Thread.sleep(Random.nextInt(200))
          hall.goToSleep()
          hall.waitAllOut()
        catch
          case _: InterruptedException => finished = true
    }

    students.foreach(_.join())
    dean.interrupt()
    dean.join()
    log("All students and the Dean have gone to sleep")
