import scala.language.implicitConversions
import chips.ast._
import chips.InfixOps._
import chips._

val rst = PBoolExpr(true)
val rsts = Seq(PBoolExpr(true), PBoolExpr(false), PBoolExpr(true), PBoolExpr(false))

// sequences
// s1 ##1 s2 ##2 s3
val sT = SBoolExpr(true)
val s1 = SBoolExpr(true)
val s2 = SBoolExpr(true)
val s3 = SBoolExpr(true)

Assert.property("hallo") {
  rst and rst
}