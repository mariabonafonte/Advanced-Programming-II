import scala.util.Random
import Concurrency._
import java.util.concurrent.locks._

/*class Car(capacity: Int) extends Thread {
  // CS-passenger1: if the car is full, a passenger cannot board until the trip is over
  // and the current passengers have gotten off
  // CS-passenger2: a passenger in the car cannot get off until the ride ends
  // CS-car: the car waits until C passengers have boarded before starting a ride

  private var numPassengers = 0
  private var startTrip = false //isfull
  private var inGateOpen = true
  private var exitGateOpen = false  //tripEnded

  //special for locks
  val l = new ReentrantLock() //lock
  val inCond = l.newCondition()
  val outCond = l.newCondition()
  val startCond = l.newCondition()

  def takeRide(id: Int): Unit =  {
    try{
      l.lock()
      while !inGateOpen do inCond.await() //dont forget it is AWAIT not WAIT
      numPassengers += 1
      log(s"Passenger $id boards the car. Total: $numPassengers passengers.")

      if numPassengers == capacity then {
        inGateOpen = false
        startTrip = true
        startCond.signal()
      }

      while !exitGateOpen do wait()
      numPassengers -= 1
      log(s"Passenger $id exits the car. Remaining: $numPassengers passengers.")
      if numPassengers == 0 then {
        exitGateOpen = false
        inGateOpen = true
        inCond.signal()
      } finally {
        l.unlock()
      }
    }
  }

  def waitUntilFull(): Unit = {
    try {
      l.lock()
      while !startTrip do startCond.await()
      log("        Car is full!!! Starting the ride....")
      startTrip = false
    }finally{
      l.unlock()
    }

  }

  def endRide(): Unit =  {
    try {
      l.lock()
      log("        Ride finished... :-(")
      exitGateOpen = true
      outCond.signalAll()
    }finally {
      l.unlock()
    }
  }

  override def run(): Unit = {
    while (true) {
      waitUntilFull()
      Thread.sleep(Random.nextInt(Random.nextInt(500))) // simulate the ride
      endRide()
    }
  }

}

object Exercise4 {
  def main(args: Array[String]): Unit = {
    val car = new Car(5)
    val passengers = new Array[Thread](20)

    car.start()

    for (i <- passengers.indices)
      passengers(i) = thread {
        Thread.sleep(Random.nextInt(500)) // simulate walking around the funfair
        car.takeRide(i)
      }
  }

}*/


