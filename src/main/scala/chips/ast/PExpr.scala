/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips.ast

/**
 * Abstract data type representing property expressions.
 *
 * === Type ===
 * [[PExpr]] = [[SExpr]]
 * | [[PExprStrong]]
 * | [[PExprWeak]]
 * | [[PExprNot]]
 * | [[PExprBinaryProperty]]
 * | [[PExprBinarySequence]]
 * | [[PExprNexttime]]
 * | [[PExprSNexttime]]
 * | [[PExprAlways]]
 * | [[PExprSAlways]]
 * | [[PExprEventually]]
 * | [[PExprSEventually]]
 *
 */
trait PExpr {
  // @formatter:off
  
  import scala.compiletime.{constValue, error}
  
  import chips.{BInt, BInf, BVal, BRange}
  import chips.BIntConversions.{given}
  import chips.BRangeConversions.{given}
  import chips.OrdInstances.{given}

  def printSVA: String
  
  def printAST(depth: Int = 0): String

  /**
   * Property negation.
   * 
   * @return not expression
   */
  transparent inline def not =
    PExprNot(PUnaryOp.not, this)

  /**
   * Strong operator.
   * 
   * @return strong expression
   */
  transparent inline def strong =
    PExprStrong(PUnaryOp.strong, this)

  /**
   * Weak operator.
   * 
   * @return weak expression
   */
  transparent inline def weak =
    PExprWeak(PUnaryOp.weak, this)

  /**
   * Bounded nexttime operator.
   * 
   * @tparam N bound
   * @return nexttime property expression
   */
  transparent inline def nexttime[N <: Int]: PExprNexttime = {
    inline val n = constValue[N]
    inline if !(n >= 0)
    then error("Invalid parameter value: 'nexttime[n]' operator requires a parameter n >= 0.")
    else nexttime(n)
  }
  
  def nexttime(n: BInt) = 
    PExprNexttime(PUnaryOp.nexttime, this, n)

  /**
   * LTL alias for [[nexttime]] operator.
   * 
   * @tparam N bound
   * @return nexttime property expression
   */
  transparent inline def X[N <: Int] =
    nexttime[N]

  /**
   * Bounded strong nexttime operator.
   * 
   * @tparam N bound
   * @return strong nexttime property expression
   */
  transparent inline def s_nexttime[N <: Int]: PExprSNexttime = {
    inline val n = constValue[N]
    inline if !(n >= 0)
    then error("Invalid parameter value: 's_nexttime[n]' operator requires a parameter n >= 0.")
    else s_nexttime(n)
  }
  
  def s_nexttime(n: BInt) =
    PExprSNexttime(PUnaryOp.s_nexttime, this, n)

  /**
   * LTL alias for [[s_nexttime]] operator.
   * 
   * @tparam N bound
   * @return strong nexttime property expression
   */
  transparent inline def sX[N <: Int] =
    s_nexttime[N]

