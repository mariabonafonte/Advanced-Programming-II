import scala.util.Random
import concurrencia.*

class Bowl(R: Int) extends Thread {
  private var bowl = R // inicialmente lleno
  
  var empty = false

  def cogerPalomita(i: Int): Unit = synchronized {
    // ...
    while !empty do 
    log(s"Joven $i coge una palomita del bowl. Quedan $bowl palomitas.")
    // ...
  }


  def llenarBowl(): Unit = synchronized {
    // ...
    log(s"El bowl se rellena. Quedan $bowl palomitas.")
    // ...
  }

  override def run(): Unit =
    while (true) {
      llenarBowl()
    }
}

@main def mainBowl =
  val numJovenes = 20 // número de jóvenes en la maratón
  val bowl = new Bowl(100) // el bowl se inicializa lleno, indicando en el constructor su capacidad
  bowl.start()
  for (i <- 0 until numJovenes)
    thread {
      while (true) {
        Thread.sleep(Random.nextInt(100)) // espera entre palomita y palomita, no se vaya a atragantar
        bowl.cogerPalomita(i)
      }
    }

