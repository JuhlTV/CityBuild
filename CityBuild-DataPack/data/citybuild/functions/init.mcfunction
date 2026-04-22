# CityBuild Data Pack - Initialization
# Run once on server startup via: /function citybuild:init

# Create scoreboards for tracking (errors ignored if already exist)
scoreboard objectives add cb_balance dummy "CityBuild Balance"
scoreboard objectives add cb_plots dummy "Player Plots"
scoreboard objectives add cb_admin dummy "Admin Stats"

# Initialize default balance for all players (10000 starting money)
execute as @a unless score @s cb_balance matches -2147483648..2147483647 run scoreboard players set @s cb_balance 10000

# Load main function
function citybuild:load
