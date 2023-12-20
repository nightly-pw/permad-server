package club.premiering.permad.tick;

import club.premiering.permad.world.World;
import lombok.Getter;

public class TickScheduler {
    private World world;
    @Getter
    private TickThread thread;

    public TickScheduler(World world) {
        this.world = world;

        this.thread = new TickThread(world);
    }

    public void startTicking() {
        this.thread.setTicking(true);
        this.thread.start();
    }

    public void stopTicking() {
        this.thread.setTicking(false);
    }
}
