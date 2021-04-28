package com.redspeaks.minecraftiaeconomy.api;

public @interface CommandInfo {
    String name();
    String permission() default "";
    boolean requiresPlayer();
}
