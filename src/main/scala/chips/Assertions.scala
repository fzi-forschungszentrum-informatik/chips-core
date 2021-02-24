/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips

import chips.ast._

object Assert {

  /**
   * === SVA reference ===
   * `assert (assertion_body) action_block`
   * */
  def immediate(id: String) = ???

  /**
   * === SVA reference ===
   * `assert #0 (assertion_body) action_block`
   */
  def observed(id: String) = ???


  /** === SVA reference ===
   * `assert final (assertion_body) action_block`
   */
  def `final`(id: String) = ???

  /** === SVA reference ===
   * `assert property (assertion_body) action_block`
   */
  def property(id: String)(body: PExpr) =
    CAssertionItem(id, AssertPropertyStmt(body))
}

object Assume {
  /** === SVA reference ===
   * `assume (assertion_body) action_block`
   */
  def immediate(id: String) = ???

  /** === SVA reference ===
   * `assume #0 (assertion_body) action_block`
   */
  def observed(id: String) = ???

  /** === SVA reference ===
   * `assume final (assertion_body) action_block`
   */
  def `final`(id: String) = ???

  /** === SVA reference ===
   * `assume property (assertion_body) action_block`
   */
  def property(id: String)(body: PExpr) =
    CAssertionItem(id, AssumePropertyStmt(body))
}

object Cover {
  /** === SVA reference ===
   * `cover (assertion_body) action_block`
   */
  def immediate(id: String) = ???

  /** === SVA reference ===
   * `cover #0 (assertion_body) action_block`
   */
  def observed(id: String) = ???

  /** === SVA reference ===
   * `cover final (assertion_body) action_block`
   */
  def `final`(id: String) = ???

  /** === SVA reference ===
   * `cover property (assertion_body) action_block`
   */
  def property(id: String)(body: PExpr) =
    CAssertionItem(id, CoverPropertyStmt(body))
}

object Restrict {
  /** === SVA reference ===
   * `restrict property (assertion_body) action_block`
   */
  def property(id: String)(body: PExpr) =
    CAssertionItem(id, RestrictPropertyStmt(body))
}
