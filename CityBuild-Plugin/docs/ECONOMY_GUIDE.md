# CityBuild Economy System v3.2.0

A complete player-driven economy with plots, businesses, stocks, taxes, and trading.

---

## 📊 Economic Ecosystem Overview

```
        ┌─ Salary from Job ─┐
        │                  │
    Players ─→ Plots ─→ Businesses ─→ Stocks
        ↑        ├        ├─→ Dividends
        │        │        │
        └─ Taxes │ ✗ Debt Seizing
              Market ─→ Trading
```

**Total Money Flows:**
- Starting balance: $10,000
- Daily login reward: $500 (+$100/day streak bonus)
- Plot purchase: -$5,000
- Plot sale: +$4,000
- Plot tax: -$500/plot/day
- Business salary: Negotiable per employee
- Stock dividends: % of profit

---

## 💰 Plotting Economy

### Plot Purchase & Sale
**Command:** `/citybuild buy`
- Cost: $5,000 (configurable)
- Instant 16×16 terrain generation
- Ownership: Can be traded on market

### Plot Market (Player Trading)
**List a plot for sale:**
```
/citybuild market list <price> [description]
```
Example: `/citybuild market list 7500 "Great location near spawn"`

**View all listings:**
```
/citybuild market browse
```
Shows all plots for sale with owner, price, location

**Buy listed plot:**
```
/citybuild market buy <plot_id>
```

**Market Statistics:**
```
/citybuild market stats
```
Shows: Active listings, average price, min/max price, volume

### Plot Taxes
**Daily Tax:** $500 per plot owned (configurable)
- Due every 24 hours
- Paid automatically on login
- Can accumulate as debt

**Check tax status:**
```
/citybuild plot tax-status
```

**Tax debt:**
- If balance < tax owed: Debt accumulates
- Debt: `/citybuild plot pay-taxes [amount]`
- Seizing: Admin can seize plots for unpaid taxes

---

## 🏢 Business System

### Create a Company
**Command:** `/citybuild business create <name> <industry>`

Industries:
- `RETAIL` - Selling goods
- `MANUFACTURING` - Production
- `SERVICES` - Offering services
- `TECHNOLOGY` - Tech services
- `AGRICULTURE` - Farming

Example: `/citybuild business create "Steve's Bakery" RETAIL`

### Employee Management
**Hire employee:**
```
/citybuild business hire <company> <player> <salary>
```
Salary: Daily payment per employee

**Fire employee:**
```
/citybuild business fire <company> <employee>
```

**Pay employees:**
```
/citybuild business pay <company>
```
Deducts payroll from company balance

### Company Operations
**Add revenue to company:**
(Admin command) `/citybuild admin business add-profit <company> <amount>`

**Withdraw profit:**
```
/citybuild business withdraw <company> <amount>
```
Money goes from company to owner balance

**View company:**
```
/citybuild business info <name>
```

### Business Statistics
```
/citybuild business stats
```
Shows:
- Total active companies
- Total employees
- Total business balance
- Total profits generated

---

## 📈 Stock Market System

### IPO: Create Stock
**Command:** `/citybuild stock ipo <project_name> <shares> <price_per_share>`

Example: `/citybuild stock ipo "Mega Project" 1000 50`
- 1000 shares available
- $50 per share
- Total market cap: $50,000

### Trading Stocks
**Buy shares:**
```
/citybuild stock buy <stock_id> <quantity>
```
Example: `/citybuild stock buy 1 100` = Buy 100 shares of stock #1

**Sell shares:**
```
/citybuild stock sell <stock_id> <quantity>
```

**View portfolio:**
```
/citybuild stock portfolio
```
Shows all shares owned and current value

### Dividends
**Pay dividend to shareholders:**
(As issuer) `/citybuild stock dividend <stock_id> <total_amount>`

All shareholders receive proportional dividend based on shares

### Market Data
```
/citybuild stock market
```
Shows all stocks, prices, market cap, total dividends paid

---

## 🏦 Banking & Wealth

### Player Balance
```
/citybuild balance [player]
```

### Transfer Money
```
/citybuild pay <player> <amount>
```
With cooldown: 10 seconds between transfers

### Leaderboard
```
/citybuild leaderboard
```
Top 10 richest players

### Personal Statistics
```
/citybuild stats
```
Shows:
- Total balance
- Total plots owned
- Businesses owned
- Stock portfolio value
- Net worth

---

