package com.redspeaks.minecraftiaeconomy.data;

public class Actions {

    public enum PlayerAction {

        SEND("Send from wallet"),
        RECEIVED("Received money"),
        SET_BALANCE("Balance updated"),
        SEND_BY_BANK("Send money from bank"),
        RECEIVED_BY_BANK("Receive money to bank"),
        BANK_DELETED("Unassigned a bank");

        String action;
        PlayerAction(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }

    }

    public enum BankAction {

        SEND_TO_BANK("Send money to bank"),
        RECEIVED_FROM_BANK("Receive money from bank"),
        CREATED("Created"),
        DELETED("Deleted"),
        ASSIGNED("Assigned to others"),
        SET_BALANCE("Balance updated"),
        SEND_TO_PLAYER("Send money to player"),
        RECEIVED_FROM_PLAYER("Received from player");

        String action;
        BankAction(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }

    }
}
