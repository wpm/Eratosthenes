package eratosthenes

import org.scalatest.FlatSpec

class EratosthenesTests extends FlatSpec {
  "The first ten primes" should "be 2,3,5,7,11,13,17,19,23,29" in {
    expect(List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29)) {
      primes.take(10)
    }
  }
}
