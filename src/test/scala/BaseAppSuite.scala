
import java.util.concurrent.TimeUnit

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.duration.FiniteDuration

trait BaseAppSuite extends Suites
  with ScalaFutures
  with Matchers
  with Inside
  with BeforeAndAfterAll
  with ScalatestRouteTest
  with FlatSpecLike
{

  implicit override val patienceConfig: PatienceConfig = PatienceConfig(timeout = Span(30, Seconds), interval = Span(100, Millis))
  val finiteDuration = FiniteDuration(30, TimeUnit.SECONDS)

  override def beforeAll(): Unit = {
    // before running all tests this block of code being executed.
  }

  override def afterAll(): Unit = {
    // after running all tests this block of code being executed.
  }

}
