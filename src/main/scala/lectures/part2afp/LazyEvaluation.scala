package lectures.part2afp

object LazyEvaluation extends App {

  // lazy delays evaluation of a val - when needed
  lazy val x: Int = throw new RuntimeException
//  println(x) // will throw

  lazy val y: Int = {
    println("Hello")
    42
  }

  println(y) // prints hello 42
  println(y) // prints 42

  //examples of implications
  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }
  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition
  println(if(simpleCondition && lazyCondition) "yes" else "no") // lazy condition not evaluated because simpleCondition is false

  //in conjunction with call by name
  def byNameMethod(n: => Int): Int = n + n + n + 1
  def retrieveMagicValue = {
    println("waiting")
    Thread.sleep(1000)
    42
  }

  println(byNameMethod(retrieveMagicValue))

  //Call by need
  def byNameMethodLazy(n: => Int): Int = {
    lazy val t = n // only evaluated once
    t + t + t + 1
  }

  // filtering with lazy vals
  def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }

  val numbers = List(1,35,40,5,23)
  val lt30 = numbers filter lessThan30
  val gt20 = numbers filter greaterThan20
  println(lt30)
  println(gt20)

  val lt30lazy = numbers withFilter lessThan30 // lazy vals under the hood
  val gt20lazy = lt30lazy withFilter greaterThan20
  println
  gt20lazy foreach println
  println(gt20lazy)

  // for comprehensions use withfilter with guards
  for {
    a <- List(1,2,3) if a % 2 == 0 // use lazy vals
  } yield a + 1
  // same as
  List(1,2,3).withFilter(_ % 2 == 0).map(_ + 1)

}
