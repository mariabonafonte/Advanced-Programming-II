import Concurrency.*

import java.util.concurrent.Semaphore
import java.util.concurrent.locks.ReentrantLock
import scala.collection.mutable.ArrayBuffer
import scala.util.Random
/*
Part A:
•	SC-Truck1: a Truck must wait until the platform is empty to be loaded.
•	SC-Truck2: a Truck in the platform must wait to be full loaded before leaving the platform.
•	SC-Forklift1: before loading a box, a Forklift must wait for a Truck in the platform and, also, if any other Forklift is already loading another box.
*/

var emptyPlatform = true
var fullLoaded = false
var canLoadBox = false

//using locks
val l = new ReentrantLock()
val emptyCond = l.newCondition()
val loadedCond = l.newCondition()
val canCond = l.newCondition()

class Context (nTrucks: Int, nLoaders: Int) :
  private val capacity = 5

  def load(id: Int) = 
      
      log(s"Loader $id puts a box. Truck with XXX slots")

  def truckRequiresLoading(id: Int) =
    log(s"Truck $id in platform to be loaded")
    log(s"Truck $id releases the platform")

object Main:
  val nLoaders = 6
  val nTrucks = 3
  def main(args: Array[String]) =
    val context = Context(nTrucks, nLoaders)
    val trucks = List.tabulate(nTrucks) { id => thread {
        while (true)
          context.truckRequiresLoading(id)
          Thread.sleep(Random.nextLong(1000))
    } }
    val loaders = List.tabulate(nLoaders) { id => thread {
      while (true)
        context.load(id)
        Thread.sleep(Random.nextLong(300))
    } }

