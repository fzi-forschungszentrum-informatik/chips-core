/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips.ast

/**
 * Abstract data type representing a sequence expression.
 *
 * === Type ===
 * [[SExpr]] =
 * [[SExprInitialCycleDelayExpr]]
 * | [[SExprCycleDelayExpr]]
 * | [[SExprExprCycleDelayExpr]]
 * | [[SExprBinary]]
 */
trait SExpr extends PExpr {

  import scala.compiletime.{constValue, error}
  import chips.{BInt, BInf, BVal, BRange}
  import chips.BIntConversions.{given}
  import chips.BRange.{given}
  import chips.OrdInstances.{given}

  def printSVA: String

  /**
   * Sequence disjunction operator.
   *
   * @param rhs sequence expression
   * @return binary sequence expression
   */
  transparent inline def or(rhs: SExpr) =
    SExprBinary(SBinaryOp.`op_or`, this, rhs)

  /**
   * Sequence conjunction operator.
   *
   * @param rhs sequence epxression
   * @return binary sequence expression
   */
  transparent inline def and(rhs: SExpr) =
    SExprBinary(SBinaryOp.`op_and`, this, rhs)

  /**
   * Throughout operator.
   *
   * @param rhs sequence expression
   * @return binary sequence expression
   */
  transparent inline def throughout(rhs: SExpr) =
    SExprBinary(SBinaryOp.`op_throughout`, this, rhs)

  /**
   * Within operator.
   *
   * @param rhs sequence expression
   * @return binary sequence expression
   */
  transparent inline def within(rhs: SExpr) =
    SExprBinary(SBinaryOp.`op_within`, this, rhs)

  /**
   * Intersect expression.
   *
   * @param rhs sequence expression
   * @return binary sequence expression
   */
  transparent inline def intersect(rhs: SExpr) =
    SExprBinary(SBinaryOp.`op_intersect`, this, rhs)

