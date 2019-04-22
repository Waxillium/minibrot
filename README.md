# minibrot
Mininal mandelbrot and julia set explorer.

The window is non-resizable, unfortunately. This was much easier to implement, but fortunately the window 
will fit to your monitor size.

FUNCTIONS:
Zoom: To zoom in and out, use the mouse wheel. This will zoom into the center and automatically increase 
the amount of iterations to essentially keep the detail the same.

Drag: Use the left mouse click to drag around the window.

Left-alt: decreases brightness of the window.

Right-alt: increases brightness of the window.

1: Decreases the number of iterations.

2: Increases the number of iterations.

A: Changes the color scheme to a preset. Pressing A again will also turn it off.

S: Another preset color scheme. Pressing S again will turn it off.

I: Inverts the non black colors for the default themes. Press I again to turn it off.

Z and X: use Z and X to modify part of the coloring of the mandelbrot set. Z increases the modification and X decreases it.

C and V: use C and V to modify part of the coloring of the mandelbrot set. C increases the modification and V decreases it.

B and N: use B and N to modify part of the coloring of the mandelbrot set. B increases the modification and N decreases it.

Enter: resets the image to the starting bounds, resets the brightness and color changes. Turns off the A and S themes.

R: Changes the coloring method to use logarithmic and sinusoidal functions. Turn it off by pressing R again,
cannot be turned off with Enter as with the other themes.

T: Changes the coloring method to use logarithmic and sinusoidal functions with a different scheme. Turn it off by pressing T
again, cannot be turned off with Enter.

Tab: Changes from Mandelbrot to Julia mode and back.

Shift-mouse: Drag the mouse around while holding shift to change the value of the complex number that the julia set is calculated
from.

Same features from mandelbrot set apply to julia set.

Enter does reset the same settings it does for the Mandelbrot set, but doesn't change the julia set itself.

Escape: closes and exits the program.
