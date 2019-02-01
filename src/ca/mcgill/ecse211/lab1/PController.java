package ca.mcgill.ecse211.lab1;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {

  /* Constants */
  private static final int MOTOR_SPEED = 200;
  private static final int FILTER_OUT = 20;
  private static final int MAX_SPEED = 200;
  private static final int MIN_SPEED = 100;

  private final int bandCenter;
  private final int bandWidth;
  private int distance;
  private int filterControl;


  public PController(int bandCenter, int bandwidth) {
    this.bandCenter = bandCenter;
    this.bandWidth = bandwidth;
    this.filterControl = 0;



    WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED); // Initalize motor rolling forward
    WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
    WallFollowingLab.leftMotor.forward();
    WallFollowingLab.rightMotor.forward();
  }

  @Override
  public void processUSData(int distance) {

    // rudimentary filter - toss out invalid samples corresponding to null
    // signal.
    // (n.b. this was not included in the Bang-bang controller, but easily
    // could have).
    //
    if (distance >= 255 && filterControl < FILTER_OUT) {
      // bad value, do not set the distance var, however do increment the
      // filter value
      filterControl++;
    } else if (distance >= 255) {
      // We have repeated large values, so there must actually be nothing
      // there: leave the distance alone
      this.distance = distance;
    } else {
      // distance went below 255: reset filter and leave
      // distance alone.
      filterControl = 0;
      this.distance = distance;
    }

    // TODO: process a movement based on the us distance passed in (P style)

    int SCALE = 1;

    int delta = bandCenter - distance;
    int correction = Math.abs(delta) * SCALE;

    if (Math.abs(delta) < bandWidth) {
      // Below bandwidth, keep going straight
      WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED);
      WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
      WallFollowingLab.leftMotor.forward();
      WallFollowingLab.rightMotor.forward();

    } else if (delta < 0) {
      // Cart is too far
      // Decrease inside wheel rotation (left)
      if (WallFollowingLab.leftMotor.getSpeed() > MIN_SPEED) {
        WallFollowingLab.leftMotor.setSpeed(WallFollowingLab.leftMotor.getSpeed() - correction);
      }
      if (WallFollowingLab.leftMotor.getSpeed() > MIN_SPEED) {
        WallFollowingLab.leftMotor.setSpeed(MIN_SPEED);
      }

      // Increase outside wheel rotation (right)
      if (WallFollowingLab.rightMotor.getSpeed() < MAX_SPEED) {
        WallFollowingLab.rightMotor.setSpeed(WallFollowingLab.rightMotor.getSpeed() + correction);
      }
      if (WallFollowingLab.rightMotor.getSpeed() > MAX_SPEED) {
        WallFollowingLab.rightMotor.setSpeed(MAX_SPEED);
      }
      WallFollowingLab.rightMotor.forward();

    } else if (delta > 0) {
      // Cart is too close
      // Increase inside wheel rotation (left)
      if (WallFollowingLab.leftMotor.getSpeed() < MAX_SPEED * 1.2) {
        WallFollowingLab.leftMotor.setSpeed(WallFollowingLab.leftMotor.getSpeed() + correction);
      }
      if (WallFollowingLab.leftMotor.getSpeed() > MAX_SPEED * 1.2) {
        WallFollowingLab.leftMotor.setSpeed((int) (MAX_SPEED * 1.2));
      }

      // Increase outside wheel rotation and reverse direction (right)
      if (WallFollowingLab.rightMotor.getSpeed() < MIN_SPEED) {
        WallFollowingLab.rightMotor.setSpeed(WallFollowingLab.rightMotor.getSpeed() + correction);
      }
      if (WallFollowingLab.rightMotor.getSpeed() > MIN_SPEED) {
        WallFollowingLab.rightMotor.setSpeed(MIN_SPEED);
      }
      WallFollowingLab.rightMotor.backward();

    }

  }


  @Override
  public int readUSDistance() {
    return this.distance;
  }

}
