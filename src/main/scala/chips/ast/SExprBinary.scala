/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips.ast

/**
 * Binary sequence expression.
 *
 * @param op
 * @param lhs
 * @param rhs
 */
final case class SExprBinary(op: SBinaryOp,
                             lhs: SExpr,
                             rhs: SExpr) extends SExpr {
  override def printSVA: String =
    s"(${lhs.printSVA}) ${op.printSVA} (${rhs.printSVA})"

  override def printAST(depth: Int): String =
    s"-${this.getClass.getSimpleName} op: '${this.op.printSVA}'\n" +
      s"`${this.lhs.printAST(depth + 1)}".indent(depth) +
      s"`${this.rhs.printAST(depth + 1)}".indent(depth)
}
