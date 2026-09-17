import scala.util.Random


class Person(private var id: Int, private var s: Room) extends Thread {
  r = new Random
  var r: Random = null

  override def run(): Unit = {
    var v = false
    try {
      while (!v) {
        Thread.sleep(50 + r.nextInt(50))
        s.makeRequest(id)
        v = s.receiveResponse(id)
      }
      s.enterSecurityRoom(id)
      Thread.sleep(50 + r.nextInt(50))
      s.exitSecurityRoom(id)
    } catch {
      case e: InterruptedException =>
        e.printStackTrace()
    }
  }
}