# Complete Command Reference - All 9 Economic Systems

## Core Plot Commands

### Buy Plot
```
/citybuild buy [plotId]
```
- Purchase plot from market
- Cost: $5,000 (configurable)
- Ownership transfers immediately
- Requires sufficient balance

**Example**: `/citybuild buy 42` → Spend $5,000, gain Plot #42

---

### Sell Plot
```
/citybuild sell [plotId] [price]
```
- List plot on market at specified price
- Price must be > $1,000
- Plot remains yours until buyer purchases
- Can cancel listing anytime

**Example**: `/citybuild sell 5 8500` → List Plot #5 for $8,500

---

### Market Listing
```
/citybuild market
/citybuild market [page]
```
- View all plots for sale
- Sorted by price (low→high)
- Shows owner, price, size, location
- Paginated (10 per page)

**Example**: `/citybuild market 1` → Show page 1 of listings

---

### Plot Info
```
/plot info [plotId]
/plot info
```
- View plot details (size, owner, members, biome)
- Without ID: Shows your current plot
- Shows coordinates, tax status, rental status

**Example**: `/plot info 5` → See Plot #5 details

---

### Plot Teleport
```
/plot spawn [plotId]
/plot home
```
- Teleport to plot spawn point
- Home goes to your first plot
- Both plots remain loaded for 5 minutes

**Example**: `/plot home` → Teleport to your main plot

---

## Tax System Commands

### Check Tax Status
```
/tax status
/tax status [playerName]
```
- View your tax balance, next collection date
- Admin: Check player's tax debt

**Example**: `/tax status` → "You owe $500 in taxes (due in 5 days)"

---

### Pay Tax Debt
```
/tax pay [amount]
/tax pay all
```
- Reduce outstanding tax debt
- `all` pays entire debt if possible

**Example**: `/tax pay 1000` → Pay $1,000 toward taxes

---

### Admin: Seize Unpaid Taxes
```
/tax seize [playerName]
/tax seize [playerName] force
```
- Seize plots for unpaid taxes
- Force: Remove without warning
- Logs seizure action

**Example**: `/tax seize PlayerName force` → Take all their plots

---

### Admin: Tax Statistics
```
/economy stats
```
- View total tax collected
- Players in debt
- Outstanding tax amount

---

## Plot Rental System Commands

### List Plot for Rent
```
/rental list [plotId] [dailyRent] [durationDays]
```
- Offer plot for lease
- Daily rent: Amount charged per day
- Duration: Maximum lease length
- Plot remains yours while renting

**Example**: `/rental list 5 200 30` → Rent Plot #5 for $200/day (max 30 days)

---

### Accept Rental Lease
```
/rental accept [plotId]
```
- Move in as tenant on listed plot
- Pay full month upfront (dailyRent × durationDays)
- Gain building rights
- Automatic daily rent collection

**Example**: `/rental accept 5` → Move into Plot #5 (pay $6,000 for 30 days)

---

### View Leases
```
/rental list
/rental list [landlord|tenant]
```
- Show all active leases
- Filter: landlord (your leases as owner), tenant (your leases as renter)

**Example**: `/rental list landlord` → See all plots you're renting out

---

### Terminate Lease
```
/rental end [plotId]
```
- Break lease early
- Pro-rata refund to tenant
- Both landlord and tenant can terminate

**Example**: `/rental end 5` → End lease on Plot #5

---

### Rental Income
```
/rental income
```
- View total rental income earned
- Shows per-plot breakdown
- Monthly projection

---

## Business System Commands

