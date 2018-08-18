// Halide self-made exercise 1: Fast polynomial multiplication using FFT.

// On os x:
// g++ exercise_01*.cpp -g -I ../include -L ../bin -lHalide -o exercise_01 -std=c++11
// DYLD_LIBRARY_PATH=../bin ./exercise_01

#include "Halide.h"
#include "clock.h"
#include <stdio.h>
#include <math.h>
#include <algorithm>
#include <iostream>

using namespace Halide;

struct Complex { // Struct copied from the tuples lesson (13)
    Expr real, imag;

    // Construct from a Tuple
    Complex(Tuple t) : real(t[0]), imag(t[1]) {}

    // Construct from a pair of Exprs
    Complex(Expr r, Expr i) : real(r), imag(i) {}

    // Construct from a call to a Func by treating it as a Tuple
    Complex(FuncRef t) : Complex(Tuple(t)) {}

    // Convert to a Tuple
    operator Tuple() const {
        return {real, imag};
    }

    // Complex addition
    Complex operator+(const Complex &other) const {
        return {real + other.real, imag + other.imag};
    }

    // Complex multiplication
    Complex operator*(const Complex &other) const {
        return {real * other.real - imag * other.imag,
                real * other.imag + imag * other.real};
    }

    // Complex magnitude, squared for efficiency
    Expr magnitude_squared() const {
        return real * real + imag * imag;
    }

    Complex reverse() const {
        return Complex(imag, real);
    }

    Complex scale(const Expr &c) const {
        return Complex(c * real, c * imag);
    }

    // Computes e to the power of pi * pi_factor
    static Complex euler_form(const Expr &pi_factor) {
        Expr angle = pi_factor * Expr(M_PI);
        return {cos(angle), sin(angle)};
    }
};

int pow_of_2(const unsigned int &power) {

  // straightforward power of 2 returned as an int
  return 1 << power;

}

int invert(const int &n, const unsigned int &power) {
  int k = 0;

  for (int y = 0; y < power; y++) {
    k = (n & pow_of_2(y)) ? k + pow_of_2(power - 1 - y) : k;
  }

  return k;
}

