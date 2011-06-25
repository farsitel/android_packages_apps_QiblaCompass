/*
 * Copyright (C) 2011 Iranian Supreme Council of ICT, The FarsiTel Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASICS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.farsitel.qiblacompass.util;

/*
 * This Interface only is responsible for Constants. Each class that wants to access these Constants can implement this class of use the interface with .operator.
 *  Written by: Majid Kalkatehci 
 *  Email: majid@farsitel.com
 */
import android.location.Location;

public interface ConstantUtilInterface {
    // Tag that is used for Log class
    public static final String NAMAZ_LOG_TAG = "namaz";
    public static final String NAMAZ_QIBLA_LOG_TAG = "namaz qibla Manager";
    // Minimum distance or time to update the location of cell phone with GPS
    public static final long MIN_LOCATION_TIME = 60000;
    public static final long MIN_LOCATION_DISTANCE = 2000;

    // These Constants are here for usage of Qibla Activity

    public static final int MENU_HELP = 35;
    public static final int MENU_PREFS = 34;
    public static final int MENU_ABOUT = 33;

    public static final int ROTATE_IMAGES_MESSAGE = 1;
    public static final String QIBLA_BUNDLE_DELTA_KEY = "qiblaDelta";
    public static final String COMPASS_BUNDLE_DELTA_KEY = "compassDelta";
    public static final String IS_QIBLA_CHANGED = "qibla";
    public static final String IS_COMPASS_CHANGED = "compass";
    // end of QiblaActivity Constants

    // QibaManager's constant
    public static final int MESSAGE_ON_DEVICE_HEAD_DIRECTION = 2;
    public static final int MESSAGE_ON_QIBLA_DIRECTION = 3;
    public static final String MESSAGE_NORTH_DIRECTION_ANGEL_KEY = "northDirection";
    public static final String MESSAGE_DEVICE_LOCATION_KEY = "deviceLocation";
    public static final String QIBLA_CHANGED_MAP_KEY = "qibla";
    public static final String NORTH_CHANGED_MAP_KEY = "north";
    public static final double MIN_DEGREE_FROM_NORTH = 3;
    public static final Location previousLocation = null;
    public static final double MIN_DISTANCE_BETWEEN_LOCATIONS = 10000;

}
