package lab7

import scala.util.Random
import Concurrency._

class Tray(portionsPerCake: Int):

  private var portions = 0
  private var hasPortions = false
  private var isEmpty = true

  def wantPortion(id: Int): Unit = synchronized {

    log(s"Child $id took a portion. Remaining: $portions")


  }

  def bakeCake(): Unit = synchronized {

    log("The baker places a new cake with fresh portions.")

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

