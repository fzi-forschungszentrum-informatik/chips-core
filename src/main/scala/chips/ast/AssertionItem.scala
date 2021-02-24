/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips.ast

import chips.ast.{PExpr, SExpr}

/**
 * Abstract data type representing assertion items.
 *
 * === Type ===
 * [[AssertionItem]] = [[CAssertionItem]] | [[DIAssertionItem]]
 */
sealed trait AssertionItem

/**
 * Deferred immediate assertion item.
 *
 * @param id
 * @param stmt
 */
final case class DIAssertionItem(id: String,
                                 stmt: DIAssertionStmt) extends AssertionItem


/**
 * Abstract data type representing deferred immediate assertion statements.
 */
sealed trait DIAssertionStmt

final case class DIAssertStmt() extends DIAssertionStmt

final case class DIAssumeStmt() extends DIAssertionStmt

final case class DICoverStmt() extends DIAssertionStmt


/**
 * Concurrent assertion item.
 *
 * @param id unique name
 * @param stmt
 */
final case class CAssertionItem(id: String,
                                stmt: AssertionStmt) extends AssertionItem

/**
 * Abstract data type representing concurrent assertion statements.
 */
trait AssertionStmt

final case class AssertPropertyStmt(property: PExpr) extends AssertionStmt

final case class AssumePropertyStmt(property: PExpr) extends AssertionStmt

final case class CoverPropertyStmt(property: PExpr) extends AssertionStmt

final case class CoverSequenceStmt(sequence: SExpr) extends AssertionStmt

final case class RestrictPropertyStmt(property: PExpr) extends AssertionStmt