package co.marcin.novaguilds.command;

import co.marcin.novaguilds.NovaGuilds;
import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.enums.Message;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandGuildMenu implements CommandExecutor {
	private final NovaGuilds plugin;

	public CommandGuildMenu(NovaGuilds pl) {
		plugin = pl;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Message.CHAT_CMDFROMCONSOLE.send(sender);
			return true;
		}

		if(!sender.hasPermission("novaguilds.guild.gui")) {
			Message.CHAT_NOPERMISSIONS.send(sender);
			return true;
		}

		Player player = (Player)sender;

		int limit = Integer.parseInt(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_TOPROWS.get()); //TODO move to config
		int i=1;

		Inventory inv = Bukkit.createInventory(null,9,plugin.getMessageManager().getMessagesString("inventory.ggui.name"));
		List<String> lore = new ArrayList<>();

		HashMap<String, String> vars = new HashMap<>();
		for(NovaGuild guild : plugin.getGuildManager().getTopGuildsByPoints(limit)) {
			vars.clear();
			vars.put("GUILDNAME", guild.getName());
			vars.put("N", String.valueOf(i));
			vars.put("POINTS", String.valueOf(guild.getPoints()));
			lore.add(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_ROW.vars(vars).get());
			i++;
		}

		ItemStack topItem = new ItemStack(Material.SIGN, 1);
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(topItem.getType());
		meta.setDisplayName(Message.HOLOGRAPHICDISPLAYS_TOPGUILDS_HEADER.prefix(false).get());
		meta.setLore(lore);
		topItem.setItemMeta(meta);

		for(ItemStack item : plugin.getCommandManager().getGuiItems()) {
			inv.addItem(item);
		}

		inv.addItem(topItem);

		player.openInventory(inv);
		return true;
	}
}
