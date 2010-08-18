/*
 * Copyright 2009 Sikirulai Braheem
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.bramosystems.oss.player.core.client.ui;

import com.bramosystems.oss.player.core.client.*;
import com.bramosystems.oss.player.core.client.MediaInfo.MediaInfoKey;
import com.bramosystems.oss.player.core.client.impl.BeforeUnloadCallback;
import com.bramosystems.oss.player.core.client.impl.LoopManager;
import com.bramosystems.oss.player.core.client.impl.VLCPlayerImpl;
import com.bramosystems.oss.player.core.client.impl.VLCStateManager;
import com.bramosystems.oss.player.core.client.impl.PlayerWidget;
import com.bramosystems.oss.player.core.client.skin.CustomPlayerControl;
import com.bramosystems.oss.player.core.event.client.*;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Widget to embed VLC Media Player&trade; plugin.
 *
 * <h3>Usage Example</h3>
 *
 * <p>
 * <code><pre>
 * SimplePanel panel = new SimplePanel();   // create panel to hold the player
 * Widget player = null;
 * try {
 *      // create the player
 *      player = new VLCPlayer("www.example.com/mediafile.vob");
 * } catch(LoadException e) {
 *      // catch loading exception and alert user
 *      Window.alert("An error occured while loading");
 * } catch(PluginVersionException e) {
 *      // catch plugin version exception and alert user, possibly providing a link
 *      // to the plugin download page.
 *      player = new HTML(".. some nice message telling the user to download plugin first ..");
 * } catch(PluginNotFoundException e) {
 *      // catch PluginNotFoundException and tell user to download plugin, possibly providing
 *      // a link to the plugin download page.
 *      player = new HTML(".. another kind of message telling the user to download plugin..");
 * }
 *
 * panel.setWidget(player); // add player to panel.
 * </pre></code>
 *
 * @author Sikirulai Braheem
 */
public class VLCPlayer extends AbstractMediaPlayer implements PlaylistSupport {

    private VLCPlayerImpl impl;
    private PlayerWidget playerWidget;
    private VLCStateManager stateHandler;
    private String playerId, _width, _height;
    private Logger logger;
    private boolean isEmbedded, autoplay, resizeToVideoSize;
    private CustomPlayerControl control;
    private LoopManager loopManager;

    VLCPlayer() throws PluginNotFoundException, PluginVersionException {
        PluginVersion req = Plugin.VLCPlayer.getVersion();
        PluginVersion v = PlayerUtil.getVLCPlayerPluginVersion();
        if (v.compareTo(req) < 0) {
            throw new PluginVersionException(Plugin.VLCPlayer, req.toString(), v.toString());
        }

        playerId = DOM.createUniqueId().replace("-", "");

        loopManager = new LoopManager(true, new LoopManager.LoopCallback() {

            @Override
            public void playNextItem() throws PlayException {
                stateHandler.getPlaylistManager().playNext();
            }

            @Override
            public void onLoopFinished() {
                firePlayStateEvent(PlayStateEvent.State.Finished, 0);
            }

            @Override
            public void loopForever(boolean loop) {
            }

            @Override
            public void playNextLoop() {
                try {
                    stateHandler.getPlaylistManager().playNext(true);
                } catch (PlayException ex) {
                    fireDebug(ex.getMessage());
                }
            }
        });
        stateHandler = new VLCStateManager(new VLCStateManager.VLCStateCallback() {

///           @Override
            public void onLoadingComplete() {
                fireLoadingProgress(1.0);
            }

            @Override
            public void onIdle() {}

            @Override
            public void onOpening(int index) {
                fireDebug("Opening playlist item #" + index);
            }

            @Override
            public void onBuffering(boolean started) {
                firePlayerStateEvent(started ? PlayerStateEvent.State.BufferingStarted
                        : PlayerStateEvent.State.BufferingFinished);
            }

            @Override
            public void onPlaying(int index) {
                fireDebug("Playback started - '" + stateHandler.getPlaylistManager().getCurrentItem() + "'");
                firePlayStateEvent(PlayStateEvent.State.Started, index);
            }

            @Override
            public void onPaused(int index) {
                fireDebug("Playback paused");
                firePlayStateEvent(PlayStateEvent.State.Paused, index);
            }

            @Override
            public void onError(String message) {
                fireError(message);
            }

            @Override
            public void onInfo(String message) {
                fireDebug(message);
            }

            @Override
            public void onEndReached(int index) {
                fireDebug("Playback complete - '" + stateHandler.getPlaylistManager().getCurrentItem() + "'");
                loopManager.notifyPlayFinished();
            }

            @Override
            public void onStopped(int index) {
                firePlayStateEvent(PlayStateEvent.State.Stopped, index);
                fireDebug("Playback stopped");
            }

            @Override
            public void onMediaInfo(MediaInfo info) {
                fireDebug("MediaInfo available");
                fireMediaInfoAvailable(info);
            }
        },
                new VLCStateManager.VLCPlayerImplCallback() {

            @Override
            public VLCPlayerImpl getImpl() {
                return impl;
            }
        });
    }

