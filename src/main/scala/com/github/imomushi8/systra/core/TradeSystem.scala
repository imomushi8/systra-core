package com.github.imomushi8.systra.core

/*--------------------------------------------------------------------------------------------*/
import cats.{Eval, Id}
import cats.data.{Kleisli, Reader, State, StateT}
import com.github.imomushi8.systra.core.action.Algorithm.Brain
import com.github.imomushi8.systra.core.util.{Chart, Report}
/*--------------------------------------------------------------------------------------------*/

trait TradeSystem {

  def contract[A]: State[(Market, A), Seq[Report]]
  def getContext(market: Market): TradeContext

  private def updateChart[A](chart:Chart): State[(Market, A), Unit] =
    State.modify { case (market, memory) => (market.updateChart(chart), memory) }

  private def action[A](chart: Chart, brain: Brain[A]): State[(Market, A), Unit] =
    State.modify { case (market, memory) => brain(chart, getContext(market), memory) }

  def once[A](chart: Chart)(brain: Brain[A]): State[(Market, A), Seq[Report]] = for {
    _    <- updateChart(chart)
    logs <- contract
    _    <- action(chart, brain)
  } yield logs
}


/*--------------------------------------------------------------------------------------------*/

object TradeSystem {

  /*------------------------------------------------------------------------------------------*/
  /* フィールド */
  final val START_MARKET: String  = "Start Market"
  final val STOP_MARKET: String   = "Stop  Market"
  final val OPEN: String          = "OPEN   {}"
  final val CLOSE: String         = "CLOSE  {}"
  final val GET: String           = "GET    {}"
  final val CANCEL: String        = "CANCEL {}"
  final val ORDER: String         = "ORDER  {}"

  final val BUY   :Side = 1
  final val SELL  :Side = -1
}