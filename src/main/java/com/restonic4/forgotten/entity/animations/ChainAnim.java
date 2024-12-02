package com.restonic4.forgotten.entity.animations;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public class ChainAnim {
    public static final AnimationDefinition HORIZONTAL = AnimationDefinition.Builder.withLength(0f)
            .addAnimation("rotationManager",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0f, KeyframeAnimations.posVec(0f, 0f, 0f),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("rotationManager",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("rotationManager",
                    new AnimationChannel(AnimationChannel.Targets.SCALE,
                            new Keyframe(0f, KeyframeAnimations.scaleVec(1f, 1f, 1f),
                                    AnimationChannel.Interpolations.LINEAR))).build();

    public static final AnimationDefinition HORIZONTAL_ALT = AnimationDefinition.Builder.withLength(0f)
            .addAnimation("rotationManager",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0f, KeyframeAnimations.posVec(-3f, 0f, 0f),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("rotationManager",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, -90f),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("rotationManager",
                    new AnimationChannel(AnimationChannel.Targets.SCALE,
                            new Keyframe(0f, KeyframeAnimations.scaleVec(1f, 1f, 1f),
                                    AnimationChannel.Interpolations.LINEAR))).build();

    public static final AnimationDefinition VERTICAL = AnimationDefinition.Builder.withLength(0f)
            .addAnimation("rotationManager",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0f, KeyframeAnimations.posVec(0f, 0f, 2f),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("rotationManager",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0f, KeyframeAnimations.degreeVec(-90f, 0f, 0f),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("rotationManager",
                    new AnimationChannel(AnimationChannel.Targets.SCALE,
                            new Keyframe(0f, KeyframeAnimations.scaleVec(1f, 1f, 1f),
                                    AnimationChannel.Interpolations.LINEAR))).build();

    public static final AnimationDefinition VERTICAL_ALT = AnimationDefinition.Builder.withLength(0f)
            .addAnimation("rotationManager",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0f, KeyframeAnimations.posVec(-2f, 0f, -1f),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("rotationManager",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0f, KeyframeAnimations.degreeVec(0f, -90f, -90f),
                                    AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("rotationManager",
                    new AnimationChannel(AnimationChannel.Targets.SCALE,
                            new Keyframe(0f, KeyframeAnimations.scaleVec(1f, 1f, 1f),
                                    AnimationChannel.Interpolations.LINEAR))).build();

    public static final AnimationDefinition BREAK = AnimationDefinition.Builder.withLength(0.375f)
            .addAnimation("part1",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0f, KeyframeAnimations.posVec(0f, 0f, 0f),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.375f, KeyframeAnimations.posVec(0f, -2f, -13f),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("part1",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.375f, KeyframeAnimations.degreeVec(175.42f, 5.94f, -127.74f),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("part1",
                    new AnimationChannel(AnimationChannel.Targets.SCALE,
                            new Keyframe(0f, KeyframeAnimations.scaleVec(1f, 1f, 1f),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.375f, KeyframeAnimations.scaleVec(0f, 0f, 0f),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("part2",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0f, KeyframeAnimations.posVec(0f, 0f, 0f),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.375f, KeyframeAnimations.posVec(3f, -7f, 0f),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("part2",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.375f, KeyframeAnimations.degreeVec(-116.51f, 20.7f, 65.85f),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("part2",
                    new AnimationChannel(AnimationChannel.Targets.SCALE,
                            new Keyframe(0f, KeyframeAnimations.scaleVec(1f, 1f, 1f),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.375f, KeyframeAnimations.scaleVec(0f, 0f, 0f),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("part3",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0f, KeyframeAnimations.posVec(0f, 0f, 0f),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.375f, KeyframeAnimations.posVec(0f, -4f, 14f),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("part3",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.375f, KeyframeAnimations.degreeVec(-124f, 28.68f, -360.67f),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("part3",
                    new AnimationChannel(AnimationChannel.Targets.SCALE,
                            new Keyframe(0f, KeyframeAnimations.scaleVec(1f, 1f, 1f),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.375f, KeyframeAnimations.scaleVec(0f, 0f, 0f),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("part4",
                    new AnimationChannel(AnimationChannel.Targets.POSITION,
                            new Keyframe(0f, KeyframeAnimations.posVec(0f, 0f, 0f),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.375f, KeyframeAnimations.posVec(-4f, 11f, 0f),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("part4",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.375f, KeyframeAnimations.degreeVec(106.1f, -26.95f, -52.93f),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("part4",
                    new AnimationChannel(AnimationChannel.Targets.SCALE,
                            new Keyframe(0f, KeyframeAnimations.scaleVec(1f, 1f, 1f),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.375f, KeyframeAnimations.scaleVec(0f, 0f, 0f),
                                    AnimationChannel.Interpolations.CATMULLROM))).build();
}
