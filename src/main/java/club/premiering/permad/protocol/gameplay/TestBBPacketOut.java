package club.premiering.permad.protocol.gameplay;

import club.premiering.permad.math.BoundingBox;
import club.premiering.permad.networking.packet.BasePacket;

// TODO: 10/1/2023 Remove
@Deprecated
public class TestBBPacketOut extends BasePacket {
    public BoundingBox bb;

    public TestBBPacketOut(BoundingBox bb) {
        this.bb = bb;
    }

    @Override
    public void read() {

    }

    @Override
    public void write() {

    }
}