    /**
     * Constructs <code>VLCPlayer</code> with the specified {@code height} and
     * {@code width} to playback media located at {@code mediaURL}. Media playback
     * begins automatically if {@code autoplay} is {@code true}.
     *
     * <p> {@code height} and {@code width} are specified as CSS units. A value of {@code null}
     * for {@code height} or {@code width} puts the player in embedded mode.  When in embedded mode,
     * the player is made invisible on the page and media state events are propagated to registered
     * listeners only.  This is desired especially when used with custom sound controls.  For custom
     * video control, specify valid CSS values for {@code height} and {@code width} but hide the
     * player controls with {@code setControllerVisible(false)}.
     *
     * @param mediaURL the URL of the media to playback
     * @param autoplay {@code true} to play playing automatically, {@code false} otherwise
     * @param height the height of the player
     * @param width the width of the player.
     *
     * @throws LoadException if an error occurs while loading the media.
     * @throws PluginVersionException if the required VLCPlayer plugin version is not installed on the client.
     * @throws PluginNotFoundException if the VLCPlayer plugin is not installed on the client.
     */
    public VLCPlayer(String mediaURL, final boolean autoplay, String height, String width)
            throws LoadException, PluginVersionException, PluginNotFoundException {
        this();

        this.autoplay = autoplay;
        _height = height;
        _width = width;

        FlowPanel panel = new FlowPanel();
        initWidget(panel);

        playerWidget = new PlayerWidget(Plugin.VLCPlayer, playerId, mediaURL, autoplay,
                new BeforeUnloadCallback() {

                    @Override
                    public void onBeforeUnload() {
                        stateHandler.close();
                    }
                });
//        playerWidget.getElement().getStyle().setProperty("backgroundColor", "#000000");   // IE workaround
        panel.add(playerWidget);

        isEmbedded = (height == null) || (width == null);
        if (!isEmbedded) {
            control = new CustomPlayerControl(this);
            panel.add(control);

            logger = new Logger();
            logger.setVisible(false);
            panel.add(logger);

            addDebugHandler(new DebugHandler() {

                @Override
                public void onDebug(DebugEvent event) {
                    logger.log(event.getMessage(), false);
                }
            });
            addMediaInfoHandler(new MediaInfoHandler() {

                @Override
                public void onMediaInfoAvailable(MediaInfoEvent event) {
                    MediaInfo info = event.getMediaInfo();
                    if (info.getAvailableItems().contains(MediaInfoKey.VideoHeight)
                            || info.getAvailableItems().contains(MediaInfoKey.VideoWidth)) {
                        checkVideoSize(Integer.parseInt(info.getItem(MediaInfoKey.VideoHeight)),
                                Integer.parseInt(info.getItem(MediaInfoKey.VideoWidth)));
                    }
                    logger.log(event.getMediaInfo().asHTMLString(), true);
                }
            });
        } else {
            _height = "0px";
            _width = "0px";
        }

        fireDebug("VLC Media Player plugin");
        stateHandler.getPlaylistManager().addToPlaylist(mediaURL);
    }

    /**
     * Constructs <code>VLCPlayer</code> to automatically playback media located at
     * {@code mediaURL} using the default height of 20px and width of 100%.
     *
     * @param mediaURL the URL of the media to playback
     *
     * @throws LoadException if an error occurs while loading the media.
     * @throws PluginVersionException if the required VLCPlayer plugin version is not installed on the client.
     * @throws PluginNotFoundException if the VLCPlayer plugin is not installed on the client.
     *
     */
    public VLCPlayer(String mediaURL) throws LoadException, PluginVersionException,
            PluginNotFoundException {
        this(mediaURL, true, "0px", "100%");
    }

