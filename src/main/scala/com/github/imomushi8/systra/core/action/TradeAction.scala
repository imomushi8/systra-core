package com.github.imomushi8.systra.core.action

import com.github.imomushi8.systra.core.Market

abstract class TradeAction(val _tag: Int) {
  private[core] var market: Option[Market] = None
}