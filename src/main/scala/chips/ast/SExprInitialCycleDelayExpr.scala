/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips.ast

import chips.{BInt, BRange}

/**
 * Initial delay expression. Cycle delay is of type [[BInt]] or [[BRange]].
 *
 * @param op  operator
 * @param cD  cycle delay
 * @param lhs sequence expression
 */
final case class SExprInitialCycleDelayExpr(op: SUnaryOp,
                                            cD: Option[BInt | BRange] = None,
                                            lhs: SExpr) extends SExpr {
  override def printSVA: String =
    if cD.isEmpty
    then op match
      case SUnaryOp.`op_##[*]` => s"${op.printSVA} ${lhs.printSVA}"
      case SUnaryOp.`op_##[+]` => s"${op.printSVA} ${lhs.printSVA}"
      case _ => throw new IllegalArgumentException(s"Illegal operator '${op.printSVA}")
    else
      cD.get match
        case x: BInt => s"${op.printSVA}${x.printSVA} ${lhs.printSVA}"
        case xr: BRange => s"${op.printSVA}[${xr.printSVA}] ${lhs.printSVA}"

  override def printAST(depth: Int): String =
    val indent = " " * depth
    s"-${this.getClass.getSimpleName} op: '${this.op.printSVA}, cD: '${cD}'\n" +
      s"`${this.lhs.printAST(depth + 1)}".indent(depth)
}
