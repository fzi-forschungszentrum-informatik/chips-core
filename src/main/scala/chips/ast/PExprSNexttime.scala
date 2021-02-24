/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips.ast

import chips.BInt

/**
 * Strong nexttime expression.
 *
 * @param op
 * @param expr
 * @param n
 */
final case class PExprSNexttime(op: PUnaryOp.s_nexttime.type,
                                lhs: PExpr,
                                n: BInt) extends PExpr {
  override def printSVA: String =
    s"${op.printSVA}[${n.printSVA}] (${lhs.printSVA})"

  override def printAST(depth: Int): String =
    val indent = " " * depth
    s"-${this.getClass.getSimpleName} op: '${this.op.printSVA}', n: '${n}'\n" +
      s"${this.lhs.printAST(depth + 1)}".indent(depth)
}
