package com.redspeaks.minecraftiaeconomy.data;

public class Actions {

    public enum PlayerAction {

        SEND,
        RECEIVED,
        SET_BALANCE,
        SEND_BY_BANK

    }

    public enum BankAction {

        SEND,
        RECEIVED,
        CREATED,
        DELETED,
        ASSIGNED,
        SET_BALANCE

    }
}
