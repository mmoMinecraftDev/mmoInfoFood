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
import java.util.Map;

import mmo.Core.InfoAPI.MMOInfoEvent;
import mmo.Core.MMOPlugin;
import mmo.Core.MMOPlugin.Support;
import mmo.Core.util.EnumBitSet;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.ContainerType;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.Gradient;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.Screen;
import org.getspout.spoutapi.gui.Texture;
import org.getspout.spoutapi.player.SpoutPlayer;

public class mmoInfoFood extends MMOPlugin implements Listener {
	private HashMap<Player, CustomLabel> widgets = new HashMap();
	private static final Map<Player, CustomWidget> foodbar = new HashMap<Player, CustomWidget>();
	private static String config_displayas = "bar";

	@Override
	public EnumBitSet mmoSupport(EnumBitSet support) {		
		support.set(Support.MMO_AUTO_EXTRACT);
		return support;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		pm.registerEvents(this, this);
	}

	@Override
	public void loadConfiguration(final FileConfiguration cfg) {
		config_displayas = cfg.getString("displayas", config_displayas);		
	}

	@EventHandler
	public void onMMOInfo(MMOInfoEvent event)
	{
		if (event.isToken("food")) {
			SpoutPlayer player = event.getPlayer();
			if (player.hasPermission("mmo.info.food")) {
				if (config_displayas.equalsIgnoreCase("bar")) {				
					final CustomWidget widget = new CustomWidget();
					foodbar.put(player, widget);
					event.setWidget(plugin, widget);
				} else { 
					player.getMainScreen().getHungerBar().setVisible(false);
					CustomLabel label = (CustomLabel)new CustomLabel().setResize(true).setFixed(true);
					this.widgets.put(player, label);
					event.setWidget(this.plugin, label);
				}
				event.setIcon("hunger.png");			
			}
		}
	}

	public class CustomLabel extends GenericLabel
	{
		@EventHandler
		public void onMMOFoodChange(FoodLevelChangeEvent event) {						
			if (event instanceof Player) {
				setText(String.format(getScreen().getPlayer().getFoodLevel() + "/20"));
			}
		}
	}

	public class CustomWidget extends GenericContainer {

		private final Gradient slider = new GenericGradient();
		private final Texture bar = new GenericTexture();

		public CustomWidget() {
			super();
			slider.setMargin(1).setPriority(RenderPriority.Normal).setHeight(5).setWidth(20).shiftXPos(1).shiftYPos(1);
			bar.setUrl("bar10.png").setPriority(RenderPriority.Lowest).setHeight(7).setWidth(103).shiftYPos(0);			
			this.setLayout(ContainerType.OVERLAY).setMinWidth(100).setMaxWidth(100);
			this.addChildren(slider, bar);
		}

		@EventHandler
		public void onMMOFoodChange(FoodLevelChangeEvent event) {
			final int playerFood = (int) (getScreen().getPlayer().getFoodLevel()*5);						
			if (playerFood>=33) {			
				slider.setColor(new Color(0,0,1f,1f)).setWidth(playerFood); //Blue				
			} else if (playerFood<33) {
				slider.setColor(new Color(0.69f,0.09f,0.12f,1f)).setWidth(playerFood);  //Red				
			}			
		}
	}
}