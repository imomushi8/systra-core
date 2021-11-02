import cats.data.State
import com.github.imomushi8.systra.core.{Market, TradeContext, TradeSystem}
import com.github.imomushi8.systra.core.util.Report

object MockSystem extends TradeSystem {
  override def contract[A]: State[(Market, A), Seq[Report]] = State { current => (current, Vector()) }

  override def getContext(market: Market): TradeContext = {
    val _market = market.asInstanceOf[MockMarket]
    TradeContext(
      _market.capital,
      _market.orders,
      _market.positions,
      _market
    )
  }
}