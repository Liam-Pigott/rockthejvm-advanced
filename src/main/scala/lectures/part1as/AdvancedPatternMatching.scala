package lectures.part1as

object AdvancedPatternMatching extends App{

  val numbers = List(1)
  val description = numbers match {
    case head :: Nil => println(s"The only element is $head")
    case _ =>
  }

  /* pattern matching patterns
   - constants
   - wildcards
   - case classes
   - tuples
   - some special magic like above
   */

  class Person(val name: String, val age: Int)
  object Person { // can be named whatever but in practice, match the class
    def unapply(p: Person): Option[(String, Int)] = {
      if(p.age < 21) None
      else Some((p.name, p.age))
    } // can now pattern match Person

    def unapply(age: Int): Option[String] = Some(if(age < 21) "minor" else "major")
  }

  val bob = new Person("Bob", 24)
//  val bob = new Person("Bob", 20) // match error

  val greeting = bob match {
    case Person(n, a) => s"Hi, my name is $n and I am $a years old"
  }
  println(greeting)

  val legalStatus = bob.age match {
    case Person(status) => s"My legal status is $status"
  }

  println(legalStatus)

  /*
  Exercise
   */

  object even {
    def unapply(arg: Int): Option[Boolean] = if(arg % 2 == 0) Some(true) else None
  }
  object singleDigit {
    def unapply(arg: Int): Option[Boolean] = if (arg > -10 && arg < 10) Some(true) else None
  }

  val n: Int = 12
  val mathProperty = n match {
    case x if x < 10 => "single digit"
    case x if x % 2 == 0 => "even number"
    case _ => "no property"
  }

  val mathPropertyCustomMatcher = n match {
    case singleDigit(_) => "single digit"
    case even(_) => "even number"
    case _ => "no property"
  }
  println(mathPropertyCustomMatcher)

  object evenReduced {
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }
  object singleDigitReduced {
    def unapply(arg: Int): Boolean = arg > -10 && arg < 10
  }
  val mathPropertyCustomMatcherReduced = n match {
    case singleDigitReduced() => "single digit"
    case evenReduced() => "even number"
    case _ => "no property"
  }

  //infix patterns
  case class Or[A, B](a: A, b: B)
  val either = Or(2, "two")
  val humanDescription = either match {
//    case Or(number, string) => s"$number is written as $string"
    case number Or string => s"$number is written as $string"
  }

  println(humanDescription)

  //decomposing sequences
  val vararg = numbers match {
    case List(1, _*) => "starting with 1" //multiple values
  }

  abstract class MyList[+A] {
    def head: A = ???
    def tail: MyList[A] = ???
  }

  case object Empty extends MyList[Nothing]
  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    // unapplySeq needed for var arg _* matching
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if(list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty)))
  val decomposed = myList match {
    case MyList(1, 2, _*) => s"starting with 1 and 2"
    case _ => s"something else"
  }

  println(decomposed)

  //custom return type for unapply
  //isEmpty: Boolean, get: Something
  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      override def isEmpty: Boolean = false
      override def get: String = person.name
    }
  }

  println(bob match {
    case PersonWrapper(n) => s"This persons name is $n"
    case _ => "an alien"
  })
}
