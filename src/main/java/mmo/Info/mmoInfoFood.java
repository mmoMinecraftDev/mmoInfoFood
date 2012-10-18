/*
 * This file is part of mmoInfoFood <http://github.com/mmoMinecraftDev/mmoInfoFood>.
 *
 * mmoInfoFood is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mmo.Info;

import java.util.HashMap;
import mmo.Core.InfoAPI.MMOInfoEvent;
import mmo.Core.MMOPlugin;
import mmo.Core.MMOPlugin.Support;
import mmo.Core.util.EnumBitSet;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.Screen;
import org.getspout.spoutapi.player.SpoutPlayer;

public class mmoInfoFood extends MMOPlugin implements Listener {
	private HashMap<Player, CustomLabel> widgets = new HashMap();	
	
	@Override
	public EnumBitSet mmoSupport(EnumBitSet support) {
		support.set(Support.MMO_NO_CONFIG);
		support.set(Support.MMO_AUTO_EXTRACT);
		return support;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		pm.registerEvents(this, this);
	}
	
	@EventHandler
	public void onMMOInfo(MMOInfoEvent event)
	{
		if (event.isToken("food")) {
			SpoutPlayer player = event.getPlayer();
			if (player.hasPermission("mmo.info.food")) {
				player.getMainScreen().getHungerBar().setVisible(false);
				CustomLabel label = (CustomLabel)new CustomLabel().setResize(true).setFixed(true);
				this.widgets.put(player, label);
				event.setWidget(this.plugin, label);
				event.setIcon("hunger.png");			
			}
		}
	}

	public class CustomLabel extends GenericLabel
	{
		private boolean check = true;

		public CustomLabel() {
		}

		public void change() {
			this.check = true;
		}
		private transient int tick = 0;
		public void onTick()
		{
			if (tick++ % 20 == 0) {
				setText(String.format(getScreen().getPlayer().getFoodLevel() + "/20"));
			}
		}
	}
}