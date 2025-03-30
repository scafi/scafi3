package it.unibo.scafi.engine.network

import it.unibo.scafi.abstractions.BidirectionalFunction.<=>
import it.unibo.scafi.collections.ValueTree

/**
 * Adapts a network to a new token and value type.
 * @param network
 *   the network to adapt
 * @param tokenAdapter
 *   the adapter for the token type
 * @param valueAdapter
 *   the adapter for the value type
 * @tparam DeviceIdA
 *   the type of the device id in the original network
 * @tparam DeviceIdB
 *   the type of the device id in the new network
 * @tparam TokenA
 *   the type of the token in the original network
 * @tparam TokenB
 *   the type of the token in the new network
 * @tparam ValueA
 *   the type of the value in the original network
 * @tparam ValueB
 *   the type of the value in the new network
 */
class ValueTreeNetworkAdapter[
    DeviceIdA,
    DeviceIdB,
    TokenA,
    TokenB,
    ValueA,
    ValueB,
](
    val network: Network[DeviceIdA, ValueTree[TokenA, ValueA]],
    val deviceIdAdapter: DeviceIdA <=> DeviceIdB,
    val tokenAdapter: TokenA <=> TokenB,
    val valueAdapter: ValueA <=> ValueB,
) extends Network[DeviceIdB, ValueTree[TokenB, ValueB]]:

  override def localId: DeviceIdB = deviceIdAdapter.forward(network.localId)

  override def send(e: Export[DeviceIdB, ValueTree[TokenB, ValueB]]): Unit =
    network.send(
      e
        .mapKeys(deviceIdAdapter.backward)
        .mapValues(
          _.map((path, value) => path.map(tokenAdapter.backward) -> valueAdapter.backward(value)),
        ),
    )

  override def receive(): Import[DeviceIdB, ValueTree[TokenB, ValueB]] = network
    .receive()
    .map(
      deviceIdAdapter.forward(_) ->
        _.map((path, value) => path.map(tokenAdapter.forward) -> valueAdapter.forward(value)),
    )
end ValueTreeNetworkAdapter

object ValueTreeNetworkAdapter:

  /**
   * Wraps a network in a network adapter with the same token and value type.
   * @param network
   *   the network to adapt
   * @tparam DeviceId
   *   the type of the device id
   * @tparam Token
   *   the type of the token in the original network
   * @tparam Value
   *   the type of the value in the original network
   * @return
   *   the adapted network
   */
  def apply[DeviceId, Token, Value](
      network: Network[DeviceId, ValueTree[Token, Value]],
  ): ValueTreeNetworkAdapter[DeviceId, DeviceId, Token, Token, Value, Value] =
    new ValueTreeNetworkAdapter(network, <=>, <=>, <=>)

  extension [DeviceIdA, TokenA, ValueA](network: Network[DeviceIdA, ValueTree[TokenA, ValueA]])

    /**
     * Adapts a network to a new device id type.
     * @param deviceIdAdapter
     *   the adapter for the device id type
     * @tparam DeviceIdB
     *   the type of the device id in the new network
     * @return
     */
    def byDeviceId[DeviceIdB](
        deviceIdAdapter: DeviceIdA <=> DeviceIdB,
    ): ValueTreeNetworkAdapter[
      DeviceIdA,
      DeviceIdB,
      TokenA,
      TokenA,
      ValueA,
      ValueA,
    ] =
      new ValueTreeNetworkAdapter(
        network,
        deviceIdAdapter = deviceIdAdapter,
        tokenAdapter = <=>,
        valueAdapter = <=>,
      )

    /**
     * Adapts a network to a new token type.
     * @param tokenAdapter
     *   the adapter for the token type
     * @tparam TokenB
     *   the type of the token in the new network
     * @return
     *   the adapted network
     */
    def byToken[TokenB](
        tokenAdapter: TokenA <=> TokenB,
    ): ValueTreeNetworkAdapter[
      DeviceIdA,
      DeviceIdA,
      TokenA,
      TokenB,
      ValueA,
      ValueA,
    ] =
      new ValueTreeNetworkAdapter(
        network,
        deviceIdAdapter = <=>,
        tokenAdapter = tokenAdapter,
        valueAdapter = <=>,
      )

    /**
     * Adapts a network to a new value type.
     * @param valueAdapter
     *   the adapter for the value type
     * @tparam ValueB
     *   the type of the value in the new network
     * @return
     */
    def byValue[ValueB](
        valueAdapter: ValueA <=> ValueB,
    ): ValueTreeNetworkAdapter[
      DeviceIdA,
      DeviceIdA,
      TokenA,
      TokenA,
      ValueA,
      ValueB,
    ] =
      new ValueTreeNetworkAdapter(
        network,
        deviceIdAdapter = <=>,
        tokenAdapter = <=>,
        valueAdapter = valueAdapter,
      )
  end extension
end ValueTreeNetworkAdapter
