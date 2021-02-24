/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips.ast

import chips.{BInt, BRange}

/**
 * Cycle delay expression. A cycle delay value is of type [[BInt]] or [[BRange]].
 *
 * @param op
 * @param cD
 * @param lhs
 */
final case class SExprCycleDelayExpr(op: SUnaryOp,
                                     cD: Option[BInt | BRange] = None,
                                     lhs: SExpr) extends SExpr {
  override def printSVA: String =
    if cD.isEmpty
    then op match
      case SUnaryOp.`op_[*]` => s"(${lhs.printSVA})${op.printSVA}"
      case SUnaryOp.`op_[+]` => s"(${lhs.printSVA})${op.printSVA}"
      case _ => throw new IllegalArgumentException(s"Illegal operator '${op.printSVA}")
    else
      cD.get match
        case x: BInt => s"(${lhs.printSVA})[${op.printSVA}${x.printSVA}]"
        case xr: BRange => s"(${lhs.printSVA})[${op.printSVA}${xr.printSVA}]"

  override def printAST(depth: Int): String =
    s"-${this.getClass.getSimpleName} op: '${this.op.printSVA}', cD: '${cD}'\n" +
      s"`${this.lhs.printAST(depth + 1)}".indent(depth)
}
