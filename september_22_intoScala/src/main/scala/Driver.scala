import scala.language.postfixOps

object Driver {
  def main(args: Array[String]): Unit = {
    val train: Train = new TrainSyncOrLocks()
    //or new TrainSemaphores();

    val ed = new EngineDriver(train)
    val pas = new Array[Passenger](20)
    for (i <- 0 until pas.length) {
      pas(i) = new Passenger(train, i)
    }
    ed.start()
    for (i <- 0 until pas.length) {
      pas(i).start()
    }
  }
}
