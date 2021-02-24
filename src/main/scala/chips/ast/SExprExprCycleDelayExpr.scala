/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips.ast

import chips.{BInt, BRange}

/**
 * Cycle delay concatenation expression. The cycle delay is of type [[BInt]] or [[BRange]].
 *
 * @param op  operator
 * @param cD  cycle delay
 * @param lhs left hand side
 * @param rhs right hand side
 */
final case class SExprExprCycleDelayExpr(op: SBinaryOp,
                                         cD: Option[BInt | BRange],
                                         lhs: SExpr,
                                         rhs: SExpr) extends SExpr {
  override def printSVA: String =
    if cD.isEmpty
    then s"(${lhs.printSVA}) ${op.printSVA} (${rhs.printSVA})"
    else
      val delay = cD.get match
        case x: BInt => s"${op.printSVA}${x.printSVA}"
        case xr: BRange => s"${op.printSVA}[${xr.printSVA}]"

      s"(${lhs.printSVA}) ${delay} (${rhs.printSVA})"

  override def printAST(depth: Int): String =
    s"-${this.getClass.getSimpleName} op: '${this.op.printSVA}', cD: '${cD}'\n" +
      s"`${this.lhs.printAST(depth + 1)}".indent(depth) +
      s"`${this.rhs.printAST(depth + 1)}".indent(depth)
}
