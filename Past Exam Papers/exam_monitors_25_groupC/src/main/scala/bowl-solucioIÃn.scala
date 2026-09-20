/*
Nos hemos comprado una máquina de hacer palomitas fantástica, tiene forma de bowl, pero cuando cogemos la última
palomita pulsamos un botón y automáticamente se rellena de palomitas. Implementa una clase Bowl que simule el bowl
de palomitas como un monitor utilizando bloques sincronizados. Tenemos dos condiciones de sincronización: (i) un
joven no puede coger una palomita del bowl si este está vacío, y (ii) el bowl no se rellena si no está vacío.
Por ejemplo, la siguiente sería una traza válida:
Thread-6: Joven 3 coge una palomita del bowl. Quedan 99 palomitas.
Thread-16: Joven 8 coge una palomita del bowl. Quedan 98 palomitas.
Thread-34: Joven 17 coge una palomita del bowl. Quedan 97 palomitas.
Thread-6: Joven 3 coge una palomita del bowl. Quedan 96 palomitas.
Thread-36: Joven 18 coge una palomita del bowl. Quedan 95 palomitas.
...
Thread-6: Joven 3 coge una palomita del bowl. Quedan 5 palomitas.
Thread-12: Joven 6 coge una palomita del bowl. Quedan 4 palomitas.
Thread-32: Joven 16 coge una palomita del bowl. Quedan 3 palomitas.
Thread-16: Joven 8 coge una palomita del bowl. Quedan 2 palomitas.
Thread-8: Joven 4 coge una palomita del bowl. Quedan 1 palomitas.
Thread-4: Joven 2 coge una palomita del bowl. Quedan 0 palomitas.
Thread-0: El bowl se rellena. Quedan 100 palomitas.
Thread-24: Joven 12 coge una palomita del bowl. Quedan 99 palomitas.
Thread-22: Joven 11 coge una palomita del bowl. Quedan 98 palomitas.
Thread-2: Joven 1 coge una palomita del bowl. Quedan 97 palomitas.
Thread-14: Joven 7 coge una palomita del bowl. Quedan 96 palomitas.
...
Para implementarlo, utilizaremos monitores y bloques sincronizados.
 */
import java.util.concurrent.*
import java.util.concurrent.locks.ReentrantLock
import scala.util.Random
import concurrencia.{log, thread}

class Bowl(R: Int) extends Thread {
  private var bowl = R // inicialmente lleno

  def cogerPalomita(i: Int): Unit = synchronized {
    while (bowl == 0) wait() // espera a que el bowl no esté vacío
    bowl -= 1
    log(s"Joven $i coge una palomita del bowl. Quedan $bowl palomitas.")
    if (bowl == 0) notifyAll() // notifica al palomitero que el bowl está vacío
  }

  def llenarBowl(): Unit = synchronized {
    while (bowl > 0) wait() // espera a que el bowl esté vacío
    bowl = R
    log(s"El bowl se rellena. Quedan $bowl palomitas.")
    notifyAll() // notifica a los jóvenes que el bowl está lleno
  }

  override def run(): Unit =
    while (true) {
      llenarBowl()
    }
}

@main def main =
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

