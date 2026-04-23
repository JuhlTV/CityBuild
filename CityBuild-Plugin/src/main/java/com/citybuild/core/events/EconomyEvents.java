package com.citybuild.core.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import java.util.UUID;

/**
 * Base class for all CityBuild economy events
 */
public abstract class CityBuildEconomyEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    protected final UUID playerUuid;
    protected final String playerName;
    protected boolean cancelled = false;
    
    public CityBuildEconomyEvent(Player player) {
        this.playerUuid = player.getUniqueId();
        this.playerName = player.getName();
    }
    
    public CityBuildEconomyEvent(UUID uuid, String name) {
        this.playerUuid = uuid;
        this.playerName = name;
    }
    
    public UUID getPlayerUuid() {
        return playerUuid;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}

/**
 * Called when a transaction is about to occur
 * Can be cancelled to prevent transaction
 */
class EconomyTransactionEvent extends CityBuildEconomyEvent {
    public enum TransactionType {
        TRANSFER, PAYMENT, DEPOSIT, WITHDRAWAL, ADJUSTMENT, REWARD
    }
    
    private final TransactionType type;
    private final long amount;
    private final String reason;
    private UUID targetUuid;
    
    public EconomyTransactionEvent(Player player, TransactionType type, long amount, String reason) {
        super(player);
        this.type = type;
        this.amount = amount;
        this.reason = reason;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public long getAmount() {
        return amount;
    }
    
    public String getReason() {
        return reason;
    }
    
    public UUID getTargetUuid() {
        return targetUuid;
    }
    
    public void setTargetUuid(UUID uuid) {
        this.targetUuid = uuid;
    }
}

/**
 * Called when player balance is retrieved
 */
class PlayerBalanceChangeEvent extends CityBuildEconomyEvent {
    private final long oldBalance;
    private final long newBalance;
    
    public PlayerBalanceChangeEvent(Player player, long oldBalance, long newBalance) {
        super(player);
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
    }
    
    public long getOldBalance() {
        return oldBalance;
    }
    
    public long getNewBalance() {
        return newBalance;
    }
    
    public long getDifference() {
        return newBalance - oldBalance;
    }
}
