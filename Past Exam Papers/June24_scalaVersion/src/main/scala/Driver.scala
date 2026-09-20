object Driver {
  val NUM_PERSONS = 5
  val NUM_VALIDATORS = 3

  def main(args: Array[String]): Unit = {
    val s = new Room
    val persons = new Array[Person](NUM_PERSONS)
    val validators = new Array[Validator](NUM_VALIDATORS)
    val comp = new Companion(s)
    for (i <- 0 until NUM_PERSONS) {
      persons(i) = new Person(i, s)
      persons(i).start
    }
    for (i <- 0 until NUM_VALIDATORS) {
      validators(i) = new Validator(i, s)
      validators(i).start
    }
    comp.start
    try for (i <- 0 until NUM_PERSONS) {
      persons(i).join
    }
    catch {
      case e: InterruptedException =>
        e.printStackTrace()
    }
    System.out.println("All people have been served. Interrupting the validators and the companion...")
    for (i <- 0 until NUM_VALIDATORS) {
      validators(i).interrupt
    }
    comp.interrupt
  }
}
