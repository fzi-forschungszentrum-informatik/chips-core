/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips

/**
 * == Infix Operators ==
 *
 * Infix operators subsume sequence and property operators.
 * All infix operators are inlined at the call site.
 */
object InfixOps {

  import chips.ast._
  import chips.{BInf}

  // Sequences

  /**
   * Sequence disjunction operator.
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @return binary sequence expression
   */
  transparent inline def or(lhs: SExpr, rhs: SExpr) =
    lhs.or(rhs)

  /**
   * Sequence conjunction operator.
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @return binary sequence expression
   */
  transparent inline def and(lhs: SExpr, rhs: SExpr) =
    lhs.and(rhs)

  /**
   * Throughout operator.
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @return binary sequence expression
   */
  transparent inline def throughout(lhs: SExpr, rhs: SExpr) =
    lhs.throughout(rhs)

  /**
   * Within operator.
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @return binary sequence expression
   */
  transparent inline def within(lhs: SExpr, rhs: SExpr) =
    lhs.within(rhs)

  /**
   * Intersect expression.
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @return binary sequence expression
   */
  transparent inline def intersect(lhs: SExpr, rhs: SExpr) =
    lhs.intersect(rhs)

  /**
   * Initial delay operator.
   *
   * @param lhs sequence expression
   * @tparam N bound
   * @return cycle delay expression
   */
  transparent inline def ##/[N <: Int | String](lhs: SExpr): SExprInitialCycleDelayExpr =
    lhs.##/[N]

  /**
   * Initial delay range operator.
   *
   * @param lhs sequence expression
   * @tparam M left bound
   * @tparam N right bound
   * @return cycle delay expression
   */
  transparent inline def ##/[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N])
                                                            (lhs: SExpr): SExprInitialCycleDelayExpr =
    lhs.##/[M, N](using ev_n)

  /**
   * SVA alias for [[delay]] operator (sequence concatenation).
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @tparam N postive integer bound or infinite range character: '*' (zero or more times), '+' (one or more times)
   * @return
   */
  transparent inline def delay[N <: Int | String](lhs: SExpr, rhs: SExpr) =
    lhs.delay[N](rhs)

