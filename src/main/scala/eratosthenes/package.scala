
import collection.immutable.StreamView
import collection.mutable

package object eratosthenes {

  /**
   * Sieve of Eratosthenes
   *
   * This uses the sieve of Eratosthenes to define an iterator over prime numbers.
   *
   * @param wheel wheel to generate candidate primes
   * @param firstPrime prime number from which to start the iteration
   */
  class SieveOfEratosthenes(wheel: Wheel, firstPrime: Int) extends BufferedIterator[Int] {
    private val composites = mutable.PriorityQueue[PrimeMultiples]()

    def head = wheel.head

    def hasNext = true

    def next() = {
      val p = head
      composites.enqueue(PrimeMultiples(p))
      wheel.next()
      while (composites.head.head <= head) {
        while (composites.head.head <= head) composites.enqueue(composites.dequeue().advance)
        wheel.next()
      }
      p
    }

    override def toString() = head + ": " +
      composites.toList.sorted.reverse.mkString("[", ", ", "]")

    /**
     * Iterator over a list of multiples of a prime number
     *
     * @param prime the prime number
     * @param multiples stream of multiples
     */
    class PrimeMultiples private(prime: Int, multiples: StreamView[Int, Stream[Int]]) extends Ordered[PrimeMultiples] {
      override def toString = prime + ": " + (0 to 2).map(multiples.head + _ * prime).mkString(",") + "..."

      val head = multiples.head

      def advance: PrimeMultiples = new PrimeMultiples(prime, multiples.tail)

      def compare(that: PrimeMultiples) = that.head.compare(head)
    }

    object PrimeMultiples {
      def apply(p: Int): PrimeMultiples = new PrimeMultiples(p, Stream.from(p * p, p).view)
    }

  }

  /**
   * Iterator over integers larger than some integer, skipping multiples of a set of smaller integers
   *
   * @param start the starting integer
   * @param steps list of step sizes to cycle through
   */
  class Wheel(start: Int, steps: Int*) extends BufferedIterator[Int] {
    private var n = start
    private var cycle = Vector(steps: _*)

    override def toString() = start + ": " + head + "..."

    def hasNext = true

    def next() = {
      val m = n
      n += cycle.head
      cycle = cycle.tail :+ cycle.head
      m
    }

    def head = n
  }

  object Wheel {
    implicit def apply(start: Int, steps: Int*) = new Wheel(start, steps: _*)

    /**
     * All integers >=3, skipping multiples of 2
     */
    def wheel2 = Wheel(3, 2)

    /**
     * All integers >=5, skipping multiples of 2 and 3
     */
    def wheel23 = Wheel(5, 2, 4)

    /**
     * All integers >=11, skipping multiples of 2, 3, 5, and 7
     */
    def wheel2357 = Wheel(11, 2, 4, 2, 4, 6, 2, 6, 4, 2, 4, 6, 6, 2, 6, 4, 2, 6, 4, 6, 8, 4, 2, 4, 2, 4, 8, 6, 4, 6,
      2, 4, 6, 2, 6, 6, 4, 2, 4, 6, 2, 6, 4, 2, 4, 2, 10, 2, 10)
  }

  object SieveOfEratosthenes {
    def apply(wheel: Wheel = Wheel(2, 1), firstPrime: Int = 2) = new SieveOfEratosthenes(wheel, firstPrime)

    /**
     * Iterator over prime numbers starting from 3
     */
    def sieveFrom3 = SieveOfEratosthenes(Wheel.wheel2, 3)

    /**
     * Iterator over prime numbers starting from 5
     */
    def sieveFrom5 = SieveOfEratosthenes(Wheel.wheel23, 5)

    /**
     * Iterator over prime numbers starting from 11
     */
    def sieveFrom11 = SieveOfEratosthenes(Wheel.wheel2357, 11)
  }

  /**
   * Iterator over all prime numbers
   */
  def primes = List(2, 3, 5, 7).view ++ SieveOfEratosthenes.sieveFrom11

}
