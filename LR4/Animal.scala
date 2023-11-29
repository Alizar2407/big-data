package zoo

import scala.util.Random
import org.apache.zookeeper._
import org.apache.zookeeper.data.Stat

case class Animal(name:String, hostPort:String, root:String, partySize:Integer) extends Watcher {
  val clientPort = Random.nextInt(101) + 3000
  println(s"Using port $clientPort")
  
  val zk = new ZooKeeper(hostPort, clientPort, this)
  val mutex = new Object()
  val animalPath = root+"/"+name

  if (zk == null) throw new Exception("ZK is NULL.")
  
  override def process(event: WatchedEvent): Unit = {
    mutex.synchronized {
      println(s"Event from keeper: ${event.getType}")

      val party = zk.getChildren(root, this)
      if (party.size() >= partySize) {
        leave()
      }
      else {
        println(s"not enough animals (${party.size()}/$partySize)")
      }
    }
  }
  
  def enter():Unit = {
    zk.create(animalPath, Array.emptyByteArray, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL)
    println(s"${name} entered.")
    mutex.synchronized {
      mutex.wait()
    }
  }

  def leave():Unit = {
    mutex.synchronized{
      val stat: Stat = zk.exists(animalPath, true)

      if (stat != null) {
        for (i <- 1 to 5 + Random.nextInt(5)) {
          val sleepTime = 1000
          Thread.sleep(sleepTime)
          println(s"${name} is running...")
        }

        zk.delete(animalPath, stat.getVersion)

        println(s"$name left zoo")
      }

      zk.close()

      mutex.notifyAll()
    }
  }
}
