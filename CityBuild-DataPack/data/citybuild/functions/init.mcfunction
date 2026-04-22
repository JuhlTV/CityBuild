# CityBuild Data Pack - Initialization

# Create scoreboards for tracking
scoreboard objectives add cb_balance dummy "CityBuild Balance"
scoreboard objectives add cb_plots dummy "Player Plots"
scoreboard objectives add cb_admin dummy "Admin Stats"

# Initialize default values
execute unless score @s cb_balance = @s cb_balance run scoreboard players set @a cb_balance 10000

# Load main function
function citybuild:load

tellraw @a {"text":"[CityBuild] System initialized! Use /function citybuild:help for commands","color":"green"}
