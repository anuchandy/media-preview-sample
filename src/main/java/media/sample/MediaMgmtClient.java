package media.sample;


import com.microsoft.azure.arm.resources.Region;
import com.microsoft.azure.credentials.AzureCliCredentials;
import com.microsoft.azure.management.mediaservices.v2018_03_30_preview.Asset;
import com.microsoft.azure.management.mediaservices.v2018_03_30_preview.MediaService;
import com.microsoft.azure.management.mediaservices.v2018_03_30_preview.StorageAccount;
import com.microsoft.azure.management.mediaservices.v2018_03_30_preview.StorageAccountType;
import com.microsoft.azure.management.mediaservices.v2018_03_30_preview.implementation.MediaManager;
import com.microsoft.rest.LogLevel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

public class MediaMgmtClient
{
    public static void main( String[] args ) throws IOException {
        // Authenticate and get media manager
        //
        AzureCliCredentials cliCredentials = AzureCliCredentials.create();

        final int proxyServerPort = 8080;
        Proxy proxy = new Proxy(Proxy.Type.HTTP,
                new InetSocketAddress("<proxy-server-ip>", proxyServerPort));

        MediaManager manager = MediaManager
                .configure()
                .withProxy(proxy)
                .withLogLevel(LogLevel.BODY_AND_HEADERS)
                .authenticate(cliCredentials, cliCredentials.defaultSubscriptionId());

        // Creates a media service account
        //
        List<StorageAccount> mediaStorageAccounts = new ArrayList<StorageAccount>();
        //
        mediaStorageAccounts.add(new StorageAccount()
                .withId("<storage-account-arm-id-1>")
                .withType(StorageAccountType.PRIMARY));
        //
        mediaStorageAccounts.add(new StorageAccount()
                .withId("<storage-account-arm-id-1>")
                .withType(StorageAccountType.SECONDARY));

        MediaService mediaService = manager.mediaservices()
                .define("<mediaservname>")
                .withRegion(Region.US_EAST)
                .withExistingResourceGroup("<existing-rg-name>")
                .withStorageAccounts(mediaStorageAccounts)
                .create();

        // Creates an asset in the media service account
        //
        Asset asset = manager.mediaservices()
                .assets()
                .define("<asset-name>")
                .withExistingMediaservice(mediaService.resourceGroupName(), mediaService.name())
                .withStorageAccountName("<asset-storage-account-name>")
                .withContainer("<asset-container>")
                .create();

        // Creates a streaming endpoint for the media service account
        //
        manager.mediaservices().streamingEndpoints()
                .define("<endpoint-name>")
                .withExistingMediaservice(mediaService.resourceGroupName(), mediaService.name())
                .withCdnEnabled(true)
                .withCdnProfile("<cdn-profile-vaule")
                .withAvailabilitySetName("<avail-set-name>")
                .create();
    }
}
