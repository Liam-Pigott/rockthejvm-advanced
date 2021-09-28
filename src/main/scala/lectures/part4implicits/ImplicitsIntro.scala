package lectures.part4implicits

object ImplicitsIntro extends App {

  val pair = "Liam" -> "111"
  val intPair = 1 -> 2

  case class Person(name: String) {
    def greet = s"Hi, my name is $name"
  }

  implicit def fromStringToPerson(str: String): Person = Person(str)

  println("Peter".greet) // greet doesn't exist for string class, looks for implicit to satisfy
  // - println(fromStringToPerson("Peter").greet)

  class A {
    def greet: Int = 2
  }

//  implicit def fromStringToA(str: String): A = new A // compiler will complain as there are 2 implicit implementations of greet
  def increment(x: Int)(implicit amount: Int) = x + amount
  implicit val defaultAmount: Int = 10

  increment(2)
  // not default args
}
