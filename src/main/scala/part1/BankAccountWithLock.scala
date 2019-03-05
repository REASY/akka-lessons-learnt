package part1

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class BankAccountWithLock {
  private var balance = 0

  def deposit(amount: Int): Unit = this.synchronized {
    if (amount > 0) balance = balance + amount
  }

  def getBalance: Int = this.synchronized { balance }

  def withdraw(amount: Int): Int = this.synchronized {
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

object BankAccountWithLock {
  def main(args: Array[String]): Unit = {
    val initBalance: Int = 100
    val b = new BankAccountWithLock()
    b.deposit(initBalance)

    val amountToWithdraw: Int = 5
    val numOfThreads: Int = 5
    val expectedBalance: Int = initBalance - numOfThreads * amountToWithdraw

    println(s"Balance is: ${b.getBalance}")
    val futures = (1 to numOfThreads).map { _ =>
      Future {
        val newBalance = b.withdraw(amountToWithdraw)
        println(s"Thread[${Thread.currentThread().getId}]. Withdraw: $amountToWithdraw, newBalance: $newBalance")
      }
    }
    Await.result(Future.sequence(futures), 10.seconds)

    println(s"Actual balance is: ${b.getBalance}, expected: $expectedBalance. Are equal? => ${b.getBalance == expectedBalance}")
  }
}