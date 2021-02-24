/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips.ast

import chips.{BInt, BRange}

/**
 * Always property expression.
 *
 * @param op
 * @param lhs
 * @param i
 */
final case class PExprAlways(op: PUnaryOp.always.type,
                             lhs: PExpr,
                             i: Option[BRange] = None) extends PExpr {
  override def printSVA: String =
    if i.isEmpty
    then s"${op.printSVA} (${lhs.printSVA})"
    else s"${op.printSVA}[${i.get.printSVA}] (${lhs.printSVA})"

  override def printAST(depth: Int): String =
    s"-${this.getClass.getSimpleName} op: '${this.op.printSVA}'\n" +
      s"`${this.lhs.printAST(depth + 1)}".indent(depth)
}
