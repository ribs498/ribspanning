package net.ribs.ribspanning;

import net.minecraft.resources.ResourceLocation;

public class Reference
{
	public static final String MOD_ID = "ribspanning";

    public static ResourceLocation id(String string) {
        return new ResourceLocation(MOD_ID, string);
    }
}
