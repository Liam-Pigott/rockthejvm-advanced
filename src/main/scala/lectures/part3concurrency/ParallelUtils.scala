package lectures.part3concurrency

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference
import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.ForkJoinTaskSupport
import scala.collection.parallel.immutable.ParVector

object ParallelUtils extends App {

  // parallel collections
  val parallelList = List(1,2,3).par

  val aParVector = ParVector[Int](1,2,3)

  def measure[T](operation: => T): Long = {
    val time = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - time
  }

  val list = (1 to 100).toList
  val serialTime = measure {
    list.map(_ + 1)
  }
  val parallelTime = measure {
    list.par.map(_ + 1)
  }

  println(s"serial time: $serialTime")
  println(s"parallel time: $parallelTime")

  /*
   parallel performs faster for big collections.
   for small collections its not as time efficient as it needs to spin up threads which is expensive

   Map-reduce model
   - split elements into chunks - Splitter
   - operation on chunks performed by separate threads
   - recombine - Combiner
   */

  // reduce and fold not always safe
  println(List(1,2,3).reduce(_ - _))
  println(List(1,2,3).par.reduce(_ - _)) // par - we don't know the order - non-associative operators

  //synchronization
  var sum = 0
  List(1,2,3).par.foreach(sum += _) // multiple threads might access value at the same time - no guarantee
  println(sum)

  //config
  aParVector.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(2))
  /*
  alternatives:
  - ThreadPoolTaskSupport
  - ExecutionContextTaskSupport
   */

  // atomic ops and references - cannot be divided - all or nothing
  val atomic = new AtomicReference[Int](2)
  val currentValue = atomic.get() // thread safe read
  atomic.set(4) // thread safe write

  atomic.getAndSet(5) // thread safe combo read and write

  atomic.compareAndSet(38, 56) // if value == 38, set 56 - reference equality

  atomic.updateAndGet(_ + 1) // thread safe function run
  atomic.getAndUpdate(_ + 1)

  atomic.accumulateAndGet(12, _ + _) // thread safe acc
  atomic.getAndAccumulate(12, _ + _)

}
