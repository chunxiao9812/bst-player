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

package com.bramosystems.gwt.player.showcase.client;

import com.bramosystems.gwt.player.client.LoadException;
import com.bramosystems.gwt.player.client.PlayerUtil;
import com.bramosystems.gwt.player.client.Plugin;
import com.bramosystems.gwt.player.client.PluginNotFoundException;
import com.bramosystems.gwt.player.client.PluginVersionException;
import com.bramosystems.gwt.player.client.ui.skin.Capsule;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

/**
 *
 * @author Sikirulai Braheem <sbraheem at gmail.com>
 */
public class DynaShowcase implements Case {
    private VerticalPanel vp;
    private Capsule p1, p2, p3, p4;

    public DynaShowcase() {
        vp = new VerticalPanel();
        vp.setSpacing(20);

        vp.add(new Label("Using custom player with an embedded Windows Media Player plugin"));
        Widget wmp = null;
        try {
            p1 = new Capsule(Plugin.WinMediaPlayer, GWT.getHostPageBaseURL() + "media/applause.mp3", false);
            wmp = p1;
            wmp.setWidth("80%");
        } catch (LoadException ex) {
            Window.alert("Load exp");
        } catch (PluginVersionException ex) {
            wmp = PlayerUtil.getMissingPluginNotice(Plugin.WinMediaPlayer);
        } catch (PluginNotFoundException ex) {
            wmp = PlayerUtil.getMissingPluginNotice(Plugin.WinMediaPlayer);
        }
        vp.add(wmp);

        vp.add(new Label("A custom player using Adobe Flash for playback"));
        Widget wmp2 = null;
        try {
            p2 = new Capsule(Plugin.FlashMP3Player, GWT.getHostPageBaseURL() + "media/applause.mp3", false);
            wmp2 = p2;
            wmp2.setWidth("80%");
        } catch (LoadException ex) {
            Window.alert("Load exp");
        } catch (PluginVersionException ex) {
            wmp2 = PlayerUtil.getMissingPluginNotice(Plugin.FlashMP3Player);
        } catch (PluginNotFoundException ex) {
            wmp2 = PlayerUtil.getMissingPluginNotice(Plugin.FlashMP3Player);
        }
        vp.add(wmp2);

        vp.add(new Label("A custom player using QuickTime plugin"));
        Widget wmp3 = null;
        try {
            p3 = new Capsule(Plugin.QuickTimePlayer, GWT.getHostPageBaseURL() + "media/applause.mp3", false);
            wmp3 = p3;
            wmp3.setWidth("80%");
        } catch (LoadException ex) {
            Window.alert("Load exp");
        } catch (PluginVersionException ex) {
            wmp3 = PlayerUtil.getMissingPluginNotice(Plugin.QuickTimePlayer);
        } catch (PluginNotFoundException ex) {
            wmp3 = PlayerUtil.getMissingPluginNotice(Plugin.QuickTimePlayer);
        }
        vp.add(wmp3);

        vp.add(new Label("A custom player using dynamically determined plugin for playback"));
        Widget wmp4 = null;
        try {
            p4 = new Capsule(GWT.getHostPageBaseURL() + "media/applause.mp3", false);
            wmp4 = p4;
            wmp4.setWidth("80%");
        } catch (LoadException ex) {
            Window.alert("Load exp");
        } catch (PluginVersionException ex) {
            wmp4 = new HTML(wmpPluginVersion);
        } catch (PluginNotFoundException ex) {
            wmp4 = new Label("No sound plugin could be found");
        }
        vp.add(wmp4);
    }

    public String getSummary() {
        return "Using custom players is quite easy too.";
    }

    public Widget getContentWidget() {
        return vp;
    }

    public void stopAllPlayers() {
        if (p1 != null) {
            p1.stopMedia();
        }
        if (p2 != null) {
            p2.stopMedia();
        }
        if (p3 != null) {
            p3.stopMedia();
        }
        if (p4 != null) {
            p4.stopMedia();
        }
    }

}