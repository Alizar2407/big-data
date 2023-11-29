package zoo

import scala.util.Random

// run with: sbt "runMain zoo.Main monkey localhost:2181 3"
object Main {
  def main(args: Array[String]): Unit = {
    println("Starting animal runner")

    val Seq(animalName, hostPort, partySize) = args.toSeq

    val animal = Animal(animalName, hostPort, "/zoo", partySize.toInt)

    try {
      animal.enter()
    } catch {
      case e: Exception => println("Animal was not permitted to the zoo. " + e)
    }
  }
}
