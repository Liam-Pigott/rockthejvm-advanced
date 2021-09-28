package lectures.part4implicits

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MagnetPattern extends App {

  //method overloading

  class P2PRequest
  class P2PResponse
  class Serializer[T]
  trait Actor {
    def receive(statusCode: Int): Int
    def receive(request: P2PRequest): Int
    def receive(response: P2PResponse): Int
    def receive[T : Serializer](message: T): Int
    def receive[T : Serializer](message: T, statusCode: Int): Int
    def receive(future: Future[P2PRequest]): Int
//    def receive(future: Future[P2PResponse]): Int // type erasure compile error
  }

  /*
  type erasure
  lifting doesn't work for all overloads

  val receiveFV = receive _ // receive what?

  code duplication
  type inference and default args
  actor.receive(?!)
   */

  trait MessageMagnet[Result] {
    def apply(): Result
  }

  def receive[R](magnet: MessageMagnet[R]): R = magnet()

  implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int] {
    def apply(): Int = {
      println("Handling p2p request")
      42
    }
  }
  implicit class FromP2PResponse(request: P2PResponse) extends MessageMagnet[Int] {
    def apply(): Int = {
      println("Handling p2p response")
      24
    }
  }

  receive(new P2PRequest)
  receive(new P2PResponse)

  // no more type erasure problems
  implicit class FromResponseFuture(future: Future[P2PResponse]) extends MessageMagnet[Int] {
    override def apply(): Int = 2
  }

  implicit class FromRequestFuture(future: Future[P2PRequest]) extends MessageMagnet[Int] {
    override def apply(): Int = 3
  }

  println(receive(Future(new P2PRequest)))
  println(receive(Future(new P2PResponse)))

  // lifting works
  trait MathLib {
    def add1(x: Int): Int = x + 1
    def add1(s: String): Int = s.toInt + 1
  }

  // magentize
  trait AddMagnet { // no type param otherwise call won't know which type _ would apply to
    def apply(): Int
  }

  def add1(magnet: AddMagnet): Int = magnet()

  implicit class AddInt(x: Int) extends AddMagnet {
    override def apply(): Int = x + 1
  }

  implicit class AddString(s: String) extends AddMagnet {
    override def apply(): Int = s.toInt + 1
  }

  val addFV = add1 _ // can now use with multiple types
  println(addFV(1))
  println(addFV("3"))

  /*
  Drawbacks
  - verbose
  - harder to read
  - can't name or place default args
  - call by name doesn't work correctly
   */

  class Handler {
    def handle(s: => String) = {
      println(s)
      println(s)
    }
  }

  trait HandleMagnet {
    def apply(): Unit
  }

  def handle(magnet: HandleMagnet) = magnet()

  implicit class StringHandle(s:  => String) extends HandleMagnet {
    override def apply(): Unit = {
      println(s)
      println(s)
    }
  }

  def sideEffectMethod(): String = {
    println("Hello, Scala")
    "haha"
  }

//  handle(sideEffectMethod())
  handle {
    println("Hello, Scala")
    "haha" // only this value is converted to magnet class
  }

}
