/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips.ast

/**
 * Boolean property expression.
 *
 * @param bExpr
 */
final case class PBoolExpr(bExpr: Boolean) extends PExpr {
  override def printSVA: String = s"${bExpr}"

  override def printAST(depth: Int): String =
    s"-${this.getClass.getSimpleName} bExpr: '${bExpr}'\n"
}