  /**
   * SVA alias for [[delay]] operator (sequence concatenation).
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @tparam M left bound
   * @tparam N (in)finite right bound
   * @return cycle delay expression
   */
  transparent inline def delay[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N])
                                                              (lhs: SExpr, rhs: SExpr) =
    lhs.delay[M, N](using ev_n)(rhs)

  /**
   * Consecutive repetition operator.
   *
   * @param lhs sequence expression
   * @tparam N postive integer bound or infinite range character: '*' (zero or more times), '+' (one or more times)
   * @return cycle delay expression
   */
  transparent inline def rep[N <: Int | String](lhs: SExpr): SExprCycleDelayExpr =
    lhs.rep[N]


  /**
   * Consecutive repetition range operator.
   *
   * @param ev_n right bound evidence
   * @param lhs  sequence expression
   * @tparam M left bound
   * @tparam N (in)finite right bound
   * @return
   */
  transparent inline def rep[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N])(lhs: SExpr): SExprCycleDelayExpr =
    lhs.rep[M, N].rep

  /**
   * Overlapping sequence implication.
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @return
   */
  transparent inline def |->(lhs: SExpr, rhs: PExpr) =
    lhs.|->(rhs)

  /**
   * Non-overlapping sequence implication.
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @return
   */
  transparent inline def |=>(lhs: SExpr, rhs: PExpr) =
    lhs.|=>(rhs)

  /**
   * TODO:
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @return
   */
  transparent inline def #=#(lhs: SExpr, rhs: PExpr) =
    lhs.#=#(rhs)

  /**
   * TODO:
   *
   * @param lsh left hand side
   * @param rhs right hand side
   * @return
   */
  transparent inline def #-#(lhs: SExpr, rhs: PExpr) =
    lhs.#-#(rhs)


  // Properties

  /**
   * Strong operator.
   *
   * @param lhs property expression
   * @return strong property expression
   */
  transparent inline def strong(lhs: PExpr): PExprStrong =
    lhs.strong

  /**
   * Weak operator.
   *
   * @param lhs property expression
   * @return weak property expression
   */
  transparent inline def weak(lhs: PExpr): PExprWeak =
    lhs.weak

  /**
   * Boolean property negation.
   *
   * @param lhs property expression
   * @return negated property expression
   */
  transparent inline def not(lhs: PExpr): PExprNot =
    lhs.not

  /**
   * Bounded nexttime operator.
   *
   * @param lhs property expression
   * @tparam N bound
   * @return nexttime property expression
   */
  transparent inline def nexttime[N <: Int](lhs: PExpr): PExprNexttime =
    lhs.nexttime[N]

  /**
   * LTL alias for [[nexttime]] operator.
   *
   * @param lhs property expression
   * @tparam N bound
   * @return nexttime property expression
   */
  transparent inline def X[N <: Int](lhs: PExpr): PExprNexttime =
    nexttime[N](lhs)

  /**
   * Bounded strong nexttime operator.
   *
   * @param lhs property expression
   * @tparam N bound
   * @return strong nexttime expression
   */
  transparent inline def s_nexttime[N <: Int](lhs: PExpr): PExprSNexttime =
    lhs.s_nexttime[N]

  /**
   * LTL alias for [[s_nexttime]] operator.
   *
   * @param lhs property expression
   * @tparam N bound
   * @return strong nexttime expression
   */
  transparent inline def sX[N <: Int](lhs: PExpr): PExprSNexttime =
    s_nexttime[N](lhs)

  /**
   * Bounded always operator.
   *
   * @param ev_n right bound evidence
   * @param lhs  property expression
   * @tparam M left bound
   * @tparam N right bound
   * @return always property expression
   */
  transparent inline def always[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N])
                                                               (lhs: PExpr): PExprAlways =
    lhs.always[M, N](using ev_n)

  /**
   * LTL alias for [[always]]  operator.
   *
   * @param ev_n right bound evidence
   * @param lhs  property expression
   * @tparam M left bound
   * @tparam N right bound
   * @return always property expression
   */
  transparent inline def G[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N])
                                                          (lhs: PExpr): PExprAlways =
    always[M, N](using ev_n)(lhs)

  /**
   * Strong always operator.
   *
   * @param ev_n right bound evidence
   * @param lhs  property expression
   * @tparam M left bound
   * @tparam N right bound
   * @return strong always expression
   */
  transparent inline def s_always[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N])
                                                                 (lhs: PExpr): PExprSAlways =
    lhs.s_always[M, N](using ev_n)

  /**
   * LTL alias for [[s_always]] operator.
   *
   * @param ev_n right bound evidence
   * @param lhs  property expression
   * @tparam M left bound
   * @tparam N right bound
   * @return strong always expression
   */
  transparent inline def sG[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N])
                                                           (lhs: PExpr): PExprSAlways =
    s_always[M, N](using ev_n)(lhs)

  /**
   * Eventually operator.
   *
   * @param ev_n right bound evidence
   * @param lhs  property expression
   * @tparam M left bound
   * @tparam N right bound
   * @return eventually expression
   */
  inline def eventually[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N])
                                                       (lhs: PExpr): PExprEventually =
    lhs.eventually[M, N](using ev_n)

  /**
   * LTL alias for [[eventually]] operator.
   *
   * @param ev_n right bound evidence
   * @param lhs  property expression
   * @tparam M left bound
   * @tparam N right bound
   * @return eventually expression
   */
  transparent inline def F[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N])
                                                          (lhs: PExpr): PExprEventually =
    eventually[M, N](using ev_n)(lhs)

  /**
   * Strong eventually expression.
   *
   * @param ev_n right bound evidence
   * @param lhs  property expression
   * @tparam M left bound
   * @tparam N right bound
   * @return strong eventually expression
   */
  transparent inline def s_eventually[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N])
                                                                     (lhs: PExpr): PExprSEventually =
    lhs.s_eventually[M, N](using ev_n)

  /**
   * LTL alias for [[s_eventually]] operator.
   *
   * @param ev_n right boud evidence
   * @param lhs  property expression
   * @tparam M left bound
   * @tparam N right bound
   * @return strong eventually expression
   */
  transparent inline def sF[M <: Int, N <: Int | BInf.type](ev_n: ValueOf[N])
                                                           (lhs: PExpr): PExprSEventually =
    s_eventually[M, N](using ev_n)(lhs)


  /**
   * If-then-else (ite) operator.
   *
   * @param cond
   * @param thenExpr
   * @param elseExpr
   * @return ite expression
   */
  transparent inline def ite(cond: PBoolExpr)
                            (thenExpr: PExpr)
                            (elseExpr: PExpr) = PExprIte(cond, thenExpr, elseExpr)

  /**
   * Property conjunction operator.
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @return binary expression
   */
  transparent inline def and(lhs: PExpr, rhs: PExpr) =
    lhs.and(rhs)

  /**
   * Property disjunction operator.
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @return binary expression
   */
  transparent inline def or(lhs: PExpr, rhs: PExpr) =
    lhs.or(rhs)

  /**
   * Property implication operator.
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @return binary expression
   */
  transparent inline def implies(lhs: PExpr, rhs: PExpr) =
    lhs.implies(rhs)

  /**
   * Property equivalence operator.
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @return binary expression
   */
  transparent inline def iff(lhs: PExpr, rhs: PExpr) =
    lhs.iff(rhs)

  /**
   * Temporal until operator.
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @return binary expression
   */
  transparent inline def until(lhs: PExpr, rhs: PExpr) =
    lhs.until(rhs)

  /**
   * LTL alias for [[until]] operator.
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @return binary expression
   */
  transparent inline def U(lhs: PExpr, rhs: PExpr) =
    until(lhs, rhs)

  /**
   * Temporal strong until operator.
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @return binary expression
   */
  transparent inline def s_until(lhs: PExpr, rhs: PExpr) =
    lhs.s_until(rhs)

  /**
   * LTL alias for [[s_until]] operator.
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @return binary expression
   */
  transparent inline def sU(lhs: PExpr, rhs: PExpr) =
    s_until(lhs, rhs)

  /**
   * Temporal until_with operator.
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @return binary expression
   */
  transparent inline def until_with(lhs: PExpr, rhs: PExpr) =
    lhs.until_with(rhs)

  /**
   * Temporal strong until_with operator.
   *
   * @param lhs left hand side
   * @param rhs right hand side
   * @return binary expression
   */
  transparent inline def s_until_with(lhs: PExpr, rhs: PExpr) =
    until_with(lhs, rhs)
}
