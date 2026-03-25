import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Manages all game sound effects using a singleton pattern.
 *
 * Sound file paths are defined as public constants below.
 * Replace each placeholder path with the actual .wav file path once
 * your audio assets are ready.  All sounds play asynchronously so they
 * never block the Event Dispatch Thread.  Missing or unplayable files
 * are silently ignored, so the game always remains fully functional
 * without audio.
 */
public class SoundManager
{
	// ── Sound file paths – replace with actual .wav paths ────────────────
	public static final String TILE_CLICK_PATH    = "sounds/tile_click.wav";
	public static final String BOMB_EXPLODE_PATH  = "sounds/bomb_explode.wav";
	public static final String BOMB_TICK_PATH     = "sounds/bomb_tick.wav";
	public static final String GAME_WON_PATH      = "sounds/game_won.wav";
	public static final String SLIDER_CHANGE_PATH = "sounds/slider_change.wav";
	// ─────────────────────────────────────────────────────────────────────

	private static SoundManager instance;
	private boolean soundEnabled = true;

	private SoundManager() {}

	/** Return the single shared instance (lazy initialised). */
	public static SoundManager getInstance()
	{
		if (instance == null)
		{
			instance = new SoundManager();
		}
		return instance;
	}

	// ── Convenience play methods ──────────────────────────────────────────

	/** Play the tile-click sound effect. */
	public void playTileClick()    { playSound(TILE_CLICK_PATH);    }

	/** Play the bomb-explosion sound effect. */
	public void playBombExplode()  { playSound(BOMB_EXPLODE_PATH);  }

	/** Play one bomb-countdown tick sound. */
	public void playBombTick()     { playSound(BOMB_TICK_PATH);     }

	/** Play the game-won fanfare. */
	public void playGameWon()      { playSound(GAME_WON_PATH);      }

	/** Play the grid-slider scroll sound. */
	public void playSliderChange() { playSound(SLIDER_CHANGE_PATH); }

	// ── Enable / disable ──────────────────────────────────────────────────

	/** Enable or disable all sound effects. */
	public void setSoundEnabled(boolean enabled)
	{
		this.soundEnabled = enabled;
	}

	/** @return {@code true} if sound effects are currently enabled. */
	public boolean isSoundEnabled()
	{
		return soundEnabled;
	}

	// ── Core playback ─────────────────────────────────────────────────────

	/**
	 * Load and play the .wav file at {@code filePath} on a background thread.
	 * Fails silently if the file does not exist or cannot be decoded.
	 */
	private void playSound(String filePath)
	{
		if (!soundEnabled)
		{
			return;
		}

		// Run on a dedicated thread so playback never blocks the EDT
		Thread t = new Thread(() ->
		{
			try
			{
				File file = new File(filePath);
				if (!file.exists())
				{
					return; // silently skip if audio file is not yet present
				}

				AudioInputStream ais = AudioSystem.getAudioInputStream(file);
				Clip clip = AudioSystem.getClip();
				clip.open(ais);

				// Release the Clip resource as soon as playback finishes
				clip.addLineListener(event ->
				{
					if (event.getType() == LineEvent.Type.STOP)
					{
						clip.close();
					}
				});

				clip.start();
			}
			catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
			{
				// Sound system unavailable – fail silently
			}
		}, "sound-player");

		t.setDaemon(true);
		t.start();
	}
}
