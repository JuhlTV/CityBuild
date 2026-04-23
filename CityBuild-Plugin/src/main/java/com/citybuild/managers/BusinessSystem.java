package com.citybuild.managers;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manages player-owned businesses, employees, and profits
 * Players can create companies, hire employees, and earn business profits
 */
public class BusinessSystem {
    private final JavaPlugin plugin;
    private final EconomyManager economyManager;

    // CompanyID -> Company
    private final Map<Integer, Company> companies = new ConcurrentHashMap<>();
    // UUID -> List of CompanyIDs owned
    private final Map<String, List<Integer>> ownerCompanies = new ConcurrentHashMap<>();
    
    private int nextCompanyId = 1;
    private long totalBusinessProfit = 0;

    public BusinessSystem(JavaPlugin plugin, EconomyManager economyManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;
    }

    // ===== COMPANY CREATION & MANAGEMENT =====

    /**
     * Create a new business company
     */
    public Company createCompany(String ownerUuid, String companyName, String industryType) {
        if (companyName.length() < 3 || companyName.length() > 50) {
            return null;
        }

        Company company = new Company(
            nextCompanyId++,
            companyName,
            ownerUuid,
            industryType,
            System.currentTimeMillis()
        );

        companies.put(company.id, company);
        ownerCompanies.computeIfAbsent(ownerUuid, k -> new ArrayList<>()).add(company.id);

        plugin.getLogger().info("✓ Company created: \"" + companyName + "\" (ID: " + company.id + ")");
        return company;
    }

    /**
     * Get company by ID
     */
    public Company getCompany(int companyId) {
        return companies.get(companyId);
    }

    /**
     * Get all companies owned by player
     */
    public List<Company> getCompaniesOwnedBy(String ownerUuid) {
        return ownerCompanies.getOrDefault(ownerUuid, new ArrayList<>()).stream()
            .map(companies::get)
            .collect(Collectors.toList());
    }

    /**
     * Get all companies
     */
    public Collection<Company> getAllCompanies() {
        return new ArrayList<>(companies.values());
    }

    // ===== EMPLOYEE MANAGEMENT =====

    /**
     * Hire an employee at company
     */
    public boolean hireEmployee(int companyId, String employeeUuid, long salary) {
        Company company = getCompany(companyId);
        if (company == null) {
            return false;
        }

        if (company.employees.containsKey(employeeUuid)) {
            return false; // Already employed
        }

        company.employees.put(employeeUuid, salary);
        plugin.getLogger().fine("✓ Hired employee at \"" + company.name + "\" for $" + salary + "/day");
        return true;
    }

    /**
     * Fire an employee
     */
    public boolean fireEmployee(int companyId, String employeeUuid, String ownerUuid) {
        Company company = getCompany(companyId);
        if (company == null || !company.ownerUuid.equals(ownerUuid)) {
            return false;
        }

        return company.employees.remove(employeeUuid) != null;
    }

    /**
     * Pay all employees of a company
     */
    public boolean payEmployees(int companyId) {
        Company company = getCompany(companyId);
        if (company == null) {
            return false;
        }

        long totalPayroll = company.getPayroll();

        if (company.balance < totalPayroll) {
            return false; // Insufficient company funds
        }

        company.balance -= totalPayroll;

        for (Map.Entry<String, Long> employee : company.employees.entrySet()) {
            economyManager.deposit(employee.getKey(), employee.getValue());
        }

        plugin.getLogger().info("✓ Paid " + company.employees.size() + " employees at \"" + 
            company.name + "\" (Total: $" + totalPayroll + ")");
        return true;
    }

    // ===== PROFIT & REVENUE =====

    /**
     * Add profit to company (from business operations)
     */
    public void addCompanyProfit(int companyId, long amount) {
        Company company = getCompany(companyId);
        if (company == null) {
            return;
        }

        company.balance += amount;
        company.totalProfit += amount;
        totalBusinessProfit += amount;
    }

    /**
     * Withdraw profit to owner
     */
    public boolean withdrawProfit(int companyId, String ownerUuid, long amount) {
        Company company = getCompany(companyId);
        if (company == null || !company.ownerUuid.equals(ownerUuid)) {
            return false;
        }

        if (company.balance < amount) {
            return false; // Insufficient funds
        }

        company.balance -= amount;
        economyManager.deposit(ownerUuid, amount);

        plugin.getLogger().fine("✓ Withdrew $" + amount + " from \"" + company.name + "\" to owner");
        return true;
    }

    // ===== STATISTICS =====

    /**
     * Get business statistics
     */
    public BusinessStats getStats() {
        long totalCompanyBalance = companies.values().stream()
            .mapToLong(c -> c.balance)
            .sum();

        long totalEmployees = companies.values().stream()
            .mapToLong(c -> c.employees.size())
            .sum();

        return new BusinessStats(
            companies.size(),
            (int) totalEmployees,
            totalCompanyBalance,
            totalBusinessProfit
        );
    }

    /**
     * Get top companies by profit
     */
    public List<Company> getTopCompaniesByProfit(int limit) {
        return companies.values().stream()
            .sorted((a, b) -> Long.compare(b.totalProfit, a.totalProfit))
            .limit(limit)
            .collect(Collectors.toList());
    }

    // ===== DATA CLASSES =====

    public static class Company {
        public final int id;
        public final String name;
        public final String ownerUuid;
        public final String industry;
        public final long createdAt;

        public long balance = 0;
        public long totalProfit = 0;
        public final Map<String, Long> employees = new ConcurrentHashMap<>(); // UUID -> Salary

        public Company(int id, String name, String ownerUuid, String industry, long createdAt) {
            this.id = id;
            this.name = name;
            this.ownerUuid = ownerUuid;
            this.industry = industry;
            this.createdAt = createdAt;
        }

        public long getPayroll() {
            return employees.values().stream()
                .mapToLong(Long::longValue)
                .sum();
        }

        public int getEmployeeCount() {
            return employees.size();
        }

        public double getAverageSalary() {
            if (employees.isEmpty()) return 0;
            return (double) getPayroll() / employees.size();
        }
    }

    public static class BusinessStats {
        public final int totalCompanies;
        public final int totalEmployees;
        public final long totalCompanyBalance;
        public final long totalProfitGenerated;

        public BusinessStats(int totalCompanies, int totalEmployees, long totalBalance, long totalProfit) {
            this.totalCompanies = totalCompanies;
            this.totalEmployees = totalEmployees;
            this.totalCompanyBalance = totalBalance;
            this.totalProfitGenerated = totalProfit;
        }
    }
}
