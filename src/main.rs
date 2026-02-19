use std::env;

fn first_n_primes(n: usize) -> Vec<u64> {
    if n == 0 {
        return vec![];
    }

    // Determine upper bound using prime number theorem approximation
    let limit: usize = if n < 6 {
        100
    } else {
        let n_f = n as f64;
        let upper = n_f * (n_f.ln() + n_f.ln().ln());
        upper.ceil() as usize + 1
    };

    // Sieve of Eratosthenes
    let mut is_prime = vec![true; limit + 1];
    is_prime[0] = false;
    if limit >= 1 {
        is_prime[1] = false;
    }

    let mut i = 2;
    while i * i <= limit {
        if is_prime[i] {
            let mut j = i * i;
            while j <= limit {
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

    if args.len() < 2 {
        eprintln!("Usage: {} <N>", args[0]);
        eprintln!("  Prints the first N prime numbers, one per line.");
        std::process::exit(1);
    }

    let n: usize = match args[1].parse() {
        Ok(val) => val,
        Err(_) => {
            eprintln!("Error: '{}' is not a valid non-negative integer.", args[1]);
            eprintln!("Usage: {} <N>", args[0]);
            std::process::exit(1);
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
    fn test_first_hundred_primes_last_element() {
        let primes = first_n_primes(100);
        assert_eq!(primes.len(), 100);
        assert_eq!(*primes.last().unwrap(), 541);
    }
}
