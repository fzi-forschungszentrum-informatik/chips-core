/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips.ast

/**
 * ADT for representing SVA unary property operators.
 */
enum PUnaryOp(keyword: String) {
  case not extends PUnaryOp("not")

  case strong extends PUnaryOp("strong")

  case weak extends PUnaryOp("weak")

  case nexttime extends PUnaryOp("nexttime")

  case s_nexttime extends PUnaryOp("s_nexttime")

  case always extends PUnaryOp("always")

  case s_always extends PUnaryOp("s_always")

  case eventually extends PUnaryOp("eventually")

  case s_eventually extends PUnaryOp("s_eventually")

  def printSVA: String = keyword
}

/**
 * ADT for representing SVA binary property operators.
 */
enum PBinaryOp(keyword: String) {

  case and extends PBinaryOp("and")

  case or extends PBinaryOp("or")

  case implies extends PBinaryOp("implies")

  case iff extends PBinaryOp("iff")

  case until extends PBinaryOp("until")

  case s_until extends PBinaryOp("s_until")

  case until_with extends PBinaryOp("until_with")

  case s_until_with extends PBinaryOp("s_until_with")

  def printSVA: String = keyword
}
