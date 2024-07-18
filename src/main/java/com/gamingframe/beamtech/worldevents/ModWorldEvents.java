package com.gamingframe.beamtech.worldevents;

import com.gamingframe.beamtech.BeamTech;
import com.gamingframe.beamtech.worldevents.events.SingularityEvent;
import net.minecraft.util.Identifier;
import team.lodestar.lodestone.systems.worldevent.WorldEventType;

import static team.lodestar.lodestone.registry.common.LodestoneWorldEventTypeRegistry.registerEventType;

public class ModWorldEvents {
    public static WorldEventType SINGULARITY_EVENT = registerEventType(new WorldEventType(new Identifier(BeamTech.MOD_ID, "singularity_event"), SingularityEvent::new, false));

    public static void initializeEvents() {

    }
}
