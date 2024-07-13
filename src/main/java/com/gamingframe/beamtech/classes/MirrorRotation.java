package com.gamingframe.beamtech.classes;

import com.gamingframe.beamtech.BeamTech;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.awt.*;

public enum MirrorRotation implements StringIdentifiable {
    ROTATE_N(0, new Vec3d(0, 0, 1),     new Point(7,0),                new Point(-7,0)),
    ROTATE_NNE(1, new Vec3d(0.5, 0, 1), new Point(6.11,-3.41),         new Point(-6.11,3.41)),
    ROTATE_NE(2, new Vec3d(1, 0, 1),    new Point(-4.949747,4.949747), new Point(4.949747,-4.949747)),
    ROTATE_NEE(3, new Vec3d(1, 0, 0.5), new Point(3.41,-6.11),         new Point(-3.41,6.11)),
    ROTATE_E(4, new Vec3d(1, 0, 0),     new Point(0,7),                new Point(0,-7)),
    ROTATE_SEE(5, new Vec3d(1, 0, -0.5),new Point(3.41,6.11),          new Point(-3.41,-6.11)),
    ROTATE_SE(6, new Vec3d(-1, 0, 1),   new Point(4.949747,4.949747),  new Point(-4.949747,-4.949747)),
    ROTATE_SSE(7, new Vec3d(-0.5, 0, 1),new Point(6.11,3.41),          new Point(-6.11,-3.41));


    private final int index;
    private static final MirrorRotation[] VALUES = values();
    public Vec3d normal;
    private Point mirrorLeft;
    private Point mirrorRight;


    MirrorRotation(int index, Vec3d normal, Point left, Point right) {
        this.index = index;
        this.normal = normal;
        this.mirrorLeft = left;
        this.mirrorRight = right;
    }

    public MirrorRotation next() {
        return VALUES[(this.index + 1) % VALUES.length];
    }

    public static Vec3d calculateReflection(Vec3d incomingDirection, MirrorRotation mirrorRotation) {
        Vec3d normalized = mirrorRotation.normal.normalize();
        double dotProduct = incomingDirection.dotProduct(normalized);

        Vec3d scaledNormal = normalized.multiply(2 * dotProduct);

        return incomingDirection.subtract(scaledNormal);
    }

    public Point CalculateIntersect(Point vector, Point pos) {
        Point leftSideMirror = mirrorLeft;
        Point rightSideMirror = mirrorRight;

        Point D = new Point(pos.x + vector.x, pos.y + vector.y);

        double A1 = rightSideMirror.y - leftSideMirror.y;
        double B1 = leftSideMirror.x - rightSideMirror.x;
        double C1 = A1 * (leftSideMirror.x) + B1 * (leftSideMirror.y);

        double A2 = D.y - pos.y;
        double B2 = pos.x - D.x;
        double C2 = A2 * (pos.x) + B2 * (pos.y);


        double determinant = A1 * B2 - A2 * B1;

        if (determinant == 0) {

            return new Point(9,9);
        } else {
            double x = (B2 * C1 - B1 * C2) / determinant;
            double y = (A1 * C2 - A2 * C1) / determinant;
            double distance = CalculateLength(0,0,x,y);
            if  (distance >= 8) {
                return null;
            } else {
                return new Point((int) x, (int) y);
            }
        }
    }

    public double CalculateLength(double Ax, double Ay, double Bx, double By) {
        return Math.sqrt(Math.pow(Ax - Bx, 2) + Math.pow(Ay - By, 2));
    }



    @Override
    public String asString() {
        return this.name().toLowerCase();
    }
}
