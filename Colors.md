#explanation on how colors work
# Colors #

the colors on the screen are 32 bits, but to make a color be inside an opcode (which is 32 bits wide) we need to make it 16 bits.
> To make this the colors are 555 RGB that means 5 bits for red, 5 bits for green, 5 bits for blue, the most significant bit isn't used.

imagine you have 16 bits with ones you will have something like:

111111111111111

or in color representation

xrrrrrgggggfffff

x: not used
r: red bit
g: green bit
b: blue bit

the transformation to make it 32 bits is to grab each part, shift it 3 bits to the right (to make it 8 bits wide) and add ones to the 3 less significant bits if the color component isn't 0 to make posible to draw the white color (which is 111111111111)

if you have white in 555 RGB you will have

111111111111111
rrrrrgggggbbbbb

take each part and shift it 3 bits to the right

111110001111100011111000

if the color is not 0 fill the 3 less significan bytes with 1's

111111111111111111111111

and you have white