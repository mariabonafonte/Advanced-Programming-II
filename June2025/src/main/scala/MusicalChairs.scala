import Concurrency.*

import java.util.concurrent.locks.ReentrantLock
import scala.util.Random

class MusicalChairs(totalChairs:Int) {
  var numChairs = totalChairs
  var personsSat =0;

  var startIter = true
  var exitChair = false

  var winnerReady = false
  var congratulated = false

  //for locks
  val l = new ReentrantLock()
  val startCond = l.newCondition()
  val exitCond = l.newCondition()
  val congratsCond = l.newCondition()
  val thankCond = l.newCondition()


  def iSit(id:Int, iter:Int):Boolean = {
    try {
      l.lock()
      while !startIter do startCond.await()

      if(personsSat==numChairs) {
        log(s"Iteration $iter: Player $id loses :-(( Exits from the game")
        startIter=false
        exitChair = true
        exitCond.signalAll()
        true
      }else {
        personsSat += 1
        log(s"Iteration $iter: Player $id is sat!! :-))")

        while !exitChair do exitCond.await()
        personsSat -= 1
        log(s"Player $id exits from the room. $personsSat")
        if (personsSat == 0) {
          numChairs-=1
          exitChair = false
          startIter = true
          startCond.signal()
          log(s"----------End iteration $iter----------")
        }
        false
      }
    }finally{
      l.unlock()
    }
  }

  /**
   * SECOND PART
   */

  def waitCongratulations()= {
    try {
      l.lock()
      winnerReady=true
      congratsCond.signal()

      while(!congratulated) do thankCond.await()
      congratulated=false
      log("Many thanks...")
    }finally{
      l.unlock()
    }
  }

  def congratulates(id:Int) = {
    try {
      l.lock()
      while(!winnerReady) do congratsCond.await()
      log("Congratulations!!!")

      congratulated = true
      winnerReady=false
      thankCond.signal()

    }finally{
      l.unlock()
    }
  }
}

object MusicalChairs {
  def main(args:Array[String]) = {
    val NChairs = 5
    val NPlayers = NChairs+1
    val musicalChairs = new MusicalChairs(NChairs)
    val players = new Array[Thread](NPlayers)
    for (i<-players.indices)
      players(i) = thread{
        var iLoss = false
        var iter = 0
        while (iter<NChairs && !iLoss)
          Thread.sleep(Random.nextInt(100))
          iLoss=musicalChairs.iSit(i,iter)
          iter += 1
        if (!iLoss){
          log(s"Player $i is the winner!!!! Waiting for other players' congratulations")
          // Uncomment for the second part
           for (i<-0 until NPlayers-1)
            musicalChairs.waitCongratulations()

        } else{
          // Uncomment for the second part
          musicalChairs.congratulates(i)
        }
      }
  }
}