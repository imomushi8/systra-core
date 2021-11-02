import cats.effect.{ExitCode, IO}
import com.github.imomushi8.systra.core._
import com.github.imomushi8.systra.core.util.{Chart, Order, OrderMethod, Position}

case class MockMarket(capital    :Double,
                      orders     :List[Order],
                      positions  :List[Position],
                      sequenceId :Int,
                      override var chart :Chart) extends Market {
  override def placeOrder(method: OrderMethod, size: Size, expire: TimeStamp): IO[(Market, ID)] = IO {
    (MockMarket(capital, orders, positions, sequenceId+1, chart), sequenceId.toString)
  }

  override def cancelOrder(id: ID): IO[(Market, ExitCode)] = IO {
    (MockMarket(capital, orders.filter(_.id != id), positions, sequenceId, chart), ExitCode.Success)
  }
}