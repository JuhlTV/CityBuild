# Reset all data (OP ONLY)
# Only OPs can run this command

scoreboard players set @a cb_balance 10000
scoreboard players set @a cb_plots 0

tellraw @a {"text":"[CityBuild] ","color":"red","extra":[{"text":"⚠ Server admin reset all data!","color":"red"}]}
tellraw @a {"text":"All players now have: $10000 and 0 plots","color":"yellow"}
