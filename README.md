# MineSweeper

MineSweeper consists of 4 distinct java classes and is a simplified reconstruction of the commonly known Minesweeper game. 

__MineSweeper Class:__

The main responsibliity of the __MineSweeper class__ is to define event handlers. This includes,
distinguishing between a right and left click and delegating these events to the Grid class,
which handles them accordingly. MineSweeper also displays and activates the New Game button
(swing component) and also displays, formats and updates the 
"Mines found: __/__" label.

__Grid Class:__

The __Grid class__ constructs a grid of GridCells. Grid class is in charge of placing random mines across
the grid as well as setting all the non-mine cells with a count of neighboring mines. This class
contains knowledge of the state of the grid such as how many mines are present, how many flags,
how many mines have been flagged, whether the game is still in session or whether player has
won or lost. The Grid constructor takes a int level (level of difficulty that player chooses)
and distributes this value throughout its methods to construct a larger, more challenging grid
with more randomly placed mines (if player chooses harder levels).

__GridCell Class:__

The __GridCell class__ stores and remembers information about an individual cell. This information
includes aspects such as whether the cell contains a mine, the number of neighbors that do contain
mines in a non-mine cell, whether the cell has been flagged, whether the cell has been revealed, or
exploded etc. The class also takes care of all the formatting (centering, positioning) required
with each possible component in a cell (mine, flag, neighbor count, white backdrop for neighbor count
and exploded backdrop for exploded cell).

__Timer Class:__


__@author Sabirah Shuaybi__

__@version 11/29/16__
