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

package com.farsitel.qiblacompass.data;

public class DirectionData {
    private double northDirectionFromDeviceHead;
    private double qiblaDirectionFromNorth;

    public double getNorthDirectionFromDeviceHead() {
        return northDirectionFromDeviceHead;
    }

    public void setNorthDirectionFromDeviceHead(double northDirectionDegree) {
        this.northDirectionFromDeviceHead = northDirectionDegree;
    }

    public double getQiblaDirectionFromNorth() {
        return qiblaDirectionFromNorth;
    }

    public void setQiblaDirectionFromNorth(double qiblaDirectionFromNorth) {
        this.qiblaDirectionFromNorth = qiblaDirectionFromNorth;
    }
    
    public double getQiblaDirectionFromDeviceHead(){
        return this.northDirectionFromDeviceHead +this.qiblaDirectionFromNorth;
    }

    public DirectionData(double northDirectionFromDeviceHead,
            double qiblaDirectionFromNorth) {
        super();
        this.northDirectionFromDeviceHead = northDirectionFromDeviceHead;
        this.qiblaDirectionFromNorth = qiblaDirectionFromNorth;
    }

}
