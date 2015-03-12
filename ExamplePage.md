#examples of repiola

# Examples #

Here some examples will be shown, if you have an example that makes something interesting send it to me, I will add it here.

## Draw 200 random pixels on random locations with random colors ##
```
# start of the program
: start

# give x a random value
rnd r0
# make the value between 0 and 200
mod r0 200
# give y a random value
rnd r1
# make the value between 0 and 200
mod r1 200
# get a random color
rnd r2
# put the pixel on screen
put r2
# increment the counter on 1
add r3 1
# if the counter is lower than 200 go to the begining
lt r3 200 start
# end of the program
```
## Draw an X with gradient colors ##
```
: begin
# put a pixel of color r2 on r0, r1
put r2
# add 1 to x
add r0 1
# add 1 to y
add r1 1
# add 1 to the color
add r2 1

# while x is lower than 200 go to begin
lt r0 200 begin

# right to left line

# set x to the right most pixel
set r0 199
# set y to the top most pixel
set r1 0
# set the color to 0
set r2 0

: another

# put the pixel
put r2
# decrement x
sub r0 1
# increment y
add r1 1
# increment the color
add r2 1

# while y is lower than 200 repeat
lt r1 200 another

# end!
```

## Draw random horizontal and vertical lines ##

```
jmp start

: line
eq r0 r2 vline
eq r1 r3 hline
ret

: vline
ge r1 r3 return
put r4
add r1 1
jmp vline

: hline
ge r0 r2 return
put r4
add r0 1
jmp hline

: return
ret

: start
set r5 0
: loop
rnd r0
mod r0 100
set r2 r0
add r2 100
rnd r1
mod r1 200
set r3 r1
rnd r4
call line
add r5 1
le r5 200 loop

set r5 0
: loop1
rnd r0
mod r0 200
set r2 r0
rnd r1
mod r1 100
set r3 r1
add r3 100
rnd r4
call line
add r5 1
le r5 200 loop1
```