# Reset all data (OP only)

execute unless score @s cb_admin matches 1 run tell @s "You don't have permission!"

scoreboard players set @a cb_balance 10000
scoreboard players set @a cb_plots 0

tellraw @a {"text":"[CityBuild] ","color":"red","extra":[{"text":"⚠ All data has been reset!","color":"red"}]}