    /**
     * Constructs <code>VLCPlayer</code> to playback media located at {@code mediaURL}
     * using the default height of 20px and width of 100%. Media playback begins
     * automatically if {@code autoplay} is {@code true}.
     *
     * @param mediaURL the URL of the media to playback
     * @param autoplay {@code true} to play playing automatically, {@code false} otherwise
     *
     * @throws LoadException if an error occurs while loading the media.
     * @throws PluginVersionException if the required VLCPlayer plugin version is not installed on the client.
     * @throws PluginNotFoundException if the VLCPlayer plugin is not installed on the client.
     */
    public VLCPlayer(String mediaURL, boolean autoplay) throws LoadException,
            PluginVersionException, PluginNotFoundException {
        this(mediaURL, autoplay, "0px", "100%");
    }

    /**
     * Overridden to register player for plugin events
     */
    @Override
    protected final void onLoad() {
        playerWidget.setSize(_width, _height);
        setWidth(_width);

        impl = VLCPlayerImpl.getPlayer(playerId);
        fireDebug("Version : " + impl.getPluginVersion());
        stateHandler.start();   // start state pooling ...

        // fire player ready ...
        firePlayerStateEvent(PlayerStateEvent.State.Ready);

        // and play if required ...
        if (autoplay) {
            stateHandler.getPlaylistManager().play(0);
        }
    }

    @Override
    public void loadMedia(String mediaURL) throws LoadException {
        checkAvailable();
        stateHandler.getPlaylistManager().clearPlaylist();
        stateHandler.getPlaylistManager().addToPlaylist(mediaURL);
    }

    @Override
    public void playMedia() throws PlayException {
        checkAvailable();
        stateHandler.getPlaylistManager().play();
    }

    @Override
    public void play(int index) throws IndexOutOfBoundsException {
        checkAvailable();
        stateHandler.getPlaylistManager().play(index);
    }

    @Override
    public void playNext() throws PlayException {
        checkAvailable();
        stateHandler.getPlaylistManager().playNext();
    }

    @Override
    public void playPrevious() throws PlayException {
        checkAvailable();
        stateHandler.getPlaylistManager().playPrevious();
    }

    @Override
    public void stopMedia() {
        checkAvailable();
        stateHandler.getPlaylistManager().stop();
    }

    @Override
    public void pauseMedia() {
        checkAvailable();
        impl.togglePause();
    }

    @Override
    public long getMediaDuration() {
        checkAvailable();
        return (long) impl.getDuration();
    }

    @Override
    public double getPlayPosition() {
        checkAvailable();
        return impl.getTime();
    }

    @Override
    public void setPlayPosition(double position) {
        checkAvailable();
        impl.setTime(position);
    }

    @Override
    public double getVolume() {
        checkAvailable();
        return impl.getVolume();
    }

    @Override
    public void setVolume(double volume) {
        checkAvailable();
        impl.setVolume(volume);
        fireDebug("Volume set to " + (volume * 100) + "%");
    }

    private void checkAvailable() {
        if (!isPlayerOnPage(playerId)) {
            String message = "Player not available, create an instance";
            fireDebug(message);
            throw new IllegalStateException(message);
        }
    }

    @Override
    public void showLogger(boolean enable) {
        if (!isEmbedded) {
            logger.setVisible(enable);
        }
    }

    @Override
    public void setControllerVisible(boolean show) {
        if (!isEmbedded) {
            control.setVisible(show);
        }
    }

    @Override
    public boolean isControllerVisible() {
        return control.isVisible();
    }

    @Override
    public int getLoopCount() {
        checkAvailable();
        return loopManager.getLoopCount();
    }

    /**
     * Sets the number of times the current media file should repeat playback before stopping.
     *
     * <p>As of version 1.0, if this player is not available on the panel, this method
     * call is added to the command-queue for later execution.
     */
    @Override
    public void setLoopCount(final int loop) {
        if (isPlayerOnPage(playerId)) {
            loopManager.setLoopCount(loop);
        } else {
            addToPlayerReadyCommandQueue("loopcount", new Command() {

                @Override
                public void execute() {
                    loopManager.setLoopCount(loop);
                }
            });
        }
    }

    @Override
    public void addToPlaylist(String mediaURL) {
        stateHandler.getPlaylistManager().addToPlaylist(mediaURL);
    }

    @Override
    public boolean isShuffleEnabled() {
        checkAvailable();
        return stateHandler.getPlaylistManager().isShuffleEnabled();
    }

