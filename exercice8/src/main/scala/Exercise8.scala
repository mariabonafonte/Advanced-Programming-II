import scala.util.Random
import Concurrency.*

import java.util.concurrent.locks.ReentrantLock

object WaterManager:

  private var numH = 0
  private var numO = 0

  var hWait = false
  var oWait = false


  def hReady(id: Int): Unit = synchronized {
      numH+=1
      log(s"Hydrogen $id is ready.")

      if(numH==2){
        hWait = true

      }
      if(hWait && oWait) {
        log(s"      Molecule formed! (H₂O)")
        numH=0
        numO=0
        hWait=false
        oWait=false
      }
  }

  def oReady(id: Int): Unit = synchronized {
      numO+=1
      log(s"Oxygen $id is ready.")
      if(numO==1){
        oWait = true
      }

      if(oWait && hWait) {
        log(s"      Molecule formed! (H₂O)")
        numH=0
        numO=0
        oWait=false
        hWait=false
      }
  }

object Exercise8:

  def main(args: Array[String]): Unit =
    val N = 5  // Number of water molecules to form
    val hydrogens = Array.tabulate(2 * N)(i =>
      thread:
        Thread.sleep(Random.nextInt(500))
        WaterManager.hReady(i)
    )
    val oxygens = Array.tabulate(N)(i =>
      thread:
        Thread.sleep(Random.nextInt(500))
        WaterManager.oReady(i)
    )

    hydrogens.foreach(_.join())
    oxygens.foreach(_.join())

    println("End of Program.")
