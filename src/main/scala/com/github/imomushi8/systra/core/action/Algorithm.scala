package com.github.imomushi8.systra.core.action

import com.github.imomushi8.systra.core.util.Chart
import com.github.imomushi8.systra.core.{Market, TradeContext}

object Algorithm {
  type Brain[A] = (Chart, TradeContext, A) => (Market, A)
  def trade[A](brain: Brain[A]): Brain[A] = brain
  def next[A](memory: A, context: TradeContext): (Market, A) = (context.market, memory)
}