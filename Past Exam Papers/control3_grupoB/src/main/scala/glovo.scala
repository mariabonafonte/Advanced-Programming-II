
import java.util.Random
import scala.collection.mutable.ListBuffer
import concurrencia.*

class Pedido(val idCliente: Int) {
  override def toString: String = s"pedido de cliente $idCliente"
  def getIdCliente: Int = idCliente
}

object glovo {
  private val pedidosEnEspera: ListBuffer[Pedido] = ListBuffer() // Pedidos preparados para ser recogidos por riders
  private val pedidosPreparados = scala.collection.mutable.Set[Pedido]() // Pedidos listos para ser entregados al cliente
  // ...

  def realizarPedido(idCliente: Int): Unit = {
    // ...
    log(s"cliente $idCliente quiere hacer un pedido.")
    // ...
  }

  def recibirPedido(idCliente: Int): Unit = {
    // ...
    // val pedido: Pedido = ...
    // ...
    // log(s"cliente $idCliente recibe $pedido.")
    // ...
  }

  def servirPedido(idRider: Int): Unit = {
    // ...
    // val pedido: Pedido = ...
    // ...
    // log(s"rider $idRider recoge $pedido.")
    Thread.sleep(500) // Simula el tiempo de entrega del pedido
    // ...
    // log(s"rider $idRider entrega $pedido.")
    // ...
  }
}

@main def main =
    val numRiders = 3 // número de riders
    val numClientes = 3 // número de clientes
    val rnd = new Random()
    for (i <- 0 until numRiders) {
      thread {
        while (true) {
          glovo.servirPedido(i)
        }
      }
    }
    for (i <- 0 until numClientes) {
      thread {
        while (true) {
          glovo.realizarPedido(i) // cliente hace un pedido
          glovo.recibirPedido(i) // cliente recibe el pedido
          Thread.sleep(rnd.nextInt(5000)) // espera entre pedidos
        }
      }
    }

