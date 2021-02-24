/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips

/**
 * Property logic utility operators.
 */
object PLogic {

  import chips.ast.PExpr

  /**
   * Variadic conjunction.
   *
   * @param properties
   * @return
   */
  def And(properties: PExpr*) =
    properties.tail.foldLeft(properties.head)(_ and _)

  /**
   * Alias for [[And]] operator.
   *
   * @param properties
   * @return
   */
  transparent inline def /\(properties: PExpr*) =
    And(properties: _*)

  /**
   * Variadic disjunction.
   *
   * @param properties
   * @return
   */
  def Or(properties: PExpr*) =
    properties.tail.foldLeft(properties.head)(_ and _)

  /**
   * Alias for [[Or]] operator.
   *
   * @param properties
   * @return
   */
  transparent inline def \/(properties: PExpr*) =
    Or(properties: _*)

  /**
   * Variadic implication.
   *
   * @param properties
   * @return
   */
  def Implies(properties: PExpr*) =
    properties.tail.foldLeft(properties.head)(_ implies _)

  /**
   * Alias for [[Implies]] operator.
   *
   * @param properties
   * @return
   */
  def ==>(properties: PExpr*) =
    Implies(properties: _*)

  /**
   * Variadic equivalence (if and only if).
   *
   * @param properties
   * @return
   */
  def Iff(properties: PExpr*) =
    properties.tail.foldLeft(properties.head)(_ iff _)

  /**
   * Alias for [[Iff]] operator.
   *
   * @param properties
   * @return
   */
  def ===(properties: PExpr*) =
    Iff(properties: _*)
}

/**
 * Sequence logic utility operators.
 */
object SLogic {

  import chips.ast.SExpr

  /**
   * Variadic conjunction.
   *
   * @param sequences
   * @return
   */
  def And(sequences: SExpr*) =
    sequences.tail.foldLeft(sequences.head)(_ and _)

  /**
   * Alias for [[And]] operator.
   *
   * @param sequences
   * @return
   */
  transparent inline def /\(sequences: SExpr*) =
    And(sequences: _*)

  /**
   * Variadic disjunction.
   *
   * @param sequences
   * @return
   */
  def Or(sequences: SExpr*) =
    sequences.tail.foldLeft(sequences.head)(_ and _)

  /**
   * Alias for [[Or]] operator.
   *
   * @param sequences
   * @return
   */
  transparent inline def \/(sequences: SExpr*) =
    Or(sequences: _*)
}
