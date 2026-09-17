import java.util.concurrent.Semaphore
import java.util.concurrent.locks.ReentrantLock
import scala.util.Random

def log(msg: String): Unit =
  println(s"${Thread.currentThread().getName}: $msg")

def thread(body: => Unit): Thread = {
  val t = new Thread {
    override def run() = body
  }
  t.start()
  t
}

class RelayRace(numTeams: Int, runnersPerTeam: Int) {
  var currentRunner = Array.fill(numTeams)(0)
  var finishedTeams = 0
  var runners = runnersPerTeam

  //lockSs
  val l = new ReentrantLock() //lock
  val teamCond = Array.fill(numTeams)(l.newCondition())


  def startRace(teamId: Int, runnerId: Int): Unit = {
    try {
      l.lock()
      while currentRunner(teamId) != runnerId do teamCond(teamId).await()
      log(s"${'\t'.toString * teamId}Team $teamId - Runner $runnerId starts running")
    }finally {
      l.unlock()
    }
  }

  def finishRace(teamId: Int, runnerId: Int): Unit = {
    try {
      l.lock()
      log(s"${'\t'.toString * teamId}Team $teamId - Runner $runnerId finishes their lap")
      if (runnerId<runners -1){
        currentRunner(teamId) +=1
        teamCond(teamId).signalAll()
      }else{
        finishedTeams+=1
        if (finishedTeams==1) {
          log(s"${'\t'.toString * teamId}Team $teamId is the winner!")
          log(s"${'\t'.toString * teamId}Team $teamId thanks for their medal")
        }else{
          log(s"${'\t'.toString * teamId}Team $teamId has finished the race, placed $finishedTeams")
        }
      }
    }finally {
      l.unlock()
    }

  }

  /*def supervise(): Unit = {
    try {
      l.lock()
      log(s"Judge awards the medal to team $team")

      log(s"Judge received thanks from team $team")

      log(s"Final podium: ${arrivalOrder.take(3).mkString(", ")}")
    }finally{
      l.unlock()
    }
  }*/
}

object RelayRace {
  def main(args: Array[String]): Unit = {
    val numTeams = 3
    val runnersPerTeam = 4
    val race = new RelayRace(numTeams, runnersPerTeam)
    for (team <- 0 until numTeams; runner <- 0 until runnersPerTeam) {
      thread {
        race.startRace(team, runner)
        Thread.sleep(200 + Random.nextInt(500))
        race.finishRace(team, runner)
      }
    }
    // Second part: the judge delivers medals with handshake
   /* val judge = thread {
      race.supervise()
    }*/
  }
}