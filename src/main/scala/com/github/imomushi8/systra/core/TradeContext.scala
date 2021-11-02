package com.github.imomushi8.systra.core

import cats.effect.{ExitCode, IO}
import com.github.imomushi8.systra.core.util.{Order, OrderMethod, Position}

/**
 * Actionで渡される現在のコンテキスト
 * @param capital 現在の資産
 * @param orders 現在の注文リスト
 * @param positions 現在のポジションリスト
 * */
case class TradeContext(capital   :Double,
                        orders    :List[Order],
                        positions :List[Position],
                        private[systra] var market :Market) {
  def placeOrder(method  :OrderMethod,
                 size    :Size,
                 expire  :TimeStamp): IO[ID] = for {
    res <- market.placeOrder(method, size, expire)
  } yield {
    market = res._1
    res._2
  }

  def cancelOrder(id:ID): IO[ExitCode] = for {
    res <- market.cancelOrder(id)
  } yield {
    market = res._1
    res._2
  }
}
