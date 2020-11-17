package crawler.core.assets.impl;

import crawler.core.assets.AssetsParser;
import model.Protokoll;
import model.Wahlperiode;

public class DefaultAssetsParser implements AssetsParser {

    public DefaultAssetsParser() {}

    @Override
    public Protokoll getProtocol(String protocolVersion, String fileName) {
        Protokoll protokoll = null;

        return null;
    }

    @Override
    public Wahlperiode getWahlPeriode(String wahlPeriodeVersion, String fileName) {
        Wahlperiode wahlperiode = null;

        return null;
    }
}