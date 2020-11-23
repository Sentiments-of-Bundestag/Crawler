package crawler.core.assets;

import models.Protokoll;
import models.Wahlperiode;

import java.util.Set;

/**
 * Interface for parsing all the assets of a HTML document.
 *
 */
public interface AssetsParser {

    Protokoll getProtokoll(String protocolVersion, String fileName);

    Wahlperiode getWahlPeriode(String wahlPeriodeVersion, String fileName);

    Set<Protokoll> getProtokolls(Set<AssetResponse> assetResponses);
}