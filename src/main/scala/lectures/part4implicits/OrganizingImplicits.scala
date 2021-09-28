package lectures.part4implicits

object OrganizingImplicits extends App {

  implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
//  implicit val normalOrdering: Ordering[Int] = Ordering.fromLessThan(_ < _) //compiler will complain
  println(List(2,1,3,5,4).sorted)   //scala.PreDef

  /*
  Implicits (used as implicit params):
  - val/var
  - object
  - accessor methods = defs with no parentheses
   */

//  object Person { // needs to be related to whatever uses the ordering
//    implicit val alphabeticOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
//  }
//  implicit val ageOrdering: Ordering[Person] = Ordering.fromLessThan((a,b) => a.age.compareTo(b.age) < 0)
  case class Person(name: String, age: Int)

  val persons = List(
    Person("Liam", 28),
    Person("Ben", 24),
    Person("John", 42)
  )
//  println(persons.sorted)

  /*

  Implicit scope:
  - local scope
  - imported scope e.g. global EC
  - companion objects of all types involved in method signature
    - List
    - Ordering
    - all types involved = A or any supertype

    Best practices:
    - if there is a single possible value, define in companion object
    - more than one value but single good one, define good implicit in companion, else local scope or elsewhere
   */

  object AlphabeticNameOrdering {
    implicit val alphabeticOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }

  object AgeOrdering {
    implicit val ageOrdering: Ordering[Person] = Ordering.fromLessThan((a,b) => a.age.compareTo(b.age) < 0)
  }

  import AlphabeticNameOrdering._
  println(persons.sorted)

  /*
  totalPrice = common 50%
  count = 25%
  unit price = 25%
   */

  case class Purchase(nUnits: Int, unitPrice: Double)
  object Purchase {
    implicit val totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((a,b) => a.nUnits * a.unitPrice < b.nUnits * b.unitPrice)
  }
  object UnitCountOrdering {
    implicit val unitCountOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.nUnits < _.nUnits)
  }
  object UnitPriceOrdering {
    implicit val unitPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.unitPrice < _.unitPrice)
  }


}
