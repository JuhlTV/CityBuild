# Add money (Usage: /function citybuild:economy/add {amount:100})

scoreboard players add @s cb_balance 100
tellraw @s {"text":"[CityBuild] ","color":"green","extra":[{"text":"+ $100 Added!","color":"green"}]}
