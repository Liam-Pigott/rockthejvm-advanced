package lectures.part4implicits

import java.{util => ju}

object ScalaJavaConversion extends App {

  import scala.jdk.CollectionConverters._

  val javaSet: ju.Set[Int] = new ju.HashSet[Int]()
  (1 to 5).foreach(javaSet.add)
  println(javaSet)

  val scalaSet = javaSet.asScala
  println(scalaSet)

  import collection.mutable._
  val numbersBuffer = ArrayBuffer[Int](1,2,3)
  val juNumbersBuffer = numbersBuffer.asJava

  println(juNumbersBuffer.asScala eq numbersBuffer) // reference is equal

  val numbersList = List(1,2,3)
  val juNumber = numbersList.asJava
  val backToScala = juNumber.asScala
  println(backToScala eq juNumber) // conversions between immutable type in scala to java and back will change type/reference
  // scala List[Int] -> Java List[Int] -> scala Buffer[Int]

//  juNumber.add(7) // throws UnsupportedOperationException

  class ToScala[T](value: => T) {
    def asScala: T = value
  }

  implicit def asScalaOptional[T](o: ju.Optional[T]): ToScala[Option[T]] = new ToScala[Option[T]](
    if(o.isPresent) Some(o.get)
    else None
  )

  val juOptional: ju.Optional[Int] = ju.Optional.of(2)
  val scalaOption = juOptional.asScala
  println(scalaOption)
}
