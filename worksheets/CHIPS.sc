/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

//import scala.language.implicitConversions

import chips.ast._
import chips.InfixOps._
import chips._

// boolean property
val rst = PBoolExpr(true)

// bolean sequence
val s1 = SBoolExpr(true)
val s2 = SBoolExpr(true)
val s3 = SBoolExpr(true)

Assert.property("prop1") {
  s1 |=> rst and rst
}
