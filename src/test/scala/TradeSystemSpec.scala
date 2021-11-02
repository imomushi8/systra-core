import brain.TestBrain
import brain.TestBrain.Memory
import cats.kernel.Monoid
import com.github.imomushi8.systra.core._
import com.github.imomushi8.systra.core.action.Algorithm.Brain
import com.github.imomushi8.systra.core.util.Chart
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.{Configuration, TableDrivenPropertyChecks}
import org.scalatestplus.scalacheck.{Checkers, ScalaCheckDrivenPropertyChecks}

import java.time.LocalDateTime

class TradeSystemSpec extends AnyFlatSpec
  with Configuration
  with Matchers
  with ScalaCheckDrivenPropertyChecks
  with TableDrivenPropertyChecks
  with Checkers {

  val genTimestamp: Gen[TimeStamp] = for {
    year <- Gen.choose[Int](1900, 2038)
    month <- Gen.choose[Int](1, 12)
    monthDay <- Gen.choose[Int](1, 12)
    hour <- Gen.choose[Int](0, 23)
    minute <- Gen.choose[Int](0, 59)
    second <- Gen.choose[Int](0, 59)
  } yield LocalDateTime.of(year, month, monthDay, hour, minute, second)

  implicit lazy val arbChart: Arbitrary[Chart] = Arbitrary{
    for {
      open <- Gen.posNum[Price]
      high <- Gen.posNum[Price]
      low <- Gen.posNum[Price]
      close <- Gen.posNum[Price]
      volume <- Gen.posNum[Volume]
      datetime <- genTimestamp
    } yield Chart(open, high, low, close, volume, datetime)
  }

  val initMarket: MockMarket = MockMarket(100, Nil, Nil, 1, null)
  val brain: Brain[Memory] = TestBrain(1)

  "In TestBrain, MockSystem.once" should "success once for all chart." in forAll { (chart: Chart) =>
    val (next, log) = MockSystem.once(chart)(brain).run((initMarket, Monoid[Memory].empty)).value
    log shouldBe Vector()
    next._1 shouldBe MockMarket(100, Nil, Nil, 3, chart)
  }
}
