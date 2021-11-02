package com.github.imomushi8.systra.core.action

import com.github.imomushi8.systra.core.TradeContext
import com.github.imomushi8.systra.core.util.Chart

object ActionTags {
  final val DeferredAction = 1
  final val DoneAction = 2
}

object ActionImpl {
  def receive(factory: (Chart, TradeContext) => TradeAction): TradeAction = Deferred(factory)
  def done(context: TradeContext): TradeAction = {
    val action = Done
    action.market = Some(context.market)
    action
  }

  case object     Done      extends TradeAction(ActionTags.DoneAction)
  abstract class  Deferred  extends TradeAction(ActionTags.DeferredAction) {
    def apply(chart: Chart, context: TradeContext): TradeAction
  }

  object Deferred {
    def apply(factory: (Chart, TradeContext) => TradeAction): TradeAction =
      new Deferred {
        def apply(chart: Chart, context: TradeContext): TradeAction = {
          val action = factory(chart, context) // ここで実行 context.marketが副作用で変更される
          action.market = Some(context.market) // ここで変更したmarketの値を付与して返す
          action
        }
        override def toString: String = s"Deferred($factory)"
      }
  }
}