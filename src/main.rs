use std::env;
use std::process;

fn first_n_primes(n: usize) -> Vec<u64> {
    if n == 0 {
        return vec![];
    }

    // Determine an upper bound for the sieve using the prime-counting upper bound.
    // For n >= 6: upper bound ~ n * (ln(n) + ln(ln(n)))
    // For n < 6: use a hardcoded ceiling of 100
    let upper_bound: usize = if n < 6 {
        100
    } else {
        let n_f = n as f64;
        let ln_n = n_f.ln();
        let ln_ln_n = ln_n.ln();
        (n_f * (ln_n + ln_ln_n)).ceil() as usize + 10
    };

    // Sieve of Eratosthenes
    let mut is_prime = vec![true; upper_bound + 1];
    is_prime[0] = false;
    if upper_bound >= 1 {
        is_prime[1] = false;
    }

    let mut i = 2;
    while i * i <= upper_bound {
        if is_prime[i] {
            let mut j = i * i;
            while j <= upper_bound {
                is_prime[j] = false;
                j += i;
            }
        }
        i += 1;
    }

    let primes: Vec<u64> = is_prime
        .iter()
        .enumerate()
        .filter(|(_, &p)| p)
        .map(|(i, _)| i as u64)
        .take(n)
        .collect();

    primes
}

fn main() {
    let args: Vec<String> = env::args().collect();

    if args.len() != 2 {
        eprintln!("Usage: {} <N>", args[0]);
        eprintln!("  N  a positive integer — prints the first N prime numbers");
        process::exit(1);
    }

    let n: usize = match args[1].parse::<usize>() {
        Ok(v) if v > 0 => v,
        Ok(_) => {
            eprintln!("Error: N must be a positive integer, got '{}'", args[1]);
            eprintln!("Usage: {} <N>", args[0]);
            process::exit(1);
        }
        Err(_) => {
            eprintln!("Error: '{}' is not a valid positive integer", args[1]);
            eprintln!("Usage: {} <N>", args[0]);
            process::exit(1);
        }
    };

    for prime in first_n_primes(n) {
        println!("{}", prime);
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_zero_primes() {
        assert_eq!(first_n_primes(0), vec![]);
    }

    #[test]
    fn test_first_prime() {
        assert_eq!(first_n_primes(1), vec![2]);
    }

    #[test]
    fn test_first_ten_primes() {
        assert_eq!(
            first_n_primes(10),
            vec![2, 3, 5, 7, 11, 13, 17, 19, 23, 29]
        );
    }

    #[test]
    fn test_hundredth_prime() {
        let primes = first_n_primes(100);
        assert_eq!(primes.len(), 100);
        assert_eq!(primes[99], 541);
    }
}
