/*
 *  Copyright 2010 Sikiru.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package com.bramosystems.oss.player.showcase.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * @author Sikiru
 */
public class PlayerOptions extends Composite {

    private static PlayerOptionsUiBinder uiBinder = GWT.create(PlayerOptionsUiBinder.class);

    @UiTemplate("xml/PlayerOptions.ui.xml")
    interface PlayerOptionsUiBinder extends UiBinder<Widget, PlayerOptions> {}

    public PlayerOptions() {
        initWidget(uiBinder.createAndBindUi(this));
    }
}