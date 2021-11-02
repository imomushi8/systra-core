package com.github.imomushi8.systra.core

import cats.effect.{ExitCode, IO}
import com.github.imomushi8.systra.core.util.{Chart, OrderMethod}

/**
 * 各種Marketの基底クラス
 * @param chart 現在のOHLCV情報を保存しておく変数（※可変だが変更しないように！）
 * */
abstract class Market { self =>
  protected var chart :Chart

  def placeOrder(method  :OrderMethod,
                 size    :Size,
                 expire  :TimeStamp): IO[(Market, ID)]

  def cancelOrder(id:ID): IO[(Market, ExitCode)]

  def updateChart(_chart:Chart): Market = {
    self.chart = _chart
    self
  }
}