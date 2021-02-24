/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips.ast

/**
 * Negation expression.
 *
 * @param op
 * @param expr
 */
final case class PExprNot(op: PUnaryOp.not.type,
                          lhs: PExpr) extends PExpr {
  override def printSVA: String =
    s"${op.printSVA} (${lhs.printSVA})"

  override def printAST(depth: Int): String =
    val indent = " " * depth
    s"-${this.getClass.getSimpleName} op: '${this.op.printSVA}'\n" +
      s"${this.lhs.printAST(depth + 1)}".indent(depth)
}
