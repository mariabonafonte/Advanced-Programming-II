import scala.util.Random
import Concurrency.*

import java.util.concurrent.locks.ReentrantLock

class MusicalChairs(totalChairs:Int) {
  var numChairs = totalChairs
  var personsSat = 0

  var canSit = true
  var canExit = false

  //using Locks
  val l = new ReentrantLock()
  val sitCond = l.newCondition()
  val exitCond = l.newCondition()



  def iSit(id:Int, iter:Int):Boolean = {
    try {
      l.lock()

      while !canSit do sitCond.await()
      if(personsSat==numChairs) {
        log(s"Iteration $iter: Player $id loses :-(( Exits from the game")
        canSit = false
        canExit = true
        exitCond.signalAll()
        return true
      }else {
        personsSat += 1
        log(s"Iteration $iter: Player $id is sat!! :-))")

        while !canExit do exitCond.await()
        personsSat -= 1
        log(s"Player $id exits from the room. $personsSat")
        if (personsSat == 0) {
          numChairs-=1
          canExit = false
          canSit = true
          sitCond.signal()
          log(s"----------End iteration $iter----------")
        }
        false
      }
    } finally {
      l.unlock()
    }
  }

  /**
   * SECOND PART
   */

  def waitCongratulations()= {
    log("Many thanks...")
  }

  def congratulates(id:Int) = {
    log("Congratulations!!!")
  }
}

object Main {
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
          // for (i<-0 until NPlayers-1)
          //  musicalChairs.waitCongratulations()

        } else{
          // Uncomment for the second part
          // musicalChairs.congratulates(i)
        }
      }
  }
}