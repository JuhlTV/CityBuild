# Sell a plot for 4000

execute unless score @s cb_plots matches 1.. run tellraw @s {"text":"[CityBuild] ","color":"red","extra":[{"text":"You don't own any plots!","color":"red"}]}

execute if score @s cb_plots matches 1.. run scoreboard players add @s cb_balance 4000
execute if score @s cb_plots matches 1.. run scoreboard players remove @s cb_plots 1
execute if score @s cb_plots matches 1.. run tellraw @s {"text":"[CityBuild] ","color":"green","extra":[{"text":"✓ Plot sold for $4000!","color":"green"}]}
