/* Let's try all I/O operations. The program waits until you have
   answered the questions. */

void putint(int x);
void putstring(char s[]);
int getint(void);
int getstring(char s[]);


int main(void) {
  char name[80];
  int age;

  putstring("Your name? ");
  getstring(name);

  putstring("Your age ");
  age = getint();

  putstring("You are: ");
  putstring(name);
  putstring(".\n");

  putstring("You are: ");
  putint(age);
  putstring(" years old.\n");
}
