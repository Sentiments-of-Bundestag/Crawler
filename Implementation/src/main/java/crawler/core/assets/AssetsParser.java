package crawler.core.assets;

import models.Person.Person;
import models.Protokoll;

import java.util.Set;

/**
 * Interface for parsing all the assets of a HTML document.
 *
 */
public interface AssetsParser {

    Set<Protokoll> getProtokolls(Set<AssetResponse> assetResponses, Set<Person> stammdaten, boolean deleteAfterParsing);

    Set<Person> getStammdaten(AssetResponse assetResponse, boolean deleteAfterParsing);
}