package com.github.imomushi8.systra.core.util

/*--------------------------------------------------------------------------------------------*/
import com.github.imomushi8.systra.core.{ID, Side, Price, Size, TimeStamp}
/*--------------------------------------------------------------------------------------------*/


/**
 * 建玉情報を保有するクラス
 * @param openTime エントリー時間
 * @param id ポジションID
 * @param side 方向（BUY:1, SELL:-1）
 * @param price エントリー価格
 * @param size 数量
 */
case class  Position(openTime :TimeStamp,
                     id       :ID,
                     side     :Side,
                     price    :Price,
                     size     :Size) {
  override val toString: String = {
    val sideStr = if(side>0) "BUY" else "SELL"
    s"Position($sideStr, ${price}yen, $size amount, ID: $id)"
  }
}