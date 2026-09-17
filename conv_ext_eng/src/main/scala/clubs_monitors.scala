// Concurrency block. Synchronization with monitors/locks.
import concurrency.{log, thread}

import java.util.concurrent.locks.ReentrantLock
import scala.util.*
// ...

// N > 1, M = 1
class ClubHouse11(T: Int) extends SocialClub {
  private var numClubA = 0
  private var numClubB = 0

  var canEnterA = true
  var canEnterB = false
  var canleaveA = true
  var canLeaveB = false
  //using locks
  val l = new ReentrantLock()
  val enterA = l.newCondition()
  val enterB = l.newCondition()
  val leaveA = l.newCondition()
  val leaveB = l.newCondition()


  def enterClubA(id: Int): Unit = {
    // ...
    try{
      l.lock()
      while !canEnterA do enterA.await()
      numClubA +=1
      log(s"Member $id of Club A enters. clubA= $numClubA")
      if(numClubA>=1){
        canleaveA=true
        if(numClubA==T){
          canEnterA=false
          enterA.signal()
        }
      }

    } finally {
      l.unlock()
    }
    // ...
  }

  def leaveClubA(id: Int): Unit = {
    // ...
    try {
      l.lock()
      while !canleaveA do leaveA.await()
      numClubA-=1
      log(s"Member $id of Club A leaves. clubA= $numClubA")

      if(numClubA==0){
        canEnterB = true
        enterB.signal()
      }
    } finally {
      l.unlock()
    }
    // ...
  }

  def enterClubB(id: Int): Unit = {
    // ...
    try {
      l.lock()
      while !canEnterB do enterB.await()
      if(numClubA!=0){
        canEnterB=false
      }
      numClubB+=1
      log(s"\t\t\t\tMember $id of Club B enters.")
      canEnterA=false
      canleaveA= false
      canEnterB=false
      canLeaveB=true
      leaveB.signal()

    } finally {
      l.unlock()
    }

    // ...
  }

  def leaveClubB(id: Int): Unit = {
    // ...
    try {
      l.lock()
      while !canLeaveB do leaveB.await()
      numClubB-=1
      log(s"\t\t\t\tMember $id of Club B leaves.")
      canLeaveB= false
      canEnterA=true
      enterA.signal()

    } finally {
      l.unlock()
    }

    // ...
  }
}

// N > 1, M > 1
class ClubHouse12(T: Int) extends SocialClub {
  private var numClubA = 0
  private var numClubB = 0

  var canEnterA = true
  var canEnterB = true
  var canleaveA = true
  var canLeaveB = false
  //using locks
  val l = new ReentrantLock()
  val enterA = l.newCondition()
  val enterB = l.newCondition()
  val leaveA = l.newCondition()
  val leaveB = l.newCondition()

  def enterClubA(id: Int): Unit = {
    // ...
    try {
      l.lock()
      while !canEnterA do enterA.await()
      numClubA+=1
      log(s"Member $id of Club A enters. clubA= $numClubA")
      if(numClubA>=1){
        canEnterB = false
        canleaveA = true
        leaveA.signal()
      }
      if(numClubA>=T){
        canEnterA=false
      }
    } finally {
      l.unlock()
    }
    // ...
  }

  def leaveClubA(id: Int): Unit = {
    // ...
    try {
      l.lock()
      numClubA-=1
      while !canleaveA do leaveA.await()
      log(s"Member $id of Club A leaves. clubA= $numClubA")
      if(numClubA==0){
        canEnterB = true
        enterB.signal()
      }
    } finally {
      l.unlock()
    }
    // ...
  }

  def enterClubB(id: Int): Unit = {
    // ...
    try {
      l.lock()
      while !canEnterB do enterB.await()
      numClubB+=1
      log(s"\t\tMember $id of Club B enters. clubB=$numClubB")
      if (numClubB >= 1) {
        canEnterA = false
        canLeaveB = true
        leaveB.signal()
      }
      if (numClubB >= T) {
        canEnterB = false
      }
    } finally {
      l.unlock()
    }
    // ...
  }

  def leaveClubB(id: Int): Unit = {
    // ...
    try {
      l.lock()
      numClubB-=1
      while !canLeaveB do leaveB.await()
      log(s"\t\tMember $id of Club B leaves. clubB=$numClubB")
      if(numClubB==0){
        canEnterA=true
        enterA.signal()
      }
    } finally {
      l.unlock()
    }
    // ...
  }
}

@main def mainClubs11 = {
  val N = 5 // members of Club A
  val M = 1 // members of Club B
  val T = 3 // capacity of the center
  val ch = new ClubHouse11(T)
  val c1 = new Array[Thread](N)
  val c2 = new Array[Thread](M)

  for (i <- c1.indices)
    c1(i) = thread {
      for (j <- 0 until 5) {
        Thread.sleep(Random.nextInt(100))
        ch.enterClubA(i)
        Thread.sleep(Random.nextInt(200))
        ch.leaveClubA(i)
      }
    }

  for (i <- c2.indices)
    c2(i) = thread {
      for (j <- 0 until 5) {
        Thread.sleep(Random.nextInt(500))
        ch.enterClubB(i)
        Thread.sleep(Random.nextInt(200))
        ch.leaveClubB(i)
      }
    }
}

@main def mainClubs12 = {
  val N = 5 // members of Club A
  val M = 5 // members of Club B
  val T = 3 // capacity of the center
  val ch = new ClubHouse12(T)
  val c1 = new Array[Thread](N)
  val c2 = new Array[Thread](M)

  for (i <- c1.indices)
    c1(i) = thread {
      for (j <- 0 until 5) {
        Thread.sleep(Random.nextInt(1000))
        ch.enterClubA(i)
        Thread.sleep(Random.nextInt(200))
        ch.leaveClubA(i)
      }
    }

  for (i <- c2.indices)
    c2(i) = thread {
      for (j <- 0 until 5) {
        Thread.sleep(Random.nextInt(500))
        ch.enterClubB(i)
        Thread.sleep(Random.nextInt(200))
        ch.leaveClubB(i)
      }
    }
}

