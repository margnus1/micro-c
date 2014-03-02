.text
.set reorder # We are not using delayed branching
# Global entry point
.global uc$global_entry
.ent uc$global_entry
uc$global_entry:
	jal main
	li $v0, 10
	syscall

# void putint(int val);
.globl putint
putint:
	li $v0, 1
	syscall
	jr $ra

# void putstring(char str[]);
.globl putstring
putstring:
	li $v0, 4
	syscall
	jr $ra

# int getint(void);
.globl getint
getint:
	li $v0, 5
	syscall
	jr $ra

# void getstring(char buffer[]);
.globl getstring
getstring:
	li $a1, 0x7fffffff
	li $v0, 8
	syscall
	jr $ra
