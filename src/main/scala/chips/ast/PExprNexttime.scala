/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips.ast

import chips.BInt

/**
 * Nexttime expression.
 *
 * @param op
 * @param expr
 * @param n
 */
final case class PExprNexttime(op: PUnaryOp.nexttime.type,
                               lhs: PExpr,
                               n: BInt) extends PExpr {
  override def printSVA: String =
    s"${op.printSVA}[${n.printSVA}] (${lhs.printSVA})"

  override def printAST(depth: Int): String =
    s"-${this.getClass.getSimpleName} op: '${this.op.printSVA}', n: '${n}'" +
      s"`${this.lhs.printAST(depth + 1)}".indent(depth)
}
