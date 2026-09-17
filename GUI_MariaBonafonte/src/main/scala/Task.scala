import Driver.messages

import java.util
import javax.swing.SwingWorker
import java.util.concurrent.ExecutionException
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*
import scala.util.Random

private class FactorizerThread(number:Int, pg: PrimeGenerator) extends Thread:
  var factors: List[Int] = Nil

  override def run(): Unit =
    factors = Task.factorize(number, pg)

object Task:

  def factorize(number: Int, generator: PrimeGenerator): List[Int] =
    var num = number
    var i=0
    val factors = ListBuffer[Int]()

    while num> 1 do
      val prime = generator.getPrime(i)
      if (num% prime) == 0 then
        factors += prime
        num /= prime
      else
        i += 1

    factors.toList

class Task(n:Int, panel: Panel) extends SwingWorker[List[(Int, Int, Int)], (Int, Int, Int)]:

  override def doInBackground(): List[(Int, Int, Int)] =
    val generator = new PrimeGenerator()
    val results = ListBuffer[(Int, Int, Int)]()

    var i = 0

    while i < n && !isCancelled do
      val n1 = Random.between(2, 10001) ///If I do Random.between(2,1000) the system go so fast that is impossible to cancel
      val n2 = Random.between(2, 10001)

      val t1 = new FactorizerThread(n1, generator)
      val t2 = new FactorizerThread(n2, generator)

      t1.start()
      t2.start()
      t1.join()
      t2.join()

      val commonFactors = t1.factors.intersect(t2.factors)
      val gcd =
        if commonFactors.isEmpty then 1
        else commonFactors.product
      
      val pair = (n1, n2, gcd)
      results += pair
      publish(pair)
      i += 1
      setProgress((i * 100) / n)

    results.toList


  override def process(l: util.List[(Int, Int, Int)]): Unit =
    for(n1, n2, gcd) <- l.asScala do
      panel.appendResult(n1, n2, gcd)
  

  override def done(): Unit =
    try
      if !isCancelled then
        get()
        panel.setProgress(100)
        panel.showMessage(messages.getString("status") + messages.getString("status_finished"))
      else
        panel.showMessage(messages.getString("status") + messages.getString("status_cancelled"))
      panel.enableCancel(false)
      panel.enableStart(true)
    catch
      case e: InterruptedException =>
        e.printStackTrace()
      case e: ExecutionException =>
        e.printStackTrace()

