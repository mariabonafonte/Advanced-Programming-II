import scala.util.Random


object Validator {
  private val r = new Random
}

class Validator(private val id: Int, private val s: Room) extends Thread {
  override def run(): Unit = {
    var end = false
    while (!isInterrupted && !end) try {
      Thread.sleep(50 + Validator.r.nextInt(50))
      s.serveRequest(id)
    } catch {
      case e: InterruptedException =>
        end = true
    }
  }
}
