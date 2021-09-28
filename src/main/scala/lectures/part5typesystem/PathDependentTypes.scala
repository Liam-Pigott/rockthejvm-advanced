package lectures.part5typesystem

object PathDependentTypes extends App {

  class Outer {
    class Inner
    object InnerObject
    type InnerType

    def print(inner : Inner) = println(inner)
    def printGeneral(i: Outer#Inner) = println(i)
  }

  def aMethod: Int = {
    class HelperClass
    type HelperType = String // anywhere other than in classes/traits, types must be aliases
    2
  }

  // per-instance
  val o = new Outer
//  val inner = new Inner // invalid
//  val inner = new Outer.Inner // invalid
  val inner = new o.Inner // o.Inner is a type

  val oo = new Outer
//  val otherInner: oo.Inner = new o.Inner // o and oo are different types

  o.print(inner)
//  oo.print(inner) // not ok

  // path-dependent types

  // Outer#Inner
  o.printGeneral(inner)
  oo.printGeneral(inner)

  trait ItemLike {
    type Key
  }

  trait Item[K] extends ItemLike {
    type Key = K
  }
  trait IntItem extends Item[Int]
  trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ???

  get[IntItem](42) // ok
  get[StringItem]("home") // ok

//  get[IntItem]("scala") // not ok


}
