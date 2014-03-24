.set reorder
.text
.globl f
f:
    addiu   $sp, $sp, -16
    sw      $ra, 12($sp)
    sw      $fp, 8($sp)
    move    $fp, $sp
    sw      $a0, 16($fp)
    sw      $a1, 20($fp)
    lw      $t0, 16($fp)
    lw      $t1, 20($fp)
    addu    $t0, $t0, $t1
    sw      $t0, 0($fp)
    lw      $t0, 0($fp)
    move    $t0, $t0
    sw      $t0, 4($fp)
    j       f.exit
f.exit:
    lw      $ra, 12($fp)
    lw      $v0, 4($fp)
    lw      $fp, 8($sp)
    addiu   $sp, $sp, 16
    jr      $ra
.globl main
main:
    addiu   $sp, $sp, -24
    sw      $ra, 20($sp)
    sw      $fp, 16($sp)
    move    $fp, $sp
    li      $t0, 2
    sw      $t0, 8($fp)
    li      $t0, 3
    sw      $t0, 4($fp)
    addiu   $sp, $sp, -8
    lw      $a0, 8($fp)
    lw      $a1, 4($fp)
    jal     f
    addiu   $sp, $sp, 8
    sw      $v0, 0($fp)
main.exit:
    lw      $ra, 20($fp)
    lw      $v0, 12($fp)
    lw      $fp, 16($sp)
    addiu   $sp, $sp, 24
    jr      $ra
