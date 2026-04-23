# Economy Guide - Complete System Reference

## Overview

CityBuild Economy operates 9 integrated systems enabling players to generate income, invest capital, and build wealth through legitimate economic participation.

**Total System Revenue Drivers:**
- Plot Sales/Rentals
- Business Operations
- Stock Markets
- Insurance Claims
- Quest Rewards
- Auction Sales
- Loan Interest
- Tax Collection

---

## 1. Plot Market System

### Purpose
Primary real estate marketplace for player-to-player land trading.

### Core Mechanics
- **Buy**: `/citybuild buy [plotId]` - Purchase from market listing
- **Sell**: `/citybuild sell [plotId] [price]` - List plot on market
- **Market Info**: `/citybuild market` - View all listings sorted by price

### Income Model
```
Player A lists Plot #5 for $10,000
Player B purchases for $10,000
→ Player A receives $10,000
→ Plot ownership transfers to Player B
```

### Strategic Value
- First-time land acquisition
- Long-term real estate investment
- Speculation on location desirability
- Passive income from resale premiums

### Configuration
```yaml
economy:
  plot_buy_price: 5000      # Initial purchase cost
  plot_sell_price: 4000     # Resale baseline
```

---

## 2. Tax System

### Purpose
Government revenue collection through progressive taxation.

### Core Mechanics
- **Auto Tax**: Collected daily per owned plot
- **Tax Debt**: Accumulated if player cannot pay
- **Seizure**: Gov can seize plots for unpaid taxes
- **Pay Debt**: `/tax pay [amount]` - Reduce outstanding debt

### Income Model
```
Day 1: Player owns 3 plots
Tax: $500/plot × 3 = $1,500 charged
→ If balance < $1,500: Debt = $1,500
→ Next day: Debt grows if unpaid
```

### Strategic Value
- Forces players to actively generate income
- Prevents idle wealth accumulation
- Funds government operations (admin discretion)
- Encourages business diversification

### Configuration
```yaml
taxes:
  plot_tax_per_plot: 500
  tax_interval_days: 1
```

---

## 3. Plot Rental System

### Purpose
Generate passive income by leasing land to other players.

### Core Mechanics
- **List**: `/rental list [plotId] [dailyRent] [durationDays]`
- **Rent**: `/rental accept [plotId]` - Move in as tenant
- **Collect**: Automatic daily rent collection
- **Terminate**: `/rental end [plotId]` - Break lease early

### Income Model
```
Landlord lists Plot #10 for $200/day (30-day lease)
Tenant accepts: Pays $6,000 upfront
→ Daily collection: $200/day → Landlord
→ After 30 days: Lease expires
→ Landlord earned: $6,000 total
```

### Strategic Value
- High-volume passive income
- Tenant building rights while leasing
- Automatic daily collection
- Early termination with pro-rata refund

### Configuration
```yaml
rentals:
  daily_collection_enabled: true
  default_duration_days: 30
```

---

## 4. Business System

### Purpose
Player-operated companies with employee management and profit generation.

### Core Mechanics
- **Create**: `/business create [name] [RETAIL|MANUFACTURING|SERVICES|TECHNOLOGY|AGRICULTURE]`
- **Hire**: `/business hire [businessId] [playerName] [dailySalary]`
- **Payroll**: `/business pay [businessId]` - Pay all employees
- **Withdraw**: `/business withdraw [businessId] [amount]` - Extract profit
- **Info**: `/business info [businessId]` - View company stats

### Income Model
```
Create Company "TechStart" (Type: TECHNOLOGY)
Hire 5 employees at $100/day = $500 daily payroll
Generate $2,000 revenue daily (via admin event or trading)
Daily profit: $2,000 - $500 = $1,500
→ Owner withdraws $1,000, reinvests $500
```

### Strategic Value
- Multiple revenue streams per company
- Employee management gameplay
- Industry specialization bonuses (future)
- Profit scaling with growth

### Configuration
```yaml
business:
  initial_capital: 10000
  max_employees_per_company: 50
```

---

## 5. Stock System (IPO & Trading)

### Purpose
Investment market for high-growth capital ventures.

### Core Mechanics
- **IPO**: `/stock ipo [projectName] [totalShares] [pricePerShare]`
- **Buy**: `/stock buy [stockId] [sharesToBuy]` - Purchase from issuer
- **Sell**: `/stock sell [stockId] [sharesToSell]` - Liquidate position
- **Dividend**: Admin payouts to all shareholders
- **Portfolio**: `/stock portfolio` - View holdings

### Income Model
```
Admin creates Stock: "MegaMall" (10,000 shares @ $10 = $100k valuation)
Player A buys 1,000 shares for $10,000
→ Owns 10% of company

Quarter 1: Company declares $50,000 profit
→ Dividend payout: $5 per share = $5,000 to Player A

Player B wants to buy, offers $12/share
→ Player A sells 500 shares for $6,000
→ Profit: $1,000 (50% ROI in 90 days)
```

