# Getting Started with BST Player #

## Note: ##
The information in this document applies to Version 0.6 and below.  Check [here](http://oss.bramosystems.com/bst-player/getting-started.html) for Version 1.0 or later.

## Overview ##
BST Player API makes adding Windows Media Player, QuickTime player and Flash player plugins to GWT applications much like other GWT widgets.

The API features a set of classes and interfaces that presents a consistent means of interacting with the supported player plugins.  Therefore, knowing how to use one player widget means one is well at home with other player widgets.

Each player widget exposes methods to load media files, initiate playback, pause, stop, change playback position and volume.  General media plugin events such as loading progress, playback started, playback finished, IO errors are also supported via event listeners.

The API also features plugin detection, which is central to the smooth operation of the library as well as classes to facilitate the development of custom player controls (skins).  Exceptions are thrown in the absence of a required plugin, hence allowing you to display an alternative message (or widgets).

The following sections describe the basics of using BST Player in GWT applications.


## First Step ##
As you might have guessed, the first step is to download the library and add it to your application classpath.  Then ensure your application module inherits the  `com.bramosystems.gwt.player.BSTPlayer` module.


## Embedding Windows Media Player ##
The **WinMediaPlayer** class in the _com.bramosystems.gwt.player.client.ui_ package wraps the Windows Media Player plugin as a GWT widget.  The following code block describes an example:

```
 SimplePanel panel = new SimplePanel();   // create panel to hold the player
 AbstractMediaPlayer player = null;
 try {
      // create the player, specifing URL of media
      player = new WinMediaPlayer("www.example.com/mediafile.wma");

      panel.setWidget(player); // add player to panel.
 } catch(LoadException e) {
      // catch loading exception and alert user
      Window.alert("An error occured while loading");
 } catch(PluginVersionException e) {
      // required plugin version is not available, alert user possibly providing a link
      // to the plugin download page.
      panel.setWidget(new HTML(".. some nice message telling the user to download plugin first .."));
 } catch(PluginNotFoundException e) {
      // catch PluginNotFoundException and tell user to download plugin, possibly providing
      // a link to the plugin download page.
      panel.setWidget(new HTML(".. another nice message telling the user to download plugin.."));
 }
```

The above code adds a Windows Media Player object to the panel and load the media at the specified URL.  Controlling the plugin is straight forward as shown below:

```
player.playMedia();  // starts playback
player.pauseMedia();  // pauses playback
player.stopMedia();  // stops playback
player.setVolume(0.8); // sets the playback volume to 80% of maximum
```

Refer to the API documentation for details.

**_NOTE:- The WinMediaPlayer class requires Windows Media Player 7 or later_**


## Working with QuickTime Player ##
Similar to the WinMediaPlayer class, the **QuickTimePlayer** class in the _com.bramosystems.gwt.player.client.ui_ package wraps the QuickTime plugin.  The following code block describes an example:

```
 SimplePanel panel = new SimplePanel();   // create panel to hold the player
 AbstractMediaPlayer player = null;
 try {
      // create the player, specifing URL of media
      player = new QuickTimePlayer("www.example.com/mediafile.mp3");

      panel.setWidget(player); // add player to panel.
 } catch(LoadException e) {
      // catch loading exception and alert user
      Window.alert("An error occured while loading");
 } catch(PluginVersionException e) {
      // required plugin version is not available, alert user possibly providing a link
      // to the plugin download page.
      panel.setWidget(new HTML(".. some nice message telling the user to download plugin first .."));
 } catch(PluginNotFoundException e) {
      // catch PluginNotFoundException and tell user to download plugin, possibly providing
      // a link to the plugin download page.
      panel.setWidget(new HTML(".. another nice message telling the user to download plugin.."));
 }
```

**_NOTE:- The QuickTimePlayer class requires QuickTime 7.2.1 or later_**


## Playing MP3 files with Flash plugin ##
The **FlashMP3Player** class in the _com.bramosystems.gwt.player.client.ui_ package wraps the Flash player plugin.  However, the current implementation only supports MP3 sound playback.  The following code block describes an example:

```
 SimplePanel panel = new SimplePanel();   // create panel to hold the player
 AbstractMediaPlayer player = null;
 try {
      // create the player, specifing URL of media
      player = new FlashMP3Player("www.example.com/mediafile.mp3");

      panel.setWidget(player); // add player to panel.
 } catch(LoadException e) {
      // catch loading exception and alert user
      Window.alert("An error occured while loading");
 } catch(PluginVersionException e) {
      // required plugin version is not available, alert user possibly providing a link
      // to the plugin download page.
      panel.setWidget(new HTML(".. some nice message telling the user to download plugin first .."));
 } catch(PluginNotFoundException e) {
      // catch PluginNotFoundException and tell user to download plugin, possibly providing
      // a link to the plugin download page.
      panel.setWidget(new HTML(".. another nice message telling the user to download plugin.."));
 }
```

**_NOTE:- Adobe Flash 9.0 or later is required_**


## Conclusion ##
With a simple set of APIs, BST Player attempts to provide an abstraction of the popular media player plugins available on the web, therefore making media playback control much more fun filled !!!

Happy coding.