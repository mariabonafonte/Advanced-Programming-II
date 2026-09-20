import Concurrency.{log, thread}

import java.util.concurrent.Semaphore
import java.util.concurrent.locks.ReentrantLock
import scala.util.Random

object BarberShop :
  // Synchronization conditions:
  // SC-1: A customer cannot enter in the Barbershop until the chair is empty
  var chairEmpty = true
  // SC-2: A barber cannot start cutting the hair until the chair is in use.
  var chairInUse = false
  // SC-3: A customer cannot exit the BarberShop until the barber has finished to cut his/her hair.
  var finishedCut = false

  val l = new ReentrantLock()
  val emptyCond = l.newCondition()
  val chairCond = l.newCondition()
  val finishCond = l.newCondition()


  def enterCustomer(id: Int) = {
    try {
      l.lock()
      while !chairEmpty do emptyCond.await()
      println(s"Customer $id is sat in the barber shop")
      chairInUse = true
      chairEmpty=false
      chairCond.signal()
      while !finishedCut do finishCond.await()
    }finally{
      l.unlock()
    }
  }

  def exitCustomer(id: Int) = {
    try {
      l.lock()
      println(s"Customer $id exits the barber shop")
      chairInUse=false
      chairEmpty = true
      finishedCut=false
      emptyCond.signal()
    }finally{
      l.unlock()
    }
  }

  def haircut(id: Int) = {
    try {
      l.lock()
      while !chairInUse do chairCond.await()
      println(s"Barber $id is cutting the hair")
      Thread.sleep(Random.nextInt(500))
      finishedCut = true
      finishCond.signal()
    }finally{
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

