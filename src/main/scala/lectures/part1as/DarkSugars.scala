package lectures.part1as

import scala.util.Try

object DarkSugars extends App{

  // syntax sugar #1: methods with single params
  def singleArgMethod(arg: Int): String = s"$arg little ducks..."

  val description = singleArgMethod {
    // write some complex code
    42
  }

  val aTryInstance = Try {
    throw new RuntimeException // apply method from try with this arg
  }

  List(1,2,3).map { x =>
    x + 1
  }

  // syntax sugar #2: single abstract method pattern
  trait Action {
    def act(x: Int): Int
  }

  val anInstance: Action = new Action {
    override def act(x: Int): Int = x + 1
  }

  val aFunkyInstance: Action = (x: Int) => x + 1 // compiler magic

  // example: Runnables
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("Hello Scala")
  })

  val aSweeterThread = new Thread(() => println("Sweet, Scala!"))

  abstract class AnAbstractType {
    def implemented: Int = 23
    def f(a: Int): Unit
  }

  val anAbstractInstance: AnAbstractType = (a: Int) => println("sweet")

  //syntax sugar #3: the :: and #:: methods are special

  val prependedList = 2 :: List(3, 4)
  // 2.::List(3,4) - nope
  // List(3,4).::(2) - its this

  // scala spec: last char decides associativity
  // ends in : = right associative, else left associative
  1 :: 2 :: 3 :: List(4,5)
  List(4,5).::(3).::(4).::(5) // equivalent

  class MyStream[T] {
    def -->:(value: T): MyStream[T] = this  // actual imp here
  }

  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int]

  // syntax sugar #4: multi word method naming

  class TeenGirl(name: String) {
    def `and then said`(gossip: String) = println(s"$name said $gossip")
  }

  val lilly = new TeenGirl("Lilly")
  lilly `and then said` "Scala is so sweet!"

  //syntax sugar #5: infix types
  class Composite[A, B]
  val composite: Composite[Int, String] = ???
  val composite2: Int Composite String = ???

  class -->[A, B]
  val towards: Int --> String = ???

  // syntax sugar #6: update() is very special, much like apply
  val anArray = Array(1,2,3)
  anArray(2) = 7 // rewritten to anArray.update(2,7)
  // used in mutable collections

  // syntax sugar #7: setters for mutable containers
  class Mutable {
    private var internalMember: Int = 0
    def member = internalMember // getter
    def member_=(value: Int): Unit = internalMember = value // setter
  }

  val mutableContainer = new Mutable
  mutableContainer.member = 42 // rewritten as mutableContainer.member_=(42)




}
