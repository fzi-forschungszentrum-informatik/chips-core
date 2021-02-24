/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips

object BIntTypes {
  /**
   * Type alias for the [[BInf.type]].
   */
  type $ = BInf.type

  /**
   * Type alias for consecutive repetition (zero or more).
   */
  type * = "*"

  /**
   * Type alias for consecutive repettion (one or more).
   */
  type + = "+"
}

/**
 * Abstract type representing a bounded integer with lower bound [[0]] and upper bound [[BInf]].
 */
sealed trait BInt {
  def printSVA: String
}

/**
 * Upper bound of ADT BInt.
 */
case object BInf extends BInt {
  override def printSVA: String = "$"
}

/**
 * Wrapper for bounded integer values.
 *
 * @param value positive integer value >= 0
 */
case class BVal(value: Int) extends BInt {
  require(value >= 0, "Bounded values must be >= 0!")

  override def printSVA: String = value.toString
}

/**
 * Implict conversions of [[Int]] and [[String]] values to [[BInt]] values.
 */
object BIntConversions {
  // @formatter:off
  given Conversion[Int, BInt] with
    def apply(x: Int): BInt = x match
      case v if v < 0 => BInf
      case v => BVal(v)
  
  given Conversion[String, BInt] with
    def apply(s: String): BInt = s match
      case "$" => BInf
      case _ => throw new IllegalArgumentException("Only '$' can be implicitly converted to BInf")
  // @formatter:on
}
