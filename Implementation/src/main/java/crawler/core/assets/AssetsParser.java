package crawler.core.assets;

import model.Protokoll;
import model.Wahlperiode;

/**
 * Interface for parsing all the assets of a HTML document.
 *
 */
public interface AssetsParser {

    Protokoll getProtocol(String protocolVersion, String fileName);

    Wahlperiode getWahlPeriode(String wahlPeriodeVersion, String fileName);
}