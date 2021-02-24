/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips.ast

import chips.BRange

/**
 * Strong always expression.
 *
 * @param op
 * @param expr
 * @param i
 */
final case class PExprSAlways(op: PUnaryOp.s_always.type,
                              lhs: PExpr,
                              range: Option[BRange] = None) extends PExpr {
  override def printSVA: String =
    if range.isEmpty
    then s"${op.printSVA} (${lhs.printSVA})"
    else s"${op.printSVA}[${range.get.printSVA}] (${lhs.printSVA})"

  override def printAST(depth: Int): String =
    s"-${this.getClass.getSimpleName} op: '${this.op.printSVA}', range: '${range}'\n" +
      s"${this.lhs.printAST(depth + 1)}".indent(depth)
}
