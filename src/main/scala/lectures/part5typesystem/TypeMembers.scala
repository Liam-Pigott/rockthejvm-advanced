package lectures.part5typesystem

object TypeMembers extends App {

  class Animal
  class Dog extends Animal
  class Cat extends Animal

  class AnimalCollection {
    type AnimalType // abstract type member
    type BoundedAnimal <: Animal // abstract type member upper bounded with Animal
    type SuperBoundedAnimal >: Dog <: Animal
    type AnimalC = Cat
  }

  val ac = new AnimalCollection
//  val dog: ac.AnimalType = ???
//  val cat: ac.BoundedAnimal = new Cat // compiler complains as it doesn't know what bounded animal is, can't be sure its a Cat

  val pup: ac.SuperBoundedAnimal = new Dog
  val cat: ac.AnimalC = new Cat

  type CatAlias = Cat
  val anotherCat: CatAlias = new Cat

  // alternative to generics
  trait MyList {
    type T
    def add(element: T): MyList
  }

  class NonEmptyList(value: Int) extends MyList {
    override type T = Int
    def add(element: Int): MyList = ???
  }

  // .type
  type CatsType = cat.type
  val newCat: CatsType = cat
//  new CatsType // compiler can't find if this type is constructable or not

  trait MList {
    type A
    def head: A
    def tail: MList
  }

  trait ApplicableToNumbers {
    type A <: Number
  }

  /*
  enforcing types to certain types only
   */
  // not ok
  class CustomList(hd: String, tl: CustomList) extends MList {
    type A = String
    def head = hd
    def tail = tl
  }

  // ok
  class IntList(hd: Integer, tl: IntList) extends MList with ApplicableToNumbers { // changed to Integer which extends Number, Scala Int doesn't
    type A = Integer
    def head = hd
    def tail = tl
  }
}
