package DemoBot1;
import discord4j.core.DiscordClientBuilder;
import java.util.*;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import com.sun.jdi.event.Event;
import discord4j.core.object.entity.Member;

import java.nio.ByteBuffer;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Message;

interface Command{
	void execute(MessageCreateEvent event);
}

  
public class Bot {
	
	private static final Map<String,Command> commands = new HashMap<>();
	
	static {
		commands.put("ping", event -> event.getMessage().getChannel().block().createMessage("Namaste").block());
		commands.put("thanks", event -> event.getMessage().getChannel().block().createMessage("You are Welcome").block());
	}
	
	public static void main(String[] args) {
		//create audio player instance to translate URL to AudioTrack instances from lava player
		final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
		//optimizing
		playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
		
		// Allow playerManager to parse remote sources like YouTube links
		AudioSourceManagers.registerRemoteSources(playerManager);
		
		//create audio player on discord to play
		final AudioPlayer player = playerManager.createPlayer();
		
		
		AudioProvider provider = new LavaPlayerAudioProvider(player);
		
		//joining voice chat (^join)
		commands.put("join", event -> {
		    final Member member = event.getMember().orElse(null);
		    if (member != null) {
		        final VoiceState voiceState = member.getVoiceState().block();
		        if (voiceState != null) {
		            final VoiceChannel channel = voiceState.getChannel().block();
		            if (channel != null) {
		                // join returns a VoiceConnection which would be required if we were
		                // adding disconnection features, but for now we are just ignoring it.
		                channel.join(spec -> spec.setProvider(provider)).block();
		            }
		        }
		    }
		});
		
		//play (^play youtube link)
		final TrackScheduler scheduler = new TrackScheduler(player);
		commands.put("play", event -> {
		    final String content = event.getMessage().getContent();
		    final List<String> command = Arrays.asList(content.split(" "));
		    playerManager.loadItem(command.get(1), scheduler);
		});
		
		GatewayDiscordClient client = DiscordClientBuilder.create("NzY0OTE0NTMxMzk4NzEzNDA1.X4NMKw.-E9ciB_FS4jQA13mDWBLbUHeAa4")
				.build()
				.login()
				.block();
		
		client.getEventDispatcher().on(MessageCreateEvent.class)
			.subscribe(event -> {
				final String content = event.getMessage().getContent();
				for(final Map.Entry<String,Command> entry : commands.entrySet()) {
					if(content.startsWith("^"+entry.getKey())) {
						entry.getValue().execute(event);
						break;
					}
				}
			});
		
		
		
		client.onDisconnect().block();
	}
}