  /**
   * Bounded always operator.
   * 
   * @param ev_n right bound evidence
   * @tparam M left bound
   * @tparam N (in)finite right bound
   * @return always expression
   */
  transparent inline def always[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N]): PExprAlways = {
    inline val m = constValue[M]
    inline val n = inline ev_n.value match
      case x: Int => x
      case BInf => -1

    inline val predicate = (m >= 0) && ((n == -1) || (m < n))
    inline if !predicate
    then error("Invalid parameter range: 'always[m,n]' operator requires a valid range [m,n] with m >= 0 and m < n.")
    else always(Some((m, n)))
  }
  
  def always(r: Option[BRange]) =
    PExprAlways(PUnaryOp.always, this, r)

  /**
   * LTL alias for [[always]] operator.
   * 
   * @param ev_n right bound evidence
   * @tparam M left bound
   * @tparam N (in)finite right bound
   * @return always expression
   */
  transparent inline def G[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N]) =
    always[M, N](using ev_n)

  /**
   * Bounded strong always operator.
   * 
   * @param ev_n right bound evidence
   * @tparam M left bound
   * @tparam N (in)finite right bound
   * @return strong always expression
   */
  transparent inline def s_always[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N]): PExprSAlways = {
    inline val m = constValue[M]
    inline val n = inline ev_n.value match
      case x: Int => x
      case BInf => -1 

    inline val predicate = (m >= 0) && ((n == -1) || m < n)
    inline if !predicate
    then error("Invalid parameter range: 's_always[m,n]' operator requires a valid range [m,n] with m >= 0 and m < n.")
    else s_always(Some((m, n )))
  }
  
  def s_always(r: Option[BRange]) =
    PExprSAlways(PUnaryOp.s_always, this, r)

  /**
   * LTL alias for [[s_always]] operator.
   * 
   * @param ev_n right bound evidence
   * @tparam M left bound
   * @tparam N (in)finite right bound
   * @return strong always expression
   */
  transparent inline def sG[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N]) =
    s_always[M, N](using ev_n)

  /**
   * Bounded eventually operator.
   * 
   * @param ev_n right bound evidence
   * @tparam M left bound
   * @tparam N (in)finite right bound
   * @return eventually expression
   */
  transparent inline def eventually[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N]): PExprEventually = {
    inline val m = constValue[M]
    inline val n = inline ev_n.value match
      case x: Int => x
      case BInf => -1 

    inline val predicate = (m >= 0) && ((n == -1) || m < n)
    inline if !predicate
    then error("Invalid parameter range: 'eventually[m,n]' operator requires valid range [m,n] with m >= 0 and m < n.")
    else eventually(Some(BRange(BVal(m), n)))
  }
  
  def eventually(r: Option[BRange]) =
    PExprEventually(PUnaryOp.eventually, this, r)

  /**
   * LTL alias for [[eventually]] operator.
   * 
   * @param ev_n right bound evidence
   * @tparam M left bound
   * @tparam N (in)finite right bound
   * @return eventually expression
   */
  transparent inline def F[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N]) =
    eventually[M, N](using ev_n)

  /**
   * Bounded strong eventually operator.
   * 
   * @param ev_n right bound evidence
   * @tparam M left bound
   * @tparam N (in)finite right bound
   * @return strong eventually expression
   */
  transparent inline def s_eventually[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N]): PExprSEventually = {
    inline val m = constValue[M]
    inline val n = inline ev_n.value match
      case x: Int => x
      case BInf => -1 

    inline val predicate = (m >= 0) && ((n == -1) || m < n)
    inline if !predicate
    then error("Invalid parameter range: 's_eventually[m,n]' operator requires valid range [m,n] with m >= 0 and m < n.")
    else s_eventually(Some((m, n)))
  }
  
  def s_eventually(r: Option[BRange]) =
    PExprSEventually(PUnaryOp.s_eventually, this, r)

  /**
   * LTL alias for [[s_eventually]] operator.
   * 
   * @param ev_n right bound evidence
   * @tparam M left bound
   * @tparam N (in)finite right bound
   * @return strong eventually expression
   */
  transparent inline def sF[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N]) =
    s_eventually[M, N](using ev_n)

  /**
   * Unbounded always operator.
   * 
   * @return always expression
   */
  transparent inline def always =
    PExprAlways(PUnaryOp.always, this)

  /**
   * LTL alias for [[always]] operator.
   * 
   * @return always expression
   */
  transparent inline def G = always

  /**
   * Unbounded strong always operator.
   * 
   * @return strong always expression
   */
  transparent inline def s_always =
    PExprSAlways(PUnaryOp.s_always, this)

  /**
   * LTL alias for [[s_always]] operator.
   * 
   * @return strong always expression
   */
  transparent inline def sG = s_always

  /**
   * Unbounded eventually operator.
   * 
   * @return eventually expression
   */
  transparent inline def eventually =
    PExprEventually(PUnaryOp.eventually, this)

  /**
   * LTL alias for [[eventually]] operator.
   * 
   * @return eventually expression
   */
  transparent inline def F = eventually

  /**
   * Unbounded strong eventually operator.
   * 
   * @return strong eventually expression
   */
  transparent inline def s_eventually =
    PExprSEventually(PUnaryOp.s_eventually, this)

  /**
   * LTL alias for [[s_eventually]] operator.
   * 
   * @return strong eventaully expression
   */
  transparent inline def sF = s_eventually

  // Boolean operators

  /**
   * Property conjunction operator.
   * 
   * @param rhs property expression
   * @return binary expression
   */
  transparent inline def and(rhs: PExpr) =
    PExprBinaryProperty(PBinaryOp.and, this, rhs)

  /**
   * Property disjunction operator.
   * 
   * @param rhs property expression
   * @return binary expression
   */
  transparent inline def or(rhs: PExpr) =
    PExprBinaryProperty(PBinaryOp.or, this, rhs)

  /**
   * Property implication operator.
   * 
   * @param rhs property expression
   * @return binary expression
   */
  transparent inline def implies(rhs: PExpr) =
    PExprBinaryProperty(PBinaryOp.implies, this, rhs)

  /**
   * Property equivalence operator.
   * 
   * @param rhs property expression
   * @return binary expression
   */
  transparent inline def iff(rhs: PExpr) =
    PExprBinaryProperty(PBinaryOp.iff, this, rhs)

  // Temporal operators

  /**
   * Temporal until operator.
   * 
   * @param rhs property expression
   * @return binary expression
   */
  transparent inline def until(rhs: PExpr) =
    PExprBinaryProperty(PBinaryOp.until, this, rhs)

  /**
   * LTL alias for [[until]] operator.
   * 
   * @param rhs property expression
   * @return binary expression
   */
  transparent inline def U(rhs: PExpr) = until(rhs)

  /**
   * Temporal strong until operator.
   * 
   * @param rhs property expression
   * @return binary expression
   */
  transparent inline def s_until(rhs: PExpr) =
    PExprBinaryProperty(PBinaryOp.s_until, this, rhs)

  /**
   * LTL alias for [[s_until]] operator.
   * 
   * @param rhs property expression
   * @return binary expression
   */
  transparent inline def sU(rhs: PExpr) =
    s_until(rhs)

  /**
   * Temporal until_with operator.
   * 
   * @param rhs property expression
   * @return binary expression
   */
  transparent inline def until_with(rhs: PExpr) =
    PExprBinaryProperty(PBinaryOp.until_with, this, rhs)

  /**
   * Temporal strong until_with operator.
   * 
   * @param rhs property expression
   * @return binary expression
   */
  transparent inline def s_until_with(rhs: PExpr) =
    until_with(rhs)

  // @formatter:on
}
