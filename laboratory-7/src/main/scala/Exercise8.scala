package lab7

import scala.util.Random
import Concurrency._

object WaterManager:

  private var numH = 0
  private var numO = 0

  private var hGateOpen = true
  private var oGateOpen = true
  private var moleculeFormed = false
  
  private var exitGateOpen = false

  def hReady(id: Int): Unit = synchronized {

    log(s"Hydrogen $id is ready.")
    exitGateOpen = true
    numH -=1
    
      log(s"      Molecule formed! (H₂O)")

  }

  def oReady(id: Int): Unit = synchronized {

    log(s"Oxygen $id is ready.")

      log(s"      Molecule formed! (H₂O)")
        while !exitGateOpen do wait()
        numO -= 1
        if numH == 0 && numO==0 then { 
          exitGateOpen = false
          hGateOpen = true
          oGateOpen = true
          notifyAll()
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
