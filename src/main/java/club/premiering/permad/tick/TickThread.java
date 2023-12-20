package club.premiering.permad.tick;

import club.premiering.permad.PermaGlobals;
import club.premiering.permad.world.World;
import lombok.Getter;
import lombok.Setter;

public class TickThread extends Thread {
    @Getter
    @Setter
    private boolean ticking = false;

    private World world;

    public TickThread(World world) {
        this.world = world;
        this.setName("World ticking thread");
    }

    @Override
    public void run() {
        while (this.ticking) {
            long tickStart = System.currentTimeMillis();
            this.world.doTick();

            try {
                Thread.sleep(Math.max((1000L / (long) PermaGlobals.TICKS_PER_SECOND) - (System.currentTimeMillis() - tickStart), 0));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
