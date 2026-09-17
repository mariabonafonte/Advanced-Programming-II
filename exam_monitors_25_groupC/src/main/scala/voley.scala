import scala.util.Random
import concurrencia.*

import java.util.concurrent.locks.ReentrantLock

class Torneo(C: Int) extends Thread {
  private val numJugadores = Array.fill(2)(0) // núm. de jugadores en cada equipo
  private var equipoPerdedor = 0 // equipo a renovar

  var gonnaPlay =true
  var canStart = false

  //using locks
  val l = new ReentrantLock()
  val playCond = l.newCondition()
  val startCond = l.newCondition()

  def juega(id: Int) = {
    try {
      l.lock()
      // ...
      while !gonnaPlay do playCond.await()
      log(s"El jugador $id se une al equipo $equipoPerdedor, que tiene ${numJugadores(equipoPerdedor)} jugadores.")
      // ...
      log(s"El jugador $id deja el equipo $equipoPerdedor, que tiene ${numJugadores(equipoPerdedor)} jugadores.")
      // ...
    } finally {
      l.unlock()
    }
  }

  def jugarPartido = {
    try {
      l.lock()
      while !canStart do startCond.await()
      // ...
      log(s"Equipos preparados, comienza un nuevo partido....")
      // ...
      Thread.sleep(Random.nextInt(500)) // juegan el partido
      equipoPerdedor = Random.nextInt(2) // gana uno de los dos equipos
      // ...
      log(s"Ha ganado el equipo ${(equipoPerdedor + 1) % 2}, se renueva el equipo $equipoPerdedor.")
      // ...
    } finally {
      l.unlock()
    }
  }

  override def run = {
    setName("Voley")
    while (true)
      jugarPartido // cuando los dos equipos están completos (sale el equipo perdedor y entra un nuevo equipo) se juega un nuevo partido
  }
}

@main def mainVoley =
  val torneo = new Torneo(2)
  val jugador = new Array[Thread](50)
  torneo.start()
  for (i <- jugador.indices)
    jugador(i) = thread {
      while (true)
        torneo.juega(i) // el jugador i participa en el torneo de voley
        Thread.sleep(Random.nextInt(500)) // se da un bañito en la playa
    }
