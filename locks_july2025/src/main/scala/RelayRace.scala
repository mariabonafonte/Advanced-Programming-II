import java.util.concurrent.Semaphore
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




  def startRace(teamId: Int, runnerId: Int): Unit = {

    log(s"${'\t'.toString * teamId}Team $teamId - Runner $runnerId starts running")
  }

  def finishRace(teamId: Int, runnerId: Int): Unit = {

    log(s"${'\t'.toString * teamId}Team $teamId - Runner $runnerId finishes their lap")

        log(s"${'\t'.toString * teamId}Team $teamId is the winner!")

        log(s"${'\t'.toString * teamId}Team $teamId has finished the race, placed $finishedTeams")

        log(s"${'\t'.toString * teamId}Team $teamId thanks for their medal")

  }

  def supervise(): Unit = {

      log(s"Judge awards the medal to team $team")

      log(s"Judge received thanks from team $team")

    log(s"Final podium: ${arrivalOrder.take(3).mkString(", ")}")

}

object Main {
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
    val judge = thread {
      race.supervise()
    }
  }
}