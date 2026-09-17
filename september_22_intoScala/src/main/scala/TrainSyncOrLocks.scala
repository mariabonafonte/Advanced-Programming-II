import java.util.concurrent.locks.ReentrantLock
import Concurrency.*
import org.w3c.dom.css.Counter

class TrainSyncOrLocks extends Train {
    val SIZE_LIMIT = 5

    var emptyW1 = true

    var canStart = false
    var tripFinished = false
    var emptyWagons = true
    var canExit = false

    //using locks
    val l = new ReentrantLock()
    val wagonCond = l.newCondition()
    val startCond = l.newCondition()
    val finishCond = l.newCondition()
    val exitCond = l.newCondition()

    var wagon = 0
    var counter = 0

    override def trip(id: Int): Unit = {
      try{
        l.lock()
        while !emptyWagons do wagonCond.await()

        if (emptyW1) {
          wagon = 1;
          counter+=1
          if(counter==SIZE_LIMIT){
            counter = 0
            emptyW1=false
          }
        } else {
          wagon = 2;
          counter+=1

          if (counter == SIZE_LIMIT) {
            counter = 0
            emptyWagons = false
            canStart = true
            startCond.signal()
          }

        }
        log(s"Passenger $id has board on wagon $wagon")

        while !canExit do exitCond.await()

        if (!emptyW1) {
          wagon = 1;
          counter += 1
          if (counter == SIZE_LIMIT) {
            counter = 0
            emptyW1 = true
          }
        } else {
          wagon = 2;
          counter += 1

          if (counter == SIZE_LIMIT) {
            counter = 0
            canExit = false
            emptyWagons = true
            wagonCond.signalAll()
          }

        }
        log(s"Passenger $id has got off the wagon $wagon")
      } finally {
        l.unlock()
      }


    }

    override def beginTrip(): Unit = {
      try{
        l.lock()
        while !canStart do startCond.await()
        log(s"Engine Driver: the trip Starts")
        canStart = false
        tripFinished = true
        finishCond.signal()
      } finally {
        l.unlock()
      }
    }

    override def endTrip(): Unit = {
      try{
        l.lock()
        while !tripFinished do finishCond.await()
        log(s"Engine Driver: the trip Ends")
        canExit = true
        exitCond.signalAll()


      } finally {
        l.unlock()
      }
    }


}
