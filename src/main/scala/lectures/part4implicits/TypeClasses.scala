package lectures.part4implicits

object TypeClasses extends App {

  trait HTMLWritable {
    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def toHtml: String = s"<div>$name ($age yo.) <a href=$email/> </div>"
  }
  // only one implementation

  User("John", 32, "john@rtjvm.com").toHtml

  // pattern matching
  object HTMLSerializePM {
    def serilaizeToHtml(value: Any) = value match {
      case User(n, a, e) =>
      case _ =>
    }
  }
  /*
  lost type safety
  need to modify code whenever a new data structure is introduced
  still one implementation per type.
   */

  //better design
  trait HTMLSerializer[T] {  // TYPE class
    def serialize(value: T): String
  }
  implicit object UserSerializer extends HTMLSerializer[User] { // TYPE class instance
    override def serialize(user: User): String = s"<div>${user.name} (${user.age} yo.) <a href=${user.email}/> </div>"
  }

  val john = User("John", 42, "john@rtjvm.com")
  println(UserSerializer.serialize(john))

  // can define serializers for other tpes
  import java.util.Date
  object DateSerializer extends HTMLSerializer[Date] {
    override def serialize(date: Date): String = s"<div>${date.toString} </div>"
  }

  // can define multiple serializers
  object PartialUserSerializer extends HTMLSerializer[User] {
    override def serialize(user: User): String = s"<div>${user.name}</div>"
  }


  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)

    def apply[T](implicit serializer: HTMLSerializer[T]) = serializer
  }

  implicit object IntSerializer extends HTMLSerializer[Int] {
    override def serialize(value: Int): String = s"<div style='color:blue'>$value</div>"
  }



  println(HTMLSerializer.serialize(42))
  // make user serializer implicit so we can also pass User type to HTMLSerializer
  println(HTMLSerializer.serialize(john))
  println(HTMLSerializer[User].serialize(john)) // access to entire type class interface


  implicit class HTMLEnrichment[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String = serializer.serialize(value)
  }

  println(john.toHTML(UserSerializer)) // println(new HTMLEnrichment[User](john).toHTML(UserSerializer))
  // make toHTML have implicit serializer can also write as
  println(john.toHTML)

  /*
  extend to new types
  choose implementation by importing implicit into local scope or passing explicitly
   */
  println(2.toHTML)
  println(john.toHTML(PartialUserSerializer))

  // context bounds
  def htmlBoilerPlate[T](content: T)(implicit serializer: HTMLSerializer[T]): String =
    s"<html><body>${content.toHTML(serializer)}</body></html>"

//  def htmlSugar[T : HTMLSerializer](content: T): String = {
//    s"<html><body>${content.toHTML}</body></html>"
//  }

  def htmlSugar[T : HTMLSerializer](content: T): String = {
    val serializer = implicitly[HTMLSerializer[T]]
    s"<html><body>${content.toHTML(serializer)}</body></html>"
  }

  //implicitly
  case class Permissions(mask: String)
  implicit val defaultPermissions: Permissions = Permissions("1111")

  // in some other part of the code
  val standardPerms = implicitly[Permissions]
}
