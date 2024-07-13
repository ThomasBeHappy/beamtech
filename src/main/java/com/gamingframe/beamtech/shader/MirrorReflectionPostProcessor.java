package com.gamingframe.beamtech.shader;

import com.gamingframe.beamtech.BeamTech;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import team.lodestar.lodestone.systems.postprocess.PostProcessHandler;
import team.lodestar.lodestone.systems.postprocess.PostProcessor;

public class MirrorReflectionPostProcessor extends PostProcessor {
    public static final MirrorReflectionPostProcessor INSTANCE = new MirrorReflectionPostProcessor();

    @Override
    public Identifier getPostChainLocation() {
        return new Identifier(BeamTech.MOD_ID, "mirror_post");
    }

    @Override
    public void beforeProcess(MatrixStack viewModelStack) {

    }

    @Override
    public void afterProcess() {

    }
}
