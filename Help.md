#brief description of repiola language

# Introduction #

Repiola is a simple virtual machine, interpreter and frontends for desktop and mobile that allows to make programs that draw on a surface.

The machine contains 8 general purpose 32 bit registers named from [r0](https://code.google.com/p/repiola/source/detail?r=0) to [r7](https://code.google.com/p/repiola/source/detail?r=7).
> The [r0](https://code.google.com/p/repiola/source/detail?r=0) and [r1](https://code.google.com/p/repiola/source/detail?r=1) registers have a special purpose, they are used to determine the location where a pixel will be painted when the put instruction is executed.

since the machine executes opcodes, a language needs to be created and an interpreter/compiler to translate that language into opcodes.

The default language has a syntax very similar to ASM 80x86 and is designed to be easy to type on phones, which are the main purpose of the project.

The instructions are the following

## Arithmetic instructions ##

  * set: sets the value of a register
    * `set r1, 10`
    * `set r2, r1 `
  * add: adds a value to the value of a register, store the result on the register
    * `add r1, 10 # r1 = r1 + 10`
    * `add r2, r1 # r2 = r2 + r1`
  * sub: substract a value to the value of a register, store the result on the register
    * `sub r1, 10 # r1 = r1 - 10`
    * `sub r2, r1 # r2 = r2 - r1`
  * mul: multiply a value with the value of a register, store the result on the register
    * ``mul r1, 10 # r1 = r1 `*` 10``
    * ``mul r2, r1 # r2 = r2 `*` r1``
  * div: divide a value with the value of a register, store the result on the register
    * `div r1, 10 # r1 = r1 / 10`
    * `div r2, r1 # r2 = r2 / r1`
  * mod: calculate the modulo of the division, store the modulo on the register
    * `mod r1, 2 # r1 = r1 % 2`
    * `mod r1, r2 # r1 = r1 % r2`

## Binary instructions ##

  * and: perform the and operation between the register and a value, store the result on the register
    * `and r1, 8 # r1 = r1 & 8`
    * `and r1, r2 # r1 = r1 & r2`
  * or: perform the or operation between the register and a value, store the result on the register
    * `or r1, 8 # r1 = r1 | 8`
    * `or r1, r2 # r1 = r1 | r2`
  * xor: perform the xor operation between the register and a value, store the result on the register
    * `xor r1, 8 # r1 = r1 ^ 8`
    * `xor r1, r2 # r1 = r1 ^ r2`
  * not: negate the binary value of a register, store it on the register
    * `not r1 # r1 = ~r1`

## Drawing instructions ##

  * put: put a pixel of the color defined by the value in the position defined by [r0](https://code.google.com/p/repiola/source/detail?r=0) and [r1](https://code.google.com/p/repiola/source/detail?r=1) (x=[r0](https://code.google.com/p/repiola/source/detail?r=0), y=[r1](https://code.google.com/p/repiola/source/detail?r=1))
    * `put r2 # draws a pixel of the color stored in r2 in the position defined by (r0, r1)`
    * `put 4 # draws a pixel of the color 4 in the position defined by (r0, r1)`
  * get: set the value of the specified register to the value of the pixel stored in ([r0](https://code.google.com/p/repiola/source/detail?r=0), [r1](https://code.google.com/p/repiola/source/detail?r=1))
    * `get r2 # store the color of the pixel in (r0, r1) in r2`
  * clr: clear the screen with a given color
    * `clr 4 # clear the screen with the RGB color 4`
    * `clr r2 # clear the screen with the RGB color stored in register 2`

## Jump instructions ##

  * ":": (colon) define a label at that point, labels allow to jump to that location with the jump instructions
    * `: foo # define a foo label`
  * jmp foo: inconditionally jump to foo, that means, when the instruction is reached execution continues where foo is defined
  * jmp r#: jump to the address stored in r#
    * `jmp r2 # jump to the address stored in r2`
  * eq: jump to a label if two values are equal
    * `eq r1 10 foo # jump to foo if r1 is equal to 10`
    * `eq r1 r2 foo # jump to foo if r1 is equal to r2`
  * ne: jump to a label if two values are not equal
    * `ne r1 10 foo # jump to foo if r1 is not equal to 10`
    * `ne r1 r2 foo # jump to foo if r1 is not equal to r2`
  * gt: jump to a label if the first value is greater than the second value
    * `gt r1 10 foo # jump to foo if r1 > 10`
    * `gt r1 r2 foo # jump to foo if r1 > r2`
  * ge: jump to a label if the first value is greater  or equal than the second value
    * `ge r1 10 foo # jump to foo if r1 >= 10`
    * `ge r1 r2 foo # jump to foo if r1 >= r2`
  * lt: jump to a label if the first value is lower than the second value
    * `lt r1 10 foo # jump to foo if r1 < 10`
    * `lt r1 r2 foo # jump to foo if r1 < r2`
  * le: jump to a label if the first value is lower or equal than the second value
    * `le r1 10 foo # jump to foo if r1 <= 10`
    * `le r1 r2 foo # jump to foo if r1 <= r2`

## Stack instructions ##

  * push: push a value to the stack
    * `push 4 # push the value 4 to the stack`
    * `push r2 # push the value stored in the register 2 to the stack`
  * pop: pop the value stored at the top of the stack and store it in a register
    * `pop r2 # pop the value stored at the top of the stack and store it in the register 2`

## Function instructions ##

instructions to simulate functions

  * call: push the instruction pointer in the stack and jmp to the specified label
    * `call draw-line # push the instruction pointer in the stack and jump to the draw-line label`
  * ret: pop the instruction pointer from the stack and jump to it
    * `ret`


## Special instructions ##

  * rnd: generates a random number and store it on a register
    * `rnd r1 # generate a random number and store it on r1`
  * #: at the start of the line means that it's a comment, the rest of the line is not interpreted
  * .: (dot) means nooperation, when read the machine does nothing for a cycle

ExamplePage