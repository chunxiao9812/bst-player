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

package com.bramosystems.oss.player.external {
    import flash.external.*;
    import flash.events.*;
    import flash.geom.Rectangle;
    import flash.media.ID3Info;
    import mx.core.Application;

    public class EventUtil {
        public static var playerId:String = "";

        public static function fireApplicationInitialized():void {
            ExternalInterface.call("bstSwfMdaInit", playerId);
        }

        public static function fireMediaStateChanged(state:int, playlistIndex:int = -1):void {
            ExternalInterface.call("bstSwfMdaMediaStateChanged", playerId, state, playlistIndex);
        }

        public static function fireLoadingProgress(progress:Number):void {
            ExternalInterface.call("bstSwfMdaLoadingProgress", playerId, progress);
        }

        public static function fireID3Metadata(info:ID3Info):void {
            // parse into CSV like values ...
            // year[$]albumTitle[$]artists[$]comment[$]genre[$]title[$]
            // contentProviders[$]copyright[$]duration[$]hardwareSoftwareRequirements[$]
            // publisher[$]internetStationOwner[$]internetStationName[$]videoWidth[$]videoHeight

            var id3:String = info.year + "[$]" + info.album + "[$]" + info.artist  + "[$]" +
                                info.comment + "[$]" + info.genre + "[$]" + info.songName + "[$]" +
                                info.TOLY + "[$]" + info.TOWN + "[$]" + info.TLEN + "[$]" +
                                info.TSSE + "[$]" + info.TPUB + "[$]" + info.TRSO + "[$]" +
                                info.TRSN + "[$]0[$]0";
            ExternalInterface.call("bstSwfMdaMetadata", playerId, id3);
        }

        public static function fireVideoMetadata(duration:Number, info:String, width:Number, height:Number):void {
            // parse into CSV like values ...
            // year[$]albumTitle[$]artists[$]comment[$]genre[$]title[$]
            // contentProviders[$]copyright[$]duration[$]hardwareSoftwareRequirements[$]
            // publisher[$]internetStationOwner[$]internetStationName[$]videoWidth[$]videoHeight

            var id3:String = "0[$] [$] [$] [$] [$] [$] [$] [$]" + (duration * 1000) +
                             "[$]" + info + "[$] [$] [$] [$]" + width + "[$]" + height;
            ExternalInterface.call("bstSwfMdaMetadata", playerId, id3);
        }

        public static function fireMouseDownEvent(event:MouseEvent):void {
            ExternalInterface.call("bstSwfMdaEvent", playerId, 1, event.buttonDown,
                event.altKey, event.ctrlKey, event.shiftKey, false, //event.commandKey,
                event.stageX, event.stageY);
        }
        public static function fireMouseUpEvent(event:MouseEvent):void {
            ExternalInterface.call("bstSwfMdaEvent", playerId, 2, event.buttonDown,
                event.altKey, event.ctrlKey, event.shiftKey, false, //event.commandKey,
                event.stageX, event.stageY);
        }
        public static function fireMouseMoveEvent(event:MouseEvent):void {
//            var rect:Rectangle = Application.application.systemManager.topLevelSystemManager.screen;
            var rx:Number = Application.application.systemManager.topLevelSystemManager.stage.mouseX;
            var ry:Number = Application.application.systemManager.topLevelSystemManager.stage.mouseY;
            ExternalInterface.call("bstSwfMdaEvent2", playerId, 3, event.buttonDown,
                event.altKey, event.ctrlKey, event.shiftKey, false, //event.commandKey,
//                event.stageX, event.stageY, rect.x, rect.y);
                event.stageX, event.stageY, rx, ry);
        }
        public static function fireClickEvent(event:MouseEvent):void {
            ExternalInterface.call("bstSwfMdaEvent", playerId, 10, event.buttonDown,
                event.altKey, event.ctrlKey, event.shiftKey, false, //event.commandKey,
                event.stageX, event.stageY);
        }
        public static function fireDoubleClickEvent(event:MouseEvent):void {
            ExternalInterface.call("bstSwfMdaEvent", playerId, 11, event.buttonDown,
                event.altKey, event.ctrlKey, event.shiftKey, false, //event.commandKey,
                event.stageX, event.stageY);
        }
        public static function fireKeyDownEvent(event:KeyboardEvent):void {
            Log.info("Firing KeyDown Event : " + event.charCode);
            ExternalInterface.call("bstSwfMdaEvent", playerId, 20, false, //event.buttonDown,
                event.altKey, event.ctrlKey, event.shiftKey, false, //event.commandKey,
                event.keyCode, event.charCode);
            ExternalInterface.call("bstSwfMdaEvent", playerId, 21, false, //event.buttonDown,
                event.altKey, event.ctrlKey, event.shiftKey, false, //event.commandKey,
                event.keyCode, event.charCode);
        }
        public static function fireKeyUpEvent(event:KeyboardEvent):void {
            ExternalInterface.call("bstSwfMdaEvent", playerId, 22, false, //event.buttonDown,
                event.altKey, event.ctrlKey, event.shiftKey, false, //event.commandKey,
                event.keyCode, event.charCode);
        }
    }
}