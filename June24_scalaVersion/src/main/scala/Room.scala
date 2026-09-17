import java.util.Random
import java.util.concurrent.locks.ReentrantLock
import Concurrency.*


object Room {
  protected val r = new Random // Used by validators to calculate if the person may enter

}
/*CS-1: a person cannot make a request until the previous one has received their response (initially,
nothing should be waited for).
CS-2: a Person who has made a request cannot leave until they have been served by two validators and
know the response to their request.
CS-3: a validator waits for a request to calculate the response.
CS-4: a validator cannot serve another request until the last one they participated in has ended.*/

class Room {

  protected var response = false // Stores the joint response of the current request
  
  var responsesReceived = 0
  var positiveResponses = 0
  var requestPending = false
  

  var canRequest = true
  var canLeave = false
  var waitValidator = false
  var canServe = false

  //using locks
  val l = new ReentrantLock()
  val requestCond = l.newCondition()
  val leaveCond = l.newCondition()
  val waitCond = l.newCondition()
  val serveCond = l.newCondition()

  def serveRequest(id: Int): Unit = {
    try{
      l.lock()
      while !canServe do serveCond.await()
      validator+=1
      log(s"Validator $id responds to request")
      response = Validator(id, this)
      if(validator==2){
        validator=0
        
      }
    } finally {
    l.unlock()
    }
  }

  def makeRequest(id: Int): Unit = {
    try {
      l.lock()
      while !canRequest do requestCond.await()
      log(s"Person $id makes request")
      
      canRequest = false
      canServe = true
      serveCond.signalAll()
    } finally {
      l.unlock()
    }
  }

  def receiveResponse(id: Int): Boolean = {
    try {
      l.lock()
      log(s"")
      return response
    } finally {
      l.unlock()
    }
  }


 /* def enterSecurityRoom(id: Int): Unit = {
    try {
      l.lock()
      log(s"")
    } finally {
      l.unlock()
    }
  }

  def exitSecurityRoom(id: Int): Unit = {
    try {
      l.lock()
      log(s"")
    } finally {
      l.unlock()
    }
  }*/

  /*def accompany(): Unit = {
    try {
      l.lock()
      log(s"")
    } finally {
      l.unlock()
    }
  }*/
}
