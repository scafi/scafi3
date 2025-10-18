package it.unibo.scafi.libraries

import java.util.concurrent.atomic.AtomicReference

import scala.scalanative.unsafe.{ alloc, exported, CStruct1, CStruct2, CStruct4, CVoidPtr, Ptr, Zone }
import scala.util.chaining.scalaUtilChainingOps

import it.unibo.scafi.language.AggregateFoundation
import it.unibo.scafi.language.common.syntax.BranchingSyntax
import it.unibo.scafi.language.xc.FieldBasedSharedData
import it.unibo.scafi.language.xc.syntax.ExchangeSyntax
import it.unibo.scafi.libraries.FullLibrary.libraryRef
import it.unibo.scafi.message.NativeBinaryCodable.nativeBinaryCodable
import it.unibo.scafi.nativebindings.structs.BinaryCodable as CBinaryCodable
import it.unibo.scafi.types.{ CMap, EqWrapper, NativeTypes }

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
class FullLibrary(using
    lang: AggregateFoundation & ExchangeSyntax & BranchingSyntax & FieldBasedSharedData,
) extends FullPortableLibrary
    with NativeFieldBasedSharedData
    with NativeTypes:

  override given valueCodable[Value, Format]: UniversalCodable[Value, Format] =
    nativeBinaryCodable.asInstanceOf[UniversalCodable[Value, Format]]

  override given deviceIdConv[ID]: Conversion[language.DeviceId, ID] =
    _.asInstanceOf[EqWrapper[Ptr[CBinaryCodable]]].value.asInstanceOf[ID]

  type CReturnSending[T] = ReturnSending[T]

  type CFieldBasedSharedData = CStruct1[ /* of */ Function1[Ptr[CBinaryCodable], Ptr[CField]]]

  type CCommonLibrary = CStruct2[
    /* local_id */ Function0[Ptr[CBinaryCodable]],
    /* device_id */ Function0[Ptr[CField]],
  ]

  type CBranchingLibrary =
    CStruct1[ /* branch */ Function3[Boolean, Function0[CVoidPtr], Function0[CVoidPtr], CVoidPtr]]

  type CExchangeLibrary = CStruct1[
    /* exchange */ Function2[
      Ptr[CField],
      Function1[Ptr[CField], CReturnSending[Ptr[CField]]],
      Ptr[CField],
    ],
  ]

  type CAggregateLibrary = CStruct4[
    /* Field */ CFieldBasedSharedData,
    /* common */ CCommonLibrary,
    /* branching */ CBranchingLibrary,
    /* exchange */ CExchangeLibrary,
  ]

  def asNative(using Zone): Ptr[CAggregateLibrary] =
    libraryRef.set(this)
    alloc[CAggregateLibrary]().tap: lib =>
      lib._1._1 = (default: Ptr[CBinaryCodable]) => NativeFieldBasedSharedData.of(default, CMap.empty)
      lib._2._1 = () => libraryRef.get().localId.asInstanceOf[Ptr[CBinaryCodable]]
      lib._3._1 = (condition: Boolean, trueBranch: Function0[CVoidPtr], falseBranch: Function0[CVoidPtr]) =>
        libraryRef.get().branch_(condition)(trueBranch)(falseBranch)
      lib._4._1 = (initial: Ptr[CField], f: Function1[Ptr[CField], CReturnSending[Ptr[CField]]]) =>
        libraryRef.get().exchange_(initial)(f)
end FullLibrary

object FullLibrary:

  /**
   * Singleton reference to the full library instance. Unfortunately, due to limitations in C, it is not possible to
   * close a function pointer over local state parameters, so we need to store the instance in a global variable.
   *
   * This is not ideal, but does not cause issues in practice since rounds are executed sequentially and independently.
   */
  private[FullLibrary] val libraryRef = new AtomicReference[FullLibrary]()

  @exported("retsend")
  def returnSending(value: CVoidPtr): ReturnSending[CVoidPtr] = ReturnSending(value, value)

  @exported("return_sending")
  def returnSending(returning: CVoidPtr, send: CVoidPtr): ReturnSending[CVoidPtr] = ReturnSending(returning, send)
