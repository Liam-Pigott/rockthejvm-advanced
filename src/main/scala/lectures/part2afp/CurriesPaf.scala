package lectures.part2afp

object CurriesPaf extends App {
  //curried functions
  val superAdder: Int => Int => Int =
    x => y => x + y

  val add3 = superAdder(3) //Int => Int = y => 3 + y
  println(add3(5))
  println(superAdder(3)(5))

  def curriedAdder(x: Int)(y: Int): Int = x + y //curried method

  val add4: Int => Int = curriedAdder(4)
//  val add4 = curriedAdder(4) //compiler will complain due to missing argument list

  //lifting = ETA-expansion

  //functions != methods (JVM limitation)
  def inc(x: Int) = x + 1
  List(1,2,3).map(inc) // ETA-expansion // converted to List(1,2,3).map(x => inc(x))

  // Partial function applications
  val add5 = curriedAdder(5) _ // force compiler to ETA-expansion: Int => Int

  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int) = x + y
  def curriedAddMethod(x: Int)(y: Int) = x + y

  val add7 = (x: Int) => simpleAddFunction(7, x)
  val add7_2 = simpleAddFunction.curried(7)
  val add7_3 = curriedAddMethod(7) _ //PAF
  val add7_4 = curriedAddMethod(7)(_) //PAF - alternative syntax to above
  val add7_5 = simpleAddMethod(7, _: Int) // alternative syntax for turning methods into function values
  val add7_6 = simpleAddFunction(7, _: Int)

  // underscores are powerful
  def concatenator(a: String, b: String, c: String) = a + b + c
  val insertName = concatenator("Hello, I'm ", _: String, ", how are you?")
  println(insertName("Liam"))

  val fillInTheBlanks = concatenator("Hello ", _: String, _: String)
  println(fillInTheBlanks("Liam", ", you're learning scala"))

  def curriedFormatter(s: String)(number: Double): String = s.format(number)
  val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)

  val simpleFormat = curriedFormatter("%4.2f") _ //lift
  val seriousFormat = curriedFormatter("%8.6f") _
  val preciseFormat = curriedFormatter("%14.12f") _

  println(numbers.map(simpleFormat))
  println(numbers.map(seriousFormat))
  println(numbers.map(preciseFormat))

  println(numbers.map(curriedFormatter("%14.12f"))) // compiler does ETA-expansion for us

  def byName(n: => Int) = n + 1
  def byFunction(f: () => Int) = f() + 1

  def method: Int = 42
  def parenMethod(): Int = 42

  byName(29) //ok
  byName(method) //ok
  byName(parenMethod()) // ok
  byName(parenMethod) // ok but beware ==> byName(parenMethod())
//  byName(() => 42) // not ok
  byName((() => 42)()) //ok
//  byName(parenMethod _) // not ok

//  byFunction(45) // not ok
//  byFunction(method) // not ok!! does not do eta expansion here as method declaration has no paren
  byFunction(parenMethod) //ok
  byFunction(() => 46) //ok
  byFunction(parenMethod _) // also works but unnecessary





}
