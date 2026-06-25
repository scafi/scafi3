package it.unibo.scafi.libraries

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.common.ConditionalExportLanguage
import it.unibo.scafi.language.common.syntax.BranchingSyntax
import it.unibo.scafi.language.fc.syntax.FieldCalculusSyntax
import it.unibo.scafi.message.Codable
import it.unibo.scafi.sensors.TimeSensor

import FieldCalculusLibrary.share
import TimeLibrary.sharedTimerWithDecay

/**
 * Process (the **S** block, spawn machinery): dynamic, keyed sub-computations that self-organise into spatial
 * "bubbles". Devices within a bubble collaborate to produce an output; once all bubble members decide to terminate the
 * bubble shrinks and eventually vanishes.
 *
 * The two entry points are:
 *   - [[spawn]] — general-purpose spawn: manages a dynamic set of keyed processes.
 *   - [[replicated]] — time-replicated spawn: runs a fixed number of overlapping instances of a process, rotating on a
 *     wall-clock period.
 *
 * '''Alignment note''': `K.toString` must be unique across all concurrently active keys because it is used both as the
 * per-process alignment token and as the sort key that ensures all devices traverse active keys in the same order.
 */
object ProcessLibrary:

  /**
   * Lifecycle of a single process instance as seen by the device running it.
   *
   * `derives CanEqual` is required because [[ProcessOutput.status]] is compared with `==` in
   * [[ProcessLibrary.handleTermination]] under strict equality.
   */
  enum ProcessStatus derives CanEqual:
    /** This device is outside the process bubble. */
    case External

    /** This device is inside the bubble but has not yet produced a final output. */
    case Bubble

    /** This device is inside the bubble and is producing a result. */
    case Output

    /** This device has decided to terminate; termination is propagating through the bubble. */
    case Terminated

  /** Value returned by a process function: the current result together with the lifecycle status. */
  final case class ProcessOutput[+R](result: R, status: ProcessStatus)

  /** Smart constructors for [[ProcessOutput]], one per [[ProcessStatus]] case, for readable call sites. */
  object ProcessOutput:
    /** This device is inside the bubble and produces `result` as the process output. */
    def output[R](result: R): ProcessOutput[R] = ProcessOutput(result, ProcessStatus.Output)

    /** This device is inside the bubble but does not contribute to the output (carries `result` for chaining). */
    def bubble[R](result: R): ProcessOutput[R] = ProcessOutput(result, ProcessStatus.Bubble)

    /** This device is outside the bubble; `result` is ignored by [[spawn]]. */
    def external[R](result: R): ProcessOutput[R] = ProcessOutput(result, ProcessStatus.External)

    /** This device requests bubble shutdown; the termination signal propagates from here. */
    def terminated[R](result: R): ProcessOutput[R] = ProcessOutput(result, ProcessStatus.Terminated)
  end ProcessOutput

  /**
   * Dynamic, keyed spawn (the **S** block). Maintains a set of process instances, one per key, organised as spatial
   * bubbles. A process instance is born when a key appears in `generation` and dies when the process function returns
   * [[ProcessStatus.Terminated]] and that termination signal has propagated to all bubble members.
   *
   * Keys spread through the network via the outer `share`; `conditionallyExport` gates participation so that devices
   * with status [[ProcessStatus.External]] are invisible inside the bubble.
   *
   * The `process` is the trailing parameter so the call site reads like a control structure:
   * {{{spawn(generation = Set(k), args = x) { key => a => ProcessOutput.output(...) }}}}
   *
   * @param generation
   *   the set of keys born at this device this round (typically a singleton or empty)
   * @param args
   *   argument forwarded to every process invocation at this device
   * @param process
   *   function from key → argument → [[ProcessOutput]]; called once per active key per round
   * @tparam K
   *   key type; `K.toString` must be unique across concurrent keys
   * @tparam A
   *   argument type
   * @tparam R
   *   result type
   * @return
   *   map from key to result for all keys currently in the [[ProcessStatus.Output]] state at this device
   */
  def spawn[K, A, R](using
      language: AggregateFoundation & FieldCalculusSyntax & ConditionalExportLanguage,
  )(using
      Codable[Map[K, R], Map[K, R]],
      Codable[(Boolean, Boolean), (Boolean, Boolean)],
  )(generation: Set[K], args: A)(process: K => A => ProcessOutput[R]): Map[K, R] =
    share[Map[K, R], Map[K, R]](Map.empty) { prevResults =>
      // Collect all keys seen by any neighbour plus locally generated ones.
      val activeKeys: Set[K] = prevResults.withoutSelf.foldLeft(generation)((ks, nbrMap) => ks ++ nbrMap.keySet)
      // Sort for deterministic per-key alignment-token ordering: identical sorted positions across devices
      // that share the same active-key set gives reproducible invocation-count paths.
      activeKeys.toList.sortBy(_.toString).foldLeft(Map.empty[K, R]) { (acc, k) =>
        language.align(k.toString) { () =>
          val out: ProcessOutput[R] =
            language.conditionallyExport((o: ProcessOutput[R]) => o.status != ProcessStatus.External) { () =>
              handleTermination(process(k)(args))
            }
          out match
            case ProcessOutput(r, ProcessStatus.Output) => acc + (k -> r)
            case _ => acc
        }
      }
    }

  /**
   * Time-replicated spawn: runs `replicates` overlapping instances of `proc`, identified by a shared wall-clock id
   * (`Long`). A new instance is born every `period` seconds and the oldest one expires when the window of `replicates`
   * slots is exceeded.
   *
   * @param period
   *   rotation period in seconds
   * @param replicates
   *   number of simultaneously active replicas
   * @param argument
   *   argument forwarded to every replica
   * @param proc
   *   the computation to replicate; receives the current `argument`
   * @tparam T
   *   argument type
   * @tparam R
   *   result type
   * @return
   *   map from replica id (`Long`) to result for all currently active replicas
   */
  def replicated[T, R](using
      language: AggregateFoundation & FieldCalculusSyntax & ConditionalExportLanguage & TimeSensor & BranchingSyntax,
  )(using
      Codable[Map[Long, R], Map[Long, R]],
      Codable[(Boolean, Boolean), (Boolean, Boolean)],
      Codable[Double, Double],
  )(period: Double, replicates: Int, argument: T)(proc: T => R): Map[Long, R] =
    val dt = language.deltaTime.toNanos.toDouble / 1e9
    val lastPid = sharedTimerWithDecay[Double, Double](period, dt).toLong
    spawn[Long, T, R](Set(lastPid), argument) { (pid: Long) => arg =>
      // `proc` must run unconditionally (it may contain aggregate operations): only the status differs per replica.
      val result = proc(arg)
      if pid > lastPid - replicates then ProcessOutput.output(result) else ProcessOutput.external(result)
    }

  /**
   * Wraps `out` with gossip-based termination logic. Once any bubble device returns [[ProcessStatus.Terminated]], that
   * signal propagates; when all in-bubble neighbours have received it the device exits the bubble
   * ([[ProcessStatus.External]]).
   *
   * Uses `share[(Boolean, Boolean)]` where `_._1` = "I or any neighbour wants to terminate" and `_._2` = "all in-bubble
   * neighbours are also terminating". The shared value is `(mustTerminate, mustExit)`.
   */
  private def handleTermination[R](using
      language: AggregateFoundation & FieldCalculusSyntax,
  )(using
      Codable[(Boolean, Boolean), (Boolean, Boolean)],
  )(out: ProcessOutput[R]): ProcessOutput[R] =
    val (mustTerminate, mustExit) = share[(Boolean, Boolean), (Boolean, Boolean)]((false, false)) { field =>
      val myMT = out.status == ProcessStatus.Terminated || field.withoutSelf.exists(_._1)
      val myME = field.withoutSelf.forall(_._1)
      (myMT, myME)
    }
    if mustTerminate && mustExit then ProcessOutput(out.result, ProcessStatus.External)
    else if mustTerminate then ProcessOutput(out.result, ProcessStatus.Terminated)
    else out

end ProcessLibrary
