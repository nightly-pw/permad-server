package club.premiering.permad.math;

/**
 * @param centerY       Center coordinates
 * @param halfHeight    Half width and half height
 * @param rotationAngle Angle in radians
 */
// TODO: This class is very poorly hacked together, and needs a better (and maybe faster?) implementation
// New Java feature :), new to the newer versions
public record BoundingBox(double centerX, double centerY, double halfWidth, double halfHeight, double rotationAngle) {
    public boolean isCollidingWith(BoundingBox other) {
        var r1X = this.centerX - this.halfWidth;
        var r1Y = this.centerY - this.halfHeight;
        var r1W = this.halfWidth * 2;
        var r1H = this.halfHeight * 2;
        var r1A = this.rotationAngle;

        var r2X = other.centerX - other.halfWidth;
        var r2Y = other.centerY - other.halfHeight;
        var r2W = other.halfWidth * 2;
        var r2H = other.halfHeight * 2;
        var r2A = other.rotationAngle;

        var r1HW = r1W / 2;
        var r1HH = r1H / 2;
        var r2HW = r2W / 2;
        var r2HH = r2H / 2;

        var r1CX = r1X + r1HW;
        var r1CY = r1Y + r1HH;
        var r2CX = r2X + r2HW;
        var r2CY = r2Y + r2HH;

        var cosR1A = Math.cos(r1A);
        var sinR1A = Math.sin(r1A);
        var cosR2A = Math.cos(r2A);
        var sinR2A = Math.sin(r2A);

        var r1RX = cosR2A * (r1CX - r2CX) + sinR2A * (r1CY - r2CY) + r2CX - r1HW;
        var r1RY = -sinR2A * (r1CX - r2CX) + cosR2A * (r1CY - r2CY) + r2CY - r1HH;
        var r2RX = cosR1A * (r2CX - r1CX) + sinR1A * (r2CY - r1CY) + r1CX - r2HW;
        var r2RY = -sinR1A * (r2CX - r1CX) + cosR1A * (r2CY - r1CY) + r1CY - r2HH;

        var cosR1AR2A = Math.abs(cosR1A * cosR2A + sinR1A * sinR2A);
        var sinR1AR2A = Math.abs(sinR1A * cosR2A - cosR1A * sinR2A);
        var cosR2AR1A = Math.abs(cosR2A * cosR1A + sinR2A * sinR1A);
        var sinR2AR1A = Math.abs(sinR2A * cosR1A - cosR2A * sinR1A);

        var r1BBH = r1W * sinR1AR2A + r1H * cosR1AR2A;
        var r1BBW = r1W * cosR1AR2A + r1H * sinR1AR2A;
        var r1BBX = r1RX + r1HW - r1BBW / 2;
        var r1BBY = r1RY + r1HH - r1BBH / 2;

        var r2BBH = r2W * sinR2AR1A + r2H * cosR2AR1A;
        var r2BBW = r2W * cosR2AR1A + r2H * sinR2AR1A;
        var r2BBX = r2RX + r2HW - r2BBW / 2;
        var r2BBY = r2RY + r2HH - r2BBH / 2;

        return r1X < r2BBX + r2BBW && r1X + r1W > r2BBX && r1Y < r2BBY + r2BBH && r1Y + r1H > r2BBY &&
                r2X < r1BBX + r1BBW && r2X + r2W > r1BBX && r2Y < r1BBY + r1BBH && r2Y + r2H > r1BBY;
    }
}