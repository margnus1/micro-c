.set reorder
.data
.align 4
.globl x
x:
    .space 4
.align 4
.globl y
y:
    .space 1
.text
.globl main
main:
    addiu   $sp, $sp, -44
    sw      $ra, 40($sp)
    sw      $fp, 36($sp)
    move    $fp, $sp
    li      $t0, 42
    sw      $t0, 20($fp)
    la      $t0, x
    sw      $t0, 16($fp)
    lw      $t0, 20($fp)
    lw      $t1, 16($fp)
    sw      $t0, 0($t1)
    li      $t0, 43
    sw      $t0, 12($fp)
    la      $t0, y
    sw      $t0, 8($fp)
    lw      $t0, 12($fp)
    lw      $t1, 8($fp)
    sb      $t0, 0($t1)
    li      $t0, 65
    sw      $t0, 4($fp)
    lw      $t0, 4($fp)
    move    $t0, $t0
    sw      $t0, 28($fp)
    li      $t0, 10
    sw      $t0, 0($fp)
    lw      $t0, 0($fp)
    move    $t0, $t0
    sb      $t0, 27($fp)
main.exit:
    lw      $ra, 40($fp)
    lw      $v0, 32($fp)
    lw      $fp, 36($sp)
    addiu   $sp, $sp, 44
    jr      $ra
