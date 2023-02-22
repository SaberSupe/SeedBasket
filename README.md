# SeedBasket
Spigot plugin that adds a seed basket that plants multiple crops at once\
Adds a seeb basket to the game which can be filled with seeds, then when farmland is left clicked, it plants multiple blocks in front of the player (default of 5 blocks)\
\
Usage:
Right-click with seed basket in your hand to open seed basket inventory, place the desired seeds inside./
Only 1 type of seed can be used at a time so you must take out the old type before putting new seeds in/
Usable seeds: Carrots, Potatoes, Wheat Seeds, Beetroot Seeds, Melon Seeds, Pumpkin Seeds\
\
Left-Clicking on farmland (tilled dirt) will plant up to the max amount specified in the config file (default 5) in a straight line in front of the player using the seeds stored in the seed basket\
\
Command:\
/seedbasket give {ign} {amount}\
amount will default to 1 if not specified\
\
Permissions:\
seedbasket.give - The ability to give seed baskets with the above command, defaults to op\
seedbasket.use - The ability to use a seed basket, defaults to everybody
