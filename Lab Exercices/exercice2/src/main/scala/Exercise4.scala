import scala.util.Random
import Concurrency.*

import java.util.concurrent.locks.ReentrantLock

class Car(capacity: Int) extends Thread {
  // CS-passenger1: if the car is full, a passenger cannot board until the trip is over
  // and the current passengers have gotten off
  // CS-passenger2: a passenger in the car cannot get off until the ride end
  // CS-car: the car waits until C passengers have boarded before starting a ride

  private var numPassengers = 0
  var carFull = false
  var canEnter = true
  var canExit = false


  //using locks
  val l = new ReentrantLock()
  val enterCond = l.newCondition()
  val exitCond = l.newCondition()
  val fullCond = l.newCondition()

  def takeRide(id: Int): Unit = { //synchronized may be not given
    // passenger id wants to take a ride on the roller coaster
    try {
      l.lock()
      while !canEnter do enterCond.await()
      numPassengers+=1
      log(s"Passenger $id boards the car. Total: $numPassengers passengers.")
      if(numPassengers==capacity){
        canEnter = false
        carFull = true
        fullCond.signal()
      }

      while !canExit do exitCond.await()
      numPassengers-=1
      log(s"Passenger $id exits the car. Remaining: $numPassengers passengers.")
      if(numPassengers==0){
        canExit=false
        canEnter=true
        enterCond.signal()
      }
    } finally {
      l.unlock()
    }
  }

  def waitUntilFull(): Unit =  {
    // car waits until it is full to start a ride
    try {
      l.lock()
      while !carFull do fullCond.await()
      log("        Car is full!!! Starting the ride....")
      carFull=false
    }finally {
      l.unlock()
    }
  }

  def endRide(): Unit =  {
    // car signals that the ride has ended
    try {
      l.lock()
      log("        Ride finished... :-(")
      canExit = true
      exitCond.signalAll()
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

}