### Create Business
```
/business create [businessName] [industry]
```
- Industries: RETAIL, MANUFACTURING, SERVICES, TECHNOLOGY, AGRICULTURE
- Starting capital: $10,000 (from player's balance)
- You become CEO with full permissions

**Example**: `/business create MyShop RETAIL` → Create retail business

---

### Hire Employee
```
/business hire [businessId] [playerName] [dailySalary]
```
- Add employee to payroll
- Daily salary charged each day
- Employee doesn't need to accept (forced employment)
- Max 50 employees per company

**Example**: `/business hire 1 John 100` → Hire John at $100/day

---

### Pay Payroll
```
/business pay [businessId]
/business payall
```
- Pay all employees for the day
- Charges from company balance
- Auto-failure if insufficient funds
- Creates debt if over-extended

**Example**: `/business pay 1` → Pay all employees of Company #1

---

### Withdraw Profits
```
/business withdraw [businessId] [amount]
```
- Take profit as owner
- Transfers to your personal balance
- Can only withdraw if balance > amount

**Example**: `/business withdraw 1 5000` → Extract $5,000 profit

---

### Company Info
```
/business info [businessId]
/business list [playerName]
```
- View company stats (balance, employees, profit)
- List: See all companies owned by player

**Example**: `/business info 1` → Show Company #1 details

---

### Admin: Award Profit
```
/business profit [businessId] [amount]
```
- Manually add revenue to company
- Simulates sales/production
- Can be used for events or fixes

**Example**: `/business profit 1 10000` → Add $10k revenue to Company #1

---

## Stock System Commands

### Create IPO (Admin Only)
```
/stock ipo [projectName] [totalShares] [pricePerShare]
```
- Launch stock offering
- Issuer: You (person issuing)
- Total value: totalShares × pricePerShare
- All shares initially go to issuer's portfolio

**Example**: `/stock ipo BigMall 5000 50` → Issue 5,000 shares @ $50 = $250k valuation

---

### Buy Shares
```
/stock buy [stockId] [numShares]
```
- Purchase shares from issuer or market
- Cost: numShares × pricePerShare
- Adds to your portfolio
- Locks capital temporarily

**Example**: `/stock buy 1 100` → Buy 100 shares of Stock #1

---

### Sell Shares
```
/stock sell [stockId] [numShares]
```
- Liquidate shares
- Receives: numShares × pricePerShare
- Shares go back to issuer's pool
- Frees up capital

**Example**: `/stock sell 1 50` → Sell 50 shares of Stock #1

---

### View Portfolio
```
/stock portfolio
/stock portfolio [playerName]
```
- Your holdings and total value
- Shows per-stock breakdown
- Admin: Check other players

**Example**: `/stock portfolio` → See your stocks worth

---

### Market Statistics
```
/stock market
```
- View all stocks
- Price, market cap, volume
- Top gainers/losers (future)

---

### Admin: Dividend Payout
```
/stock dividend [stockId] [totalAmount]
```
- Distribute profits to all shareholders
- Automatic per-share calculation
- Adds to player balances

**Example**: `/stock dividend 1 50000` → Distribute $50k to Stock #1 shareholders

---

## Auction System Commands

### Create Auction
```
/auction create [itemName] [startingBid] [durationHours] [description]
```
- List item for auction
- Duration: 1-168 hours (7 days max)
- Starting bid: Initial minimum bid
- Description: Optional item details

**Example**: `/auction create "Diamond Pickaxe" 500 48 "Unbreakable, Sharp V"` → Auction pickaxe for 2 days

---

### Place Bid
```
/auction bid [auctionId] [bidAmount]
```
- Bid on active auction
- Must exceed current bid by minimum $100
- Your bid is held in escrow
- Previous bidder is refunded

**Example**: `/auction bid 5 750` → Bid $750 on Auction #5

---

### View Auctions
```
/auction list
/auction list [page]
/auction search [keyword]
```
- Show all active auctions
- Sorted by time remaining
- Search: Find auctions by name

**Example**: `/auction list 1` → Show page 1 of auctions

---

### Auction Info
```
/auction info [auctionId]
```
- View auction details
- Current bid, bidder, time remaining
- Full item description

**Example**: `/auction info 5` → See Auction #5 status

---

### Admin: End Auction
```
/auction end [auctionId]
/auction end all
```
- Manually conclude auction
- Transfers money to seller
- Logs transaction
- All: End all expired auctions

**Example**: `/auction end 5` → Conclude Auction #5

---

## Banking System Commands

### Deposit to Savings
```
/bank deposit [amount]
```
- Move cash from hand to savings account
- Earns 5% annual interest (0.013% daily)
- Locked until withdrawal

**Example**: `/bank deposit 5000` → Save $5,000

---

### Withdraw from Savings
```
/bank withdraw [amount]
/bank withdraw all
```
- Retrieve saved money
- Fails if insufficient balance
- All: Withdraw entire savings

**Example**: `/bank withdraw all` → Get all savings back

---

### Take Loan
```
/loan take [amount] [durationDays]
```
- Borrow money with interest
- Interest: 10% (due with principal)
- Duration: 1-365 days
- Max loan: $100,000

**Example**: `/loan take 10000 30` → Borrow $10k for 30 days (repay $11k)

---

### View Loans
```
/loan status
/loan list
```
- Your active loans
- Shows amount owed, days remaining
- Interest accrued so far

**Example**: `/loan status` → See all your debts

---

### Repay Loan
```
/loan repay [loanIndex] [amount]
/loan repay all
```
- Pay down loan
- Partial payments allowed
- All: Pay all loans (if possible)

**Example**: `/loan repay 0 5000` → Pay $5k toward first loan

---

### Bank Savings Interest
```
/bank interest
```
- View interest earned today
- Compound daily
- Automatic deposits

---

### Admin: Apply Daily Interest
```
/economy interest apply
```
- Process daily interest
- Savings interest paid
- Loan interest charged
- Usually automated

---

## Insurance System Commands

### Buy Plot Insurance
```
/insurance plot buy [plotId]
```
- $500/month protection
- Covers plot damage/destruction
- Automatically renews (if funds available)
- Can be cancelled anytime

**Example**: `/insurance plot buy 5` → Insure Plot #5 for $500/mo

---

### Buy Business Insurance
```
/insurance business buy [businessId]
```
- $1,000/month protection
- Covers business fund loss
- Claim if company fails

**Example**: `/insurance business buy 1` → Insure Company #1

---

### Buy Stock Insurance
```
/insurance stock buy [stockId]
```
- 2% of stock value per month
- Covers portfolio crash (if mechanics enabled)
- Protection: Value floor at 80% of purchase price

**Example**: `/insurance stock buy 3` → Insure Stock #3 position

---

### File Claim
```
/insurance claim [policyId] [amount] [reason]
```
- Request insurance payout
- Amount: Loss amount claimed
- Reason: What happened
- Pays 80% of claim (copay: 20%)

**Example**: `/insurance claim p1 5000 "Plot flooded"` → Claim $5k, receive $4k

---

### View Policies
```
/insurance list
/insurance list [type]
```
- Your active policies
- Type: plot, business, stock
- Shows expiration dates

**Example**: `/insurance list plot` → See all your plot insurance

---

### Renew Insurance
```
/insurance renew [policyId]
```
- Extend coverage another month
- Charges next month's premium
- Auto-renewal failure = lapse

**Example**: `/insurance renew p1` → Renew Policy p1

---

### Admin: Approve Claim
```
/insurance approve [claimId]
```
- Manually approve pending claim
- Transfers payout to claimant
- Logs claim

---

## Quest System Commands

### Accept Quest
```
/quest accept [questId]
```
- Start working on quest
- Track progress automatically
- Can have multiple simultaneous quests

**Example**: `/quest accept 1` → Accept Quest #1

---

### View Available Quests
```
/quest list
/quest list [type]
```
- Show all active quests
- Type: BUILD, TRADE, WEALTH, BUSINESS
- Shows duration, reward, status

**Example**: `/quest list BUILD` → See all building quests

---

### Check Progress
```
/quest progress [questId]
/quest status
```
- See how close to completion
- Shows target and current count
- Percentage complete

**Example**: `/quest progress 1` → "Build Challenge: 47/100 blocks (47%)"

---

### Complete Quest
```
/quest complete [questId]
```
- Claim reward if requirements met
- Reward deposited immediately
- Quest removed from active list

**Example**: `/quest complete 1` → Claim $1,000 reward

---

### Quest History
```
/quest history
/quest history [playerName]
```
- Your completed quests
- Admin: Check other players

**Example**: `/quest history` → Show your quest completion log

---

### Leaderboard
```
/quest leaderboard
/quest leaderboard [type]
```
- Top quest completers
- Show rank, name, quests completed
- Type: Filter by quest type

**Example**: `/quest leaderboard` → Top 10 quest players

---

### Admin: Create Quest
```
/quest create [name] [objective] [reward] [durationDays] [type] [target]
```
- Define new quest
- Type: BUILD, TRADE, WEALTH, BUSINESS
- Target: Count threshold

**Example**: `/quest create "Daily Grind" "Build your empire" 1000 1 BUILD 100`

---

### Admin: Award Quest
```
/quest award [playerName] [amount]
```
- Manually give quest reward
- Bypasses normal completion
- For fixes/events

**Example**: `/quest award John 5000` → Give John $5k

---

## Economy-Wide Commands

### Economy Statistics
```
/economy stats
/economy stats full
```
- Total money in circulation
- Total players
- Tax revenue
- Average wealth per player
- Full: Detailed breakdown by system

**Example**: `/economy stats` → "Total circulation: $2.5M | Tax revenue: $50k/day"

---

### Personal Net Worth
```
/worth
/worth [playerName]
```
- Your total liquid + invested wealth
- Breakdown by system
- Admin: Check other players

**Example**: `/worth` → "Net worth: $150,000 (Cash: $20k, Plots: $80k, Stocks: $50k)"

---

### Economy Health
```
/economy health
```
- Inflation rate
- Wealth inequality (Gini coefficient)
- Recommendations for admin adjustments

---

### Admin: Money Supply Control
```
/economy inject [amount]
/economy remove [amount]
```
- Manually add/remove money
- For large balancing adjustments
- Logs transaction

**Example**: `/economy inject 100000` → Add $100k to total circulation

---

## Help & Documentation

### In-Game Help
```
/economy help
/economy help [system]
/command help [command]
```
- Quick reference
- System-specific guides
- Command syntax

**Example**: `/economy help auctions` → Show auction system guide

---

### Balance Check
```
/balance
/balance [playerName]
```
- Your current hand cash
- Does not include bank savings
- Admin: Check others

**Example**: `/balance` → "You have: $5,432.50"

---

### Transaction History
```
/transactions
/transactions [playerName] [limit]
```
- Last 10 transactions by default
- Shows source, destination, amount
- Admin: Check any player

**Example**: `/transactions 20` → Show last 20 of your transactions

---

## Command Summary Table

| System | Main Command | Subcommands | Frequency |
|--------|--------------|-------------|-----------|
| Plot | /citybuild | buy, sell, market, info | Daily |
| Tax | /tax | status, pay, seize | Daily (auto) |
| Rental | /rental | list, accept, end, income | Daily (auto) |
| Business | /business | create, hire, pay, withdraw, info | Daily/Weekly |
| Stock | /stock | ipo, buy, sell, portfolio, market | Weekly/Monthly |
| Auction | /auction | create, bid, list, info, end | Daily |
| Banking | /bank, /loan | deposit, withdraw, take, repay | Weekly |
| Insurance | /insurance | plot, business, stock, claim, list | Monthly (auto) |
| Quest | /quest | accept, list, progress, complete, leaderboard | Daily |
| Economy | /economy, /worth | stats, health, inject, remove | Admin only |

---

**Total Commands**: 50+ | **Permissions Required**: Most player-based, admins for enforcement | **Last Updated**: v4.3.0
