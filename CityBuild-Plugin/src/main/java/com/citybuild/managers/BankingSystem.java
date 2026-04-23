package com.citybuild.managers;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages loans, savings accounts, and interest
 * Players can borrow money and earn interest on savings
 */
public class BankingSystem {
    private final JavaPlugin plugin;
    private final EconomyManager economyManager;
    
    // UUID -> SavingsAccount
    private final Map<String, SavingsAccount> accounts = new ConcurrentHashMap<>();
    // UUID -> List<Loan>
    private final Map<String, List<Loan>> playerLoans = new ConcurrentHashMap<>();

    private final double savingsInterestRate; // 5% = 0.05
    private final double loanInterestRate;   // 10% = 0.10
    private final long maxLoanAmount;

    public BankingSystem(JavaPlugin plugin, EconomyManager economyManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;
        this.savingsInterestRate = plugin.getConfig().getDouble("banking.savings_interest_rate", 0.05);
        this.loanInterestRate = plugin.getConfig().getDouble("banking.loan_interest_rate", 0.10);
        this.maxLoanAmount = plugin.getConfig().getLong("banking.max_loan_amount", 100000);
    }

    // ===== SAVINGS ACCOUNTS =====

    /**
     * Create or get savings account for player
     */
    public SavingsAccount getAccount(String playerUuid) {
        return accounts.computeIfAbsent(playerUuid, k -> new SavingsAccount(playerUuid, System.currentTimeMillis()));
    }

    /**
     * Deposit to savings account
     */
    public boolean depositToSavings(String playerUuid, long amount) {
        if (!economyManager.hasBalance(playerUuid, amount)) {
            return false;
        }

        economyManager.withdraw(playerUuid, amount);
        SavingsAccount account = getAccount(playerUuid);
        account.balance += amount;
        account.lastModified = System.currentTimeMillis();

        plugin.getLogger().fine("✓ Deposited $" + amount + " to " + playerUuid.substring(0, 8) + 
            "'s savings (Total: $" + account.balance + ")");
        return true;
    }

    /**
     * Withdraw from savings account
     */
    public boolean withdrawFromSavings(String playerUuid, long amount) {
        SavingsAccount account = getAccount(playerUuid);
        if (account.balance < amount) {
            return false; // Insufficient savings
        }

        account.balance -= amount;
        account.lastModified = System.currentTimeMillis();
        economyManager.deposit(playerUuid, amount);

        plugin.getLogger().fine("✓ Withdrew $" + amount + " from " + playerUuid.substring(0, 8) + 
            "'s savings (Remaining: $" + account.balance + ")");
        return true;
    }

    /**
     * Apply interest to all savings accounts
     */
    public void applyDailyInterest() {
        for (SavingsAccount account : accounts.values()) {
            long interestAmount = (long) (account.balance * savingsInterestRate);
            if (interestAmount > 0) {
                account.balance += interestAmount;
                account.interestEarned += interestAmount;
                economyManager.deposit(account.playerUuid, interestAmount);
            }
        }
        plugin.getLogger().info("✓ Applied daily savings interest to " + accounts.size() + " accounts");
    }

    // ===== LOANS =====

    /**
     * Take out a loan
     */
    public Loan takeLoan(String playerUuid, long amount, long durationDays) {
        if (amount > maxLoanAmount) {
            return null; // Exceeds max loan
        }

        // Check existing debt
        long totalDebt = getTotalDebt(playerUuid);
        if (totalDebt + amount > maxLoanAmount) {
            return null; // Would exceed max debt
        }

        long interestAmount = (long) (amount * loanInterestRate);
        long totalOwed = amount + interestAmount;

        Loan loan = new Loan(
            playerUuid,
            amount,
            totalOwed,
            durationDays,
            System.currentTimeMillis()
        );

        playerLoans.computeIfAbsent(playerUuid, k -> new ArrayList<>()).add(loan);
        economyManager.deposit(playerUuid, amount);

        plugin.getLogger().info("✓ Loan granted: $" + amount + " to " + playerUuid.substring(0, 8) + 
            " (Repay: $" + totalOwed + " in " + durationDays + " days)");
        return loan;
    }

