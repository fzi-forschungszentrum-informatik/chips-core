/*
 * Copyright (c) 2021 FZI Forschungszentrum Informatik
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package chips

import chips.OrdInstances.{given}

/**
 * Bounded range.
 *
 * @param m   finite left bound
 * @param n   (in)finite right bound
 * @param ord typeclass instance
 */
final case class BRange(m: BVal, n: BInt)(using ord: Ord[BInt]) {
  require((m >= ord.zero) && (m < n), s"Invalid range `[${m.printSVA}:${n.printSVA}]`!")

  def printSVA: String =
    s"${m.printSVA}:${n.printSVA}"
}

/**
 * Implict conversions of [[Tuple2]] values to [[BRange]] values.
 */
object BRangeConversions {

  import chips.{BVal}

  import chips.BIntConversions.{given}

  // @formatter:off
  given Conversion[(Int, Int), BRange] with
    def apply(t: (Int, Int)): BRange = BRange(BVal(t._1), t._2)
  
  given Conversion[(Int, String), BRange] with
    def apply(t: (Int, String)): BRange = BRange(BVal(t._1), t._2)
  // @formatter:on
}