## 💸 Income Sources

| Source | Amount | Frequency |
|--------|--------|-----------|
| Login Reward | $500 + streak | Daily |
| Business Salary | Custom | Daily |
| Stock Dividends | % of profit | On IPO |
| Plot Flip | $2000+ profit | On sale |

---

## 💳 Expenses

| Expense | Amount | Frequency |
|---------|--------|-----------|
| Plot Tax | $500/plot | Daily |
| Payroll (business) | Custom | Daily |
| Plot Purchase | $5,000 | One-time |
| Stock Purchase | Custom | One-time |

---

## 🎯 Wealth Growth Strategy

### Starter (0-50k)
1. Farm login rewards ($500/day = $3,500/week)
2. After 10 days → Buy first plot
3. Lists can flip plots for +$2,500 profit
4. Total: Reach $50k in ~4 weeks

### Intermediate (50-250k)
1. Own 5-10 plots, collect taxes daily
2. Create business, hire employees
3. IPO stock for capital raise
4. Profit: $5k+/week possible

### Advanced (250k+)
1. Multiple businesses with employees
2. Large stock portfolio
3. Market manipulation potential
4. Rental income from sub-leasing

---

## 📋 Admin Commands for Economy

### Business Management
```
/citybuild admin business create <player> <name> <industry>
/citybuild admin business add-profit <company> <amount>
/citybuild admin business delete <company>
```

### Tax Management
```
/citybuild admin tax collect <player>
/citybuild admin tax forgive <player> <amount>
/citybuild admin tax seize <player>   # Seize plots for debt
```

### Market Management
```
/citybuild admin market freeze <stock_id>
/citybuild admin market setprice <stock_id> <new_price>
/citybuild admin market stats
```

### Economy Reset
```
/citybuild admin economy reset   # WARNING: Clears all data!
```

---

## ⚙️ Configuration

**config.yml economy section:**

```yaml
economy:
  starting_balance: 10000
  plot_buy_price: 5000
  plot_sell_price: 4000

taxes:
  plot_tax_per_plot: 500        # Daily per plot
  tax_interval_days: 1          # Frequency
  
  seize_after_days_debt: 30     # Days before plot seizing

business:
  creation_fee: 0               # Cost to create company
  max_employees_per_company: 50
  
stocks:
  min_shares_for_ipo: 100
  min_price_per_share: 1
  max_price_per_share: 10000
  
  dividend_tax: 0.10            # 10% tax on dividends
```

---

## 📊 Economy Monitoring

### Check System Health
```
/citybuild admin economy stats
```
Shows:
- Total money in circulation
- Total plots owned
- Business count & employees
- Stock market cap
- Tax revenue collected

### View Player Economy
```
/citybuild admin player-stats <player>
```
Shows:
- Balance
- Net worth
- Owned plots
- Businesses
- Stock portfolio

---

## 🎮 Gameplay Tips

### Maximize Income
1. **Login daily** - $500/day base
2. **Build streak** - +$100 per day bonus (max $5000/day at 49-day streak)
3. **Own plots** - Tax income if prices rise
4. **Run business** - Hire employees and generate profit
5. **Stock dividends** - Passive income from investments

### Avoid Debt
1. Pay taxes on time (before 24 hours due)
2. Don't buy plots you can't afford tax on
3. Check balance before major purchases
4. Keep 2-3 weeks of tax savings reserved

### Flip Profits
1. Buy plot: -$5,000
2. Hold 1 month (build value perception)
3. List on market: +$7,000-10,000
4. Net profit: $2,000-5,000 per flip
5. Repeat with multiple plots

---

## 🚨 Emergency Commands

**If economy breaks:**

```bash
# Backup economy data
/citybuild admin backup economy

# View current state
/citybuild admin economy status

# Manual balance correction
/citybuild admin balance-set <player> <amount>

# Restore from backup
/citybuild admin restore economy [date]
```

---

## 📖 Further Reading

- [ADMIN_COMMANDS.md](ADMIN_COMMANDS.md) - All admin commands
- [CONFIGURATION.md](CONFIGURATION.md) - All config options
- [BUSINESS_GUIDE.md](BUSINESS_GUIDE.md) - Detailed business system
- [STOCK_GUIDE.md](STOCK_GUIDE.md) - Stock market mechanics

---

**Economy Version:** 3.2.0  
**Status:** ✅ Fully operational  
**Last Updated:** April 23, 2026
