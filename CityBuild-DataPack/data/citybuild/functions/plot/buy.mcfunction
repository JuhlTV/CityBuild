# Buy a plot for 5000

execute unless score @s cb_balance matches 5000.. run tellraw @s {"text":"[CityBuild] ","color":"red","extra":[{"text":"Insufficient funds! (Need $5000, Have $","color":"red"},{"score":{"name":"@s","objective":"cb_balance"},"color":"yellow"},{"text":") ","color":"red"}]}

execute if score @s cb_balance matches 5000.. run scoreboard players remove @s cb_balance 5000
execute if score @s cb_balance matches 5000.. run scoreboard players add @s cb_plots 1
execute if score @s cb_balance matches 5000.. run tellraw @s {"text":"[CityBuild] ","color":"green","extra":[{"text":"✓ Plot purchased! ID: plot_","color":"green"},{"score":{"name":"@s","objective":"cb_plots"},"color":"aqua"}]}
