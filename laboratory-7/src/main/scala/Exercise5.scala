package lab7

import scala.util.Random
import Concurrency._
import java.util.concurrent.locks.*

object Boat:

  private val l = new ReentrantLock()
  private val ipCond = l.newCondition()
  private val andCond = l.newCondition()
  private val startCond = l.newCondition()

  private var nIPhone = 0
  private var nAndroid = 0
  private var iphoneGateOpen = true
  private var androidGateOpen = true
  private var tripStarted = false

  def tripIPhone(id: Int): Unit = {
    try {
      l.lock()
      while (!iphoneGateOpen) ipCond.await()
      nIPhone+=1
      log(s"IPhone student $id boards the boat. Current: iPhone=$nIPhone, Android=$nAndroid")
      if (nIPhone==3) androidGateOpen = false
      if(nAndroid>0 && nIPhone==2) iphoneGateOpen= false
      if(nAndroid==2) androidGateOpen=false

      if(nAndroid+nIPhone ==4) {
        tripStarted = true
        androidGateOpen=false
        iphoneGateOpen=true
        startCond.signalAll()
      }

      while(!tripStarted) startCond.await()
      if(nIPhone+nAndroid==4)
        log("Starting the trip....")
        Thread.sleep(Random.nextInt(200))
        log("Trip finished....")

      nIPhone -=1
      log(s"IPhone student $id disembarks. Remaining: iPhone=$nIPhone, Android=$nAndroid")
      if(nIPhone + nAndroid ==0){
        tripStarted=false
        iphoneGateOpen= true
        androidGateOpen = true
        ipCond.signalAll()
        andCond.signalAll()
      }

    }finally {
      l.unlock()
    }
  }

  def tripAndroid(id: Int): Unit =  {
    try {
      l.lock()
      while (!androidGateOpen) ipCond.await()
      nAndroid += 1
      log(s"Android student $id boards the boat. Current: iPhone=$nIPhone, Android=$nAndroid")
      if (nAndroid == 3) iphoneGateOpen = false
      if (nIPhone > 0 && nAndroid == 2) androidGateOpen = false
      if (nIPhone == 2) iphoneGateOpen = false

      if (nAndroid + nIPhone == 4) {
        tripStarted = true
        androidGateOpen = true
        iphoneGateOpen = false
        startCond.signalAll()
      }

      while (!tripStarted) startCond.await()
      if (nIPhone + nAndroid == 4)
        log("Starting the trip....")
        Thread.sleep(Random.nextInt(200))
        log("Trip finished....")

      nAndroid -= 1
      log(s"Android student $id disembarks. Remaining: iPhone=$nIPhone, Android=$nAndroid")
      if (nIPhone + nAndroid == 0) {
        tripStarted = false
        iphoneGateOpen = true
        androidGateOpen = true
        ipCond.signalAll()
        andCond.signalAll()
      }

    } finally {
      l.unlock()
    }

  }

object Exercise5:

  def main(args: Array[String]): Unit =
    val totalIPhones = 10
    val totalAndroids = 10

    val iphones = Array.tabulate(totalIPhones)(i =>
      thread:
        Thread.sleep(Random.nextInt(400))
        Boat.tripIPhone(i)
    )

    val androids = Array.tabulate(totalAndroids)(i =>
      thread:
        Thread.sleep(Random.nextInt(400))
        Boat.tripAndroid(i)
    )

