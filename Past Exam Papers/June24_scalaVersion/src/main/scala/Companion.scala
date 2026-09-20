class Companion(private val s: Room) extends Thread {
  override def run(): Unit = {
    var end = false
    while (!isInterrupted && !end) try s.accompany()
    catch {
      case e: InterruptedException =>
        end = true
    }
  }
}