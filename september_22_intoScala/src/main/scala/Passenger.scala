
  class Passenger(private var train: Train, private var id: Int) extends Thread {
    override def run(): Unit = {
      while (true) try {
        Thread.sleep(2000)
        train.trip(id)
      } catch {
        case e: InterruptedException =>
          e.printStackTrace()
      }
    }
  }


