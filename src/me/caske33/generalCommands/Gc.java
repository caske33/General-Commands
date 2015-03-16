package me.caske33.generalCommands;

import java.util.Calendar;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Gc extends JavaPlugin {

	public int countdownover = 0;
	public Calendar start, end;

	public void onEnable() {
	}

	public void onDisable() {
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (sender instanceof Player && ((Player) sender).isOp()) {
			if (command.getName().equalsIgnoreCase("tpc")) {
				World world = ((Player) sender).getWorld();
				int x = Integer.parseInt(args[0]);
				int z = Integer.parseInt(args[1]);
				int y = world.getHighestBlockYAt(x, z);
				Player player = (Player) sender;
				Location loc = new Location(world, x, y, z);
				player.teleport(loc);
				return true;
			} else if (command.getName().equalsIgnoreCase("gg")) {
				getServer().broadcastMessage(ChatColor.GOLD + "GG");
				end = Calendar.getInstance();
				getServer().broadcastMessage(ChatColor.GOLD + "The match took " + compareDates(start, end));
				return true;
			} else if (command.getName().equalsIgnoreCase("gm")) {
				Player player = (Player) sender;
				GameMode gm = player.getGameMode();
				if (gm == GameMode.CREATIVE)
					player.setGameMode(GameMode.SURVIVAL);
				else
					player.setGameMode(GameMode.CREATIVE);
				return true;
			} else if (command.getName().equalsIgnoreCase("rfw") && countdownover == 0) {
				long time = 0;
				Server s = getServer();

				if (args.length >= 1) {
					if (args[0].equalsIgnoreCase("dusk")) {
						time = 20L * 60 * 10;
					} else if (args[0].equalsIgnoreCase("night")) {
						time = 20L * 60 * (23 / 2);
					} else if (args[0].equalsIgnoreCase("dawn")) {
						time = 20L * 60 * (37 / 2);
					} else if (args[0].equalsIgnoreCase("day")) {
						time = 0L;
					} else {
						try {
							time = Long.parseLong(args[0]);
						} catch (NumberFormatException e) {
						}
					}
				}
				World w = ((Player) sender).getWorld();
				heal(w);
				w.setTime(time);
				s.broadcastMessage(ChatColor.GREEN + "Time has been set!");
				for (Entity entity : w.getEntities()) {
					// remove drops
					if (entity instanceof Item)
						entity.remove();
					if (entity instanceof LivingEntity && !(entity instanceof Player))
						entity.remove();
				}
				s.broadcastMessage(ChatColor.GREEN + "All mobs have been butchered!");

				countdownover = 5;
				countdown();
				return true;
			} else if (command.getName().equalsIgnoreCase("ci")) {
				Player player = (Player) sender;
				player.getInventory().clear();
				return true;
			} else if (command.getName().equalsIgnoreCase("heal")) {
				Player player = (Player) sender;
				heal(player.getWorld());
				return true;
			}
		}
		return false;
	}

	public void countdown() {
		if (countdownover > 0) {
			getServer().broadcastMessage(ChatColor.DARK_PURPLE + "The game starts in " + countdownover--);
			getServer().getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
				public void run() {
					countdown();
				}
			}, 20L);
		} else {
			getServer().broadcastMessage(ChatColor.GOLD + "GO!");
			start = Calendar.getInstance();
		}

	}

	public void heal(World w) {
		for (Player player2 : w.getPlayers()) {
			// heal the player
			player2.setHealth(20);
			// feed the player
			player2.setFoodLevel(20);
			player2.setExhaustion(0.0F);
			player2.setSaturation(5.0F);
			// clear the exp of the player
			player2.setTotalExperience(0);
			player2.setExp(0);
			player2.setLevel(0);
			// clear the inventory if in survival mode
			if (player2.getGameMode() == GameMode.SURVIVAL)
				player2.getInventory().clear();
			player2.sendMessage(ChatColor.GREEN + "You have been healed and fed!");
		}
	}

	public String compareDates(Calendar c1, Calendar c2) {
		String s = "";
		Long l = c2.getTimeInMillis() - c1.getTimeInMillis();

		// seconds
		Double d = Math.floor(l / 1000);
		int dS = remainder(d, 60);

		// minutes
		d = Math.floor(d / 60);
		int dM = remainder(d, 60);

		// hours
		d = Math.floor(d / 60);
		int dH = remainder(d, 24);

		// The string
		if (dH <= 9)
			s += "0";
		s += dH + ":";
		if (dM <= 9)
			s += "0";
		s += dM + ":";
		if (dS <= 9)
			s += "0";
		s += dS;
		return s;
	}

	public int remainder(Double d, int divisor) {
		return ((int) (d - (Math.floor((d / divisor)) * divisor)));
	}
}
