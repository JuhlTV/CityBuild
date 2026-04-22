# Check player balance

tellraw @s {"text":"[CityBuild] Your balance: $","color":"gold","extra":[
  {"score":{"name":"@s","objective":"cb_balance"},"color":"green"}
]}
