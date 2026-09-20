import scala.util.Random
import Concurrency.*

import java.util.concurrent.locks.ReentrantLock

object Kindergarten:

  private var numBabies = 0
  private var numAdults = 0

  //baby who wants to enter - condition including himself
  var babyCanEnter = false
  //adult who wants to leave - condition excluding himself
  var adultCanLeave = false

  //using locks
  val l = new ReentrantLock()
  val babyEnterCond = l.newCondition()
  val adultLeaveCond = l.newCondition()

  def babyEnters(id: Int): Unit =  {
    try {
      l.lock()
      while !babyCanEnter do babyEnterCond.await()
      numBabies+=1
      log(s"Baby $id entered. Babies=$numBabies, Adults=$numAdults — OK=${numBabies <= 3 * numAdults}")
      if(numBabies >= 3 * numAdults){
        babyCanEnter = false
        adultCanLeave = true
        adultLeaveCond.signal()
      }
    } finally {
      l.unlock()
    }
  }
  
  def babyLeaves(id: Int): Unit =  {
    try {
      l.lock()
      numBabies -= 1
      log(s"Baby $id left. Babies=$numBabies, Adults=$numAdults — OK=${numBabies <= 3 * numAdults}")
    } finally {
      l.unlock()
    }
  }

  def adultEnters(id: Int): Unit =  {
    try {
      l.lock()
      numAdults+=1
      log(s"Adult $id entered. Babies=$numBabies, Adults=$numAdults — OK=${numBabies <= 3 * numAdults}")
      if(numBabies <= 3 * numAdults){
        babyCanEnter = true
        babyEnterCond.signal()
      }
    } finally {
      l.unlock()
    }
  }

  def adultLeaves(id: Int): Unit =  {
    try {
      l.lock()
      while !adultCanLeave do adultLeaveCond.await()
      numAdults-=1
      log(s"Adult $id left. Babies=$numBabies, Adults=$numAdults — OK=${numBabies <= 3 * numAdults}")
    } finally {
      l.unlock()
    }
  }

object Exercise7:

  def main(args: Array[String]): Unit =
    val totalBabies = 15
    val totalAdults = 5

    val babies = Array.tabulate(totalBabies)(i =>
      thread:
        while true do
          Thread.sleep(Random.nextInt(700))
          Kindergarten.babyEnters(i)
          Thread.sleep(Random.nextInt(500))
          Kindergarten.babyLeaves(i)
    )

    val adults = Array.tabulate(totalAdults)(i =>
      thread:
        while true do
          Thread.sleep(Random.nextInt(700))
          Kindergarten.adultEnters(i)
          Thread.sleep(Random.nextInt(500))
          Kindergarten.adultLeaves(i)
    )