Buffer<double> fast_multiply(Func &vector_a, Func &vector_b, Var &x, Var &y, const int &power_of_two) {

  double t1 = current_time();

  // Recursive partitioning of odd and even terms
  Expr zero = cast<double>(0);

  int split_factor = power_of_two < 4 ? pow_of_2(power_of_two - 1) : 8;

  Func base_a("base_a"), base_b("base_b");
  base_a(x) = Complex(zero, zero);
  base_b(x) = Complex(zero, zero);
  for (int i = 0; i < pow_of_2(power_of_two); i++) {
    base_a(i) = vector_a(invert(i, power_of_two));
    base_b(i) = vector_b(invert(i, power_of_two));
  }
  Var xi("xi"), xo("xo");

  // Compute FFT in stages of convolution
  Func stages_a[power_of_two], odds_a, evens_a, 
    stages_b[power_of_two], odds_b, evens_b;
  for (int i = 1; i < power_of_two + 1; i++) {
    stages_a[i - 1] = Func("stage_a_" + std::to_string(i));
    stages_b[i - 1] = Func("stage_a_" + std::to_string(i));
    Func prior_a = i - 1 ? stages_a[i - 2] : base_a;
    Func prior_b = i - 1 ? stages_b[i - 2] : base_b;

    // Initialize odds and evens sums
    Func odds_a("odds_a_" + std::to_string(i - 1));
    Func evens_a("evens_a_" + std::to_string(i - 1));
    Func odds_b("odds_b_" + std::to_string(i - 1));
    Func evens_b("evens_b_" + std::to_string(i - 1));

    // Compute sums
    int block_size = pow_of_2(i);
    int block_off = pow_of_2(i - 1);
    evens_a(x) = prior_a((x % block_off) + (x / block_size) * block_size);
    odds_a(x) = prior_a((x % block_off) + block_off + (x / block_size) * block_size);
    evens_b(x) = prior_b((x % block_off) + (x / block_size) * block_size);
    odds_b(x) = prior_b((x % block_off) + block_off + (x / block_size) * block_size);

    // Compute FFT result
    Func euler("euler"), odd_prod_a("odd_prod_a"), odd_prod_b("odd_prod_b");
    euler(x) = Complex::euler_form(cast<double>(-2 * x) / cast<double>(block_size));

    odd_prod_a(x) = Complex(select((x % block_size) < block_off, 
      cast<double>(1), cast<double>(-1)), zero);
    odd_prod_a(x) = Complex(odd_prod_a(x)) * Complex(odds_a(x));
    odd_prod_a(x) = Complex(odd_prod_a(x)) * Complex(euler(x % block_off));
    stages_a[i - 1](x) = Complex(evens_a(x)) + Complex(odd_prod_a(x));

    odd_prod_b(x) = Complex(select((x % block_size) < block_off, 
      cast<double>(1), cast<double>(-1)), zero);
    odd_prod_b(x) = Complex(odd_prod_b(x)) * Complex(odds_b(x));
    odd_prod_b(x) = Complex(odd_prod_b(x)) * Complex(euler(x % block_off));
    stages_b[i - 1](x) = Complex(evens_b(x)) + Complex(odd_prod_b(x));

    odds_a.compute_at(odd_prod_a, x);
    odds_b.compute_at(odd_prod_b, x);

    evens_a.compute_at(stages_a[i - 1], (i == power_of_two) ? xo : x);
    evens_b.compute_at(stages_b[i - 1], (i == power_of_two) ? xo : x);

    if (i == power_of_two) {
      stages_a[i - 1].compute_root();
      stages_a[i - 1].split(x, xo, xi, split_factor);
      stages_a[i - 1].parallel(xo);
      stages_b[i - 1].compute_root();
      stages_b[i - 1].split(x, xo, xi, split_factor);
      stages_b[i - 1].parallel(xo);
    }
    prior_a.compute_at(stages_a[i - 1], (i == power_of_two) ? xo : x);
    prior_b.compute_at(stages_b[i - 1], (i == power_of_two) ? xo : x);
  }

  // Multiply evaluation bases together and normalize the result vector
  Func vector_c("vector_c"), base_c("base_c");
  vector_c.compute_root();
  vector_c(x) = Complex(stages_a[power_of_two - 1](x)) 
      * Complex(stages_b[power_of_two - 1](x));
  vector_c(x) = Complex(vector_c(x)).scale(cast<double>(1) / cast<double>(pow_of_2(power_of_two)));
  base_c(x) = Complex(zero, zero);
  for (int i = 0; i < pow_of_2(power_of_two); i++) {
    base_c(i) = vector_c(invert(i, power_of_two));
  }
  vector_c.split(x, xo, xi, split_factor);
  vector_c.parallel(xo);
  vector_c.compute_root();

  // Compute iFFT in stages of convolution
  Func stages_c[power_of_two], odds_c, evens_c;
  for (int i = 1; i < power_of_two + 1; i++) {
    stages_c[i - 1] = Func("stage_c_" + std::to_string(i));
    Func prior_c = i - 1 ? stages_c[i - 2] : base_c;

    // Initialize odds and evens sums
    Func odds_c("odds_c_" + std::to_string(i - 1));
    Func evens_c("evens_c_" + std::to_string(i - 1));

    // Compute sums
    int block_size = pow_of_2(i);
    int block_off = pow_of_2(i - 1);
    evens_c(x) = prior_c((x % block_off) + (x / block_size) * block_size);
    odds_c(x) = prior_c((x % block_off) + block_off + (x / block_size) * block_size);

    // Compute iFFT result
    Func euler("euler"), odd_prod_c("odd_prod_c");
    euler(x) = Complex::euler_form(cast<double>(2 * x) / cast<double>(block_size));

    odd_prod_c(x) = Complex(select((x % block_size) < block_off, 
      cast<double>(1), cast<double>(-1)), zero);
    odd_prod_c(x) = Complex(odd_prod_c(x)) * Complex(odds_c(x));
    odd_prod_c(x) = Complex(odd_prod_c(x)) * Complex(euler(x % block_off));
    stages_c[i - 1](x) = Complex(evens_c(x)) + Complex(odd_prod_c(x));


    odds_c.compute_at(odd_prod_c, x);
    if (i == power_of_two) {
      stages_c[i - 1].compute_root();
      stages_c[i - 1].split(x, xo, xi, split_factor);
      stages_c[i - 1].parallel(xo);
      prior_c.compute_at(stages_c[i - 1], xo);
    } else {
      prior_c.compute_at(stages_c[i - 1], x);
    }
  }
  double t2 = current_time();

  // Compute results buffers
  Func mag_sq_a("magnitude_squared_a"), mag_sq_b("magnitude_squared_b"), mag_sq("magnitude_squared");
  mag_sq_a(x, y) = Complex(vector_a(x)).real;
  mag_sq_a(x, 1) = Complex(vector_a(x)).imag;
  mag_sq_a(x, 2) = Complex(vector_a(x)).magnitude_squared();
  mag_sq_a.parallel(y);
  mag_sq_b(x, y) = Complex(vector_b(x)).real;
  mag_sq_b(x, 1) = Complex(vector_b(x)).imag;
  mag_sq_b(x, 2) = Complex(vector_b(x)).magnitude_squared();
  mag_sq_b.parallel(y);
  mag_sq(x, y) = Complex(stages_c[power_of_two - 1](x)).real;
  mag_sq(x, 1) = Complex(stages_c[power_of_two - 1](x)).imag;
  mag_sq(x, 2) = Complex(stages_c[power_of_two - 1](x)).magnitude_squared();
  mag_sq.parallel(y);
  Buffer<double> initial_a = mag_sq_a.realize(1 << power_of_two, 3);
  Buffer<double> initial_b = mag_sq_b.realize(1 << power_of_two, 3);
  Buffer<double> result = mag_sq.realize(1 << power_of_two, 3);

  // Print vector data
  printf("Base vector a data:\n");
  for (int n = 0; n < (1 << power_of_two); n++) {
    printf("%f", initial_a(n, 0));
    printf(n == pow_of_2(power_of_two) - 1 ? "\n" : " ");
  }

  printf("Base vector b data:\n");
  for (int n = 0; n < (1 << power_of_two); n++) {
    printf("%f", initial_b(n, 0));
    printf(n == pow_of_2(power_of_two) - 1 ? "\n" : " ");
  }

  printf("Final vector data:\n");
  for (int n = 0; n < (1 << power_of_two); n++) {
    printf("%f", result(n, 0));
    printf(n == pow_of_2(power_of_two) - 1 ? "\n" : " ");
  }

  if (std::thread::hardware_concurrency())
    printf("Multiplication took %f seconds\n", (t2 - t1) / std::thread::hardware_concurrency());

  return result;
}

