import scala.util.Random
import Concurrency.*

import java.util.concurrent.locks.ReentrantLock

class Tray(portionsPerCake: Int):

  private var portions = 0
  private var hasPortions = false
  private var isEmpty = true

  //using locks
  val l = new ReentrantLock()
  val emptyCond = l.newCondition()
  val availableCond = l.newCondition()

  def wantPortion(id: Int): Unit =  {
    try {
      l.lock()
      while !hasPortions do availableCond.await()
      portions -=1
      log(s"Child $id took a portion. Remaining: $portions")
      if(portions==0){
        isEmpty = true
        hasPortions = false
        emptyCond.signal()
      }
    } finally {
      l.unlock()
    }

  }

  def bakeCake(): Unit =  {
    try {
      l.lock()
      while !isEmpty do emptyCond.await()
      log("The baker places a new cake with fresh portions.")
      portions =portionsPerCake
      isEmpty=false
      hasPortions=true
      availableCond.signal()
    } finally {
      l.unlock()
    }
  }

object Exercise6:

  def main(args: Array[String]): Unit =
    val portionsPerCake = 5
    val numChildren = 10

    val tray = new Tray(portionsPerCake)
    val children = Array.tabulate(numChildren)(i =>
      thread:
        while true do
          Thread.sleep(Random.nextInt(500))
          tray.wantPortion(i)
    )

    val baker = thread:
      while true do
        Thread.sleep(Random.nextInt(100))
        tray.bakeCake()

