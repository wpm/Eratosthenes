Genuine Sieve of Eratosthenes
=============================

This implements the algorithm described in Melissa E. O'Neill's ["The Genuine Sieve of Eratosthenes"](http://www.cs.hmc.edu/~oneill/papers/Sieve-JFP.pdf).
It is an efficient functional-programming algorithm for generating all the prime numbers.

    scala> eratosthenes.primes.take(10).toList
    // Returns List[Int] = List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29)
