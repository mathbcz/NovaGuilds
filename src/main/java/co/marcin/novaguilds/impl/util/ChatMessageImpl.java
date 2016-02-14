/*
 *     NovaGuilds - Bukkit plugin
 *     Copyright (C) 2015 Marcin (CTRL) Wieczorek
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package co.marcin.novaguilds.impl.util;

import co.marcin.novaguilds.api.basic.NovaGuild;
import co.marcin.novaguilds.api.util.ChatMessage;
import co.marcin.novaguilds.api.util.PreparedTag;
import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.enums.VarKey;
import co.marcin.novaguilds.util.LoggerUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatMessageImpl implements ChatMessage {
	private final Player player;
	private String message;
	private String format;
	private PreparedTag tag;
	private boolean reportToConsole = true;
	private boolean reported = false;

	public ChatMessageImpl(Player player) {
		this.player = player;
	}

	@Override
	public void send() {
		sendToPlayers(new ArrayList<>(Bukkit.getOnlinePlayers()));
	}

	@Override
	public void send(Player player) {
		if(player == null) {
			return;
		}

		player.sendMessage(parse());
		report();
	}

	@Override
	public void send(NovaPlayer nPlayer) {
		send(nPlayer.getPlayer());
	}

	@Override
	public void send(NovaGuild guild) {
		sendToPlayers(guild.getOnlinePlayers());
	}

	@Override
	public void sendToGuilds(List<NovaGuild> guildList) {
		for(NovaGuild guild : guildList) {
			send(guild);
		}
	}

	@Override
	public void sendToPlayers(List<Player> playerList) {
		for(Player player : playerList) {
			send(player);
		}
	}

	@Override
	public void sendToNovaPlayers(List<NovaPlayer> playerList) {
		for(NovaPlayer nPlayer : playerList) {
			send(nPlayer);
		}
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getFormat() {
		return format;
	}

	@Override
	public PreparedTag getTag() {
		return tag;
	}

	@Override
	public boolean isReportToConsole() {
		return reportToConsole;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public void setFormat(String format) {
		Map<String, String> vars = new HashMap<>();

		format = StringUtils.replace(format, "%1$s", "{DISPLAYNAME}");
		format = StringUtils.replace(format, "%2$s", "{MESSAGE}");

		vars.put("0", "{GROUP}");
		vars.put("1", "{WORLD}");
		vars.put("1", "{WORLDNAME}");
		vars.put("2", "{SHORTWORLDNAME}");
		vars.put("3", "{TEAMPREFIX}");
		vars.put("4", "{TEAMSUFFIX}");
		vars.put("5", "{TEAMNAME}");

		format = co.marcin.novaguilds.util.StringUtils.replaceMap(format, vars);

		this.format = format;
	}

	@Override
	public void setTag(PreparedTag tag) {
		this.tag = tag;
	}

	@Override
	public void setReportToConsole(boolean reportToConsole) {
		this.reportToConsole = reportToConsole;
	}

	@Override
	public void report() {
		if(!reported && isReportToConsole()) {
			LoggerUtils.info(parse());
			reported = true;
		}
	}

	private String parse() {
		String format = getFormat();

		Map<VarKey, String> vars = new HashMap<>();
		vars.put(VarKey.DISPLAYNAME, getPlayer().getDisplayName());
		vars.put(VarKey.PLAYER, getPlayer().getName());
		vars.put(VarKey.WORLD, getPlayer().getWorld().getName());
		vars.put(VarKey.WORLDNAME, getPlayer().getWorld().getName());
		vars.put(VarKey.TAG, tag.get());

		format = co.marcin.novaguilds.util.StringUtils.replaceVarKeyMap(format, vars);
		format = co.marcin.novaguilds.util.StringUtils.fixColors(format);
		format = StringUtils.replace(format, "{MESSAGE}", getMessage());

		return format;
	}
}
