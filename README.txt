Justin Ard
Alex Thomas
cs 447
Project 2 Alpha

PlanetDefense in a game in which the user pilots a spaceship in
three dimensions.  The current alpha implementation renders a game
world which includes your home planet, and the user's spaceship.

The spaceship currently travels forward through space while the user
clicks and holds the left mouse button, and rotates in the x-z plane
while the user presses and holds the 'a' or 'd' key.

Rotation is limited to x-z plane at this point due to problems with
rendering the modeling rotations in the correct order. The spaceship's
orientation axes are correctly rotated, however I am having some difficulty
applying the rotations when drawing the ship. It appears that the rotation
matrix I am using compiles the rotations in the reverse order, therefore the
ship drawn to the screen does not rotate about the correct axes.

We have also successfully locked the camera perspective to the position and
orientation of the user's ship, including when rotation is allowed in all
three dimensions.  Therefore, at this point we could implement a first
person perspective for rotation with 3 degrees of freedom.

However, I believe I am close to debugging the rotation problem, and am confident
we can implement the full set of navigational controls, and the rest of the
functionality listed in our low bar goals. Over the break, I further intend 
to complete the collision detection functionality between game objects. I feel 
this still has the potential to result in an enjoyable game.

It is still early in the process of implementing the collision detection techincal
showpiece, however I plan to make significant process on this task in the next week.
In the next update I should have a much better idea of the feasability of implementing
these features.


Navigational Controls:
Left Mouse Button: thrust forward
Release left Mouse Button: stop
A: Rotate (yaw) left
D: Rotate (yaw) right