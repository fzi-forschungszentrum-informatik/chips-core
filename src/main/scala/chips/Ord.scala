/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips

/**
 * Typeclass to support ordering.
 *
 * @tparam T
 */
trait Ord[T] {
  def compare(x: T, y: T): Int

  def zero: T

  extension (x: T) def <(y: T) = compare(x, y) < 0
  extension (x: T) def >(y: T) = compare(x, y) > 0

  extension (x: T) def <=(y: T) = compare(x, y) <= 0
  extension (x: T) def >=(y: T) = compare(x, y) >= 0
}

/**
 * [[Ord]] typeclass instances.
 */
object OrdInstances {
  // @formatter:off
  given Ord[Int] with
    override def compare(x: Int, y: Int): Int = if x < y then -1 else if x > y then +1 else 0
  
    override def zero: Int = 0

  given (using ord: Ord[Int]): Ord[BInt] with
    override def compare(bx: BInt, by: BInt): Int =
      (bx, by) match
        case (BInf, BInf) => 0
        case (_, BInf) => -1
        case (BInf, _) => +1
        case (BVal(x), BVal(y)) => ord.compare(x, y)

    override def zero: BVal = BVal(0)
  // @formatter:on
}
