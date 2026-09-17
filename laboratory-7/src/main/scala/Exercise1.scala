package lab7

import scala.util.Random
import Concurrency._

class Buffer(numConsumers: Int, size: Int) {
  // numConsumers - number of consumers
  // size - buffer size

  private val buffer = new Array[Int](size)
  for (i <- buffer.indices)
    buffer(i) = 0 // to be displayed

  private val bufferAux = new Array[Int](size)
  for (i <- bufferAux.indices)
    bufferAux(i) = 0

  // if buffer(i) holds a data item, bufferAux(i) holds the number of consumers
  // still needing to consume it

  private var freeSlots = size
  private val pendingItems = new Array[Int](numConsumers)
  for (i <- pendingItems.indices)
    pendingItems(i) = 0 // items each consumer still needs to consume

  private var producerIndex = 0
  private val consumerIndices = new Array[Int](numConsumers)
  for (i <- consumerIndices.indices)
    consumerIndices(i) = 0 // index for each consumer

  def newData(item: Int): Unit = synchronized {
    // producer inserts a new item
    while freeSlots==0 do wait()
    buffer(producerIndex)= item
    bufferAux(producerIndex) = numConsumers
    freeSlots -=1
    producerIndex = (producerIndex+1) % size
    log(s"Producer stores $item: buffer=${buffer.mkString("[", ",", "]")}")
    for (i<-0 until numConsumers)
      pendingItems(i) +=1
    notifyAll()
  }

  def extractData(id: Int): Int = synchronized {
    while pendingItems(id)==0 do wait()
    val item = buffer(consumerIndices(id))
    log(s"Consumer $id reads $item: buffer=${buffer.mkString("[", ",", "]")}")
    bufferAux(consumerIndices(id)) -=1
    if bufferAux(consumerIndices(id))==0 then {
      freeSlots+=1
      notify()
      //
    }
    pendingItems(id) -=1
    consumerIndices(id) = (consumerIndices(id) +1) % size
  //
    item
  }

}

object Exercise1 {

  def main(args: Array[String]): Unit = {
    val numConsumers = 4
    val bufferSize = 3
    val numIterations = 10

    val buffer = new Buffer(numConsumers, bufferSize)
    val consumers = new Array[Thread](numConsumers)

    for (i <- consumers.indices)
      consumers(i) = thread {
        for (_ <- 0 until numIterations) {
          val item = buffer.extractData(i)
          Thread.sleep(Random.nextInt(200))
        }
      }

    val producer = thread {
      for (i <- 0 until numIterations) {
        Thread.sleep(Random.nextInt(50))
        buffer.newData(i + 1)
      }
    }
  }

}
