package git.nova.optical.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class OpticalNetwork {

    private OpticalNetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToServer(
                        AdjustSpotlightPayload.TYPE,
                        AdjustSpotlightPayload.STREAM_CODEC,
                        AdjustSpotlightPayload::handle
                );
    }
}
