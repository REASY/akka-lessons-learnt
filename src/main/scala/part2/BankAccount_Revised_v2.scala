package part2

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern._
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object BankAccount_Revised_v2 {
  case class Deposit(amount: BigInt)
  case class Withdraw(amount: BigInt)
  case object GetBalance
  case object Done
  case class Balance(amount: BigInt)
  case object Failed

  def main(args: Array[String]): Unit = {
    val sys = ActorSystem("BankAccount_Revised_v2")
    try {

      val b = sys.actorOf(Props[BankAccount_Revised_v2], "BankAccount")
      b ! Deposit(200)

      val sends = (1 to 5).map { _ =>
        Future {
          b ! Withdraw(10)
        }
      }
      Await.result(Future.sequence(sends), 10.seconds)
      implicit val timeout: Timeout = Timeout(5.seconds) // needed for `?` below

      val f = (b ? GetBalance).mapTo[Balance]
      val balance = Await.result(f, 10.seconds)

      println(s"Balance is $balance")
    }
    finally {
      sys.terminate()
      ()
    }
  }
}

class BankAccount_Revised_v2 extends Actor {
  import BankAccount_Revised_v2._

  def receive: PartialFunction[Any, Unit] = state(balance = 0)

  def state(balance: BigInt): PartialFunction[Any, Unit] = {
    case GetBalance =>
      sender ! Balance(balance)
    case Deposit(amount) =>
      context.become(state(balance + amount))
      sender ! Done
    case Withdraw(amount) if amount <= balance =>
      context.become(state(balance - amount))
      sender ! Done
    case _ => sender ! Failed
  }
}
