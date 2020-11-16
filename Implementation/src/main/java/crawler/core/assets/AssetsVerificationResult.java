package crawler.core.assets;

import java.util.Set;

/**
 * Holds the result of an assets verification.
 */
public class AssetsVerificationResult {

    private final Set<AssetResponse> nonWorkingAssets;
    private final Set<AssetResponse> workingAssets;

    public AssetsVerificationResult(Set<AssetResponse> theWorkingAssets,
                                    Set<AssetResponse> theNonWorkingAssets) {
        nonWorkingAssets = theNonWorkingAssets;
        workingAssets = theWorkingAssets;
    }

    public Set<AssetResponse> getNonWorkingAssets() {
        return nonWorkingAssets;
    }

    public Set<AssetResponse> getWorkingAssets() {
        return workingAssets;
    }

}