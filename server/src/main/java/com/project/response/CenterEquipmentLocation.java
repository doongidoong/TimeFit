package com.project.response;

import com.project.domain.CenterEquipment;
import com.project.domain.Equipment;
import lombok.Getter;


@Getter
public class CenterEquipmentLocation {

    private final Long id;
    private final Equipment equipment;
    private final Long xLoc;
    private final Long yLoc;
    private final Long height;
    private final Long width;

    public CenterEquipmentLocation(CenterEquipment equip){
        this.id = equip.getId();
        this.equipment = equip.getEquipment();
        this.xLoc = equip.getXLoc();
        this.yLoc = equip.getYLoc();
        this.height = equip.getHeight();
        this.width = equip.getWidth();
    }
}
