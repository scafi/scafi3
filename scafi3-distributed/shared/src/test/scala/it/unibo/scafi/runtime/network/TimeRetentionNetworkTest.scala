package it.unibo.scafi.runtime.network

import scala.concurrent.duration.{ Duration, DurationInt, SECONDS }

import it.unibo.scafi.message.{ Export, Path, ValueTree }
import it.unibo.scafi.runtime.network.TimeRetention
import it.unibo.scafi.test.AsyncSpec

trait TimeRetentionNetworkTest extends AsyncSpec:

  given ExpirationConfiguration = ExpirationConfiguration(Duration(5, SECONDS))

  class DummyTimeRetentionNetwork extends LatestBufferingNetwork with TimeRetention:
    override type DeviceId = String
    override def localId: DeviceId = "device-1"
    override def send(message: Export[String]): Unit = ()

  "A network configured with time retention policy" should "drop neighbor values after expiration time" in:
    val network = DummyTimeRetentionNetwork()
    network.deliverableReceived(from = "device-2", message = ValueTree(Map(Path("dummyPath") -> "dummyValue")))
    network.receive.neighbors shouldBe Set("device-2")
    after(6.seconds)(network.receive.neighbors shouldBe Set.empty[String])
