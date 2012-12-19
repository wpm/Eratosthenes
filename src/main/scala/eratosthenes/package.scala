import testing.Benchmark

package object eratosthenes {
  /**
   * All the prime numbers.
   */
  def primes(): Stream[Int] = {
    val p = List(2, 3, 5, 7, 11)
    p.toStream ++ primesRec(wheel2357, Sieve(p))
  }

  def primesRec(ns: Stream[Int] = Stream.from(2), composites: Sieve = Sieve()): Stream[Int] = {
    val n = ns.head
    if (!composites.contains(n))
      n #:: primesRec(ns.tail, composites + n)
    else
      primesRec(ns.tail, composites ! n)
  }

  /**
   * A table of infinite lists of multiples of primes indexed by the smallest multiple.
   */
  class Sieve private(val m: Map[Int, List[PrimeMultiples]] = Map()) {
    def contains(n: Int): Boolean = m.contains(n)

    /**
     * Add multiples of a prime
     *
     * @param p the prime to add
     * @return a sieve with the prime's multiples added
     */
    def +(implicit p: PrimeMultiples) = new Sieve(addToSieve(m, p))

    /**
     * Advance multiples lists
     *
     * @param n the composite
     * @return a sieve will all the lists with n as the smallest multiple shifted by one
     */
    def !(n: Int): Sieve = new Sieve(((m - n) /: m(n)) {
      (a, b) => addToSieve(a, b.tail)
    })

    private def addToSieve(m: Map[Int, List[PrimeMultiples]], multiples: PrimeMultiples) = {
      val min = multiples.head
      m + (min -> (multiples :: m.getOrElse(min, Nil)))
    }

    override def toString = m.keys.toSeq.sorted.map(k => k + ":" + m(k).mkString("(", ",", ")")).mkString(", ")
  }

  object Sieve {
    def apply() = new Sieve()

    def apply(ps: Traversable[Int]): Sieve = (Sieve() /: ps)(_ + _)
  }

  /**
   * An infinite list of the multiples of a prime number starting at its square
   */
  class PrimeMultiples private(p: Int, multiples: Stream[Int]) {
    def head: Int = multiples.head

    def tail: PrimeMultiples = new PrimeMultiples(p, multiples.tail)

    override def toString = "%d: %s...".format(p, multiples.take(3).mkString(","))
  }

  object PrimeMultiples {
    /**
     * @param p the prime
     * @return the multiples of the prime starting at p*p
     */
    def apply(p: Int) = new PrimeMultiples(p, Stream.from(p * p, p))

    implicit def primeToMultiples(p: Int): PrimeMultiples = PrimeMultiples(p)
  }

  /**
   * Integers larger than a start value, skipping multiples of a set of integers smaller than the start value.
   *
   * @param start the starting value
   * @param steps list of step sizes to cycle through
   * @return stream of integers
   */
  def wheel(start: Int, steps: Traversable[Int]): Stream[Int] = {
    def wheelRec(n: Int, cycle: Stream[Int]): Stream[Int] = {
      val m = n + cycle.head
      m #:: wheelRec(m, cycle.tail)
    }
    wheelRec(start, Stream.continually(0).flatMap(_ => steps))
  }

  /**
   * All integers >=3, skipping multiples of 2
   */
  val wheel2 = wheel(3, List(2))

  /**
   * All integers >=5, skipping multiples of 2 and 3
   */
  val wheel23 = wheel(5, List(2, 4))

  /**
   * All integers >=11, skipping multiples of 2, 3, 5, and 7
   */
  val wheel2357 = wheel(11, List(2, 4, 2, 4, 6, 2, 6, 4, 2, 4, 6, 6, 2, 6, 4, 2, 6, 4, 6, 8, 4, 2, 4, 2, 4, 8, 6, 4, 6,
    2, 4, 6, 2, 6, 6, 4, 2, 4, 6, 2, 6, 4, 2, 4, 2, 10, 2, 10))

  def primesPerSecond(n: Int, reps: Int = 10) = {
    object Timer extends {} with Benchmark {
      def run() {
        primes().take(n).force
      }
    }
    1000 * n / (Timer.runBenchmark(reps).sum.toDouble / reps)
  }

  def main(args: Array[String]) {
    val n = args(0).toInt
    val reps = args(1).toInt
    // Repeat the test a few times because the first run is
    // always an order of magnitude slower for reasons I don't understand.
    for (_ <- (1 to 5))
      println(primesPerSecond(n, reps).toInt + " primes/sec")
  }
}
