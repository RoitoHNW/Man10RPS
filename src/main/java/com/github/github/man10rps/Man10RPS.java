package com.github.github.man10rps;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static java.lang.Thread.sleep;

public final class Man10RPS extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("start");
        Objects.requireNonNull(getCommand("rps")).setExecutor(this);
        this.saveDefaultConfig();
        vault = new VaultManager(this);
        handdata = new HashMap<>();
        config = this.getConfig();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("stop");
    }

    VaultManager vault;
    HashMap<UUID, GameData> handdata;
    FileConfiguration config;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) return true;
        if (args[0].equals("create")) {
            if (args.length != 3) {
                sender.sendMessage("§e§l[RPS]使い方が間違ってます");
                return true;
            }
            Player comP = (Player) sender;
            if (!args[1].matches("[+-]?\\d*(\\.\\d+)?")) {
                sender.sendMessage("§e§l[RPS]/rps create [money] [rock or paper or scissors]");
                return true;
            }

            if (vault.getBalance(comP.getUniqueId()) < Double.parseDouble(args[1])) {
                sender.sendMessage("§e§l[RPS]所持金が足りません");
                return true;
            }

            if (handdata.containsKey(((Player) sender).getUniqueId())) {
                sender.sendMessage("§e§l[RPS]一部屋しか立てれません。");
                return true;
            }

            if (args[2].equals("rock")) {
                double bet = Double.parseDouble(args[2]);
                vault.withdraw(((Player) sender).getUniqueId(), Double.parseDouble(args[1]));
                handdata.put(((Player) sender).getUniqueId(), new GameData(Double.parseDouble(args[1]), "rock"));
                new Thread(() -> {
                    for (int i = 0; i < 30; i++) {
                        if (i % 10 == 0)
                            sender.sendMessage(Component.text("§2§l[RPS]" + sender.getName() + "が" + bet + "円じゃんけんを開いています"));
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (!handdata.containsKey(((Player) sender).getUniqueId())) {
                            return;
                        }
                    }
                });
            }
            if (args[2].equals("paper")) {
                double bet = Double.parseDouble(args[2]);
                vault.withdraw(((Player) sender).getUniqueId(), Double.parseDouble(args[1]));
                handdata.put(((Player) sender).getUniqueId(), new GameData(Double.parseDouble(args[1]), "rock"));
                new Thread(() -> {
                    for (int i = 0; i < 30; i++) {
                        if (i % 10 == 0)
                            sender.sendMessage(Component.text("§2§l[RPS]" + sender.getName() + "が" + bet + "円じゃんけんを開いています"));
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (!handdata.containsKey(((Player) sender).getUniqueId())) {
                            return;
                        }
                    }
                });
            }
            if (args[2].equals("scissors")) {
                double bet = Double.parseDouble(args[2]);
                vault.withdraw(((Player) sender).getUniqueId(), Double.parseDouble(args[1]));
                handdata.put(((Player) sender).getUniqueId(), new GameData(Double.parseDouble(args[1]), "rock"));
                new Thread(() -> {
                    for (int i = 0; i < 30; i++) {
                        if (i % 10 == 0)
                            sender.sendMessage(Component.text("§2§l[RPS]" + sender.getName() + "が" + bet + "円じゃんけんを開いています"));
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (!handdata.containsKey(((Player) sender).getUniqueId())) {
                            return;
                        }
                    }
                    vault.deposit(((Player) sender),(Double.parseDouble(args[1])));
                });
            }
        }
        if (args[0].equals("join")) {

            if (args.length != 2) {
                sender.sendMessage(Component.text("§e§l[CF]使い方が間違ってます"));
                return true;
            }
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                sender.sendMessage("§e§l[CF]プレイヤーが存在しない、またはオフラインです");
                return true;
            }
            if (!handdata.containsKey(player.getUniqueId())) {
                sender.sendMessage("§e§l[CF]その部屋は存在しません");
                return true;
            }
            if (player == sender) {
                sender.sendMessage("§e§l[CF]自分の部屋には入れません");
                return true;
            }
            Double bet = handdata.get(player.getUniqueId()).bet;
            if (vault.getBalance(((Player) sender).getUniqueId()) < bet) {
                sender.sendMessage("§e§l[CF]所持金が足りません");
                return true;
            }
            vault.withdraw(((Player) sender).getUniqueId(), bet);
            boolean mainhand = Boolean.parseBoolean(handdata.get(player.getUniqueId()).rock);

            Inventory inv = Bukkit.createInventory(null, 45, Component.text("§d§lRock-§f§lPaper-§2§lScissors"));
            ((Player) sender).openInventory(inv);
            player.openInventory(inv);
        }
        if (args[0].equals("help")) {
            sender.sendMessage(
                    "§f§l＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝\n" +
                            "§6§l/rps create [金額] [r or p or s]で\n" +
                            "§6§l部屋を作成することができます。\n" +
                            "§e§l注意:部屋を複数立てることはできません。\n" +
                            "§6§l/rps join [r or p or s] [Player]で参加できます。\n" +
                            "§f§l＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝"
            );
        }
        return true;
    }
class GameData {
    double bet;
    String rock;

    GameData(double bet, String rock) {
        this.bet = bet;
        this.rock = rock;
    }
}}