    /**
     * Repay loan
     */
    public boolean repayLoan(String playerUuid, int loanIndex, long amount) {
        List<Loan> loans = playerLoans.get(playerUuid);
        if (loans == null || loanIndex >= loans.size()) {
            return false;
        }

        Loan loan = loans.get(loanIndex);
        if (loan.isDefaulted()) {
            return false; // Loan defaulted
        }

        if (!economyManager.hasBalance(playerUuid, amount)) {
            return false; // Insufficient funds
        }

        economyManager.withdraw(playerUuid, amount);
        loan.amountRepaid += amount;

        if (loan.isFullyRepaid()) {
            loans.remove(loanIndex);
            plugin.getLogger().info("✓ Loan fully repaid by " + playerUuid.substring(0, 8));
        } else {
            plugin.getLogger().fine("✓ Loan payment: $" + amount + " (Remaining: $" + 
                (loan.totalOwed - loan.amountRepaid) + ")");
        }

        return true;
    }

    /**
     * Get total debt for player
     */
    public long getTotalDebt(String playerUuid) {
        List<Loan> loans = playerLoans.get(playerUuid);
        if (loans == null) {
            return 0;
        }

        return loans.stream()
            .mapToLong(l -> l.totalOwed - l.amountRepaid)
            .sum();
    }

    /**
     * Get all loans for player
     */
    public List<Loan> getPlayerLoans(String playerUuid) {
        return new ArrayList<>(playerLoans.getOrDefault(playerUuid, new ArrayList<>()));
    }

    /**
     * Apply default penalties to overdue loans
     */
    public void applyDefaultPenalties() {
        int defaults = 0;
        for (List<Loan> loanList : playerLoans.values()) {
            for (Loan loan : loanList) {
                if (loan.isDefaulted() && !loan.penaltyApplied) {
                    // Add 20% penalty
                    long penalty = (long) ((loan.totalOwed - loan.amountRepaid) * 0.20);
                    loan.totalOwed += penalty;
                    loan.penaltyApplied = true;
                    defaults++;
                }
            }
        }
        if (defaults > 0) {
            plugin.getLogger().warning("⚠ Applied default penalties to " + defaults + " loans");
        }
    }

    // ===== STATISTICS =====

    /**
     * Get banking statistics
     */
    public BankingStats getStats() {
        long totalSavings = accounts.values().stream()
            .mapToLong(a -> a.balance)
            .sum();

        long totalLoaned = playerLoans.values().stream()
            .flatMap(List::stream)
            .mapToLong(l -> l.amount)
            .sum();

        long totalDebt = playerLoans.values().stream()
            .flatMap(List::stream)
            .mapToLong(l -> l.totalOwed - l.amountRepaid)
            .sum();

        return new BankingStats(
            accounts.size(),
            totalSavings,
            playerLoans.size(),
            totalLoaned,
            totalDebt
        );
    }

    // ===== DATA CLASSES =====

    public static class SavingsAccount {
        public final String playerUuid;
        public final long createdAt;
        public long balance = 0;
        public long interestEarned = 0;
        public long lastModified;

        public SavingsAccount(String playerUuid, long createdAt) {
            this.playerUuid = playerUuid;
            this.createdAt = createdAt;
            this.lastModified = createdAt;
        }
    }

    public static class Loan {
        public final String playerUuid;
        public final long amount;
        public long totalOwed;
        public final long durationDays;
        public final long createdAt;
        
        public long amountRepaid = 0;
        public boolean penaltyApplied = false;

        public Loan(String playerUuid, long amount, long totalOwed, long durationDays, long createdAt) {
            this.playerUuid = playerUuid;
            this.amount = amount;
            this.totalOwed = totalOwed;
            this.durationDays = durationDays;
            this.createdAt = createdAt;
        }

        public boolean isFullyRepaid() {
            return amountRepaid >= totalOwed;
        }

        public boolean isDefaulted() {
            long daysSinceCreation = (System.currentTimeMillis() - createdAt) / (1000 * 60 * 60 * 24);
            return daysSinceCreation > durationDays;
        }

        public long getRemainingAmount() {
            return Math.max(0, totalOwed - amountRepaid);
        }

        public long getDaysRemaining() {
            long daysSinceCreation = (System.currentTimeMillis() - createdAt) / (1000 * 60 * 60 * 24);
            return Math.max(0, durationDays - daysSinceCreation);
        }
    }

    public static class BankingStats {
        public final int totalAccounts;
        public final long totalSavings;
        public final int loanersCount;
        public final long totalLoaned;
        public final long totalOutstandingDebt;

        public BankingStats(int accounts, long savings, int loaners, long loaned, long debt) {
            this.totalAccounts = accounts;
            this.totalSavings = savings;
            this.loanersCount = loaners;
            this.totalLoaned = loaned;
            this.totalOutstandingDebt = debt;
        }
    }
}