### Strategic Value
- Long-term wealth building
- Dividend income stream
- Trading speculation
- Company growth participation

### Configuration
```yaml
stocks:
  min_ipo_shares: 1000
  max_ipo_price: 10000
```

---

## 6. Auction System (NEW)

### Purpose
Item-based bidding marketplace with automatic sale mechanics.

### Core Mechanics
- **Create**: `/auction create [itemName] [startingBid] [durationHours] [description]`
- **Bid**: `/auction bid [auctionId] [bidAmount]` - Place/raise bid
- **List**: `/auction list` - View all active auctions
- **End**: Auto-concludes when timer expires, highest bidder wins

### Income Model
```
Player A auctions "Diamond Pickaxe" for opening bid $500
Player B bids $500
Player C bids $750
Player B bids $1,000
→ Timer expires at 48 hours
→ Player B wins for $1,000
→ Player A receives $1,000
→ Player B receives pickaxe in-game
```

### Strategic Value
- Immediate item liquidity
- Competitive price discovery
- Time-limited scarcity
- Refund mechanism (previous bids returned)

### Configuration
```yaml
auctions:
  min_starting_bid: 100
  max_auction_duration_hours: 168
```

---

## 7. Banking System (NEW)

### Purpose
Savings accounts, loans with interest, and credit management.

### Core Mechanics
- **Deposit**: `/bank deposit [amount]` - Move cash to savings
- **Withdraw**: `/bank withdraw [amount]` - Access saved funds
- **Loan**: `/bank loan [amount] [durationDays]` - Borrow with interest
- **Repay**: `/bank repay [loanIndex] [amount]` - Pay down debt
- **Interest**: Daily compound interest on savings & loan interest charged

### Income Model
```
Player A has $20,000 cash
→ Deposits $15,000 to savings account
→ Daily interest: 5% annual = 0.013% daily
→ Day 1: Interest = $2 earned

Player B borrows $10,000 for 30 days
→ Interest: 10% annual = $1,000 total due
→ Must repay $11,000 within 30 days
→ Default penalty: +20% = $2,200 total debt
```

### Strategic Value
- Interest-bearing savings
- Emergency liquidity via loans
- Debt mechanics for risk/reward gameplay
- Compound interest rewards patience

### Configuration
```yaml
banking:
  savings_interest_rate: 0.05
  loan_interest_rate: 0.10
  max_loan_amount: 100000
```

---

## 8. Insurance System (NEW)

### Purpose
Risk mitigation through policies and claims payouts.

### Core Mechanics
- **Plot Insurance**: `/insurance plot [plotId]` - $500/month protection
- **Business Insurance**: `/insurance business [businessId]` - $1,000/month
- **Stock Insurance**: `/insurance stock [stockId]` - 2% of value/month
- **Claim**: `/insurance claim [policyId] [amount] [reason]` - File claim
- **Renew**: `/insurance renew [policyId]` - Extend coverage

### Income Model
```
Player A buys plot insurance for $500/month
→ Owns plot worth $10,000
→ Catastrophe: Plot destroyed (if mechanics enabled)
→ Files claim for $9,500 damage
→ Insurance pays: $9,500 × 80% = $7,600 payout
→ Player A net loss: $2,400 instead of $9,500

Insurer revenue: $500 × 12 months = $6,000/year
→ After claim: $6,000 - $7,600 = Net -$1,600 (loss)
→ Incentivizes balanced underwriting
```

### Strategic Value
- Reduces risk of catastrophic loss
- Enables risky/high-reward projects
- Monthly subscription income for admin
- Claims processing as content

### Configuration
```yaml
insurance:
  plot_premium: 500
  business_premium: 1000
  stock_premium: 0.02
  claim_payout_ratio: 0.80
```

---

## 9. Quest System (NEW)

### Purpose
Time-limited tasks and challenges with monetary rewards.

### Core Mechanics
- **Accept**: `/quest accept [questId]` - Register for challenge
- **Progress**: Automatic tracking (building blocks, trades, wealth, etc.)
- **Complete**: `/quest complete [questId]` - Claim reward when done
- **Leaderboard**: `/quest leaderboard` - Top quest completers

### Quest Types
| Type | Objective | Example | Reward |
|------|-----------|---------|--------|
| BUILD | Place 100 blocks | "Daily Build Challenge" | $1,000 |
| TRADE | Complete 10 trades | "Trading Expert" | $2,500 |
| WEALTH | Accumulate $50k | "Millionaire Journey" | $5,000 |
| BUSINESS | $50k company profit | "CEO Challenge" | $3,000 |

