.text
.set reorder # We are not using delayed branching

# Sample function:
# void main(void) {
# 	getstring(main_buffer);
#	putstring(main_buffer);
# }
.globl main
main:
	addiu	$sp, $sp, -16
	sw	$fp, 16($sp)
	sw	$ra, 12($sp)
	move 	$fp, $sp
	
	# We only need 4 bytes of stack space for the call, but since the
	# stack pointer must be kept 8-byte aligned by convention, we allocate
	# 8 bytes for the function call
	addiu	$sp, $sp, -8
	la	$a0, main_buffer
	jal	getstring
	# # Store return value in a stack temporary
	# sw	$v0, -8($fp)
	addiu	$sp, $sp, 8
	
	addiu	$sp, $sp, -8
	la	$a0, main_buffer
	jal	putstring
	addiu	$sp, $sp, 8
	
	# Load saved registers, pop our stack frame and return
	lw	$fp, 16($sp)
	lw	$ra, 12($sp)
	addiu	$sp, $sp, 16
	jr	$ra

# char main_buffer[256];
.data
main_buffer: .space 256