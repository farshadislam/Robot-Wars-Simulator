# CPSC233W24A3 - Robot Wars Map Editor
Author: Farshad Islam
Tutorial: 07
Date: Thursday, April 4th, 2024
-----------------------------------------
This program runs a visual representation of any Battle
class objects while also allowing users to modify them to their
liking. Battles can be saved in the form of .txt files within
the same 'src' folder that holds all the necessary components of the program.
#
### How to run my program:
Open IDE and run /java/rw/app/Main.java. 
Everything else should be functional from there.

### Saving/Loading battles:

Save function: By locating the saved-battle.txt file in 'src',
users can save battles to the directory. Users are instructed
upon first running the code that they are able to do this.

Save as function: Users can create a whole new .txt file
holding information regarding their battle. Selecting this
option opens their file explorer and makes them name a file
to save to a particular location on the OS.

Load function: Any previous battles that have been saved onto
the OS can be accessed via this feature. Users will be able to
view the newly loaded battle in the GUI immediately after
successfully loading it.

All these features have associated success/failure messages
that appear in the bottom left of the GUI window whenever 
the user attempts to make use of any of these functions.

### Draw a new grid

Users can create a whole new GridPane by filling the relevant fields under "Battle".
Invalid integer inputs for the row and column sizes will produce an error message
at the bottom left of the screen in bright red text. Users can only create grids with row
and column size 1 or higher.

### Adding robots

Users have the option of adding either a PredaCon or 
Maximal robot to the grid after properly initializing them.
If they enter invalid types into the requested fields then an error 
message will appear in the bottom left in bright red text.

Placing PredaCon: Users must select the radio button for PredaCon, then fill the fields
- Symbol
- Name
- Health
- WeaponType (by selecting a value from the ComboBox)

Placing Maximal: Users must select the Maximal radio button before writing its
- Symbol
- Name
- Health
- Attack
- Armour

For either robot, once these fields are filled with valid fields, the user can
click any non-wall space on the GridPane to add it. This deselects the radio button
and will also prevent them from placing another robot with the same symbol.

### Ending the program
Users can end the program by going File -> Quit at the top left. This closes the window
as well as exiting the program out of the console.

### Viewing details about the program
By clicking Help -> About in the top left, users can read about the program author
and learn about the utility of the program in question.






