package lab7

import scala.util.Random
import Concurrency.*

import java.util.concurrent.locks.ReentrantLock

object Couples {
  private var count = 0
  private var manPresent = false
  private var womanPresent = false
  private var waitingForPartner = true

  //usng locks
  val l = new ReentrantLock()
  val manCond = l.newCondition()
  val womanCond = l.newCondition()
  val waitCond = l.newCondition()

  def manArrives(id: Int): Unit =  {
    try {
      l.lock()
      while !manPresent do manCond.await()
      manPresent=true
      log(s"Man $id wants a girlfriend")
      if (womanPresent){
        waitCond.signal()
      }else{
        while (!womanPresent) do waitCond.await()
      }

      log("A couple has been formed!!!")
      manPresent = false
      manCond.signal()
    }finally {
      l.unlock()
    }



  }

  def womanArrives(id: Int): Unit =  {
    try {
      l.lock()
      while !womanPresent do womanCond.await()
      womanPresent = true
      log(s"Woman $id wants a boyfriend")
      if (manPresent) {
        waitCond.signal()
      } else {
        while (!manPresent) do waitCond.await()
      }

      log("A couple has been formed!!!")
      womanPresent = false
      womanCond.signal()
    } finally {
      l.unlock()
    }

  }

}

object Exercise3 {

  def main(args: Array[String]): Unit = {
    val numPairs = 10
    val women = new Array[Thread](numPairs)
    val men = new Array[Thread](numPairs)

    for (i <- women.indices)
      women(i) = thread {
        Couples.womanArrives(i)
      }

    for (i <- men.indices)
      men(i) = thread {
        Couples.manArrives(i)
      }
  }

}
