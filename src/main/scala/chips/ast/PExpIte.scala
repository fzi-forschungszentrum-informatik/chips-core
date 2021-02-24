/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips.ast

/**
 * If-then-else property expression.
 *
 * @param cond
 * @param thenExpr
 * @param elseExpr
 */
final case class PExprIte(cond: PBoolExpr,
                          thenExpr: PExpr,
                          elseExpr: PExpr) extends PExpr {
  override def printSVA: String =
    s"if (${cond.printSVA}) (${thenExpr.printSVA}) else (${elseExpr.printSVA})"

  override def printAST(depth: Int): String =
    s"-${this.getClass.getSimpleName}\n" +
      s"`${this.cond.printAST(depth + 1)}".indent(depth) +
      s"`${this.thenExpr.printAST(depth + 1)}".indent(depth) +
      s"`${this.elseExpr.printAST(depth + 1)}".indent(depth)
}
