
def log(msg:String): Unit =
  println(s"${Thread.currentThread().getName}: $msg")

def thread(body: =>Unit):Thread = {
  val t = new Thread{
    override def run() = body
  }
  t.start()
  t
}

import scala.util.Random
class MusicalChairs(totalChairs:Int) {
  var numChairs = totalChairs

  def iSit(id:Int, iter:Int):Boolean = { 
      log(s"Iteration $iter: Player $id loses :-(( Exits from the game")
      log(s"Iteration $iter: Player $id is sat!! :-))")
      log(s"Player $id exits from the room. $personsSat")
        log(s"----------End iteration $iter----------")
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
          // for (i<-0 until NPlayers-1)
          //  musicalChairs.waitCongratulations()

        } else{
          // Uncomment for the second part
          // musicalChairs.congratulates(i)
        }
      }
  }
}