package com.github.imomushi8.systra.core.action

import com.github.imomushi8.systra.core.TradeContext
import com.github.imomushi8.systra.core.util.Chart

object Actions {
  def receive(factory: (Chart, TradeContext) => TradeAction): TradeAction = ActionImpl.receive(factory)
  def stopped(context: TradeContext): TradeAction = ActionImpl.done(context)
}