.text
.set reorder # We are not using delayed branching

# Sample function:
# void main(void) {
# 	getstring(main_buffer);
#	putstring(main_buffer);
# }
.globl main
main:
	addiu	$sp, $sp, -16 # We allocate stack slots 12($sp), 8($sp), 4($sp) and
	     	              # 0($sp)
	sw	$fp, 12($sp)
	sw	$ra, 8($sp)
	move 	$fp, $sp
	
	# We only need 4 bytes of stack space for the call, but since the
	# stack pointer must be kept 8-byte aligned by convention, we allocate
	# 8 bytes for the function call
	addiu	$sp, $sp, -8
	la	$a0, main_buffer
	jal	getstring
	addiu	$sp, $sp, 8
	
	addiu	$sp, $sp, -8
	la	$a0, main_buffer
	jal	putstring
	addiu	$sp, $sp, 8
	
	# Load saved registers, pop our stack frame and return
	lw	$fp, 12($sp)
	lw	$ra, 8($sp)
	addiu	$sp, $sp, 16
	jr	$ra

# char main_buffer[256];
.data
main_buffer: .space 256
