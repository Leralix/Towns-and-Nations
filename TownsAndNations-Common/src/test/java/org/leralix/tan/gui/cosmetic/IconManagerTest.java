package org.leralix.tan.gui.cosmetic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.gui.cosmetic.type.CustomMaterialIcon;
import org.leralix.tan.gui.cosmetic.type.IconType;
import org.leralix.tan.gui.cosmetic.type.ItemIconBuilder;
import org.leralix.tan.gui.cosmetic.type.UrlHeadIconType;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class IconManagerTest extends BasicTest {

    private IconManager iconManager;

    @Override
    @BeforeEach
    protected void setUp(){
        super.setUp();
        iconManager = IconManager.getInstance();
    }


    @Test
    void chooseIconBuilderType_url(){

        IconType menuIcon = iconManager.chooseIconBuilderType("https://test.com");

        assertInstanceOf(UrlHeadIconType.class, menuIcon);
    }

    @Test
    void chooseIconBuilderType_itemstack(){

        IconType menuIcon = iconManager.chooseIconBuilderType("minecraft:IRON_ORE");

        assertInstanceOf(ItemIconBuilder.class, menuIcon);
    }

    @Test
    void chooseIconBuilderType_itemstack_with_modelData(){

        IconType menuIcon = iconManager.chooseIconBuilderType("minecraft:EMERALD:101");

        assertInstanceOf(CustomMaterialIcon.class, menuIcon);
    }


}