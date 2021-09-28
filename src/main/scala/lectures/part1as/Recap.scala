package lectures.part1as

import scala.annotation.tailrec

object Recap extends App{

  val aCondition: Boolean = false
  val aConditionedVal = if(aCondition) 42 else 45

  val aCodeBlock = {
    if(aCondition) 54
    56
  }

  // Unit - side effects - void
  val theUnit = println("Hello scala")

  //functions
  def aFunction(x: Int): Int = x + 1

  //recursion: stack and tail
  @tailrec def factorial(n: Int, acc: Int): Int =
    if(n <= 0) acc
    else factorial(n - 1, n * acc)

  // OOP
  class Animal
  class Dog extends Animal
  val aDog: Animal = new Dog // subtyping polymorphism

  trait Carnivore {
    def eat(a: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = println("Crunch")
  }

  // method notations
  val aCroc = new Crocodile
  aCroc eat aDog // natural language DDL

  // 1 + 2 compiles to 1.+(2)

  // anonymous classes
  val aCarnivore = new Carnivore {
    override def eat(a: Animal): Unit = println("Roar")
  }

  // generics
  abstract class MyList[+A]
  //singleton and companions
  object MyList

  // case classes
  case class Person(name: String, age: Int)

  // exceptions and try/catch/finally
//  val throwsException = throw new RuntimeException // Nothing type
  val aPotentialFailure = try {
    throw new RuntimeException
  } catch {
    case e: Exception => "Caught an exception"
  } finally {
    println("Some log")
  }

  // functional programming
  val incrementer = new Function[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }
  incrementer(1)

  val anonymousIncrementer = (x: Int) => x + 1
  List(1,2,3).map(anonymousIncrementer) // HOF

  // for-comprehension
  val pairs = for {
    num <- List(1,2,3)
    char <- List('a', 'b', 'c')
  } yield num + "-" + char

  // Scala collections: Seqs, Arrays, Lists, Vectors, Mpas, Tuples
  val aMap = Map(
    "Liam" -> 123,
    "Daniel" -> 555
  )

  // "collections" - more like abstract computations - Options/Try
  val anOption = Some(2)

  // pattern matching
  val x = 2
  val order = x match {
    case 1 => "first"
    case 2 => "second"
    case 3 => "third"
    case _ => x + "th"
  }

  val bob = Person("Bob", 22)
  val greeting = bob match {
    case Person(n, _) => s"Hi, my name is $n"
  }

  println(greeting)


}
