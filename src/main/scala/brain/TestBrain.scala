package brain

import cats.Monoid
import cats.effect.unsafe.implicits.global
import com.github.imomushi8.systra.core.TradeSystem.{BUY, SELL}
import com.github.imomushi8.systra.core.action.Algorithm
import com.github.imomushi8.systra.core.action.Algorithm.Brain
import com.github.imomushi8.systra.core.util._

object TestBrain {
  case class Memory(chartList:List[Chart])

  implicit val memoryInstance: Monoid[Memory] = new Monoid[Memory] {
    override def empty: Memory = Memory(Nil)
    override def combine(x: Memory, y: Memory): Memory = Memory(x.chartList++y.chartList)
  }

  def apply(previousDay: Int): Brain[Memory] = Algorithm.trade {(chart, context, memory) =>
    val newMemory =
      if (memory.chartList.size > previousDay + 1) Memory(memory.chartList.takeRight(previousDay + 1) :+ chart)
      else Memory(memory.chartList :+ chart)

    /* ポジションがない場合のみ注文する */
    if (context.positions.isEmpty) {
      val isUp = newMemory.chartList.head.close > newMemory.chartList.head.open
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
      }yield ()).unsafeRunSync()

    } else {
      context.cancelOrder(context.orders.head.id).unsafeRunSync()
    }

    Algorithm.next(newMemory, context)
  }
}
