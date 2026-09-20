
  import scala.util.*

  class EngineDriver(private val train: Train) extends Thread {
    final private val r = new Random

    override def run(): Unit = {
      while (true) try {
        train.beginTrip()
        Thread.sleep(r.nextInt(1000))
        train.endTrip()
      } catch {
        case e: InterruptedException =>
          e.printStackTrace()
      }
    }
  }


