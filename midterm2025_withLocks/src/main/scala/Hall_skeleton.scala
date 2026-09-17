import Concurrency.{log, thread}

import java.util.concurrent.*
import java.util.concurrent.locks.ReentrantLock
import scala.util.Random

package object Concurrency {
  def log(msg:String): Unit =
    println(s"${Thread.currentThread().getName}: $msg")

  def thread(body: =>Unit):Thread = {
    val t = new Thread{
      override def run() = body
    }
    t.start()
    t
  }
}

class Hall(capacity: Int):
  private var numStudents = 0

  // CS-Dean1: The Dean does not enter the hall until notified that capacity is exceeded
  var capacityExceed = false
  // CS-Dean2: The Dean waits until the hall is empty to go back to sleep
  var emptyHall = false
  // CS-Student1: A student cannot enter if the Dean is inside
  var canEnter = true //Dean is not Inside if true
  var deanAwake = false
  var waitUntilEmpty = false

  val l = new ReentrantLock()
  val capacityCond = l.newCondition()
  val hallCond = l.newCondition()
  val enterCond = l.newCondition()
  val awakeCond = l.newCondition()
  val waitingCond = l.newCondition()


  def arriveParty(id: Int): Unit =
    try {
      l.lock()
      while !canEnter do enterCond.await()
      numStudents+=1
      log(s"Student $id arrives at the party. Present: $numStudents")

      if(numStudents>=capacity) {
        log(s"                 Student $id notifies the Dean. Present: $numStudents")
        canEnter=false
        capacityExceed=true
        capacityCond.signal()
      }
    }finally {
      l.unlock()
    }


  def leaveParty(id: Int): Unit = {
    try {
      l.lock()
      numStudents-=1
      log(s"Student $id leaves the party. Remaining: $numStudents")
      if(numStudents==0) {
        emptyHall = true
        hallCond.signal()
      }
    }finally {
      l.unlock()
    }
  }


  def goToSleep(): Unit =
    try {
      l.lock()
      while !capacityExceed do capacityCond.await()
      log("Dean: I'm waking up")
      deanAwake = true
      awakeCond.signalAll()
      log("Dean: I enter the hall")
      waitUntilEmpty = true
      waitingCond.signal()

    }finally{
      l.unlock()
    }

  def waitAllOut(): Unit =
    try {
      l.lock()
      log("Dean: I wait for everyone to leave")

      while !emptyHall do hallCond.await()
      emptyHall = false
      capacityExceed=false
      waitUntilEmpty = false
      log("Dean: I go back to sleep")
      deanAwake=false
      canEnter=true
      enterCond.signalAll()
    }finally {
      l.unlock()
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
