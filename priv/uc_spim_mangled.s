.text
.set reorder # We are not using delayed branching
.globl main
main: # Tail call the actual main function
        j $$MANGLED_RTL_SYMBOL_main$$

# void putint(int val);
.globl $$MANGLED_RTL_SYMBOL_putint$$
$$MANGLED_RTL_SYMBOL_putint$$:
	li $v0, 1
	syscall
	jr $ra

# void putstring(char str[]);
.globl $$MANGLED_RTL_SYMBOL_putstring$$
$$MANGLED_RTL_SYMBOL_putstring$$:
	li $v0, 4
	syscall
	jr $ra

# int getint(void);
.globl $$MANGLED_RTL_SYMBOL_getint$$
$$MANGLED_RTL_SYMBOL_getint$$:
	li $v0, 5
	syscall
	jr $ra

# void getstring(char buffer[]);
.globl $$MANGLED_RTL_SYMBOL_getstring$$
$$MANGLED_RTL_SYMBOL_getstring$$:
	li $a1, 0x7fffffff
	li $v0, 8
	syscall
	jr $ra
