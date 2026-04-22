# Buy a plot

execute unless score @s cb_balance matches 5000.. run tellraw @s {"text":"[CityBuild] ❌ Insufficient funds! Need $5000","color":"red"}

execute if score @s cb_balance matches 5000.. run scoreboard players remove @s cb_balance 5000
execute if score @s cb_balance matches 5000.. run scoreboard players add @s cb_plots 1
execute if score @s cb_balance matches 5000.. run tellraw @s {"text":"[CityBuild] ✓ Plot purchased!","color":"green"}
