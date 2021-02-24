import scala.language.implicitConversions

import chips._
import chips.BoundConversions.{given _}
import OrdInstances.{given _}

val b1 = BVal(10)
val b2 = BVal(20)

val bound: SVARange[Int] = (0, 10)

val bound2: SVARange[Int] = SVARange(Zero, (-10))


