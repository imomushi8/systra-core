package brain

import cats.effect.unsafe.implicits.global
import com.github.imomushi8.systra.core.TradeSystem.{BUY, SELL}
import com.github.imomushi8.systra.core.action.{Actions, TradeAction}
import com.github.imomushi8.systra.core.util._
import com.typesafe.scalalogging.LazyLogging

object FollowBrain extends LazyLogging {
  def apply(previousDay: Int): TradeAction = collect(previousDay, Nil)

  def collect(previousDay: Int,
              chartList: List[Chart]): TradeAction = Actions.receive { (chart, _) =>
    if (chartList.size > previousDay + 1) {
      follow(previousDay, chartList)
    } else
      collect(previousDay, chartList :+ chart)
  }

  def follow(previousDay: Int,
             chartList: List[Chart]): TradeAction = Actions.receive { (chart, context) =>

    val nextCharts = chartList.takeRight(previousDay + 1) :+ chart
    /* ポジションがない場合のみ注文する */
    if (context.positions.isEmpty) {
      val isUp = chartList.head.close > chartList.head.open
      val size = 200
      val expire = chart.datetime.plusMonths(1)

      /** previousDay日前の値動きが上昇なら買い、下落なら売りでIFDOCO */
      (if (isUp) for {
          id <- context.placeOrder(MARKET(BUY), size, expire)
          _ <- context.placeOrder(OCO(LIMIT(SELL, chart.close * 1.001, id), STOP(SELL, chart.close * 0.999, id)), size, expire)
        }yield ()
      else for {
          id <- context.placeOrder(MARKET(SELL), size, expire)
          _ <- context.placeOrder(OCO(LIMIT(BUY, chart.close * 0.999, id), STOP(BUY, chart.close * 1.001, id)), size, expire)
        }yield ()).unsafeRunAsync {_=>()}

      follow(previousDay, nextCharts)

    } else {
      context.cancelOrder(context.orders.head.id).unsafeRunAsync { _ => () }
      follow(previousDay, nextCharts)
    }
  }
}
