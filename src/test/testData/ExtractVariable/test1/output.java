public class Test1 {
    void renderBanner() {
        final boolean extVar1 = (platform.toUpperCase().indexOf("MAC") > -1) &&
                (browser.toUpperCase().indexOf("IE") > -1) &&
                wasInitialized() && resize > 0;
        if (extVar1) {
            // do something
        }
    }
}