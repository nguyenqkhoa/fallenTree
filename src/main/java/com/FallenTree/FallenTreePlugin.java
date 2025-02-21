package com.FallenTree;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.sound.sampled.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.applet.AudioClip;
import java.io.*;

@Slf4j
@PluginDescriptor(
	name = "Fallen Tree Sound",
	description = "Plays a noise (Jerma sound) when a tree you're currently chopping falls."
)
public class FallenTreePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private FallenTreeConfig config;

	private Clip soundClip;

	private static final int[] TREE_IDS = {
			1276, 1278, 1277, 1280, 3879, 3881, 3882, 3883, 36677, 36672, 36674, 40750, 40752, 1330, 1331, 1332,//logs
			1279, 42393, 42832, 1286, 1290, 1285, 1365, 1383, 1291, 3648, 1318, 1319, 2091, 2092, 4818, 4820, //also logs
			2023, //achey
			10820, 42395, 42831, 8467, //oak
			10819, 10829, 10831, 10833, 8488, //willow
			9036, 36686, 40758, 30445, //teak
			27499, //juni
			10832, 36681, 40754, 8444, //maple
			10821, 10830, //hollow
			9034, 36688, 40760, 30417, //mahog
			3037, //arctic pine
			10822, 36683, 40756, 42391, 8513, //yew
			10834, 8409, //magic
			34284, 34286, 34288, 34290 //redwood
	};


	@Override
	protected void startUp() throws Exception
	{
		log.info("Fallen Tree started!");
		checkForSoundFolder();
		loadSound();
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Fallen Tree stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			//client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Fallen Tree says ", null);
		}
	}

	private void loadSound(){
		try{
			File soundFile = new File(System.getProperty("user.home") + "/.runelite/fallen-tree-sounds/Jerma_noise.wav");
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			soundClip = AudioSystem.getClip();
			soundClip.open(audioInputStream);
		}
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException e){
			log.error("Error loading sound: ", e);
		}
	}

	private void playSound(){
		if (soundClip != null){
			if(soundClip.isRunning()){
				soundClip.stop();
			}
			soundClip.setFramePosition(0);
			setVolume(config.masterVolume());
			soundClip.start();

		}
	}

	private void setVolume(int volume){
		if (soundClip != null){
			float volumeValue = volume / 100.0f;
			FloatControl volumeControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
			volumeControl.setValue(20f * (float) Math.log10(volumeValue));
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event){
		GameObject object = event.getGameObject();
		int objectId = object.getId();

		for (int treeId : TREE_IDS){
			if (objectId == treeId){
				playSound();
				break;
			}
		}
	}

	private void checkForSoundFolder(){
		File soundDirectory = new File(System.getProperty("user.home") + "/.runelite/fallen-tree-sounds");
		File soundFile = new File(soundDirectory, "Jerma_noise.wav");

		if(!soundDirectory.exists()){
			soundDirectory.mkdir();
		}

		if(!soundFile.exists()){
			try(InputStream in = getClass().getResourceAsStream("/Jerma_noise.wav");
				OutputStream out = new FileOutputStream(soundFile)){
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = in.read(buffer)) != -1){
					out.write(buffer, 0, bytesRead);
				}
				log.info(("Copied Jerma sound file to: " + soundFile.getAbsolutePath()));
			}
			catch (IOException e){
				log.error("Jerma has escape containment. ", e );
			}
		}
	}


	@Provides
	FallenTreeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FallenTreeConfig.class);
	}
}
