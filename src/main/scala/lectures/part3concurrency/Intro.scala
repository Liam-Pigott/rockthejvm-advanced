package lectures.part3concurrency

import jdk.jfr.DataAmount

import java.util.concurrent.Executors

object Intro extends App {

  // JVM threads
  val runnable = new Runnable {
    override def run(): Unit = println("running in parallel")
  }
  val aThread = new Thread(runnable)

  aThread.start() // create JVM thread on OS thread
  runnable.run() // doesn't do anything in parallel
  aThread.join() // blocks until aThread finishes running

  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
  val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("goodbye")))

//  threadHello.start()
//  threadGoodbye.start()
  //different runs produce different results

  // executors - threads are expensive to create/destory so re-use
//  val pool = Executors.newFixedThreadPool(10)
//  pool.execute(() => println("something in the thread pool"))
//  pool.execute(() => {
//    Thread.sleep(1000)
//    println("sleep for 1 second")
//  })
//
//  pool.execute(() => {
//    Thread.sleep(1000)
//    println("almost done")
//    Thread.sleep(1000)
//    println("done after 2 seconds")
//  })

//  pool.shutdown() // still finishes running threads already started
//  pool.execute(() => println("Should not appear")) // throws exception in the main thread

//  pool.shutdownNow() // interrupts threads already on pool


  def runInParallel = {
    var x = 0

    val thread1 = new Thread(() => {
      x = 1
    })

    val thread2 = new Thread(() => {
      x = 2
    })

//    thread1.start()
//    thread2.start()
//    println(x)
  }

  for(_ <- 1 to 10000) runInParallel
  // race condition

  class BankAccount(var amount: Int) {
    override def toString: String = "" + amount
  }

  def buy(account: BankAccount, thing: String, price: Int) = {
    account.amount -= price
//    println(s"I've bought $thing")
//    println(s"My account is now $account")
  }

//  for(_ <- 1 to 10000) {
//    val account = new BankAccount(50000)
//    val thread1 = new Thread(() => buy(account, "shoes", 3000))
//    val thread2 = new Thread(() => buy(account, "iphone", 4000))
//
//    thread1.start()
//    thread2.start()
//    Thread.sleep(10)
//    if(account.amount != 43000) println("Aha: " + account.amount) // bought both but amount only subtracted one value
//  }

  // option #1: use synchronized()
  def buySafe(account: BankAccount, thing: String, price: Int) = {
    account.synchronized {
      // no two threads can evaluate this at the same time
      account.amount -= price
      println(s"I've bought $thing")
      println(s"My account is now $account")
    }
  }

  //option #2: use @volatile
//  class BankAccount(@volatile var amount: Int)

  def inceptionThreads(maxThreads: Int, i: Int = 1): Thread = new Thread(() => {
    if (i < maxThreads) {
      val newThread = inceptionThreads(maxThreads, i + 1)
      newThread.start()
      newThread.join() // wait to finish
    }
    println(s"Hello from thread $i")
  })

  inceptionThreads(50).start()

  var x = 0
  val threads = (1 to 100).map(_ => new Thread(() => x += 1))
  threads.foreach(_.start())

  // biggest possible value = 100
  // smallest possible value = 1
  // for all threads write x = 1 at the same time

  threads.foreach(_.join())
  println(x)


  var message = ""
  val awesomeThread = new Thread(() => {
    Thread.sleep(1000)
    message = "Scala is awesome"
  })

  message = "Scala sucks"
  awesomeThread.start()
  Thread.sleep(1001)
  awesomeThread.join() // wait for the awesome thread to join
  println(message)

  /*
  what's the value of message? almost always "Scala is awesome"
  is it guaranteed? NO!
  why? why not?
  (main thread)
    message = "Scala sucks"
    awesomeThread.start()
    sleep() - relieves execution
  (awesome thread)
    sleep() - relieves execution
  (OS gives the CPU to some important thread - takes CPU for more than 2 seconds)
  (OS gives the CPU back  to the MAIN thread)
    println("Scala sucks")
  (OS gives the CPU to awesomethread)
    message = "Scala is awesome"
 */

  // how do we fix this?
  // syncrhonizing does NOT work - wait for join
}
