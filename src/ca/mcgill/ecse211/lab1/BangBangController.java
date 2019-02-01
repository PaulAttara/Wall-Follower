package ca.mcgill.ecse211.lab1;

import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController {

  private final int bandCenter;
  private final int bandwidth;
  private final int motorLow;
  private final int motorHigh;
  private int distance;

  public BangBangController(int bandCenter, int bandwidth, int motorLow, int motorHigh) {
    // Default Constructor
    this.bandCenter = bandCenter;
    this.bandwidth = bandwidth;
    this.motorLow = motorLow;
    this.motorHigh = motorHigh;
    WallFollowingLab.leftMotor.setSpeed(motorHigh); // Start robot moving forward
    WallFollowingLab.rightMotor.setSpeed(motorHigh);
    WallFollowingLab.leftMotor.forward();
    WallFollowingLab.rightMotor.forward();
  }

  @Override
  public void processUSData(int distance) {
    this.distance = distance;

    // TODO: process a movement based on the us distance passed in (BANG-BANG style)

    int delta = bandCenter - distance;
    if (Math.abs(delta) < bandwidth) {
      // Below bandwidth, keep going straight
      WallFollowingLab.leftMotor.setSpeed(motorHigh);
      WallFollowingLab.rightMotor.setSpeed(motorHigh);

      WallFollowingLab.rightMotor.forward();

    } else if (delta < 0) {
      // Cart is too far
      // Decrease speed of inside wheel rotation (left)
      WallFollowingLab.leftMotor.setSpeed(motorLow);
      WallFollowingLab.rightMotor.setSpeed(motorHigh);

      WallFollowingLab.rightMotor.forward();

    } else if (delta > 0) {
      // Cart is too close
      // Decrease speed and reverse outside wheel rotation (right)
      // Increase speed of inside wheel rotation (left)
      WallFollowingLab.leftMotor.setSpeed((int) (motorHigh * 1.25)); // Robot tends to get too close
                                                                     // very often, so motorHigh is
                                                                     // increased by 25%
      WallFollowingLab.rightMotor.setSpeed(motorLow);

      WallFollowingLab.rightMotor.backward();

      // If distance reading falls below 10cm, enter special routine where robot turns outwards and
      // walks straight
      if (distance < 10) {
        // Turn outward in-place, and move for 0.4 seconds
        WallFollowingLab.leftMotor.setSpeed(motorLow);
        WallFollowingLab.rightMotor.setSpeed(motorHigh);

        WallFollowingLab.leftMotor.forward();
        WallFollowingLab.rightMotor.backward();
        try {
          Thread.sleep(400);
        } catch (Exception e) {
        }
        // Move forward for 0.4 seconds
        WallFollowingLab.leftMotor.setSpeed(motorLow);
        WallFollowingLab.rightMotor.setSpeed(motorLow);

        WallFollowingLab.leftMotor.forward();
        WallFollowingLab.rightMotor.forward();
        try {
          Thread.sleep(400);
        } catch (Exception e) {
        }

      }
    }
  }

  @Override
  public int readUSDistance() {
    return this.distance;
  }

}
