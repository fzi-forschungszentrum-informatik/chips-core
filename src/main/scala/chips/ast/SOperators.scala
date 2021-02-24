/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips.ast

/**
 * ADT for representing SVA unary sequence operators.
 * Operators come in two forms: intitial delay operators have prefix notation whereas consecutive repetition operators
 * have postfix notation.
 */
enum SUnaryOp(keyword: String) {
  // initial delay sequence operators

  case `op_##` extends SUnaryOp("##")

  case `op_##[*]` extends SUnaryOp("##[*]")

  case `op_##[+]` extends SUnaryOp("##[+]")

  // consecutive repetition operators

  case `op_*` extends SUnaryOp("*")

  case `op_[*]` extends SUnaryOp("[*]")

  case `op_[+]` extends SUnaryOp("[+]")

  case `op_->` extends SUnaryOp("->")

  case `op_eq` extends SUnaryOp("=")

  def printSVA: String = keyword
}

/**
 * ADT for representing SVA binary sequence operators.
 * Binary operators have infix notation.
 */
enum SBinaryOp(keyword: String) {
  case `op_##[*]` extends SBinaryOp("##[*]")

  case `op_##[+]` extends SBinaryOp("##[+]")

  case `op_##` extends SBinaryOp("##")

  case `op_or` extends SBinaryOp("or")

  case `op_and` extends SBinaryOp("and")

  case `op_throughout` extends SBinaryOp("throughout")

  case `op_within` extends SBinaryOp("within")

  case `op_intersect` extends SBinaryOp("intersect")

  def printSVA: String = keyword
}

enum SPBinaryOp(keyword: String) {
  case `op_|->` extends SPBinaryOp("|->")

  case `op_|=>` extends SPBinaryOp("|=>")

  case `op_#=#` extends SPBinaryOp("#=#")

  case `op_#-#` extends SPBinaryOp("#-#")

  def printSVA: String = keyword
}
