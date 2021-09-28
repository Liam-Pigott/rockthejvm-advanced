package lectures.part3concurrency

import scala.util.Random
import scala.collection.mutable._

object ThreadCommunication extends App {

  /*
    Producer consumer problem

    producer -> [ ? ] -> consumer
   */

  class SimpleContainer {
    private var value: Int = 0

    def isEmpty: Boolean = value == 0
    def set(newValue: Int) = value = newValue
    def get = {
      val result = value
      value = 0
      result  // consume and reset
    }
  }

  def naiveProdCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("consumer: waiting")
      while(container.isEmpty) {
        println("Consumer: actively waiting")
      }
      println("consumer: I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("producer: computing")
      Thread.sleep(500)
      val value = 42
      println("producer: I have produced value=" + value)
      container.set(value)
    })

    consumer.start()
    producer.start()
  }

//  naiveProdCons()

  // wait and notify
  def smartProdCons(): Unit = {
    val container = new SimpleContainer
    val consumer = new Thread(() => {
      println("consumer: waiting")
      container.synchronized {
        container.wait()
      }

      // container must have some value as only thing to wake from wait is the producer
      println("consumer: I have consumed " + container.get)
    })
    val producer = new Thread(() => {
      println("producer: computing")
      Thread.sleep(2000)
      val value = 42

      container.synchronized {
        println("producer: i'm producing value=" + value)
        container.set(value)
        container.notify() // no more busy waiting
      }
    })

    consumer.start()
    producer.start()
  }

//  smartProdCons()


  /*

    producer -> [? ? ?] -> consumer

   */

  def prodConsLargeBuffer(): Unit = {
    val buffer: scala.collection.mutable.Queue[Int] = new scala.collection.mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {
      val rand = new Random()

      while(true) {
        buffer.synchronized {
          if(buffer.isEmpty) {
            println("consumer: buffer empty, waiting...")
            buffer.wait()
          }

          //there must be at least one value
          val x = buffer.dequeue()
          println("consumer: consumed " + x)

          buffer.notify() // consumer has finished consuming value so notify
        }

        Thread.sleep(rand.nextInt(500))
      }
    })

    val producer = new Thread(() => {
      val rand = new Random()
      var i = 0

      while(true) {
        buffer.synchronized {
          if(buffer.size == capacity) {
            println("producer: buffer is full, waiting for consumer...")
            buffer.wait()
          }

          // there must be at least one empty space
          println("producer: producing " + i)
          buffer.enqueue(i)

          buffer.notify() // producer has finished producing value so notify consumer

          i += 1
        }

        Thread.sleep(rand.nextInt(500))
      }
    })
    consumer.start()
    producer.start()
  }

//  prodConsLargeBuffer()

  /*

  producer1 -> [? ? ?] -> consumer1
  producer2 ----^    ^--- consumer2
  producer3 ----^    ^--- consumer3

 */
  class Consumer(id: Int, buffer: scala.collection.mutable.Queue[Int]) extends Thread  {
    override def run(): Unit = {
      val rand = new Random()
      while(true) {
        buffer.synchronized {
          /*
          producer produces value, 2 consumers waiting
          notifies one consumer
           */
          while(buffer.isEmpty) {
            println(s"consumer $id: buffer empty, waiting...")
            buffer.wait()
          }

          //there must be at least one value
          val x = buffer.dequeue()
          println(s"consumer $id: consumed " + x)

          buffer.notify() // consumer has finished consuming value so notify producer
        }

        Thread.sleep(rand.nextInt(500))
      }
    }
  }

  class Producer(id: Int, buffer: scala.collection.mutable.Queue[Int], capacity: Int) extends Thread  {
    override def run(): Unit = {
      val rand = new Random()
      var i = 0

      while(true) {
        buffer.synchronized {
          while(buffer.size == capacity) {
            println(s"producer $id: buffer is full, waiting for consumer...")
            buffer.wait()
          }

          // there must be at least one empty space
          println(s"producer $id: producing " + i)
          buffer.enqueue(i)

          buffer.notify() // producer has finished producing value so notify consumer

          i += 1
        }

        Thread.sleep(rand.nextInt(500))
      }
    }
  }

  def multiProdCons(nConsumers: Int, nProducers: Int) = {
    val buffer: scala.collection.mutable.Queue[Int] = new scala.collection.mutable.Queue[Int]
    val capacity = 3

    (1 to nConsumers).foreach(i => new Consumer(i, buffer).start())
    (1 to nProducers).foreach(i => new Producer(i, buffer, capacity).start())
  }

//  multiProdCons(3,3)


  //notfiy all
  def testNotifyAll() = {
    val bell = new Object

    (1 to 10).foreach(i => new Thread(() => {
      bell.synchronized {
        println(s"Thread $i: waiting")
        bell.wait()
        println(s"Thread $i: hooray")
      }
    }).start())

    new Thread(() => {
      Thread.sleep(1000)
      println("Announcer: Rock n roll")
      bell.synchronized {
        bell.notifyAll()
      }
    }).start()
  }

//  testNotifyAll()
  // all threads will get notified and print hooray
  // if just notify only 1 thread will pick up the lock and others will keep waiting


  // deadlock
  case class Friend(name:String) {
    def bow(other: Friend) = {
      this.synchronized {
        println(s"$this: I am bowing to my frined $other")
        other.rise(this)
        println(s"$this: my frined $other has risen")
      }
    }

    def rise(other: Friend) = {
      this.synchronized {
        println(s"$this: I am rising to my friend $other")
      }
    }

    var side = "right"
    def switchSide() = {
      if(side == "right") side = "left"
      else side = "right"
    }

    def pass(other: Friend) = {
      while(this.side == other.side) {
        println(s"$this: oh but please $other, fell free to pass")
        switchSide()
        Thread.sleep(1000)
      }
    }
  }

  val sam = Friend("sam")
  val pierre = Friend("pierre")

//  new Thread(() => sam.bow(pierre)).start() // sam's lock then pierre's lock
//  new Thread(() => pierre.bow(sam)).start() // pierre's lock then sam's lock
  // both waiting for the other to rise


  // live lock
  new Thread(() => sam.pass(pierre)).start()
  new Thread(() => pierre.pass(sam)).start()
}
