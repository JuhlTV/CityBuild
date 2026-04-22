# Show player plot info

tellraw @s {"text":"[CityBuild] ","color":"gold","extra":[
  {"text":"Your Plot Info:\n","bold":true},
  {"text":"  Plots Owned: ","color":"yellow"},
  {"score":{"name":"@s","objective":"cb_plots"},"color":"aqua"},
  {"text":"\n  Balance: $","color":"yellow"},
  {"score":{"name":"@s","objective":"cb_balance"},"color":"aqua"}
]}
