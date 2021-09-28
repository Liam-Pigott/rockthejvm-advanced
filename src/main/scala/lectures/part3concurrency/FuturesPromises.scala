package lectures.part3concurrency

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.duration._
import scala.util.{Failure, Random, Success, Try}

object FuturesPromises extends App {

  def calculateMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calculateMeaningOfLife // calculate on another thread
  }

  println(aFuture.value) // Option[Try[Int]]

  println("waiting on the future")
  aFuture.onComplete {
    case Success(meaningOfLife) => println(s"the meaning of life is $meaningOfLife")
    case Failure(e) => println(s"I have failed with exception: $e")
  } // called by SOME thread

  Thread.sleep(3000)

  // mini social network
  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile) = {
      println(s"${this.name} poking ${anotherProfile.name}")
    }
  }

  object SocialNetwork {
    //'database'
    val names = Map(
      "fb.id.1-zuck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.3-dummy" -> "Dummy"
    )
    val friends = Map(
      "fb.id.1-zuck" -> "fb.id.2-bill"
    )

    val random = new Random()

    //API
    def fetchProfile(id: String): Future[Profile] = Future {
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }
  }

  // client: mark to poke bill
  val mark = SocialNetwork.fetchProfile("fb.id.1-zuck")
//  mark.onComplete {
//    case Success(markProfile) => {
//      val bill = SocialNetwork.fetchBestFriend(markProfile)
//      bill.onComplete {
//        case Success(billProfile) => markProfile.poke(billProfile)
//        case Failure(e) => e.printStackTrace()
//      }
//    }
//    case Failure(e) => e.printStackTrace()
//  }

  Thread.sleep(1000)

  // functional copmposition
  // map, flatmap, filter
  val nameOnTheWall = mark.map(profile => profile.name)
  val marksBestFriend = mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
  val zucksBestFriendRestricted = marksBestFriend.filter(profile => profile.name.startsWith("Z"))

  // for-comprehensions
  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-zuck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)

  Thread.sleep(1000)

  //fallbacks
  val fallbackProfile = SocialNetwork.fetchProfile("unknown").recover {
    case e: Throwable => Profile("fb.id.3-dummy", "Forever alone")
  }

  val fallbackFetchedProfile = SocialNetwork.fetchProfile("unknown").recoverWith { // recover with another future
    case e: Throwable => SocialNetwork.fetchProfile("fb.id.3-dummy")
  }

  val fallbackResult = SocialNetwork.fetchProfile("unknown").fallbackTo(SocialNetwork.fetchProfile("fb.id.3-dummy"))


  //online banking
  case class User(name: String)
  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    val name = "RTJVM banking"

    def fetchUser(name: String): Future[User] = Future {
      //simulate fetching from db
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
      // simulate processes
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    def purchase(username: String, item: String, merchantName: String, cost: Double): String = {
      val transactionStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status

      Await.result(transactionStatusFuture, 2.seconds)
    }
  }

  println(BankingApp.purchase("Liam", "iPhone", "RTJVM store", 3000))

  //promises
  val promise = Promise[Int]() // controller over a future
  val future = promise.future

  // thread1 consumer
  future.onComplete {
    case Success(res) => println("consumer: I've received " + res)
  }

  val producer = new Thread(() => {
    println("producer: crunching numbers")
    Thread.sleep(500)
    //fulfill promise
    promise.success(42)
    println("producer: done")
  })

  producer.start()
  Thread.sleep(1000)


  // fulfill future immediately
  def fulfillImmediately[T](value: T): Future[T] = Future(value)

  // insequence
  def inSequence[A, B](first: Future[A], second: Future[B]): Future[B] = {
    first.flatMap(_ => second)
  }

  //first out of 2 futures
  def first[A](fa: Future[A], fb: Future[A]) = {
    val promise = Promise[A]

    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)

    promise.future

//    def tryComplete(promise: Promise[A], result: Try[A]) = result match {
//      case Success(res) => try {
//        promise.success(res)
//      } catch {
//        case _ =>
//      }
//      case Failure(f) => try {
//        promise.failure(f)
//      } catch {
//        case _ =>
//      }
//    }
    //    fa.onComplete(tryComplete(promise, _))
    //    fb.onComplete(tryComplete(promise, _))
    // promise already had this built-in
  }

  // last of 2 futures
  def last[A](fa: Future[A], fb: Future[A]) = {
    // promise which both futures will try to complete
    // promise which the last future will complete
    val bothPromise = Promise[A]
    val lastPromise = Promise[A]

    def checkAndComplete = (result: Try[A]) => {
      if(!bothPromise.tryComplete(result)) {
        lastPromise.complete(result)
      }
    }

    fa.onComplete(checkAndComplete)
    fb.onComplete(checkAndComplete)

    lastPromise.future
  }

  val fast = Future {
    Thread.sleep(100)
    42
  }
  val slow = Future {
    Thread.sleep(200)
    45
  }
  first(fast, slow).foreach(println)
  last(fast, slow).foreach(println)

  Thread.sleep(1000)

  //retry until
  def retryUntil[A](action: () => Future[A], condition: A => Boolean): Future[A] = {
    action()
      .filter(condition)
      .recoverWith { // failure
        case _ => retryUntil(action, condition) // recurse until success
      }
  }

  val random = new Random()
  val action = () => Future {
    Thread.sleep(100)
    val nextValue = random.nextInt(100)
    println("generated " + nextValue)
    nextValue
  }

  retryUntil(action, (x: Int) => x < 10).foreach(res => println("settled at " + res))
  Thread.sleep(10000)
}
