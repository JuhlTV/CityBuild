# CityBuild Init

scoreboard objectives add cb_balance dummy "CityBuild Balance"
scoreboard objectives add cb_plots dummy "Player Plots"
scoreboard objectives add cb_admin dummy "Admin Stats"

execute as @a unless score @s cb_balance matches -2147483648..2147483647 run scoreboard players set @s cb_balance 10000

function citybuild:load
