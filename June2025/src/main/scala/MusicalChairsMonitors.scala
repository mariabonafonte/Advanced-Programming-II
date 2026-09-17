
def log2(msg:String): Unit =
  println(s"${Thread.currentThread().getName}: $msg")

def thread2(body: =>Unit):Thread = {
  val t = new Thread{
    override def run() = body
  }
  t.start()
  t
}

import java.util.concurrent.Semaphore
import scala.util.Random
class MusicalChairsMonitors(totalChairs:Int) {
  var numChairs = totalChairs
  var personsSat = 0
  private var congratulate = false
  private var thank = false
  private var canSit = true
  private var canLeave = false
  def iSit(id:Int, iter:Int):Boolean = synchronized {
    while !canSit do wait()
    if(personsSat < numChairs)
      personsSat += 1
      log2(s"Iteration $iter: Player $id is sat!! :-))")
    else
      log2(s"Iteration $iter: Player $id loses :-(( Exits from the game")
      canSit = false
      canLeave = true
      notifyAll()
      return true
    while !canLeave do wait()
    personsSat -= 1
    log2(s"Player $id exits from the room. $personsSat")
    if personsSat<= 0 then
      numChairs-=1
      log2(s"----------End iteration $iter----------")
      canSit = true
      canLeave = false
      if numChairs == 0 then
        congratulate = true
      notifyAll()
    false
  }

  /**
   * SECOND PART
   */

  def waitCongratulations()= synchronized{
    congratulate = true
    notifyAll()
    while !thank do wait()
    log2("Many thanks...")
    thank = false
  }
  def congratulates(id:Int) = synchronized {
    while !congratulate do wait()
    log2("Congratulations!!!")
    thank = true
    congratulate = false
    notifyAll()


  }
}

object MainB {
  def main(args:Array[String]) = {
    val NChairs = 5
    val NPlayers = NChairs+1
    val musicalChairs = new MusicalChairsMonitors(NChairs)
    val players = new Array[Thread](NPlayers)
    for (i<-players.indices)
      players(i) = thread2{
        var iLoss = false
        var iter = 0
        while (iter<NChairs && !iLoss)
          Thread.sleep(Random.nextInt(100))
          iLoss=musicalChairs.iSit(i,iter)
          iter += 1
        if (!iLoss){
          log2(s"Player $i is the winner!!!! Waiting for other players' congratulations")
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