    @Override
    public void setShuffleEnabled(final boolean enable) {
        if (isPlayerOnPage(playerId)) {
            stateHandler.getPlaylistManager().setShuffleEnabled(enable);
        } else {
            addToPlayerReadyCommandQueue("shuffle", new Command() {

                @Override
                public void execute() {
                    stateHandler.getPlaylistManager().setShuffleEnabled(enable);
                }
            });
        }
    }

    @Override
    public void removeFromPlaylist(int index) {
        checkAvailable();
        stateHandler.getPlaylistManager().removeFromPlaylist(index);
    }

    @Override
    public void clearPlaylist() {
        checkAvailable();
        stateHandler.getPlaylistManager().clearPlaylist();
    }

    @Override
    public int getPlaylistSize() {
        checkAvailable();
        return impl.getPlaylistCount();
    }

    /**
     * Sets the audio channel mode of the player
     * 
     * <p>Use {@linkplain #getAudioChannelMode()} to check if setting of the audio channel
     * is succeessful
     *
     * @param mode the audio channel mode
     * @see #getAudioChannelMode()
     */
    public void setAudioChannelMode(AudioChannelMode mode) {
        checkAvailable();
        impl.setAudioChannelMode(mode.ordinal() + 1);
    }

    /**
     * Gets the current audio channel mode of the player
     *
     * @return the current mode of the audio channel
     * @see #setAudioChannelMode(AudioChannelMode)
     */
    public AudioChannelMode getAudioChannelMode() {
        checkAvailable();
        return AudioChannelMode.values()[impl.getAudioChannelMode() - 1];
    }

    @Override
    public int getVideoHeight() {
        checkAvailable();
        return Integer.parseInt(impl.getVideoHeight());
    }

    @Override
    public int getVideoWidth() {
        checkAvailable();
        return Integer.parseInt(impl.getVideoWidth());
    }

    public void toggleFullScreen() {
        checkAvailable();
        impl.toggleFullScreen();
    }

    @Override
    public void setRate(final double rate) {
        if (isPlayerOnPage(playerId)) {
            impl.setRate(rate);
        } else {
            addToPlayerReadyCommandQueue("rate", new Command() {

                @Override
                public void execute() {
                    impl.setRate(rate);
                }
            });
        }
    }

    @Override
    public double getRate() {
        checkAvailable();
        return impl.getRate();
    }

    /*
     * TODO:// check up aspect ratio later...
    public AspectRatio getAspectRatio() {
    checkAvailable();
    if (impl.hasVideo(playerId)) {
    return AspectRatio.parse(impl.getAspectRatio(playerId));
    } else {
    throw new IllegalStateException("No video input can be found");
    }
    }

    public void setAspectRatio(AspectRatio aspectRatio) {
    checkAvailable();
    if (impl.hasVideo(playerId)) {
    impl.setAspectRatio(playerId, aspectRatio.toString());
    } else {
    throw new IllegalStateException("No video input can be found");
    }
    }
     */
    @Override
    public void setResizeToVideoSize(boolean resize) {
        resizeToVideoSize = resize;
        if (isPlayerOnPage(playerId)) {
            // if player is on panel now update its size, otherwise
            // allow it to be handled by the MediaInfoHandler...
            checkVideoSize(getVideoHeight(), getVideoWidth());
        }
    }

    @Override
    public boolean isResizeToVideoSize() {
        return resizeToVideoSize;
    }

    private void checkVideoSize(int vidHeight, int vidWidth) {
        String _h = _height, _w = _width;
        if (vidHeight == 0) {
//            _h = "0px";
        }

        if (resizeToVideoSize) {
            if ((vidHeight > 0) && (vidWidth > 0)) {
                // adjust to video size ...
                fireDebug("Resizing Player : " + vidWidth + " x " + vidHeight);
                _h = vidHeight + "px";
                _w = vidWidth + "px";
            }
        }

        playerWidget.setSize(_w, _h);
        setWidth(_w);

        if (!_height.equals(_h) && !_width.equals(_w)) {
            firePlayerStateEvent(PlayerStateEvent.State.DimensionChangedOnVideo);
        }
    }

    /**
     * An enum of Audio Channel modes for VLC Media Player&trade;
     */
    public static enum AudioChannelMode {

        /**
         * Stereo mode
         */
        Stereo,
        /**
         * Reverse Stereo mode
         */
        ReverseStereo,
        /**
         * Left only mode
         */
        Left,
        /**
         * Right only mode
         */
        Right,
        /**
         * Dolby mode
         */
        Dolby
    }
}
