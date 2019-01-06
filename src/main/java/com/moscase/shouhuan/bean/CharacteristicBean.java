package com.moscase.shouhuan.bean;

import java.util.UUID;

/**
 * Created by lxj on 2017/3/22.
 */

public class CharacteristicBean {
    private UUID Service_UUID;
    private UUID Characteristic_UUID;

    public CharacteristicBean(UUID Service_UUID, UUID Characteristic_UUID) {
        this.Characteristic_UUID = Characteristic_UUID;
        this.Service_UUID = Service_UUID;
    }

    public CharacteristicBean(){}

    public void setService_UUID(UUID service_UUID) {
        Service_UUID = service_UUID;
    }

    public void setCharacteristic_UUID(UUID characteristic_UUID) {
        Characteristic_UUID = characteristic_UUID;
    }

    public UUID getService_UUID() {
        return Service_UUID;
    }

    public UUID getCharacteristic_UUID() {
        return Characteristic_UUID;
    }
}
