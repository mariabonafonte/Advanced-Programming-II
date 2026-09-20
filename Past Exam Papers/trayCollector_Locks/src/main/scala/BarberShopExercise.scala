import Concurrency.{log, thread}

import java.util.concurrent.Semaphore
import java.util.concurrent.locks.ReentrantLock
import scala.util.Random

object BarberShop :
  // Synchronization conditions:
  // SC-1: A customer cannot enter in the Barbershop until the chair is empty
  var emptyChair = true
  // SC-2: A barber cannot start cutting the hair until the chair is in use.
  var barberStart = false
  // SC-3: A customer cannot exit the BarberShop until the barber has finished to cut his/her hair.
  var canExit = false

  //using locks
  val l = new ReentrantLock()
  val enterCond = l.newCondition()
  val startCond = l.newCondition()
  val exitCond = l.newCondition()



  def enterCustomer(id: Int) = {
    try {
      l.lock()
      while !emptyChair do enterCond.await()
      println(s"Customer $id is sat in the barber shop")
      emptyChair = false
      barberStart = true
      startCond.signal()
    } finally {
      l.unlock()
    }
  }

  def exitCustomer(id: Int) = {
    try {
      l.lock()
      while !canExit do exitCond.await()
      println(s"Customer $id exits the barber shop")
      canExit = false
      emptyChair = true
      enterCond.signal()
    } finally {
      l.unlock()
    }

  }

  def haircut(id: Int) = {
    try {
      l.lock()
      while !barberStart do startCond.await()
      println(s"Barber $id is cutting the hair")
      barberStart = false
      canExit = true
      exitCond.signal()
      Thread.sleep(Random.nextInt(500))
    } finally {
      l.unlock()
    }
  }

object MainBarberShop :
  private val NUM_BARBERS = 2
  private val NUM_CUSTOMERS = 10
  def main(args: Array[String]) =
    val barbers = new Array[Thread](NUM_BARBERS)
    val customers = new Array[Thread](NUM_CUSTOMERS)
    for (i <- barbers.indices)
      barbers(i) = thread {
        while(true)
          BarberShop.haircut(i)
          // Barber rests a bit
          Thread.sleep(Random.nextInt(500))
      }
    for (i <- customers.indices)
      customers(i) = thread {
        while(true)
          BarberShop.enterCustomer(i)
          BarberShop.exitCustomer(i)
          // Customer walks around
          Thread.sleep(Random.nextInt(5000))
      }

