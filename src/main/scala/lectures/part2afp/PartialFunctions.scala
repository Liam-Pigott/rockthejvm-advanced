package lectures.part2afp

object PartialFunctions extends App{

  val aFunction = (x: Int) => x + 1 // Function1[Int, Int] === Int => Int

  val aFussyFunction = (x: Int) =>
    if(x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new FunctionNotApplicableException

  class FunctionNotApplicableException extends RuntimeException

  val aNicerFussyFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }
  //{1,2,5} => Int
  // partial function from Int to Int

  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
    //partial function value
  }

  println(aPartialFunction(2))
//  println(aPartialFunction(234)) //throws

  // PF utils
  println(aPartialFunction.isDefinedAt(42)) // == false, check if partial function can be run with these args

  //lift
  val lifted = aPartialFunction.lift // Int => Option[Int]
  println(lifted(2))
  println(lifted(68))

  //OrElse
  val chainedPf = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  }

  println(chainedPf(2))
  println(chainedPf(45))

  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  // HOFs accept partial functions
  val aMappedList = List(1,2,3).map {
    case 1 => 42
    case 2 => 45
    case 3 => 1563
    case 5 => 1563 // would crash as 5 isn't in the args
  }

  println(aMappedList)

  /*
    Partial functions can only have one parameter type
   */

  /**
   *
   * 1 - construct a PF instance
   * 2 - chatbot as PF
   *
   */

  val manualFussyFunction = new PartialFunction[Int, Int] {
    override def apply(x: Int): Int = x match {
      case 1 => 42
      case 2 => 45
      case 3 => 1563
    }

    override def isDefinedAt(x: Int): Boolean = x == 1 || x == 2 || x == 5
  }

  val chatbot: PartialFunction[String, String] = {
    case "hello" => "Hi, my name is chatbot"
    case "goodbye" => "farewell"
    case "call mum" => "unable to find phone"
  }

//  scala.io.Source.stdin.getLines().map(chatbot).foreach(println)

}