### Income Model
```
Admin creates: "Build Challenge" quest
→ Reward: $1,000
→ Duration: 24 hours
→ Target: 100 blocks placed

Player A accepts quest
→ Builds 100 blocks
→ Completes quest
→ Receives $1,000

Total earning: $1,000 (new money injected into economy)
```

### Strategic Value
- New player onboarding via quests
- Daily login incentive
- Progression-based rewards
- Leaderboard competition
- Admin content creation tool

### Configuration
```yaml
quests:
  daily_quest_reward: 1000
  weekly_quest_reward: 2500
  monthly_quest_reward: 5000
```

---

## Complete Income Breakdown

### Daily Income Opportunity
| System | Method | Amount | Frequency |
|--------|--------|--------|-----------|
| Rentals | 10 plots @ $200/day | $2,000 | Daily |
| Business | 1 business @ $1,500 profit | $1,500 | Daily |
| Savings | $20,000 @ 5% interest | $2.73 | Daily |
| Quests | 1 daily quest | $1,000 | Daily |
| Loan Interest | Bank interest (payer) | Variable | Daily |
| **Total Realistic** | **Multi-system player** | **~$4,500/day** | |

### Monthly Income Opportunity
| System | Method | Amount | Frequency |
|--------|--------|--------|-----------|
| Plot Sales | 3 @ $7,000 profit each | $21,000 | Monthly |
| Auctions | 20 @ avg $500 each | $10,000 | Monthly |
| Stock Dividends | $50k portfolio @ 15% | $7,500 | Monthly |
| Insurance Claims | 1 major claim | $5,000 | Monthly |
| **Total Extreme** | **Wealthy player** | **~$43,500/month** | |

---

## Wealth Distribution Strategy

### New Player (Day 1)
- Starting balance: $1,000
- Job: Accept Day 1 quest → +$1,000 → Total: $2,000
- Activity: Explore economy mechanics

### Established Player (Week 1)
- Accumulated: $10,000
- Investment: Buy first plot for $5,000
- Income: Complete 5 quests × $1,000 = +$5,000
- Active sources: Quest rewards, property appreciation

### Wealthy Player (Month 1+)
- Net worth: $100,000+
- Portfolio: 3 plots, 1 business, 10 stock shares
- Monthly income: $4,500+ from multiple streams
- Focus: Optimization and compound growth

### Extreme Wealth (Established Server)
- Net worth: $1,000,000+
- Operations: 10+ plots rented, 3 businesses, $500k stock portfolio
- Monthly income: $50,000+
- Role: Economic leader, market maker

---

## Admin Tools & Monitoring

### Useful Commands
```
# View all market listings
/citybuild market

# Tax someone for unpaid debt
/tax seize [playerName]

# Award quest reward manually
/quest award [playerName] [amount]

# Get economy statistics
/economy stats
```

### Key Metrics to Monitor
1. **Total Money in Circulation**: Sum of all player balances
2. **Tax Revenue**: Daily/monthly collection
3. **Quest Spending**: Money injected via rewards
4. **Inflation Rate**: % increase in total money/month
5. **Income Inequality**: Gini coefficient of wealth distribution

### Inflation Control Levers
- Adjust quest reward amounts
- Modify business profit mechanics
- Change auction listing fees
- Adjust tax rates
- Control stock dividend payouts

---

## Anti-Inflation Strategies

### Money Sinks (Remove Cash)
- Increase plot taxes
- Charge auction fees (10% of final bid)
- Plot deletion costs
- Customization cosmetics

### Money Sources (Add Cash)
- Decrease taxes
- Increase quest rewards
- Admin giveaways
- Event bonuses

---

## Player Economics FAQ

**Q: Can I just sit in my plot and earn money?**
A: No. Taxes force active participation. Rentals require tenant finding. Businesses need payroll management.

**Q: What's the fastest way to earn $100k?**
A: Rent out 5 plots @ $200/day = $1,000/day = $100k in 100 days (while doing quests/stock dividends).

**Q: Can admins print infinite money?**
A: Yes (danger). Use `/economy stats` to track total circulation and adjust inflation via quest rewards and taxes.

**Q: What happens if I default on a loan?**
A: After 30 days: 20% penalty added. Your credit is damaged. No future loans until repaid.

**Q: Can I have multiple businesses?**
A: Yes. Each generates profit independently. Scale to your management capacity.

---

## Version History
- **v4.3.0**: Enhanced economy balancing, performance optimization
- **v4.0.0**: Added Auctions, Banking, Insurance, Rentals, Quests
- **v3.2.0**: Plot Markets, Taxes, Businesses, Stocks (4 core systems)
- **v3.0.0**: Basic plot system with protection
- **v1.0.0**: Initial release

---

**Last Updated**: 2025 | **Total Systems**: 9 | **Commands**: 50+ | **Customizable Options**: 40+
