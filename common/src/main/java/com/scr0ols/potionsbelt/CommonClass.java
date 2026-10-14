package com.scr0ols.potionsbelt;

/** Loader-independent startup, invoked by each loader's entrypoint. */
public class CommonClass {

    private CommonClass() {
    }

    public static void init() {
        ModItems.init();
        ModSounds.init();
        ModMenus.init();
    }
}
