int a[2];
int foo(int x[]) {}

int foo(int x[]) {
    x[0] = 1;
}

int main(void) {
  foo(a);
}