int main(int argc, char **argv) {

  Var x("x"), y("y");
  Func a("vector_a"), b("vector_b");

  a(x) = Complex(cast<double>(0), cast<double>(0));
  b(x) = Complex(cast<double>(0), cast<double>(0));

  char sa[1024];
  printf("Please enter in a space-delimited string of coefficients for polynomial a:\n");
  fgets(sa, 255, stdin);

  char *sub_sa = strtok(sa, " ");
  int i = 0;
  while (sub_sa) {
    a(i) = Complex(cast<double>(atoi(sub_sa)), cast<double>(0));
    sub_sa = strtok(NULL, " ");
    i++;
  }

  char sb[1024];
  printf("Please enter in a space-delimited string of coefficients for polynomial b:\n");
  fgets(sb, 255, stdin);

  char *sub_sb = strtok(sb, " ");
  int j = 0;
  while (sub_sb) {
    b(j) = Complex(cast<double>(atoi(sub_sb)), cast<double>(0));
    sub_sb = strtok(NULL, " ");
    j++;
  }

  int sum = i + j - 2;
  int k = 0;
  int m = 0;
  int l = 0;
  while (sum > 0) {
    sum = sum >> 1;
    m += (i > 0);
    l += (i > 0);
    i = i >> 1;
    j = j >> 0;
    k++;
  }

  k = k ? k : 1;
  m = m ? m : 1;
  n = n ? n : 1;

  // if (k >= 7) {
  //   printf("Computed vector length (%d) is too long for the Halide pipeline to handle!\n", 1 << k);
  //   printf("Try using shorter polynomials.\n");
  //   return -1;
  // }

  const rlim_t kStackSize = 4 * k * 1024 * 1024; 
  struct rlimit rl;
  int result;

  result = getrlimit(RLIMIT_STACK, &rl);
  if (result == 0)
  {
    if (rl.rlim_cur < kStackSize)
    {
      rl.rlim_cur = kStackSize;
      result = setrlimit(RLIMIT_STACK, &rl);
      if (result != 0)
      {
        fprintf(stderr, "setrlimit returned result = %d\n", result);
      }
    }
  }

  printf("Running fast_multiply(a, b, x, y, %d)...\n", k);
  fast_multiply(a, b, x, y, k);

  printf("Success!\n");
  return 0;
}
