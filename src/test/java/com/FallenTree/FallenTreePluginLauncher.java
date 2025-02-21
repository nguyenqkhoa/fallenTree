package com.FallenTree;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class FallenTreePluginLauncher
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(FallenTreePlugin.class);
		RuneLite.main(args);
	}
}