  /**
   * Initial delay operator.
   *
   * @tparam N postive integer bound or infinite range character: '*' (zero or more times), '+' (one or more times)
   * @return initial delay expression
   */
  transparent inline def ##/[N <: Int | String]: SExprInitialCycleDelayExpr = {
    inline val n = constValue[N]
    inline n match
      case "*" => SExprInitialCycleDelayExpr(SUnaryOp.`op_##[*]`, None, this)
      case "+" => SExprInitialCycleDelayExpr(SUnaryOp.`op_##[+]`, None, this)
      case x: Int =>
        inline if !(x >= 0)
        then error("Invalid parameter value: initial delay operator `##/[n]` requires a parameter n >= 0.")
        else initialDelay(x)
      case _ => error("Invalid parameter value: initial delay operator `##/[n]` operator requires a parmeter of type Int or String with n = '*' | '+' ")
  }

  def initialDelay(n: BInt) =
    SExprInitialCycleDelayExpr(SUnaryOp.`op_##`, Some(n), this)

  /**
   * Initial delay range operator.
   *
   * @param ev_n right bound evidence
   * @tparam M left bound
   * @tparam N (in)finite right bound
   * @return initial delay expression
   */
  transparent inline def ##/[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N]): SExprInitialCycleDelayExpr = {
    inline val m = constValue[M]
    inline val n = inline ev_n.value match
      case x: Int => x
      case BInf => -1

    inline val predicate = (m >= 0 && m < n)
    inline if !predicate
    then error("Invalid parameter range: initial delay range operator `##/[m,n]` requires a valid range with  m >= 0 and m < n")
    else initialDelay(BRange(BVal(m), n))
  }

  def initialDelay(r: BRange) =
    SExprInitialCycleDelayExpr(SUnaryOp.`op_##`, Some(r), this)

  /**
   * Delay operator.
   *
   * @param rhs sequence expression
   * @tparam N postive integer bound or infinite range character: '*' (zero or more times), '+' (one or more times)
   * @return cycle delay expression
   */
  transparent inline def delay[N <: Int | String](rhs: SExpr): SExprExprCycleDelayExpr = {
    inline val n = constValue[N]
    inline n match {
      case "*" => SExprExprCycleDelayExpr(SBinaryOp.`op_##[*]`, None, this, rhs)
      case "+" => SExprExprCycleDelayExpr(SBinaryOp.`op_##[+]`, None, this, rhs)
      case x: Int => {
        inline if !(x >= 0)
        then error("Invalid parameter value: `delay[n]` operator requires a parameter n >= 0.")
        else delay(x)(rhs)
      }
      case _ => error("Invalid parameter value: `delay[n]` operator requires a parmeter of type Int or String with n = '*' | '+' ")
    }
  }

  def delay(n: BInt)(rhs: SExpr) =
    SExprExprCycleDelayExpr(SBinaryOp.`op_##`, Some(n), this, rhs)

  /**
   * Delay range operator.
   *
   * @param ev_n right bound evidence
   * @param rhs  sequence expression
   * @tparam M left bound
   * @tparam N (in)finite right bound
   * @return cycle delay expression
   */
  transparent inline def delay[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N])
                                                              (rhs: SExpr): SExprExprCycleDelayExpr = {
    inline val m = constValue[M]
    inline val n = inline ev_n.value match
      case x: Int => x
      case BInf => -1

    inline val predicate = (m >= 0) && (m < n)
    inline if !predicate
    then error("Invalid parameter range: `delay[m,n]` operator requires a valid range with m >= 0 and m < n.")
    else delay(BRange(BVal(m), n))(rhs)
  }

  def delay(n: BRange)(rhs: SExpr) =
    SExprExprCycleDelayExpr(SBinaryOp.`op_##`, Some(n), this, rhs)

  /**
   * SVA alias for [[delay]] operator.
   *
   * @param rhs sequence expression
   * @tparam N postive integer bound or infinite range character: '*' (zero or more times), '+' (one or more times)
   * @return
   */
  transparent inline def ##[N <: Int | String](rhs: SExpr) =
    delay[N](rhs)

  /**
   * SVA alias for [[delay]] operator.
   *
   * @param rhs sequence expression
   * @tparam M left bound
   * @tparam N (in)finite right bound
   * @return cycle delay expression
   */
  transparent inline def ##[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N])
                                                           (rhs: SExpr) =
    delay[M, N](using ev_n)(rhs)

  /**
   * Consecutive repetition operator.
   *
   * @tparam N postive integer bound or infinite range character: '*' (zero or more times), '+' (one or more times)
   * @return cycle delay expression
   */
  transparent inline def rep[N <: Int | String]: SExprCycleDelayExpr = {
    inline val n = constValue[N]
    inline n match {
      case "*" => SExprCycleDelayExpr(SUnaryOp.`op_[*]`, None, this)
      case "+" => SExprCycleDelayExpr(SUnaryOp.`op_[+]`, None, this)
      case x: Int => {
        inline if !(x >= 0)
        then error("Invalid parameter value: `rep[n]` operator requires a parameter n >= 0.")
        else rep(x)
      }
      case _ => error("Invalid parameter value: `rep[n]` operator requires a parmeter of type Int or String with n = '*' | '+' ")
    }
  }

  def rep(n: BInt) =
    SExprCycleDelayExpr(SUnaryOp.`op_*`, Some(n), this)

  /**
   * Consecutive repetition range operator.
   *
   * @param ev_n right bound evidence
   * @tparam M left bound
   * @tparam N (in)finite right bound
   * @return
   */
  transparent inline def rep[M <: Int, N <: Int | BInf.type](using ev_n: ValueOf[N]): SExprCycleDelayExpr = {
    inline val m = constValue[M]
    inline val n = inline ev_n.value match
      case x: Int => x
      case BInf => -1

    inline val predicate = (m >= 0) && ((n == -1) || (m < n))
    inline if !predicate
    then error("Invalid parameter values: `rep[m,n]` operator requires parameters m >= 0 and m < n.")
    else rep(BRange(BVal(m), n))
  }

  def rep(r: BRange) =
    SExprCycleDelayExpr(SUnaryOp.`op_*`, Some(r), this)

  // Suffix implication

  /**
   * Overlapping sequence implication.
   *
   * @param rhs property expression
   * @return
   */
  transparent inline def |->(rhs: PExpr) =
    PExprBinarySequence(SPBinaryOp.`op_|->`, this, rhs)

  /**
   * Non-overlapping sequence implication.
   *
   * @param rhs property expression
   * @return
   */
  transparent inline def |=>(rhs: PExpr) =
    PExprBinarySequence(SPBinaryOp.`op_|=>`, this, rhs)

  // Suffix conjunction

  /**
   *
   * @param rhs property expression
   * @return
   */
  transparent inline def #=#(rhs: PExpr) =
    PExprBinarySequence(SPBinaryOp.`op_#=#`, this, rhs)

  /**
   *
   * @param rhs property expression
   * @return
   */
  transparent inline def #-#(rhs: PExpr) =
    PExprBinarySequence(SPBinaryOp.`op_#-#`, this, rhs)
}
