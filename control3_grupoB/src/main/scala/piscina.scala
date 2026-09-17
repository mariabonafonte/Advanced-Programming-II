
import concurrencia.{log, thread}

import java.util.concurrent.locks.ReentrantLock
import scala.util.Random

object piscina {
  private var nNadadores = 0
  private var nSocorristas = 0

  var puedeNadador = false
  var faltaSocorrista = true
  var sobraSocorrista = false

  // using locks
  val l = new ReentrantLock()
  val condicionFaltaSocorrista = l.newCondition()
  val condicionEntraNadador = l.newCondition()
  val condicionSaleSocorrista = l.newCondition()


  def entraNadador(id: Int): Unit = {
    try{
      l.lock()
      while !puedeNadador do condicionEntraNadador.await()
      nNadadores+=1
      log(s"Ha llegado un nadador. Nadadores=$nNadadores, Socorristas=$nSocorristas")

      if(nNadadores<(3*nSocorristas)){
        puedeNadador = true

      }else{
        puedeNadador=false
        faltaSocorrista = true
        condicionFaltaSocorrista.signal()
      }

    } finally {
      l.unlock()
    }
    // ...
    // ...
  }

  def saleNadador(id: Int): Unit = {
    try {
      l.lock()
      if(nNadadores>0) {
        nNadadores -= 1
        log(s"Ha salido un nadador. Nadadores=$nNadadores, Socorristas=$nSocorristas")
        if( nNadadores < 3*nSocorristas){
          sobraSocorrista = true
          condicionSaleSocorrista.signal()

        }
      }
    } finally {
      l.unlock()
    }
    // ...
    // ...
  }

  def entraSocorrista(id: Int): Unit = {
    try {
      l.lock()
      while !faltaSocorrista do condicionFaltaSocorrista.await()
      nSocorristas+=1
      log(s"Ha llegado un socorrista. Nadadores=$nNadadores, Socorristas=$nSocorristas")

      if(nNadadores< 3*nSocorristas){
        puedeNadador = true
        condicionEntraNadador.signal()
      }else{
        faltaSocorrista=true

      }

    } finally {
      l.unlock()
    }
    // ...
    // ...
  }

  def saleSocorrista(id: Int): Unit = {
    try {
      l.lock()
      while !sobraSocorrista do condicionSaleSocorrista.await()
      nSocorristas-=1
      log(s"Ha salido un socorrista. Nadadores=$nNadadores, Socorristas=$nSocorristas")

      if(nSocorristas*3 > nNadadores-3){
        sobraSocorrista=true
      }else{
        sobraSocorrista= false
      }

    } finally {
      l.unlock()
    }
    // ...
    // ...
  }
}

@main def mainPiscina =
  val numNadadores = 13
  val numSocorristas = 3
  val nadador = new Array[Thread](numNadadores)
  val socorrista = new Array[Thread](numSocorristas)
  for (i <- nadador.indices)
    nadador(i) = thread {
      while (true) {
        Thread.sleep(Random.nextInt(700))
        piscina.entraNadador(i)
        Thread.sleep(Random.nextInt(300))
        piscina.saleNadador(i)
      }
    }
  for (i <- socorrista.indices)
    socorrista(i) = thread {
      while (true) {
        Thread.sleep(Random.nextInt(700))
        piscina.entraSocorrista(i)
        Thread.sleep(Random.nextInt(300))
        piscina.saleSocorrista(i)
      }
    }

