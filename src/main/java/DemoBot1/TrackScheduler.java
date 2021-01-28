package DemoBot1;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public final class TrackScheduler implements AudioLoadResultHandler{
	
	private final AudioPlayer player;
	
	public TrackScheduler(final AudioPlayer player) {
		this.player = player;
	}
	
	@Override
	public void trackLoaded(final AudioTrack track) {
		// TODO Auto-generated method stub
		player.playTrack(track);
	}

	@Override
	public void playlistLoaded(final AudioPlaylist playlist) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noMatches() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadFailed(FriendlyException exception) {
		// TODO Auto-generated method stub
		
	}

}
