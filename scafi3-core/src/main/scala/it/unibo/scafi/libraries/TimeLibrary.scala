package it.unibo.scafi.libraries

import scala.concurrent.duration.{ DurationLong, FiniteDuration }
import scala.math.Numeric.Implicits.infixNumericOps

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.common.syntax.BranchingSyntax
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax
import it.unibo.scafi.message.{ Codable, CodableFromTo }
import it.unibo.scafi.sensors.TimeSensor

import FieldCalculusLibrary.share

/** Temporal building blocks: decay, timers, clocks, and wall-clock helpers. */
object TimeLibrary:

  /** Decays `initial` each round using `decayFn`, clamped into `[floor, initial]`. */
  def decay[V: Numeric as num](initial: V, floor: V, decayFn: V => V)(using
      language: AggregateFoundation & FieldCalculusSyntax,
  ): V =
    language.evolve(initial)(v => num.min(initial, num.max(floor, decayFn(v))))

  /** Decay toward `num.zero`. */
  def decay[V: Numeric as num](initial: V, decayFn: V => V)(using
      language: AggregateFoundation & FieldCalculusSyntax,
  ): V =
    TimeLibrary.decay(initial, num.zero, decayFn)

  /** Countdown timer: decays `length` by one unit each round, clamped at zero. */
  def timer[V: Numeric as num](length: V)(using language: AggregateFoundation & FieldCalculusSyntax): V =
    TimeLibrary.decay(length, v => v - num.one)

  /** `(value if timer still running, expValue once expired; remaining time)`. */
  def limitedMemory[V, T: Numeric as num](value: V, expValue: V, timeout: T)(using
      language: AggregateFoundation & FieldCalculusSyntax,
  ): (V, T) =
    val t = timer[T](timeout)
    (if num.gt(t, num.zero) then value else expValue, t)

  /** Counts down `length` by `decayAmt`; `true` fires on the round the counter resets to `length`. */
  def cyclicTimerWithDecay[T: Numeric as num](length: T, decayAmt: T)(using
      language: AggregateFoundation & FieldCalculusSyntax & BranchingSyntax,
  ): Boolean =
    val left = language.evolve(length): prev =>
      language.branch(num.equiv(prev, num.zero))(length)(
        num.min(length, num.max(num.zero, prev - decayAmt)),
      )
    num.equiv(left, length)

  /** Cyclic timer with unit decay; `true` every `length` rounds. */
  def cyclicTimer[T: Numeric as num](length: T)(using
      language: AggregateFoundation & FieldCalculusSyntax & BranchingSyntax,
  ): Boolean =
    cyclicTimerWithDecay(length, num.one)

  /** Neighbour-synchronised counter that snaps to the fastest neighbour. */
  def sharedTimerWithDecay[Format, T: {Numeric as num, CodableFromTo[Format]}](period: T, dt: T)(using
      language: AggregateFoundation & FieldCalculusSyntax & BranchingSyntax,
  ): T =
    share[Format, T](num.zero): nbrClocks =>
      val myClock = nbrClocks.onlySelf
      val clockPerceived = nbrClocks.withoutSelf.foldLeft(myClock)(num.max)
      language.branch(num.lteq(clockPerceived, myClock))(
        myClock + (if cyclicTimerWithDecay(period, dt) then num.one else num.zero),
      )(
        clockPerceived,
      )

  /** Increments a counter each time the cyclic timer wraps. */
  def clock[T: Numeric as num](length: T, decayAmt: T)(using
      language: AggregateFoundation & FieldCalculusSyntax & BranchingSyntax,
  ): Long =
    language
      .evolve((0L, length)): (k, left) =>
        language.branch(num.equiv(left, num.zero))(
          (k + 1L, length),
        )(
          (k, num.min(length, num.max(num.zero, left - decayAmt))),
        )
      ._1

  /** `true` on the first round the cyclic timer fires, `false` on the reset round itself. */
  def impulsesEvery[T: Numeric as num](period: T)(using
      language: AggregateFoundation & FieldCalculusSyntax & BranchingSyntax,
  ): Boolean =
    language.evolve(false): impulse =>
      language.branch(impulse)(false)(num.equiv(timer(period), num.zero))

  /** Exponential moving average: `alpha * prev + (1 - alpha) * signal`. */
  def exponentialBackoffFilter[T: Numeric as num](signal: T, alpha: T)(using
      language: AggregateFoundation & FieldCalculusSyntax,
  ): T =
    language.evolve(signal)(prev => prev * alpha + signal * (num.one - alpha))

  /** Wall-clock shared timer; synchronises to the fastest neighbour. */
  def sharedTimer(period: FiniteDuration)(using
      language: AggregateFoundation & FieldCalculusSyntax & BranchingSyntax & TimeSensor,
  )(using Codable[Long, Long]): FiniteDuration =
    sharedTimerWithDecay[Long, Long](period.toMillis, language.deltaTime.toMillis).millis

  /** Stays `true` for `window` after `cond` last fired. */
  def recentlyTrue(window: FiniteDuration, cond: => Boolean)(using
      language: AggregateFoundation & FieldCalculusSyntax & BranchingSyntax & TimeSensor,
  ): Boolean =
    language.evolve(false): happened =>
      language.branch(cond)(true):
        language.branch(!happened)(false):
          TimeLibrary.decay(window.toNanos, 0L, _ - language.deltaTime.toNanos) > 0L

  /** Decays `length` toward zero using `decayFn`, returning the current count alongside `info`. */
  def evaporation[T: Numeric, V](length: T, decayFn: T => T, info: V)(using
      language: AggregateFoundation & FieldCalculusSyntax,
  ): (T, V) =
    (TimeLibrary.decay(length, decayFn), info)

  /** Invokes `f` on each round the wall-clock cyclic timer fires; returns `default` otherwise. */
  def cyclicFunction[T](period: FiniteDuration, f: () => T, default: T)(using
      language: AggregateFoundation & FieldCalculusSyntax & BranchingSyntax & TimeSensor,
  ): T =
    language.branch(cyclicTimerWithDecay(period.toNanos, language.deltaTime.toNanos))(f())(default)

end TimeLibrary
