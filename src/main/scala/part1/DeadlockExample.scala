package part1

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object A {
  lazy val a0 = B.b
  lazy val a1 = 42
}

object B {
  lazy val b = A.a1
}

object DeadlockExample {
  def main(args: Array[String]): Unit = {
    val f1 = Future {
      val t = Thread.currentThread()
      println(s"Thread[${t.getId}]: ${t.getName}")
      A.a0
    }
    val f2 = Future {
      val t = Thread.currentThread()
      println(s"Thread[${t.getId}]: ${t.getName}")
      B.b
    }
    println("Created futures f1 & f2")
    println("Waiting...")
    val r = Await.result(Future.sequence(Seq(f1, f2)), 60.seconds)
    println(s"Done. r: ${r}")
  }
}
