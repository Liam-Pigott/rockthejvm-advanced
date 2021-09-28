package lectures.part5typesystem

object RockingInheritance extends App {

  // convenience
  trait Writer[T] {
    def write(value: T): Unit
  }

  trait Closable {
    def close(status: Int): Unit
  }

  trait GenericStream[T] {
    def foreach(f: T => Unit): Unit
  }

  def processStream[T](stream: GenericStream[T] with Writer[T] with Closable): Unit = {
    stream.foreach(println)
    stream.close(0)
  }

  // diamond problem
  trait Animal {
    def name: String
  }
  trait Lion extends Animal {
    override def name: String = "Lion"
  }
  trait Tiger extends Animal {
    override def name: String = "Tiger"
  }
//  class Mutant extends Lion with Tiger {
//    override def name: String = "ALIEN" // compiles fine
//  }
  class Mutant extends Lion with Tiger // no conflict for name method
  val m = new Mutant
  println(m.name) // prints tiger

  /*
  Mutant extends Animal with {override def name: String = "Lion" }
  with Animal with { override def name: String = "Tiger" }

  LAST OVERRIDE GETS PICKED
   */

  // super problem + type linearization
  trait Cold {
    def print = println("cold")
  }

  trait Green extends Cold {
    override def print: Unit = {
      println("green")
      super.print
    }
  }

  trait Blue extends Cold {
    override def print: Unit = {
      println("blue")
      super.print
    }
  }

  class Red {
    def print = println("red")
  }

  class White extends Red with Green with Blue {
    override def print: Unit = {
      println("white")
      super.print
    }
  }

  val color = new White
  color.print

  /*

              Cold
             /    \
            /      \
   Red    Green    Blue
     \       |       /
       \     |      /
         \   |     /
            White

  Cold = AnyRef with <Cold>
  Green
    = Cold with <Green>
    = AnyRef with <Cold> with <Green>
  Blue
    = Cold with <Blue>
    = AnyRef with <Cold> with <Blue>
  Red = AnyRef with <Red>

  White = Red with Green with Blue with <White>
        = AnyRef with <Red>
        with (AnyRef with <Cold> with <Green>)
        with (AnyRef with <Cold> with <Blue>)
        with <White>

  Compiler skips anything seen before so:
       = AnyRef with Red with Cold with Green with Blue with White // type linearization

       work from right to left so:
       print white -> call super.print
       print blue -> call super.print
       print green -> call super.print
       print cold

   */

}
