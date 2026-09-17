package lab7

import scala.util.Random
import Concurrency._
import scala.collection.mutable.ListBuffer

class Resources(total: Int) {

  private val waitingQueue = new ListBuffer[Int]()      // Queue of waiting process IDs
  private var available = total                         // Number of available resources
  //private var next = -1                                 // Next process ID to be served
  //private var waitingCount = 0                          // Number of processes currently waiting

  def requestResources(id: Int, amount: Int): Unit = synchronized {
    // Process `id` requests `amount` resources
    while waitingQueue(0) != id || amount > available do
      wait()
      available -= amount
      waitingQueue.remove(0)
    log(s"Process $id requests $amount resources. Waiting count: ${waitingQueue.length}")


    log(s"Process $id acquires $amount resources. Remaining: $available")


  }

  def releaseResources(id: Int, amount: Int): Unit = synchronized {
    // Process `id` releases `amount` resources
    available += amount
    log(s"Process $id releases $amount resources. Available now: $available")
    notifyAll()
  }


}

object Exercise2 {

  def main(args: Array[String]): Unit = {
    val totalResources = 5
    val numProcesses = 10

    val resources = new Resources(totalResources)
    val processes = new Array[Thread](numProcesses)

    for (i <- processes.indices)
      processes(i) = thread {
        val r = Random.nextInt(totalResources) + 1
        resources.requestResources(i, r)
        Thread.sleep(Random.nextInt(300))
        resources.releaseResources(i, r)
      }
  }

}
