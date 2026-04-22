# Remove money

scoreboard players remove @s cb_balance 100
tellraw @s {"text":"[CityBuild] ","color":"red","extra":[{"text":"- $100 Removed!","color":"red"}]}
