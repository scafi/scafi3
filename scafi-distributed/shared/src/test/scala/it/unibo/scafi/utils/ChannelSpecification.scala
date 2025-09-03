package it.unibo.scafi.utils

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should
import org.scalatest.propspec.AnyPropSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

import unsafeExceptions.canThrowAny

class ChannelSpecification
    extends AnyPropSpec
    with ScalaCheckDrivenPropertyChecks
    with should.Matchers
    with ScalaFutures:

  property("`poll` should return `None` when the channel is empty"):
    Channel[Int].poll shouldBe None

  property("`poll` should return the channel head when it is available"):
    forAll: (i: Int) =>
      val channel = Channel[Int]
      noException should be thrownBy channel.push(i)
      channel.poll shouldBe Some(i)

  property("`take` should return a `Future` completing with the next item pushed whenever it is available"):
    forAll: (i: Int) =>
      val channel = Channel[Int]
      val future = channel.take
      future.isCompleted shouldBe false
      noException should be thrownBy channel.push(i)
      whenReady(future)(_ shouldBe i)

  property("Channels can be closed"):
    val channel = Channel[Int]
    noException should be thrownBy channel.close()
    channel.isClosed shouldBe true

  property("If already closed, `close` should fail"):
    val channel = Channel[Int]
    noException should be thrownBy channel.close()
    an[Channel.ChannelClosedException] should be thrownBy channel.close()

  property("Once closed, `push` should fail"):
    val channel = Channel[Int]
    noException should be thrownBy channel.close()
    an[Channel.ChannelClosedException] should be thrownBy channel.push(1)

  property("Once closed, pending `take` should fail immediately with `ChannelClosedException`"):
    val channel = Channel[Int]
    val future = channel.take
    future.isCompleted shouldBe false
    noException should be thrownBy channel.close()
    whenReady(future.failed)(_ shouldBe a[Channel.ChannelClosedException])

  property("Once closed and empty, `take` should return a failed `Future`"):
    val channel = Channel[Int]
    noException should be thrownBy channel.close()
    val future = channel.take
    whenReady(future.failed)(_ shouldBe a[Channel.ChannelClosedException])

  property("Once closed, `poll` will provide items until the channel is empty"):
    forAll: (l: List[Int]) =>
      val channel = Channel[Int]
      l.foreach(channel.push)
      noException should be thrownBy channel.close()
      l.foreach(i => channel.poll shouldBe Some(i))
      channel.poll shouldBe None

end ChannelSpecification
