package com.FallenTree;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("fallenTree")
public interface FallenTreeConfig extends Config
{
	@ConfigItem(
		keyName = "enableFallenTreeSound",
		name = "Enable Sound",
		description = "Plays a Jerma noise when the tree falls",
		position = 1
	)

	default boolean enableFallenTreeSound(){
		return true;
	}

	@ConfigItem(
			keyName = "masterVolume",
			name = "Master Volume",
			description = "Adjust volume of sound",
			position = 2
	)
	default int masterVolume()
	{
		return 50;
	}

}
