/* Let's try all I/O operations. The program waits until you have
   answered the questions. */

void putint(int x);
void putstring(char s[]);
int getint(void);
int getstring(char s[]);


int main(void) {
  char nameq[];
  char ageq[];
  char youare[];
  char cr[];

  char name[80];
  int age;

  nameq = "Your name? ";
  ageq = "Your age ";
  youare = "You are: ";
  cr = "\n";

  putstring(nameq);
  getstring(name);

  putstring(ageq);
  age = getint();

  putstring(youare);
  putstring(name);
  putstring(cr);

  putstring(youare);
  putint(age);
  putstring(cr);


}
