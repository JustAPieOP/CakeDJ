package me.justapie.cakedj.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;

public class TrackScheduler extends AudioEventAdapter {
    public final LinkedList<AudioTrack> queue;
    public final LinkedList<AudioTrack> previous;
    private final AudioPlayer audioPlayer;

    public boolean isInLoop = false;
    public boolean isEarrape = false;
    public boolean isNightcore = false;

    public TrackScheduler(AudioPlayer audioPlayer) {
        this.queue = new LinkedList<>();
        this.previous = new LinkedList<>();
        this.audioPlayer = audioPlayer;
    }

    public EmbedBuilder getNowPlaying() {
        AudioTrack playing = audioPlayer.getPlayingTrack();
        String desc = "Now playing **" + playing.getInfo().title + "** \nRequested by **" + playing.getUserData(String.class) + "**";
        return new EmbedBuilder()
                .setColor(Color.GREEN)
                .setDescription(desc);
    }

    public void enqueue(AudioTrack track) {
        if (!this.audioPlayer.startTrack(track, true))
            this.queue.add(track);
    }

    public void endTrack() {
        this.isInLoop = false;
        AudioTrack track = this.queue.poll();
        AudioTrack playing = this.audioPlayer.getPlayingTrack();
        if (playing != null) this.previous.add(playing.makeClone());
        audioPlayer.setPaused(false);
        audioPlayer.startTrack(track, false);
    }

    public void clear() {
        this.queue.clear();
        this.previous.clear();
        endTrack();
    }

    public void previous() {
        AudioTrack playing = this.audioPlayer.getPlayingTrack();
        if (playing != null) this.queue.addFirst(playing.makeClone());
        this.audioPlayer.startTrack(this.previous.poll(), false);
    }

    public void swap(int a, int b) throws IndexOutOfBoundsException {
        Collections.swap(this.queue, a, b);
    }

    public void restart() {
        this.audioPlayer.startTrack(this.audioPlayer.getPlayingTrack().makeClone(), false);
    }

    public void shuffle() {
        Collections.shuffle(this.queue);
    }

    public void setInLoop(boolean b) {
        this.isInLoop = b;
    }

    public void setNightcore(boolean b) {
        this.isNightcore = b;
    }

    public void setEarrape(boolean b) {
        this.isEarrape = b;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            if (isInLoop) this.queue.add(track.makeClone());
            else this.previous.add(track.makeClone());
            audioPlayer.startTrack(this.queue.poll(), false);
        }
    }
}
