package lectures.part5typesystem

object Variance extends App {

  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // What is variance?
  // "inheritance" - type substitution of generics

  class Cage[T]
  // yes - covariance
  class CCage[+T]
  val ccage: CCage[Animal] = new CCage[Cat]

  // no - invariance
  class ICage[T]
//  val icage: ICage[Animal] = new ICage[Cat]

  // hell no - contravariance
  class XCage[-T]
  val xcage: XCage[Cat] = new XCage[Animal]

  class InvaraintCage[T](animal: T)
  class CovaraintCage[+T](val animal: T)

//  class ContaravaraintCage[-T](val animal: T) // compiler complains
  /*
    val catCage: XCage[Cat] = new XCage[Animal](new Crocodile)
   */

//  class CovariantVariableCage[+T](var animal: T) // types of vars are in contravariant position
  /*
    val ccage: CCage[Animal] = new CCage[Cat](new Cat)
    ccage.animal = new Crocodile
   */

//  class ContravariantVariableCage[-T](var animal: T) // also in covariant position
  /*
    val catCage: XCage[Cat] = new XCage[Animal](new Crocodile)
   */

  class InvariantVariableCage[T](var animal: T) // ok

//  trait AnotherCovariantCage[+T] {
//    def addAnimal(animal: T) // method args in contravariant position
//  }
  // val ccage: CCage[Animal] = new CCage[Dog]
  // ccage.add(new Cat)

  class AnotherContravariantCage[-T] {
    def addAnimal(animal: T) = true
  }
  val acc: AnotherContravariantCage[Cat] = new AnotherContravariantCage[Animal]
  acc.addAnimal(new Cat)
  class Kitty extends Cat
  acc.addAnimal(new Kitty)

  class MyList[+A] {
    def add[B >: A](element: B): MyList[B] = new MyList[B] // B super type of A - widening the type
  }
  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty)
  val moreAnimals = animals.add(new Cat)
  val evenMoreAnimals = moreAnimals.add(new Dog)

  // return types
  class PetShop[-T] {
//    def get(isItAPuppy: Boolean): T // METHOD RETURN TYPES ARE IN COVARIANT POSITION
    /*
    val catShop = new PetShop[Animal] {
      def get(isItAPuppy: Boolean): Animal = new Cat
    }

    val dogShop: PetShop[Dog] = catShop
    dogShop.get(true) // EVIL CATS
     */

    def get[S <: T](isItAPuppy: Boolean, defaultAnimal: S): S = defaultAnimal
  }

  val shop: PetShop[Dog] = new PetShop[Animal]
//  val evilCat = shop.get(true, new Cat)
  class TerraNova extends Dog
  val bigFurry = shop.get(true, new TerraNova)

  /*
    Big rule
    - method args are in CONTRAVARIANT position
    - return types are in covariant position
   */
  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle
  class IList[T]

  //invariant
  class IParking[T](things: List[T]) {
    def park(vehicle: T): IParking[T] = ???
    def impound(vehicles: List[T]): IParking[T] = ???
    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => IParking[S]): IParking[S] = ???
  }

  //covariant
  class CParking[+T](things: List[T]) {
    def park[S >: T](vehicle: S): CParking[S] = ???
    def impound[S >: T](vehicles: List[S]): CParking[S] = ???
    def checkVehicles(conditions: String): List[T] = ???

    def flatMap[S](f: T => CParking[S]): CParking[S] = ???
  }

  // contravariant
  class XParking[-T](things: List[T]) {
    def park(vehicle: T): XParking[T] = ???
    def impound(vehicles: List[T]): XParking[T] = ???
    def checkVehicles[S <: T](conditions: String): List[S] = ??? // list is covariant so need to add [S <: T]

    def flatMap[R <: T, S](f: R => XParking[S]): XParking[S] = ???
  }

  /*
  Rule of thumb
  - use covariance = collection of things
  - use contravariance = group of actions
   */

  /*
  use another version of list API that is invariant
  invariant stays the same
   */
  //covariant
  class CParking2[+T](things: IList[T]) {
    def park[S >: T](vehicle: S): CParking2[S] = ???
    def impound[S >: T](vehicles: IList[S]): CParking2[S] = ???
    def checkVehicles[S >: T](conditions: String): IList[S] = ???
  }

  // contravariant
  class XParking2[-T](things: IList[T]) {
    def park(vehicle: T): XParking2[T] = ???
    def impound[S <: T](vehicles: IList[S]): XParking2[S] = ???
    def checkVehicles[S <: T](conditions: String): IList[S] = ???
  }
}