int main(void);

int fib(int a[], int b[]) {
  int i;
  int tmp[];
  i = 0;
  while (a[i] > b[i]) {
    tmp = a;
    a = b;
    b = tmp;
    i = i + 1;
  }
  return a[i];
}

int main(void) {
  int foos[3];
  int bars[3];
  return fib(foos, bars);
}
