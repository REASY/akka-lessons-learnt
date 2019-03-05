package part1

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class BankAccount {
  private var balance = 0

  def deposit(amount: Int): Unit = {
    if (amount > 0) balance = balance + amount
  }

  def getBalance: Int = balance

  def withdraw(amount: Int): Int = {
    // Read the balance
    val oldBalance = balance
    if (0 < amount && amount <= oldBalance) {
      val newBalance = oldBalance - amount
      // Write the balance back
      balance = newBalance
      newBalance
    } else throw new Error("insufficient funds")
  }
}

object BankAccount {
  def main(args: Array[String]): Unit = {
    val initBalance: Int = 100
    val b = new BankAccount()
    b.deposit(initBalance)

    println(s"Balance is: ${b.getBalance}")
    val f1 = Future {
      val newBalance = b.withdraw(70)
      println(s"Thread[${Thread.currentThread().getId}]. Withdraw: 70, newBalance: $newBalance")
      newBalance
    }
    val f2 = Future {
      val newBalance = b.withdraw(40)
      println(s"Thread[${Thread.currentThread().getId}]. Withdraw: 40, newBalance: $newBalance")
      newBalance
    }

    val res = for {
      _ <- f1
      _ <- f2
    } yield ()

    Await.result(res, 10.seconds)

    println(s"Actual balance is: ${b.getBalance}, but should not reach this point!")
  }
}