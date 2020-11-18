package crawler.core.assets.impl;

import crawler.core.assets.AssetResponse;
import crawler.core.assets.AssetsParser;
import models.Protokoll;
import models.Wahlperiode;

import java.util.LinkedHashSet;
import java.util.Set;

public class DefaultAssetsParser implements AssetsParser {

    public DefaultAssetsParser() {}

    @Override
    public Protokoll getProtokoll(String protocolVersion, String fileName) {
        Protokoll protokoll = new Protokoll();
        // Need to pe implemented (@Marlon)

        return protokoll;
    }

    @Override
    public Wahlperiode getWahlPeriode(String wahlPeriodeVersion, String fileName) {
        Wahlperiode wahlperiode = new Wahlperiode();
        // Need to pe implemented (@Marlon)

        return wahlperiode;
    }

    @Override
    public Set<Protokoll> getProtokolls(Set<AssetResponse> assetResponses) {
        Set<Protokoll> protokolls = new LinkedHashSet<>();
        // Need to pe implemented (@Marlon)

        return protokolls;
    }
}