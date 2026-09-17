
import java.util.concurrent.Semaphore
import Driver.messages

class PrimeGenerator {
  private var list = List[Int](2,3)
  private val mutex = new Semaphore(1, true)

  @throws[Exception]
  def getPrime (pos: Int): Int =
    if pos < 0 then
      throw new Exception(messages.getString("getPrime_exception"))
    mutex.acquire()
    try
      while pos >= list.length do
        computeNextPrime()
    finally 
      mutex.release()
    list(pos)

  private def computeNextPrime(): Int =
    var number = list.last + 2 
    while !isPrime(number) do 
      number+=2
    list = list:+number
    number


  private def isPrime(number: Int): Boolean =
    for prime <- list do
      if (number%prime == 0) then false
    true
  
  @throws[Exception]
  def main(args: Array[String]): Unit =
    for i <- 0 until 100 do
      println(getPrime(i))

}

